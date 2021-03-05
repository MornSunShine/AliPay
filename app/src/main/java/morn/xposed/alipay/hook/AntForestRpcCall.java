package morn.xposed.alipay.hook;

import android.text.TextUtils;

import java.util.List;

import morn.xposed.alipay.util.Log;

public class AntForestRpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠂ";

    public static String rpcCall_pageQueryDynamics(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.pageQueryDynamics", "[{\"av\":\"5\",\"ct\":\"android\",\"pageSize\":15,\"startIndex\":0}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_pageQueryDynamics err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_operateDynamic(ClassLoader loader, long bizDt, String bizNo, String receiverUserId) {
        try {
            String arg1 = "[{\"action\":\"PRAISE\",\"bizDt\":" +
                    bizDt +
                    ",\"bizNo\":\"" +
                    bizNo +
                    "\",\"bizType\":\"FRIEND_COLLECT\",\"receiverUserId\":\"" +
                    receiverUserId +
                    "\"}]";
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.operateDynamic", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_operateDynamic err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryFriendHomePage(ClassLoader loader, String userId) {
        try {
            String arg1 = "[{\"canRobFlags\":\"T,F,F,F\",\"configVersionMap\":{\"redPacketConfig\":20200613,\"wateringBubbleConfig\":\"10\"},\"source\":\"_NO_SOURCE_\",\"userId\":\"" + userId + "\",\"version\":\"20181220\"}]";
            return RpcCall.invoke(loader, "alipay.antforest.forest.h5.queryFriendHomePage", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryFriendHomePage err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_collectEnergy(ClassLoader loader, String userId, long bubbleId, String bizType) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[{");
            if (bizType != null) {
                sb.append("\"bizType\":\"").append(bizType).append("\",");
            }
            sb.append(bizType)
                    .append("\"bubbleIds\":[")
                    .append(bubbleId)
                    .append("],\"userId\":\"")
                    .append(userId)
                    .append("\"}]");
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.collectEnergy", sb.toString());
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_collectEnergy err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_transferEnergy(ClassLoader loader, String targetUser, String bizNo, int ordinal, int energyId, String packetNo) {
        try {
            String arg1 = "[{\"bizNo\":\"" + bizNo + ordinal + "\",\"energyId\":" + energyId
                    + ",\"sendChat\":\"N\",\"targetUser\":\"" + targetUser
                    + "\",\"transferType\":\"WATERING\",\"version\":\"20181217\"}]";
            StringBuilder sb = new StringBuilder();
            sb.append("[{\"bizNo\":\"");
            sb.append(bizNo);
            sb.append(ordinal);
            sb.append("\",\"energyId\":");
            sb.append(energyId);
            sb.append(",\"extInfo\":{");
            if (!packetNo.isEmpty()) {
                sb.append("\"packetNo\":\"");
                sb.append(packetNo);
                sb.append("\",");
            }
            sb.append("\"sendChat\":\"N\"},\"from\":\"friendIndex\",\"source\":\"_NO_SOURCE_\",\"targetUser\":\"");
            sb.append(targetUser);
            sb.append("\",\"transferType\":\"WATERING\",\"version\":\"20181217\"}]");
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.transferEnergy", sb.toString());
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_transferEnergy err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_fillUserRobFlag(ClassLoader loader, List<String> userIdList) {
        try {
            String arg1 = "[{\"userIdList\":[\"" + TextUtils.join("\",\"", userIdList) + "\"]}]";
            return RpcCall.invoke(loader, "alipay.antforest.forest.h5.fillUserRobFlag", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_fillUserRobFlag err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryEnergyRankingTotal(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.queryEnergyRanking", "[{\"startPoint\":\"\",\"type\":\"total\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryEnergyRanking err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryPKRecord(ClassLoader loader, String pkUser) {
        try {
            String arg1 = "[{\"pkType\":\"Week\",\"pkUser\":\"" + pkUser + "\"}]";
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.queryPKRecord", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryPKRecord err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_forFriendCollectEnergy(ClassLoader loader, String targetUserId, long bubbleId, String bizType) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[{");
            if (bizType.isEmpty()) {
                bizType = "";
            } else {
                StringBuilder localStringBuilder2 = new StringBuilder();
                sb.append("\"bizType\":\"");
                sb.append(bizType);
                sb.append("\",");
            }
            sb.append("\"bubbleIds\":[");
            sb.append(bubbleId);
            sb.append("],\"targetUserId\":\"");
            sb.append(targetUserId);
            sb.append("\"}]");
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.forFriendCollectEnergy", sb.toString());
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_forFriendCollectEnergy err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryEnergyRankingWeek(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "alipay.antmember.forest.h5.queryEnergyRanking", "[{\"startPoint\":\"\",\"type\":\"week\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryEnergyRanking err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_receiveTaskAward(ClassLoader loader, String taskType) {
        try {
            String arg1 = "[{\"ignoreLimit\":false,\"requestType\":\"H5\",\"sceneCode\":\"ANTFOREST_TASK\",\"source\":\"ANTFOREST\",\"taskType\":\""
                    + taskType + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antiep.receiveTaskAward", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_receiveTaskAward err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryHomePage(ClassLoader loader) {
        try {
            String arg1 = "[{\"configVersionMap\":{\"redPacketConfig\":20200613,\"wateringBubbleConfig\":\"10\"},\"source\":\"_NO_SOURCE_\",\"version\":\"20181220\"}]";
            return RpcCall.invoke(loader, "alipay.antforest.forest.h5.queryHomePage", arg1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryHomePage err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryTaskList(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "alipay.antforest.forest.h5.queryTaskList", "[{\"version\":\"20191010\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryTaskList err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}