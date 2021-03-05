package morn.xposed.alipay;

import android.os.PowerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import morn.xposed.alipay.hook.FriendsManager;
import morn.xposed.alipay.hook.GreatYouthRpcCall;
import morn.xposed.alipay.hook.KBMemberRpcCall;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;
import morn.xposed.alipay.util.Statistics;

public class KBMember {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠈ";
    private static Runnable runnable;

    public static void start(final ClassLoader loader, int times) {
        if (times != 0) {
            return;
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    PowerManager.WakeLock lock = XposedHook.getWakeLock();
                    if (Config.getKbSignIn() && Statistics.canKbSignInToday()) {
                        signIn(loader);
                    }
                    if (Config.getCollectCreditFeedback()) {
                        collectCreditFeedback(loader);
                    }
                    if (Config.getZmDonate()) {
                        donatePoint(loader);
                    }
                    if (Config.getElectricSignIn()) {
                        collectElectric(loader);
                    }
                    if (Config.getGreatyouthReceive()) {
                        receive(loader);
                        helpCollect(loader);
                    }
                    if (Config.getDeleteStranger()) {
                        FriendsManager.forceRefreshMyFriends(loader);
                        FriendsManager.deleteEnemy(loader);
                    }
                    if (Config.getOpenDoorSignIn()) {
                        kmdkQuery(loader);
                    }
                    XposedHook.setWakeLock(lock);
                }
            };
        }
        if (Config.getOriginalMode()) {
            new Thread(runnable).start();
        } else {
            AntBroadcastReceiver.triggerTask(runnable, RandomUtils.nextInt(1500, 2000));
        }
    }

    private static void collectCreditFeedback(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(GreatYouthRpcCall.rpcCall_queryCreditFeedback(loader));
            if (jo.optString("resultCode").equals("SUCCESS")) {
                JSONArray ja = jo.optJSONArray("creditFeedbackVOS");
                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject joo = ja.getJSONObject(i);
                        String title = joo.optString("title");
                        String potentialSize = joo.optString("potentialSize");
                        String creditFeedbackId = joo.optString("creditFeedbackId");
                        if (new JSONObject(GreatYouthRpcCall.rpcCall_collectCreditFeedback(loader, creditFeedbackId)).optBoolean("success", false)) {
                            Log.other("芝麻信用：收芝麻粒〈" + title + "〉〈" + potentialSize + "粒〉");
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void collectElectric(ClassLoader loader) {
        if (!Statistics.canElectricSignIn())
            return;
        try {
            String s = GreatYouthRpcCall.rpcCall_userapply(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.optString("code").equals("SUCCESS")) {
                Log.other("攒电能：签到");
                Statistics.electricSignInToday();
            }
            s = GreatYouthRpcCall.rpcCall_userdataquery(loader);
            jo = new JSONObject(s);
            if (jo.optString("code").equals("SUCCESS")) {
                JSONArray ja = jo.getJSONObject("userDataInfos")
                        .getJSONObject("queryApplyingPointRecord")
                        .optJSONArray("electric");
                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        String recordId = jo.getString("recordId");
                        String point = jo.getString("point");
                        String name = jo.getString("name");
                        jo = new JSONObject(GreatYouthRpcCall.rpcCall_point_apply(loader, recordId));
                        if (jo.getString("code").equals("SUCCESS")) {
                            Log.other("攒电能：收取〈" + point + name + "〉");
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static void receive(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(GreatYouthRpcCall.rpcCall_getIndexInfo(loader)).getJSONObject("result").optJSONObject("payList");
            if (jo != null) {
                JSONArray ja = jo.optJSONArray("bbOrderList");
                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        if (jo.optString("status").equals("CAN_RECEIVE")) {
                            JSONObject joo = new JSONObject(GreatYouthRpcCall.rpcCall_receive(loader, jo.optString("id"))).optJSONObject("result");
                            if (joo.optBoolean("success")) {
                                int cent = joo.optJSONObject("balance").optInt("cent");
                                Log.recordLog("宝呗青年：收体验金〈" + jo.optString("amount") + "元〉，可用体验金〈" + cent / 100 + "元〉");
                            }

                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static void helpCollect(ClassLoader loader) {
        try {
            JSONArray ja = new JSONObject(GreatYouthRpcCall.rpcCall_getFriends(loader)).getJSONObject("result").getJSONArray("friends");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (!jo.optBoolean("hasHand")) {
                    return;
                }
                String name = jo.optString("name");
                String uid = jo.optString("uid");
                jo = new JSONObject(GreatYouthRpcCall.rpcCall_info(loader, uid)).getJSONObject("result").optJSONObject("payList");
                if (jo != null) {
                    JSONArray jaa = jo.optJSONArray("bbOrderList");
                    if (jaa != null) {
                        for (int j = 0; j < jaa.length(); j++) {
                            jo = jaa.getJSONObject(i);
                            if (jo.optString("status").equals("CAN_RECEIVE")) {
                                jo = new JSONObject(GreatYouthRpcCall.rpcCall_receiveFriend(loader, jo.optString("id"), uid));
                                if (jo.optBoolean("success")) {
                                    String rewardsAmount = jo.optJSONObject("result").optString("rewardsAmount");
                                    Log.recordLog("宝呗青年：帮收〈" + name + "〉，获得〈" + rewardsAmount + "元〉");
                                }
                            }
                        }
                        if (i == ja.length() - 1) {
                            helpCollect(loader);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void signIn(ClassLoader loader) {
        try {
            String s = KBMemberRpcCall.rpcCall_signIn(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.getBoolean("success")) {
                jo = jo.getJSONObject("data");
                Log.other("口碑签到〈第" + jo.getString("dayNo") + "天〉，获得〈" + jo.getString("value") + "积分〉");
                Statistics.kbSignInToday();
            } else if (s.contains("\"HAS_SIGN_IN\"")) {
                Statistics.kbSignInToday();
            } else {
                Log.i(TAG, jo.getString("errorMessage"));
            }
        } catch (Throwable t) {
            Log.i(TAG, "signIn err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public static void kmdkQuery(ClassLoader loader) {
        try {
            String s = GreatYouthRpcCall.rpcCall_query_activity(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                String activityNo = jo.optString("activityNo");
                String activityPeriodName = jo.optString("activityPeriodName");
                String signInTimeTips = jo.optString("signInTimeTips");
                String signInStatus = jo.optString("signInStatus");
                String signUpStatus = jo.optString("signUpStatus");
                if (jo.optBoolean("signUpEnable")
                        && signUpStatus.equals("UN_SIGN_UP")) {
                    s = GreatYouthRpcCall.rpcCall_signUp(loader, activityNo);
                    if (new JSONObject(s).optBoolean("success")) {
                        Log.other("[开门打卡]" + activityPeriodName + "报名成功，" + signInTimeTips + "打卡");
                    } else {
                        Log.i(TAG, "未具有开门打卡报名资格" + s);
                    }
                } else if (signUpStatus.equals("SIGN_UP") && signInStatus.equals("SIGN_IN_ENABLE")) {
                    s = GreatYouthRpcCall.rpcCall_signIn(loader, activityNo);
                    if (new JSONObject(s).optBoolean("success")) {
                        String signInTips = jo.optString("signInTips");
                        Log.other("[开门打卡]" + activityPeriodName + "打卡成功，" + signInTips);
                        Thread.sleep(RandomUtils.delay());
                        kmdkQuery(loader);
                    } else {
                        Log.other("[开门打卡]打卡失败，" + s);
                    }
                }
            } else {
                Log.i(TAG, "kmdkQuery err:" + s);
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void donatePoint(ClassLoader loader) {
        if (!Statistics.canZmDonate()) {
            return;
        }
        try {
            JSONObject jo = new JSONObject(GreatYouthRpcCall.rpcCall_dispatchCreditStation(loader));
            if (jo.optString("resultCode").equals("SUCCESS")) {
                jo = jo.optJSONObject("data");
                int availablePoint = jo.optInt("availablePoint");
                jo = jo.optJSONObject("creditStation");
                int unitPoint = jo.optInt("unitPoint");
                String stationId = jo.optString("stationId");
                String stationName = jo.optString("stationName");
                int i = 0;
                while (i < 3) {
                    i += 1;
                    if (availablePoint < unitPoint * i
                            || !new JSONObject(GreatYouthRpcCall.rpcCall_donatePoint(loader, stationId))
                            .optString("resultCode").equals("SUCCESS")) {
                        break;
                    }
                    Log.other("芝麻信用：捐赠〈" + stationName + "〉〈" + unitPoint + "芝麻粒〉");
                }
                Statistics.zmDonateToday();
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }
}