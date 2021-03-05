package morn.xposed.alipay.hook;

import morn.xposed.alipay.util.Log;

public class GreatYouthRpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠆ";

    public static String rpcCall_dispatchCreditStation(ClassLoader loader) {
        try {
            String args1 = "[{}]";
            return RpcCall.invoke(loader, "com.antgroup.zmxy.zmmemberop.biz.rpc.creditstation.CreditStationRpcManager.dispatchCreditStation", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_collectCreditFeedback(ClassLoader loader, String creditFeedbackId) {
        try {
            String args1 = "[{\"creditFeedbackId\":\"" + creditFeedbackId + "\"}]";
            return RpcCall.invoke(loader, "com.antgroup.zmxy.zmcustprod.biz.rpc.home.creditaccumulate.api.CreditAccumulateRpcManager.collectCreditFeedback", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_receiveFriend(ClassLoader loader, String bbOrderId, String friendId) {
        try {
            String args1 = "[{\"bbOrderId\":\"" + bbOrderId + "\",\"friendId\":\"" + friendId + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.greatyouthtwa.gyPayList.receiveFriend", args1);
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        return null;
    }

    public static String rpcCall_userapply(ClassLoader loader) {
        try {
            String args1 = "[{\"booth\":\"electricUserSign\",\"contentId\":\"8000266\",\"extParams\":{\"signRewardBooth\":\"electricUserSignReward\"},\"scene\":\"jiaofei\",\"touchPoint\":\"electricPage\"}]";
            return RpcCall.invoke(loader, "alipay.imasp.scene.userapply", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_donatePoint(ClassLoader loader, String stationId) {
        try {
            String args1 = "[{\"stationId\":\"" + stationId + "\"}]";
            return RpcCall.invoke(loader, "com.antgroup.zmxy.zmmemberop.biz.rpc.creditstation.CreditStationRpcManager.donatePoint", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_userdataquery(ClassLoader loader) {
        try {
            String args1 = "[{\"operateType\":[\"queryPointInfo\",\"queryApplyingPointRecord\"],\"pageNumber\":1,\"pageSize\":10,\"scene\":\"jiaofei\"}]";
            return RpcCall.invoke(loader, "alipay.imasp.scene.userdataquery", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_point_apply(ClassLoader loader, String pointId) {
        try {
            String args1 = "[{\"bizType\":\"jiaofei\",\"operateType\":\"default\",\"pointId\":\"" + pointId + "\"}]";
            return RpcCall.invoke(loader, "alipay.imasp.point.apply", args1);
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        return null;
    }

    public static String rpcCall_getFriends(ClassLoader loader) {
        try {
            String args1 = "[{\"index\":1}]";
            return RpcCall.invoke(loader, "com.alipay.greatyouthtwa.gyFriends.getFriends", args1);
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        return null;
    }

    public static String rpcCall_info(ClassLoader loader, String friendId) {
        try {
            String args1 = "[{\"friendId\":\"" + friendId + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.greatyouthtwa.gyFriends.info", args1);
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        return null;
    }

    public static String rpcCall_getIndexInfo(ClassLoader loader) {
        try {
            String args1 = "[{\"channelFrom\":\"from\",\"checkType\":\"normal\",\"receiveType\":\"MANUAL\"}]";
            return RpcCall.invoke(loader, "com.alipay.greatyouthtwa.greatYouth.getIndexInfo", args1);
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        return null;
    }

    public static String rpcCall_receive(ClassLoader loader, String bbOrderId) {
        try {
            String args1 = "[{\"bbOrderId\":\"" + bbOrderId + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.greatyouthtwa.gyPayList.receive", args1);
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
        }
        return null;
    }

    public static String rpcCall_query_activity(ClassLoader loader) {
        try {
            String args1 = "[{\"scene\":\"activityCenter\"}]";
            return RpcCall.invoke(loader, "alipay.merchant.kmdk.query.activity", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_signIn(ClassLoader loader, String activityNo) {
        try {
            String args1 = "[{\"activityNo\":\"" + activityNo + "\"}]";
            return RpcCall.invoke(loader, "alipay.merchant.kmdk.signIn", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryCreditFeedback(ClassLoader loader) {
        try {
            String args1 = "[{\"includeUnclaimed\":false,\"size\":100,\"sourceTypes\":[\"point\"]}]";
            return RpcCall.invoke(loader, "com.antgroup.zmxy.zmcustprod.biz.rpc.home.creditaccumulate.api.CreditAccumulateRpcManager.queryCreditFeedback", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_signUp(ClassLoader loader, String activityNo) {
        try {
            String args1 = "[{\"activityNo\":\"" + activityNo + "\"}]";
            return RpcCall.invoke(loader, "alipay.merchant.kmdk.signUp", args1);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}