package morn.xposed.alipay.tasks;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.AntBroadcastReceiver;
import morn.xposed.alipay.AntCooperate;
import morn.xposed.alipay.AntFarm;
import morn.xposed.alipay.AntForest;
import morn.xposed.alipay.AntForestNotification;
import morn.xposed.alipay.AntForestToast;
import morn.xposed.alipay.AntMember;
import morn.xposed.alipay.AntSports;
import morn.xposed.alipay.KBMember;
import morn.xposed.alipay.hook.FriendsManager;
import morn.xposed.alipay.hook.RpcCall;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RootManager;
import morn.xposed.alipay.util.Statistics;

public class HookTask extends XC_MethodHook {
    private static final String TAG = XposedHook.class.getCanonicalName();
    private static WakeLock wakeLock;
    private ClassLoader loader;
    private Service service;

    public HookTask(ClassLoader loader) {
        this.loader = loader;
    }

    private void acquireLock() {
        try {
            if (wakeLock == null) {
                wakeLock = ((PowerManager) this.service.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.service.getClass().getName());
                wakeLock.setReferenceCounted(false);
                wakeLock.acquire();
            } else {
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private boolean restart() {
        if (Config.getRestartService() && !XposedHook.a1 && !Config.enableOpenDoor()) {
            if (XposedHook.times == 0 || XposedHook.times == 3600000 / Config.getCheckInterval() / 2) {
                try {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.other("间隔30分钟重启支付宝服务");
                            XposedHook.a2 = true;
                            service.stopService(new Intent(service, service.getClass()));
                        }
                    };
                    if (Config.getOriginalMode()) {
                        XposedHook.handler.postDelayed(runnable, 1000L);
                    } else {
                        AntBroadcastReceiver.execute(runnable, 1000L);
                    }
                } catch (Throwable t) {
                    Log.printStackTrace(TAG, t);
                }
                XposedHook.a1 = false;
                return true;
            }
        }
        XposedHook.a1 = false;
        return false;
    }

    private void releaseLock() {
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    protected void afterHookedMethod(MethodHookParam param) {
        service = (Service) param.thisObject;
        XposedHook.service = service;
        XposedHook.loader = this.loader;
        AntBroadcastReceiver.register(service);
        AntForestToast.context = service.getApplicationContext();
        XposedHook.a1 = true;
        XposedHook.startTime = System.currentTimeMillis();
        FriendIdMap.currentUid = FriendsManager.getSelfId(loader, service);
        if (FriendIdMap.friendMap.isEmpty()) {
            FriendsManager.getAllFriends(loader);
            FriendIdMap.saveFarmMap();
            FriendIdMap.updateForestInfo();
        }
        if (Config.getStayAwake()) {
            acquireLock();
        }
        if (XposedHook.handler == null) {
            XposedHook.handler = new Handler();
        }
        if (XposedHook.runnable == null) {
            XposedHook.runnable = new Runnable() {
                @Override
                public void run() {
                    WakeLock lock = XposedHook.getWakeLock();
                    Config.shouldReload = true;
                    AntForestNotification.setAutoStart(false);
                    RpcCall.b1 = true;
                    RpcCall.sendXEdgeProBroadcast = true;
                    Statistics.resetToday();
                    if (!restart()) {
                        AntForest.checkEnergyRanking(loader, XposedHook.times);
                        AntCooperate.start(loader, XposedHook.times);
                        AntFarm.start(loader, XposedHook.times);
                        AntMember.start(loader, XposedHook.times);
                        AntSports.start(loader, XposedHook.times);
                        KBMember.start(loader, XposedHook.times);
                        int interval = Config.getCheckInterval();
                        if ((!Config.getCollectEnergy()) && (!Config.getEnableFarm()) && (!Config.getSmallAccountList().contains(FriendIdMap.currentUid))) {
                            AntForestNotification.stop(service, false);
                        } else if (Config.getOriginalMode()) {
                            XposedHook.handler.postDelayed(this, interval);
                        } else {
                            AntBroadcastReceiver.execute(this, interval);
                        }
                        if (Config.getStayAwake()) {
                            acquireLock();
                        } else {
                            releaseLock();
                        }
                        Calendar instance = Calendar.getInstance();
                        int day = instance.get(Calendar.DAY_OF_MONTH);
                        instance.setTimeInMillis(instance.getTimeInMillis() + interval);
                        if (day != instance.get(Calendar.DAY_OF_MONTH)) {
                            XposedHook.times = 0;
                            XposedHook.a1 = true;
                        } else {
                            XposedHook.times = (XposedHook.times + 1) % (3600000 / interval);
                        }
                        XposedHook.setWakeLock(lock);
                    }
                }
            };
        }
        AntForestNotification.start(service);
        XposedHook.handler.post(XposedHook.runnable);
        if (!XposedHook.a2) {
            AntForestToast.show("秋风加载成功");
        }
        Log.other("秋风加载成功");
        XposedHook.a2 = false;
        if (Config.getAutoRestart() == Config.RestartType.ROOT || Config.getAutoRestart2() == Config.RestartType.ROOT) {
            new Thread() {
                @Override
                public void run() {
                    XposedHook.unlock = RootManager.stop(service);
                    Log.recordLog("hasRootAccess:" + XposedHook.unlock);
                }
            }.start();
        }
    }

    public void hookOnCreate() {
        try {
            XposedHelpers.findAndHookMethod("com.alipay.android.launcher.service.LauncherService", loader, "onCreate", new Object[]{this});
            Log.i(TAG, "hook onCreate successfully");
        } catch (Throwable t) {
            Log.i(TAG, "hook onCreate err:");
            Log.printStackTrace(TAG, t);
        }
    }
}