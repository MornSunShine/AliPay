package morn.xposed.alipay;

import android.os.PowerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import morn.xposed.alipay.hook.AntCooperateRpcCall;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.CooperationIdMap;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;
import morn.xposed.alipay.util.Statistics;

public class AntCooperate {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠁ";
    private static Runnable task;

    public static void start(ClassLoader loader, int times) {
        if (Config.getCooperateWater() && times == 0) {
            if (task == null) {
                task = new Runnable() {
                    ClassLoader loader;

                    public Runnable setData(ClassLoader loader) {
                        this.loader = loader;
                        return this;
                    }

                    @Override
                    public void run() {
                        PowerManager.WakeLock wakeLock = XposedHook.getWakeLock();
                        try {
                            while (FriendIdMap.currentUid == null || FriendIdMap.currentUid.isEmpty()) {
                                Thread.sleep(100L);
                            }
                            String s = AntCooperateRpcCall.rpcCall_queryUserCooperatePlantList(loader);
                            if (s == null) {
                                Thread.sleep(RandomUtils.delay());
                                s = AntCooperateRpcCall.rpcCall_queryUserCooperatePlantList(loader);
                            }
                            JSONObject jo = new JSONObject(s);
                            if (jo.getString("resultCode").equals("SUCCESS")) {
                                int userCurrentEnergy = jo.getInt("userCurrentEnergy");
                                JSONArray ja = jo.getJSONArray("cooperatePlants");
                                for (int i = 0; i < ja.length(); i++) {
                                    jo = ja.getJSONObject(i);
                                    String cooperationId = jo.getString("cooperationId");
                                    if (!jo.has("name")) {
                                        s = AntCooperateRpcCall.rpcCall_queryCooperatePlant(loader, cooperationId);
                                        jo = new JSONObject(s).getJSONObject("cooperatePlant");
                                    }
                                    String name = jo.getString("name");
                                    int waterDayLimit = jo.getInt("waterDayLimit");
                                    CooperationIdMap.putIdMap(cooperationId, name);
                                    if (!Statistics.canCooperateWaterToday(FriendIdMap.currentUid, cooperationId))
                                        continue;
                                    int index = -1;
                                    for (int j = 0; j < Config.getCooperateWaterList().size(); j++) {
                                        if (Config.getCooperateWaterList().get(j).equals(cooperationId)) {
                                            index = j;
                                            break;
                                        }
                                    }
                                    if (index >= 0) {
                                        int num = Config.getCooperateWaterNumList().get(index);
                                        if (num > waterDayLimit)
                                            num = waterDayLimit;
                                        if (num > userCurrentEnergy)
                                            num = userCurrentEnergy;
                                        if (num > 0)
                                            cooperateWater(loader, FriendIdMap.currentUid, cooperationId, num, name);
                                    }
                                }
                            } else {
                                Log.i(TAG, jo.getString("resultDesc"));
                            }
                        } catch (Throwable t) {
                            Log.i(TAG, "start.run err:");
                            Log.printStackTrace(TAG, t);
                        }
                        CooperationIdMap.saveIdMap();
                        XposedHook.setWakeLock(wakeLock);
                    }
                }.setData(loader);
            }
            if (Config.getOriginalMode()) {
                new Thread(task).start();
            } else {
                AntBroadcastReceiver.triggerTask(task, RandomUtils.nextInt(1000, 2000));
            }
        }
    }

    private static void cooperateWater(ClassLoader loader, String uid, String coopId, int count, String name) {
        try {
            JSONObject jo = new JSONObject(AntCooperateRpcCall.rpcCall_cooperateWater(loader, uid, coopId, count));
            if (jo.getString("resultCode").equals("SUCCESS")) {
                Log.forest("合种【" + name + "】" + jo.getString("barrageText"));
                Statistics.cooperateWaterToday(FriendIdMap.currentUid, coopId);
            } else {
                Log.i(TAG, jo.getString("resultDesc"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "cooperateWater err:");
            Log.printStackTrace(TAG, t);
        }
    }
}