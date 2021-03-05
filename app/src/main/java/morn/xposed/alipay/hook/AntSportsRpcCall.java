package morn.xposed.alipay.hook;

import morn.xposed.alipay.util.Log;

public class AntSportsRpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠄ";

    public static String rpcCall_queryMyHomePage(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "alipay.antsports.walk.map.queryMyHomePage", "[{\"chInfo\":\"antsports-account\",\"pathListUsePage\":true,\"timeZone\":\"Asia\\/Shanghai\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryMyHomePage err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryProjectList(ClassLoader loader, int index) {
        try {
            String arg1 = "[{\"chInfo\":\"antsports-account\",\"index\":" +
                    index +
                    ",\"projectListUseVertical\":true}]";
            return RpcCall.invoke(loader, "alipay.antsports.walk.charity.queryProjectList", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryProjectList err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryProjectList(ClassLoader loader, int donateCharityCoin, String projectId) {
        try {
            String args1 = "[{\"chInfo\":\"antsports-account\",\"donateCharityCoin\":" +
                    donateCharityCoin + ",\"projectId\":\"" + projectId + "\"}]";
            return RpcCall.invoke(loader, "alipay.antsports.walk.charity.donate", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryProjectList err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_exchange_success(ClassLoader loader, String exchangeId) {
        try {
            String localStringBuilder = "[{\"cityCode\":\"\",\"exchangeId\":\"" +
                    exchangeId +
                    "\",\"timezone\":\"GMT+08:00\",\"version\":\"" +
                    "3.0.1.2" +
                    "\"}]";
            return RpcCall.invoke(loader, "alipay.charity.mobile.donate.exchange.success", localStringBuilder);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_exchange_success err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_exchange(ClassLoader loader, String actId, int count, String donateToken) {
        try {
            String args1 = "[{\"actId\":\"" + actId + "\",\"count\":" + count + ",\"donateToken\":\"" + donateToken
                    + "\",\"timezoneId\":\"" + "Asia\\/Shanghai" + "\",\"ver\":0}]";
            return RpcCall.invoke(loader, "alipay.charity.mobile.donate.walk.exchange", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_exchange err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_openTreasureBox(ClassLoader loader, String boxNo, String userId) {
        try {
            String args1 = "[{\"boxNo\":\"" + boxNo + "\",\"chInfo\":\""
                    + "antsports-account" + "\",\"userId\":\"" + userId + "\"}]";
            return RpcCall.invoke(loader, "alipay.antsports.walk.treasureBox.openTreasureBox", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_openTreasureBox err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_go(ClassLoader loader, String day, String rankCacheKey, int useStepCount) {
        try {
            String arg1 = "[{\"chInfo\":\"antsports-account\",\"day\":\"" +
                    day +
                    "\",\"needAllBox\":true,\"rankCacheKey\":\"" +
                    rankCacheKey +
                    "\",\"timeZone\":\"" +
                    "Asia\\/Shanghai" +
                    "\",\"useStepCount\":" +
                    useStepCount +
                    "}]";
            return RpcCall.invoke(loader, "alipay.antsports.walk.map.go", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_go err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_querySteps(ClassLoader loader) {
        try {
            String args1 = "[{\"appId\":\"healthstep\",\"bizId\":\"donation\",\"chInfo\":\"lunax_healthkit_donation_healthstep\",\"timeZone\":\"Asia/Shanghai\"}]";
            return RpcCall.invoke(loader, "alipay.antsports.steps.query", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_querySteps err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_walkHome(ClassLoader loader, int steps) {
        try {
            String args1 = "[{\"module\":\"3\",\"steps\":" + steps + ",\"timezoneId\":\"Asia/Shanghai\"}]";
            return RpcCall.invoke(loader, "alipay.charity.mobile.donate.walk.home", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_walkHome err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_join(ClassLoader loader, String paramString) {
        try {
            String args1 = "[{\"chInfo\":\"antsports-account\",\"pathId\":\"" + paramString + "\"}]";
            return RpcCall.invoke(loader, "alipay.antsports.walk.map.join", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_join err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}