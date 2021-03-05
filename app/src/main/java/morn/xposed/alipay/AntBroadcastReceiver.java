package morn.xposed.alipay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import morn.xposed.alipay.hook.FriendsManager;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.ui.AliFriend;
import morn.xposed.alipay.util.FileUtils;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;
import morn.xposed.alipay.util.TaskTimer;

public class AntBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = AntBroadcastReceiver.class.getCanonicalName();
    private static AntBroadcastReceiver instance;
    private static AtomicInteger atomic = new AtomicInteger(100);
    private static final PriorityBlockingQueue<TaskTimer> QUEUE = new PriorityBlockingQueue<>();
    private static ThreadPoolExecutor pool1 = new ThreadPoolExecutor(8, 20, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(4), new DiscardOldestPolicy());
    private static ThreadPoolExecutor pool2;
    private Service service;

    static {
        pool2 = new ThreadPoolExecutor(100, 150, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(500), new DiscardOldestPolicy());
        pool2.allowCoreThreadTimeOut(true);
    }

    private AntBroadcastReceiver(Service service) {
        this.service = service;
    }

    public static void triggerTask(long delay) {
        Intent intent = new Intent(TAG);
        triggerTask(PendingIntent.getBroadcast(instance.service, atomic.getAndIncrement(), intent, PendingIntent.FLAG_UPDATE_CURRENT), delay);
    }

    public static void triggerTask(PendingIntent operation, long delay) {
        AlarmManager manager = (AlarmManager) instance.service.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(operation);
        long triggerAtMillis = SystemClock.elapsedRealtime() + delay;
        int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, operation);
        } else if (version >= 19) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, operation);
        } else {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, delay, operation);
        }
    }

    public static void register(Service service) {
        QUEUE.clear();
        if (instance != null) {
            try {
                instance.service.unregisterReceiver(instance);
            } catch (Exception e) {
                Log.printStackTrace(TAG, e);
            }
        }
        instance = new AntBroadcastReceiver(service);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TAG);
        service.registerReceiver(instance, intentFilter);
        Log.recordLog("注册接收器");
    }

    public static void execute(Runnable runnable) {
        pool1.execute(runnable);
    }

    public static void execute(Runnable runnable, long delay) {
        try {
            Iterator iterator = QUEUE.iterator();
            while (iterator.hasNext()) {
                if (((TaskTimer) iterator.next()).getRunnable().equals(runnable)) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
            QUEUE.offer(new TaskTimer(runnable, System.currentTimeMillis() + delay, true));
            Intent intent = new Intent(TAG);
            intent.putExtra("mainTask", true);
            triggerTask(PendingIntent.getBroadcast(instance.service, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT), delay);
        }
    }

    private static String str() {
        if (QUEUE.size() <= 5) {
            return QUEUE.toString();
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (TaskTimer taskTimer : QUEUE) {
            if (i < 5) {
                sb.append(taskTimer.toString());
                sb.append(", ");
                i += 1;
            } else {
                break;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void triggerTask(Runnable runnable, long delay) {
        QUEUE.offer(new TaskTimer(runnable, System.currentTimeMillis() + delay, true));
        triggerTask(delay);
    }

    public static void unregister() {
        QUEUE.clear();
        if (instance != null) {
            try {
                instance.service.unregisterReceiver(instance);
            } catch (Exception e) {
                Log.printStackTrace(TAG, e);
            }
        }
        instance = null;
        Log.recordLog("取消注册接收器");
    }

    public static void offerTask(Runnable runnable, long delay) {
        QUEUE.offer(new TaskTimer(runnable, System.currentTimeMillis() + delay));
        triggerTask(delay);
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getStringExtra("action");
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case "deleteFriend":
                    pool1.execute(new Runnable() {
                        @Override
                        public void run() {
                            String userId = intent.getStringExtra("userId");
                            FriendsManager.deleteFriend(XposedHook.getLoader(), userId);
                            try {
                                FriendIdMap.friendMap.remove(userId);
                                FriendIdMap.removeIdMap(userId);
                                FriendIdMap.saveIdMap();
                                AntForest.removeIds(userId);
                                Thread.sleep(1000);
                            } catch (Throwable t) {
                                Log.printStackTrace(TAG, t);
                            }
                            FriendsManager.forceRefreshMyFriends(XposedHook.getLoader());
                        }
                    });
                    break;
                case "deleteFriends":
                    pool1.execute(new Runnable() {
                        @Override
                        public void run() {
                            String userIds = intent.getStringExtra("userIds");
                            try {
                                AntForest.removeIds(userIds);
                                for (String id : userIds.split(",")) {
                                    FriendsManager.deleteFriend(XposedHook.getLoader(), id);
                                    FriendIdMap.friendMap.remove(id);
                                    FriendIdMap.removeIdMap(id);
                                    Thread.sleep(RandomUtils.delay());
                                }
                            } catch (Throwable t) {
                                Log.printStackTrace(TAG, t);
                            }
                            FriendIdMap.saveIdMap();
                            FriendsManager.forceRefreshMyFriends(XposedHook.getLoader());
                        }
                    });
                    break;
                case "forceRefreshMyFriends":
                    FriendsManager.forceRefreshMyFriends(XposedHook.getLoader());
                    Log.i(TAG, "forceRefreshMyFriends successfully");
                    break;
                case "getUnidirectionalFiendsList":
                    pool1.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<AliFriend> friends = FriendsManager.getAllFriends(XposedHook.getLoader());
                            JSONArray ja = new JSONArray();
                            try {
                                for (AliFriend friend : friends) {
                                    if (friend.getFriendStatus() == 2) {
                                        JSONObject jo = new JSONObject();
                                        jo.put("id", friend.id);
                                        jo.put("name", friend.name);
                                        ja.put(jo);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.printStackTrace(TAG, e);
                            }
                            String fileName = intent.getStringExtra("fileName");
                            FileUtils.write2File(ja.toString(), new File(FileUtils.getConfigDirectoryFile(), fileName));
                        }
                    });
                    break;
                case "deleteUnidirectionalFiends":
                    pool1.execute(new Runnable() {
                        @Override
                        public void run() {
                            FriendsManager.deleteEnemy(XposedHook.getLoader());
                        }
                    });
                    break;
                case "restart":
                    XposedHook.saveRestartLog("重启成功");
                    break;
            }
        } else {
            Log.recordLog("开始执行定时任务，当前" + QUEUE.size() + "个定时任务：" + str());
            boolean mainTask = intent.getBooleanExtra("mainTask", false);
            boolean bool = false;
            TaskTimer task = QUEUE.poll();
            while (task != null && task.getStartTime() <= System.currentTimeMillis()) {
                if (task.getRunnable().equals(XposedHook.runnable)) {
                    bool = true;
                }
                if (task.getA()) {
                    pool1.execute(task.getRunnable());
                } else {
                    pool2.execute(task.getRunnable());
                }
                task = QUEUE.poll();
            }
            if (task != null) {
                QUEUE.offer(task);
                long leftTime = task.getStartTime() - System.currentTimeMillis();
                if (leftTime <= 200) {
                    if (leftTime < 100) {
                        triggerTask(100);
                    } else {
                        triggerTask(leftTime);
                    }
                }
            }
            if (mainTask && !bool) {
                for (TaskTimer tk : QUEUE) {
                    if (tk.getRunnable().equals(XposedHook.runnable)) {
                        QUEUE.remove(tk);
                        pool1.execute(tk.getRunnable());
                    }
                }
            }
        }
    }
}