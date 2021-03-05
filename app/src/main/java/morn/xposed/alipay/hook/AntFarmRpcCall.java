package morn.xposed.alipay.hook;

import java.security.MessageDigest;
import java.util.UUID;

import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;

public class AntFarmRpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠁ";

    private static final String cityAdCode = "000000",
            districtAdCode = "000000", version = "1.8.2007312105.11";

    public static int getScore(String gameType, int level) {
        int score;
        if (gameType.equals("starGame")) {
            score = level * 31;
            if (score < 200) {
                score = RandomUtils.nextInt(200, 300);
            } else if (score > 1000) {
                score = RandomUtils.nextInt(1000, 1100);
            }
        } else if (gameType.equals("jumpGame")) {
            score = level * 3;
            if (score < 1500) {
                score = RandomUtils.nextInt(150, 160) * 10;
            } else if (score > 5000) {
                score = RandomUtils.nextInt(500, 510) * 10;
            }
        } else {
            score = 210;
        }
        return score;
    }

    private static String getUUID() {
        StringBuilder builder = new StringBuilder();
        String[] uuid = UUID.randomUUID().toString().split("-");
        for (String str : uuid) {
            builder.append(str.substring(str.length() / 2));
        }
        return builder.toString();
    }

    private static String byte2hex(byte b) {
        String[] hexStr = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        if (b < 0) {
            b += 256;
        }
        return hexStr[b / 16] + hexStr[b % 16];
    }

    public static String rpcCall_acceptGift(ClassLoader loader) {
        try {
            String args1 = "[{\"ignoreLimit\":false,\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.acceptGift", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_acceptGift err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_rankingList(ClassLoader loader, int startNum) {
        try {
            String args1 = "[{\"pageSize\":20,\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"startNum\":"
                    + startNum + ",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.rankingList", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_rankingList err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_triggerTbTask(ClassLoader loader, int activityId, int deliveryId, String implId, int sceneId, String taskId) {
        try {
            String args1 = "[{\"activityId\":" + activityId + ",\"deliveryId\":" + deliveryId
                    + ",\"implId\":\"" + implId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ORCHARD\",\"sceneId\":" + sceneId
                    + ",\"source\":\"h5\",\"taskId\":\"" + taskId
                    + "\",\"taskPlantType\":\"TAOBAO\",\"version\":\""
                    + "0.1.2007161130.31"
                    + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.triggerTbTask", args1);

        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_triggerTbTask err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_collectManurePot(ClassLoader loader, String manurePotNOs) {
        try {
            String args1 = "[{\"isSkipTempLimit\":false,\"manurePotNOs\":\"" + manurePotNOs
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.collectManurePot", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_collectManurePot err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_answerQuestion(ClassLoader loader, String questionId, int answers) {
        try {
            String args1 = "[{\"answers\":\"[{\\\"questionId\\\":\\\"" + questionId
                    + "\\\",\\\"answers\\\":[" + answers
                    + "]}]\",\"bizkey\":\"ANSWER\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.doFarmTask", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_answerQuestion err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_receiveToolTaskReward(ClassLoader loader, String awardType, int rewardCount, String taskType) {
        try {
            String args1 = "[{\"awardType\":\"" + awardType
                    + "\",\"ignoreLimit\":false,\"requestType\":\"NORMAL\",\"rewardCount\":" + rewardCount
                    + ",\"rewardType\":\"" + awardType
                    + "\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"taskType\":\"" + taskType
                    + "\",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.receiveToolTaskReward", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_receiveToolTaskReward err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_enterFarm(ClassLoader loader, String farmId, String userId) {
        try {
            String args1 = "[{\"animalId\":\"\",\"cityAdCode\":\"000000\",\"districtAdCode\":\"000000\",\"farmId\":\"" + farmId +
                    "\",\"masterFarmId\":\"\",\"recall\":false,\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"touchRecordId\":\"\",\"userId\":\""
                    + userId + "\",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.enterFarm", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_enterFarm err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_recallAnimal(ClassLoader loader, String animalId, String currentFarmId, String masterFarmId) {
        try {
            String args1 = "[{\"animalId\":\"" + animalId + "\",\"currentFarmId\":\"" + currentFarmId +
                    "\",\"masterFarmId\":\"" + masterFarmId +
                    "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\"" +
                    version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.recallAnimal", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_recallAnimal err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_rewardFriend(ClassLoader loader, String consistencyKey, String friendId, String productNum, String time) {
        try {
            String args1 = "[{\"canMock\":true,\"consistencyKey\":\"" + consistencyKey + "\",\"friendId\":\"" + friendId +
                    "\",\"operType\":\"1\",\"productNum\":" + productNum +
                    ",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"time\":" + time +
                    ",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.rewardFriend", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_rewardFriend err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String farmId2UserId(String farmId) {
        return farmId.substring(farmId.length() / 2);
    }

    private static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byte2hex(b));
        }
        return sb.toString();
    }

    public static String rpcCall_getAnswerInfo(ClassLoader loader) {
        try {
            String args1 = "[{\"answerSource\":\"foodTask\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.getAnswerInfo", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_getAnswerInfo err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_donation(ClassLoader loader, String activityId) {
        try {
            String args1 = "[{\"activityId\":\"" + activityId +
                    "\",\"donationAmount\":5,\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.donation", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_donation err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_harvestGoldenEgg(ClassLoader loader, String farmId, String goldenEggId) {
        try {
            String args1 = "[{\"farmId\":\"" + farmId + "\",\"giftType\":\"\",\"goldenEggId\":\"" + goldenEggId
                    + "\",\"harvestType\":\"GOLDENEGG\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.harvestProduce", args1);

        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_harvestGoldenEgg err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_useFarmTool(ClassLoader loader, String targetFarmId, String toolId, String toolType) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"targetFarmId\":\""
                    + targetFarmId + "\",\"toolId\":\"" + toolId + "\",\"toolType\":\"" + toolType + "\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.useFarmTool", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_useFarmTool err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_sendBackAnimal(ClassLoader loader, String sendType, String animalId, String currentFarmId, String masterFarmId) {
        try {
            String args1 = "[{\"animalId\":\"" + animalId + "\",\"currentFarmId\":\"" + currentFarmId
                    + "\",\"masterFarmId\":\"" + masterFarmId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"sendType\":\"" + sendType
                    + "\",\"source\":\"H5\",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.sendBackAnimal", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_sendBackAnimal err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    private static String hexMD5(String s) {
        try {
            return bytes2hex(MessageDigest.getInstance("MD5").digest(s.getBytes()));
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_listActivityInfo(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.listActivityInfo", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_listActivityInfo err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_feedAnimal(ClassLoader loader, String farmId) {
        try {
            String args1 = "[{\"animalType\":\"CHICK\",\"canMock\":true,\"farmId\":\"" + farmId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.feedAnimal", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_feedAnimal err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_hireAnimal(ClassLoader loader, String friendFarmId, String hireAnimalId) {
        try {
            String args1 = "[{\"friendFarmId\":\"" + friendFarmId + "\",\"hireAnimalId\":\"" + hireAnimalId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.hireAnimal", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_hireAnimal err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_listFarmTask(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.listFarmTask", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_listFarmTask err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_feedFriendAnimal(ClassLoader loader, String friendFarmId) {
        try {
            String args1 = "[{\"animalType\":\"CHICK\",\"canMock\":true,\"friendFarmId\":\"" + friendFarmId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.feedFriendAnimal", args1);

        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_feedFriendAnimal err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_notifyFriend(ClassLoader loader, String animalId, String notifiedFarmId) {
        try {
            String args1 = "[{\"animalId\":\"" + animalId
                    + "\",\"animalType\":\"CHICK\",\"canBeGuest\":true,\"notifiedFarmId\":\"" + notifiedFarmId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.notifyFriend", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_notifyFriend err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_listFarmTool(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.listFarmTool", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_listFarmTool err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_harvestProduce(ClassLoader loader, String farmId) {
        try {
            String args1 = "[{\"canMock\":true,\"farmId\":\"" + farmId
                    + "\",\"giftType\":\"\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version
                    + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.harvestProduce", args1);

        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_harvestProduce err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_surprisePrizeReceive(ClassLoader loader, String goldenEggId, String receiveOrderIds) {
        try {
            String args1 = "[{\"goldenEggId\":\"" + goldenEggId + "\",\"receiveOrderIds\":\"" + receiveOrderIds
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.surprisePrizeReceive", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_surprisePrizeReceive err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_listToolTaskDetails(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.listToolTaskDetails", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_listToolTaskDetails err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_initFarmGame(ClassLoader loader, String gameType) {
        try {
            String args1 = "[{\"gameType\":\"" + gameType
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"toolTypes\":\"STEALTOOL,ACCELERATETOOL,SHARETOOL\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.initFarmGame", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_initFarmGame err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_orchardCollectDailyManure(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ORCHARD\",\"source\":\"h5\",\"version\":\"0.1.2007161130.31\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.orchardCollectDailyManure", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_orchardCollectDailyManure err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_receiveFarmTaskAward(ClassLoader loader, String taskId) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"taskId\":\""
                    + taskId + "\",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.receiveFarmTaskAward", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_receiveFarmTaskAward err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_orchardIndex(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ORCHARD\",\"source\":\"h5\",\"version\":\"0.1.2007161130.31\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.orchardIndex", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_orchardIndex err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_recordFarmGame(ClassLoader loader, String gameType) {
        try {
            String uuid = getUUID();
            String md5 = hexMD5(uuid);
            int score = getScore(gameType, 1);
            String args1 = "[{\"gameType\":\"" + gameType + "\",\"md5\":\"" + md5
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"score\":" + score
                    + ",\"source\":\"H5\",\"toolTypes\":\"STEALTOOL,ACCELERATETOOL,SHARETOOL\",\"uuid\":\""
                    + uuid + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.recordFarmGameQiufeng", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_recordFarmGame err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_orchardListTask(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ORCHARD\",\"source\":\"h5\",\"version\":\"0.1.2007161130.31\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.orchardListTask", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_orchardListTask err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_syncAnimalStatus(ClassLoader loader, String farmId) {
        try {
            String args1 = "[{\"farmId\":\"" + farmId +
                    "\",\"operType\":\"FEEDSYNC\",\"queryFoodStockInfo\":false,\"recall\":false,\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"userId\":\""
                    + farmId2UserId(farmId) + "\",\"version\":\"" + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.syncAnimalStatus", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_syncAnimalStatus err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_orchardSpreadManure(ClassLoader loader) {
        try {
            String args1 = "[{\"requestType\":\"NORMAL\",\"sceneCode\":\"ORCHARD\",\"source\":\"h5\",\"useWua\":true,\"version\":\"0.1.2007161130.31\",\"wua\":\"\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.orchardSpreadManure", args1);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_orchardSpreadManure err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_visitFriend(ClassLoader loader, String friendFarmId) {
        try {
            String args1 = "[{\"friendFarmId\":\"" + friendFarmId
                    + "\",\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"version\":\""
                    + version + "\"}]";
            return RpcCall.invoke(loader, "com.alipay.antfarm.visitFriend", args1);

        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_visitFriend err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_rankingList(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "com.alipay.antfarm.rankingList", "[{\"pageSize\":1000,\"requestType\":\"NORMAL\",\"sceneCode\":\"ANTFARM\",\"source\":\"H5\",\"startNum\":0,\"version\":\"1.8.2007312105.11\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_rankingList err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}