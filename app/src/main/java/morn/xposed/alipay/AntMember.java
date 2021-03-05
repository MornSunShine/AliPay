package morn.xposed.alipay;

import android.os.PowerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import morn.xposed.alipay.hook.AntMemberRpcCall;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;
import morn.xposed.alipay.util.Statistics;

public class AntMember {
    public static final String TAG = "pansong291.xposed.quickenergy.ࠆ";
    public static Runnable runnable;

    public static void start(ClassLoader loader, int times) {
        if (!Config.getReceivePoint() || times != 0)
            return;
        if (runnable == null) {
            runnable = new Runnable() {
                ClassLoader loader;

                public void run() {
                    try {
                        PowerManager.WakeLock lock = XposedHook.getWakeLock();
                        if (Statistics.canMemberSignInToday()) {
                            String s = AntMemberRpcCall.rpcCall_memberSigin(loader);
                            JSONObject jo = new JSONObject(s);
                            if (jo.getString("resultCode").equals("SUCCESS")) {
                                Log.other(
                                        "领取〈每日签到〉〈" + jo.getString("signinPoint") +
                                                "积分〉，已签到〈" + jo.getString("signinSumDay") + "天〉");
                                Statistics.memberSignInToday();
                            } else {
                                Log.recordLog(jo.getString("resultDesc"), s);
                            }
                        }
                        queryPointCert(loader, 1, 8);
                        claimFamilyPoint(loader);
                        XposedHook.setWakeLock(lock);
                    } catch (Throwable t) {
                        Log.i(TAG, "receivePoint.run err:");
                        Log.printStackTrace(TAG, t);
                    }
                }

                public Runnable setData(ClassLoader loader) {
                    this.loader = loader;
                    return this;
                }
            }.setData(loader);
        }
        if (Config.getOriginalMode()) {
            new Thread(runnable).start();
        } else {
            AntBroadcastReceiver.triggerTask(runnable, RandomUtils.nextInt(1500, 2000));
        }
    }

    private static void claimFamilyPoint(ClassLoader loader) {
        try {
            AntMemberRpcCall.rpcCall_familySignin(loader);
            String s = AntMemberRpcCall.rpcCall_familyHomepage(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.getBoolean("success")) {
                jo = jo.getJSONObject("data");
                if (jo.getBoolean("success")) {
                    jo = jo.getJSONObject("familyInfoView");
                    String familyId = jo.getString("familyId");
                    s = AntMemberRpcCall.rpcCall_queryFamilyPointCert(loader, familyId);
                    jo = new JSONObject(s);
                    if (jo.getBoolean("success")) {
                        JSONArray ja = jo.getJSONArray("familyPointCertInfos");
                        for (int i = 0; i < ja.length(); i++) {
                            jo = ja.getJSONObject(i);
                            String bizTitle = jo.getString("bizTitle");
                            long certId = jo.getLong("certId");
                            s = AntMemberRpcCall.rpcCall_claimFamilyPointCert(loader, certId, familyId);
                            jo = new JSONObject(s);
                            if (jo.getBoolean("success")) {
                                Log.other("领取〈" + bizTitle + "〉〈" + jo.getInt("realPoint") + "家庭积分〉");
                            } else {
                                Log.recordLog(jo.getString("resultDesc"), s);
                            }
                        }
                    } else {
                        Log.recordLog(jo.getString("resultDesc"), s);
                    }
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "claimFamilyPoint err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void queryPointCert(ClassLoader loader, int page, int pageSize) {
        try {
            String s = AntMemberRpcCall.rpcCall_queryPointCert(loader, page, pageSize);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                boolean hasNextPage = jo.getBoolean("hasNextPage");
                JSONArray jaCertList = jo.getJSONArray("certList");
                for (int i = 0; i < jaCertList.length(); i++) {
                    jo = jaCertList.getJSONObject(i);
                    String bizTitle = jo.getString("bizTitle");
                    String id = jo.getString("id");
                    int pointAmount = jo.getInt("pointAmount");
                    s = AntMemberRpcCall.rpcCall_receivePointByUser(loader, id);
                    jo = new JSONObject(s);
                    if (jo.getString("resultCode").equals("SUCCESS")) {
                        Log.other("领取〈" + bizTitle + "〉〈" + pointAmount + "积分〉");
                    } else {
                        Log.recordLog(jo.getString("resultDesc"), s);
                    }
                }
                if (hasNextPage)
                    queryPointCert(loader, page + 1, pageSize);
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryPointCert err:");
            Log.printStackTrace(TAG, t);
        }
    }
}