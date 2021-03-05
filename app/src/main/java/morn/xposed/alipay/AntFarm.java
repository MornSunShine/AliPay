package morn.xposed.alipay;

import android.os.PowerManager;

import morn.xposed.alipay.util.Statistics;

import org.json.JSONArray;
import org.json.JSONObject;

import morn.xposed.alipay.hook.AntFarmRpcCall;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;

public class AntFarm {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠂ";
    private static String ownerFarmId;
    private static Animal[] animals;
    private static Animal ownerAnimal;
    private static int foodStock;
    private static int foodStockLimit;
    private static String rewardProductNum;
    private static RewardFriend[] rewardList;
    private static double benevolenceScore;
    private static double harvestBenevolenceScore;
    private static int unreceiveTaskAward;
    private static int countdown;
    private static boolean orchard;
    private static boolean hasGoldenEgg;
    private static String goldenEggId;

    private static void receiveFoodNum(ClassLoader loader) {
        if (foodStock >= foodStockLimit) {
            return;
        }
        try {
            String s = AntFarmRpcCall.rpcCall_acceptGift(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success")) {
                Log.farm("收取麦子〔" + jo.optInt("receiveFoodNum") + "克〕");
                if (jo.has("foodStock")) {
                    foodStock = jo.getInt("foodStock");
                }
            } else {
                Log.i(TAG, s);
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static void start(ClassLoader loader, int times) {
        if (Config.getEnableFarm()) {
            if (Config.enableOpenDoor() && times % 3 != 0) {
                return;
            }
            Runnable runnable = new Runnable() {
                ClassLoader loader;
                int times;

                public Runnable setData(ClassLoader loader,int times) {
                    this.loader = loader;
                    this.times=times;
                    return this;
                }

                @Override
                public void run() {
                    try {
                        PowerManager.WakeLock wakeLock = XposedHook.getWakeLock();
                        String s = AntFarmRpcCall.rpcCall_enterFarm(loader, "", FriendIdMap.currentUid);
                        if (s == null) {
                            Thread.sleep(RandomUtils.delay());
                            s = AntFarmRpcCall.rpcCall_enterFarm(loader, "", FriendIdMap.currentUid);
                        }
                        JSONObject jo = new JSONObject(s);
                        String memo = jo.getString("memo");
                        if (memo.equals("SUCCESS")) {
                            rewardProductNum = jo.getJSONObject("dynamicGlobalConfig").getString("rewardProductNum");
                            JSONObject joFarmVO = jo.getJSONObject("farmVO");
                            foodStock = joFarmVO.getInt("foodStock");
                            foodStockLimit = joFarmVO.getInt("foodStockLimit");
                            harvestBenevolenceScore = joFarmVO.getDouble("harvestBenevolenceScore");
                            orchard = false;
                            if (jo.has("orchardInfo") && jo.getJSONObject("orchardInfo").optBoolean("userOpenOrchard")) {
                                orchard = true;
                            }
                            parseSyncAnimalStatusResponse(joFarmVO.toString());
                        } else {
                            Log.recordLog(memo, s);
                        }
                        if (Config.getRewardFriend()) rewardFriend(loader);

                        if (Config.getSendBackAnimal()) sendBackAnimal(loader);

                        if (!AnimalInteractStatus.HOME.name().equals(ownerAnimal.animalInteractStatus)) {
                            syncAnimalStatusAtOtherFarm(loader, ownerAnimal.currentFarmId, "");
                            boolean guest = false;
                            switch (SubAnimalType.valueOf(ownerAnimal.subAnimalType)) {
                                case GUEST:
                                    guest = true;
                                    Log.recordLog("小鸡到好友家去做客了", "");
                                    break;
                                case NORMAL:
                                    Log.recordLog("小鸡太饿，离家出走了", "");
                                    break;
                                case PIRATE:
                                    Log.recordLog("小鸡外出探险了", "");
                                    break;
                                default:
                                    Log.recordLog("小鸡不在庄园", ownerAnimal.subAnimalType);
                            }

                            boolean hungry = false;
                            String userName = FriendIdMap.getNameById(AntFarmRpcCall.farmId2UserId(ownerAnimal.currentFarmId));
                            switch (AnimalFeedStatus.valueOf(ownerAnimal.animalFeedStatus)) {
                                case HUNGRY:
                                    hungry = true;
                                    Log.recordLog("小鸡在〔" + userName + "〕的庄园里挨饿", "");
                                    break;

                                case EATING:
                                    Log.recordLog("小鸡在〔" + userName + "〕的庄园里吃得津津有味", "");
                                    break;
                            }

                            boolean recall = false;
                            switch (Config.getRecallAnimalType()) {
                                case ALWAYS:
                                    recall = true;
                                    break;
                                case WHEN_THIEF:
                                    recall = !guest;
                                    break;
                                case WHEN_HUNGRY:
                                    recall = hungry;
                                    break;
                            }
                            if (recall) {
                                recallAnimal(loader, ownerAnimal.animalId, ownerAnimal.currentFarmId, ownerFarmId, userName);
                                syncAnimalStatus(loader, ownerFarmId);
                            }
                        }
                        if (Config.getReceiveFarmToolReward()) {
                            receiveToolTaskReward(loader);
                        }
                        if (Config.getUseNewEggTool()) {
                            useFarmTool(loader, ownerFarmId, ToolType.NEWEGGTOOL);
                            syncAnimalStatus(loader, ownerFarmId);
                        }
                        if (Config.getHarvestProduce()) {
                            harvestGoldenEgg(loader);
                        }
                        if (Config.getHarvestProduce() && benevolenceScore >= 1) {
                            Log.recordLog("有可收取的爱心鸡蛋", "");
                            harvestProduce(loader, ownerFarmId);
                        }
                        if (Config.getDonation() && harvestBenevolenceScore >= 5) {
                            Log.recordLog("爱心鸡蛋已达到可捐赠个数", "");
                            donation(loader);
                        }
                        if (Config.getAnswerQuestion() && Statistics.canAnswerQuestionToday(FriendIdMap.currentUid)) {
                            answerQuestion(loader);
                        }

                        if (Config.getReceiveFarmTaskAward()) receiveFarmTaskAward(loader);

                        if (Config.getNotifyFriend()) {
                            notifyFriend(loader);
                        }

                        if (Config.getReceiveFarmTaskAward()) receiveFarmTaskAward(loader);

                        if (Config.getAcceptGift()) {
                            receiveFoodNum(loader);
                        }

                        if (Config.getZmDonate() && times == 0) {
                            playGame(loader);
                        }

                        if (AnimalInteractStatus.HOME.name().equals(ownerAnimal.animalInteractStatus)) {
                            if (Config.getFeedAnimal() && AnimalFeedStatus.HUNGRY.name().equals(ownerAnimal.animalFeedStatus)) {
                                Log.recordLog("小鸡在挨饿", "");
                                feedAnimal(loader, ownerFarmId);
                                //syncAnimalStatus(loader,ownerFarmId);
                            }

                            if (AnimalBuff.ACCELERATING.name().equals(ownerAnimal.animalBuff)) {
                                Log.recordLog("小鸡正双手并用着加速吃饲料", "");
                            } else if (Config.getUseAccelerateTool()) {
                                // 加速卡
                                useFarmTool(loader, ownerFarmId, ToolType.ACCELERATETOOL);
                            }

                            if (unreceiveTaskAward > 0) {
                                Log.recordLog("还有待领取的饲料", "");
                                receiveFarmTaskAward(loader);
                            }
                        }
                        feedFriend(loader);

                        if (times == 0) {
                            giveFriendFood(loader);
                        }

                        if (Config.getNotifyFriend()) {
                            notifyFriend(loader);
                        }
                        if (times == 0) {
                            if (Config.getCollectManurePot() && orchard) {
                                for (int i = 1; i <= 3; i++) {
                                    collectManurePot(loader, String.valueOf(i));
                                    Thread.sleep(RandomUtils.delay());
                                }
                            }
                            if (Config.getCollectManurePot() && orchard) {
                                collectOrchardManure(loader);
                            }
                            if (Config.getTriggerTbTask() && orchard) {
                                Thread.sleep(RandomUtils.delay());
                                orchardSignInTask(loader);
                            }
                            if (Config.getOrchardSpreadManure() && orchard) {
                                Thread.sleep(RandomUtils.delay());
                                spreadOrchardManure(loader);
                            }
                        }
                        FriendIdMap.saveIdMap();
                        XposedHook.setWakeLock(wakeLock);
                    } catch (Throwable t) {
                        Log.i(TAG, "AntFarm.start.run err:");
                        Log.printStackTrace(TAG, t);
                    }
                }
            }.setData(loader, times);
            if (Config.getOriginalMode()) {
                new Thread(runnable).start();
            } else {
                AntBroadcastReceiver.triggerTask(runnable, RandomUtils.nextInt(1000, 2000));
            }
        }
    }

    private static boolean notifyFriend(ClassLoader loader, JSONObject joAnimalStatusVO, String friendFarmId, String animalId, String user) {
        try {
            if (AnimalInteractStatus.STEALING.name().equals(joAnimalStatusVO.getString("animalInteractStatus"))
                    && AnimalFeedStatus.EATING.name().equals(joAnimalStatusVO.getString("animalFeedStatus"))) {
                String s = AntFarmRpcCall.rpcCall_notifyFriend(loader, animalId, friendFarmId);
                JSONObject jo = new JSONObject(s);
                String memo = jo.getString("memo");
                if (memo.equals("SUCCESS")) {
                    double rewardCount = jo.getDouble("rewardCount");
                    if (jo.getBoolean("refreshFoodStock"))
                        foodStock = (int) jo.getDouble("finalFoodStock");
                    else
                        add2FoodStock((int) rewardCount);
                    Log.farm("通知〔" + user + "〕被偷吃，奖励〔" + rewardCount + "克〕");
                    return true;
                } else {
                    Log.recordLog(memo, s);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "notifyFriend err:");
            Log.printStackTrace(TAG, t);
        }
        return false;
    }

    private static void feedFriendAnimal(ClassLoader loader, String friendFarmId, String user) {
        try {
            morn.xposed.alipay.util.Log.recordLog("〔" + user + "〕的小鸡在挨饿", "");
            if (foodStock < 180) {
                morn.xposed.alipay.util.Log.recordLog("喂鸡饲料不足", "");
                if (unreceiveTaskAward > 0) {
                    morn.xposed.alipay.util.Log.recordLog("还有待领取的饲料", "");
                    receiveFarmTaskAward(loader);
                }
            }
            if (foodStock >= 180) {
                String s = morn.xposed.alipay.hook.AntFarmRpcCall.rpcCall_feedFriendAnimal(loader, friendFarmId);
                JSONObject jo = new JSONObject(s);
                String memo = jo.getString("memo");
                if (memo.equals("SUCCESS")) {
                    int feedFood = foodStock - jo.getInt("foodStock");
                    if (feedFood > 0) {
                        add2FoodStock(-feedFood);
                        morn.xposed.alipay.util.Log.farm("喂〔" + user + "〕的小鸡〔" + feedFood + "克〕，剩余〔" + foodStock + "克〕");
                        Statistics.feedFriendToday(morn.xposed.alipay.hook.AntFarmRpcCall.farmId2UserId(friendFarmId));
                    }
                } else {
                    morn.xposed.alipay.util.Log.recordLog(memo, s);
                }
            }
        } catch (Throwable t) {
            morn.xposed.alipay.util.Log.i(TAG, "feedFriendAnimal err:");
            morn.xposed.alipay.util.Log.printStackTrace(TAG, t);
        }
    }

    private static void recallAnimal(ClassLoader loader, String animalId, String currentFarmId, String masterFarmId, String user) {
        try {
            String s = AntFarmRpcCall.rpcCall_recallAnimal(loader, animalId, currentFarmId, masterFarmId);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                double foodHaveStolen = jo.getDouble("foodHaveStolen");
                Log.farm("召回小鸡，偷吃〔" + user + "〕〔" + foodHaveStolen + "克〕");
                // 这里不需要加
                // add2FoodStock((int)foodHaveStolen);
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "recallAnimal err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void useFarmTool(ClassLoader loader, String targetFarmId, ToolType toolType) {
        try {
            String s = AntFarmRpcCall.rpcCall_listFarmTool(loader);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                JSONArray jaToolList = jo.getJSONArray("toolList");
                for (int i = 0; i < jaToolList.length(); i++) {
                    jo = jaToolList.getJSONObject(i);
                    if (toolType.name().equals(jo.getString("toolType"))) {
                        int toolCount = jo.getInt("toolCount");
                        if (toolCount > 0) {
                            String toolId = "";
                            if (jo.has("toolId")) toolId = jo.getString("toolId");
                            s = AntFarmRpcCall.rpcCall_useFarmTool(loader, targetFarmId, toolId, toolType.name());
                            jo = new JSONObject(s);
                            memo = jo.getString("memo");
                            if (memo.equals("SUCCESS"))
                                Log.farm("使用" + toolType.nickName() + "成功，剩余〔" + (toolCount - 1) + "张〕");
                            else Log.recordLog(memo, s);
                        }
                        break;
                    }
                }
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "useFarmTool err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void add2FoodStock(int i) {
        foodStock += i;
        if (foodStock > foodStockLimit) foodStock = foodStockLimit;
        if (foodStock < 0) foodStock = 0;
    }

    private static void syncAnimalStatusAtOtherFarm(ClassLoader loader, String farmId, String userId) {
        try {
            String s = AntFarmRpcCall.rpcCall_enterFarm(loader, farmId, userId);
            JSONObject jo = new JSONObject(s);
            jo = jo.getJSONObject("farmVO").getJSONObject("subFarmVO");
            countdown = jo.optInt("countdown");
            JSONArray jaAnimals = jo.getJSONArray("animals");
            for (int i = 0; i < jaAnimals.length(); i++) {
                jo = jaAnimals.getJSONObject(i);
                if (jo.getString("masterFarmId").equals(ownerFarmId)) {
                    if (ownerAnimal == null) ownerAnimal = new Animal();
                    jo = jaAnimals.getJSONObject(i);
                    ownerAnimal.animalId = jo.getString("animalId");
                    ownerAnimal.currentFarmId = jo.getString("currentFarmId");
                    ownerAnimal.masterFarmId = ownerFarmId;
                    ownerAnimal.animalBuff = jo.getString("animalBuff");
                    ownerAnimal.subAnimalType = jo.getString("subAnimalType");
                    jo = jo.getJSONObject("animalStatusVO");
                    ownerAnimal.animalFeedStatus = jo.getString("animalFeedStatus");
                    ownerAnimal.animalInteractStatus = jo.getString("animalInteractStatus");
                    Log.i("owner", "animalId=" + ownerAnimal.animalId);
                    Log.i("owner", "currentFarmId=" + ownerAnimal.currentFarmId);
                    Log.i("owner", "masterFarmId=" + ownerAnimal.masterFarmId);
                    Log.i("owner", "animalBuff=" + ownerAnimal.animalBuff);
                    Log.i("owner", "subAnimalType=" + ownerAnimal.subAnimalType);
                    Log.i("owner", "animalFeedStatus=" + ownerAnimal.animalFeedStatus);
                    Log.i("owner", "animalInteractStatus=" + ownerAnimal.animalInteractStatus);
                    break;
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "syncAnimalStatusAtOtherFarm err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void parseSyncAnimalStatusResponse(String resp) {
        try {
            JSONObject jo = new JSONObject(resp);
            jo = jo.getJSONObject("subFarmVO");
            ownerFarmId = jo.getString("farmId");
            JSONObject joo = jo.getJSONObject("farmProduce");
            benevolenceScore = joo.getDouble("benevolenceScore");
            hasGoldenEgg = joo.optBoolean("hasGoldenEgg");
            goldenEggId = joo.optString("goldenEggId");
            countdown = jo.optInt("countdown");
            if (jo.has("rewardList")) {
                JSONArray jaRewardList = jo.getJSONArray("rewardList");
                if (jaRewardList.length() > 0) {
                    rewardList = new RewardFriend[jaRewardList.length()];
                    for (int i = 0; i < rewardList.length; i++) {
                        JSONObject joRewardList = jaRewardList.getJSONObject(i);
                        if (rewardList[i] == null) rewardList[i] = new RewardFriend();
                        rewardList[i].consistencyKey = joRewardList.getString("consistencyKey");
                        rewardList[i].friendId = joRewardList.getString("friendId");
                        rewardList[i].time = joRewardList.getString("time");
                    }
                }
            }
            JSONArray jaAnimals = jo.getJSONArray("animals");
            animals = new Animal[jaAnimals.length()];
            for (int i = 0; i < animals.length; i++) {
                if (animals[i] == null) animals[i] = new Animal();
                jo = jaAnimals.getJSONObject(i);
                animals[i].animalId = jo.getString("animalId");
                animals[i].currentFarmId = jo.getString("currentFarmId");
                animals[i].masterFarmId = jo.getString("masterFarmId");
                animals[i].animalBuff = jo.getString("animalBuff");
                animals[i].subAnimalType = jo.getString("subAnimalType");
                jo = jo.getJSONObject("animalStatusVO");
                animals[i].animalFeedStatus = jo.getString("animalFeedStatus");
                animals[i].animalInteractStatus = jo.getString("animalInteractStatus");
                if (animals[i].masterFarmId.equals(ownerFarmId))
                    ownerAnimal = animals[i];
            }
        } catch (Throwable t) {
            Log.i(TAG, "parseSyncAnimalStatusResponse err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static boolean collectManurePot(ClassLoader loader, String manurePotNOs) {
        try {
            String s = AntFarmRpcCall.rpcCall_collectManurePot(loader, manurePotNOs);
            JSONObject jo = new JSONObject(s);
            if (jo.optBoolean("success") && jo.optBoolean("addToTaobaoSuccess")) {
                int collectManurePotNum = jo.optInt("collectManurePotNum");
                if (collectManurePotNum > 0) {
                    Log.farm("收鸡屎" + collectManurePotNum + "g");
                    return true;
                }
            } else {
                Log.recordLog("collectManurePot fail:" + s);
                s = jo.optString("memo");
                if (s.contains("未开通") || s.contains("建设中")) {
                    return false;
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return true;
    }

    private static void feedAnimal(ClassLoader loader, String farmId) {
        try {
            if (foodStock < 180) {
                Log.recordLog("喂鸡饲料不足", "");
            } else {
                String s = AntFarmRpcCall.rpcCall_feedAnimal(loader, farmId);
                JSONObject jo = new JSONObject(s);
                String memo = jo.getString("memo");
                if (memo.equals("SUCCESS")) {
                    int feedFood = foodStock - jo.getInt("foodStock");
                    add2FoodStock(-feedFood);
                    Log.farm("喂小鸡［" + feedFood + "克］，剩余〔" + foodStock + "克〕");
                    if (Config.getUseAccelerateTool()) {
                        useFarmTool(loader, ownerFarmId, ToolType.ACCELERATETOOL);
                    }
                } else {
                    Log.recordLog(memo, s);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "feedAnimal err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void harvestProduce(ClassLoader loader, String farmId) {
        try {
            String s = AntFarmRpcCall.rpcCall_harvestProduce(loader, farmId);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                double harvest = jo.getDouble("harvestBenevolenceScore");
                harvestBenevolenceScore = jo.getDouble("finalBenevolenceScore");
                Log.farm("收取〔" + harvest + "颗〕爱心鸡蛋，剩余〔" + harvestBenevolenceScore + "颗〕");
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "harvestProduce err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void syncAnimalStatus(ClassLoader loader, String farmId) {
        try {
            String s = AntFarmRpcCall.rpcCall_syncAnimalStatus(loader, farmId);
            parseSyncAnimalStatusResponse(s);
        } catch (Throwable t) {
            Log.i(TAG, "syncAnimalStatus err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void answerQuestion(ClassLoader loader) {
        try {
            String s = AntFarmRpcCall.rpcCall_listFarmTask(loader);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                JSONArray jaFarmTaskList = jo.getJSONArray("farmTaskList");
                for (int i = 0; i < jaFarmTaskList.length(); i++) {
                    jo = jaFarmTaskList.getJSONObject(i);
                    if (jo.getString("title").equals("庄园小课堂")) {
                        switch (TaskStatus.valueOf((jo.getString("taskStatus")))) {
                            case TODO:
                                s = AntFarmRpcCall.rpcCall_getAnswerInfo(loader);
                                jo = new JSONObject(s);
                                memo = jo.getString("memo");
                                if (memo.equals("SUCCESS")) {
                                    jo = jo.getJSONArray("answerInfoVOs").getJSONObject(0);
                                    JSONArray jaOptionContents = jo.getJSONArray("optionContents");
                                    String rightReply = jo.getString("rightReply");
                                    Log.recordLog(jo.getString("questionContent"), "");
                                    Log.recordLog(jaOptionContents.toString(), "");
                                    String questionId = jo.getString("questionId");
                                    int answer = 0;
                                    for (int j = 0; j < jaOptionContents.length(); j++) {
                                        if (rightReply.contains(jaOptionContents.getString(j))) {
                                            answer += j + 1;
                                            //break;
                                        }
                                    }
                                    if (0 < answer && answer < 3) {
                                        s = AntFarmRpcCall.rpcCall_answerQuestion(loader, questionId, answer);
                                        jo = new JSONObject(s);
                                        memo = jo.getString("memo");
                                        if (memo.equals("SUCCESS")) {
                                            s = jo.getBoolean("rightAnswer") ? "正确" : "错误";
                                            Log.farm("答题" + s + "，可领取［" + jo.getInt("awardCount") + "克］");
                                            Statistics.answerQuestionToday(FriendIdMap.currentUid);
                                        } else {
                                            Log.recordLog(memo, s);
                                        }
                                        Statistics.setQuestionHint(null);
                                    } else {
                                        Statistics.setQuestionHint(rightReply);
                                        Log.farm("未找到正确答案，放弃作答。提示：" + rightReply);
                                        Statistics.answerQuestionToday(FriendIdMap.currentUid);
                                    }
                                } else {
                                    Log.recordLog(memo, s);
                                }
                                break;

                            case RECEIVED:
                                Statistics.setQuestionHint(null);
                                Log.recordLog("今日答题已完成", "");
                                Statistics.answerQuestionToday(FriendIdMap.currentUid);
                                break;

                            case FINISHED:
                                Statistics.setQuestionHint(null);
                                Log.recordLog("已经答过题了，饲料待领取", "");
                                Statistics.answerQuestionToday(FriendIdMap.currentUid);
                                break;
                        }
                        break;
                    }
                }
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "answerQuestion err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void donation(ClassLoader loader) {
        try {
            String s = AntFarmRpcCall.rpcCall_listActivityInfo(loader);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                JSONArray jaActivityInfos = jo.getJSONArray("activityInfos");
                String activityId = null, activityName = null;
                for (int i = 0; i < jaActivityInfos.length(); i++) {
                    jo = jaActivityInfos.getJSONObject(i);
                    if (!jo.get("donationTotal").equals(jo.get("donationLimit"))) {
                        activityId = jo.getString("activityId");
                        activityName = jo.getString("activityName");
                        break;
                    }
                }
                if (activityId == null) {
                    Log.recordLog("今日已无可捐赠的活动", "");
                } else {
                    s = AntFarmRpcCall.rpcCall_donation(loader, activityId);
                    jo = new JSONObject(s);
                    memo = jo.getString("memo");
                    if (memo.equals("SUCCESS")) {
                        jo = jo.getJSONObject("donation");
                        harvestBenevolenceScore = jo.getDouble("harvestBenevolenceScore");
                        Log.farm("捐赠活动〔" + activityName + "〕，累计捐赠〔" + jo.getInt("donationTimesStat") + "次〕");
                    } else {
                        Log.recordLog(memo, s);
                    }
                }
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "donation err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void feedFriend(ClassLoader loader) {
        try {
            String s, memo;
            JSONObject jo;
            for (int i = 0; i < Config.getFeedFriendAnimalList().size(); i++) {
                String userId = Config.getFeedFriendAnimalList().get(i);
                if (userId.equals(FriendIdMap.currentUid))
                    continue;
                if (!Statistics.canFeedFriendToday(userId, Config.getFeedFriendAnimalNumList().get(i)))
                    continue;
                s = AntFarmRpcCall.rpcCall_enterFarm(loader, "", userId);
                jo = new JSONObject(s);
                memo = jo.getString("memo");
                if (memo.equals("SUCCESS")) {
                    jo = jo.getJSONObject("farmVO").getJSONObject("subFarmVO");
                    String friendFarmId = jo.getString("farmId");
                    JSONArray jaAnimals = jo.getJSONArray("animals");
                    for (int j = 0; j < jaAnimals.length(); j++) {
                        jo = jaAnimals.getJSONObject(j);
                        String masterFarmId = jo.getString("masterFarmId");
                        if (masterFarmId.equals(friendFarmId)) {
                            jo = jo.getJSONObject("animalStatusVO");
                            if (AnimalInteractStatus.HOME.name().equals(jo.getString("animalInteractStatus"))
                                    && AnimalFeedStatus.HUNGRY.name().equals(jo.getString("animalFeedStatus"))) {
                                feedFriendAnimal(loader, friendFarmId, FriendIdMap.getNameById(userId));
                            }
                            break;
                        }
                    }
                } else {
                    Log.recordLog(memo, s);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "feedFriend err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void harvestGoldenEgg(ClassLoader loader) {
        if (hasGoldenEgg && goldenEggId != null) {
            if ("".equals(goldenEggId)) {
                return;
            }
        }
        try {
            JSONObject jo = new JSONObject(AntFarmRpcCall.rpcCall_harvestGoldenEgg(loader, ownerFarmId, goldenEggId));
            if (jo.optBoolean("success") && jo.has("surprisePrizeDrawRecordVO")) {
                JSONArray ja = jo.getJSONObject("surprisePrizeDrawRecordVO").optJSONArray("surprisePrizeInfoList");
                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject joo = ja.getJSONObject(i);
                        String prizeName = joo.optString("prizeName");
                        joo = new JSONObject(AntFarmRpcCall.rpcCall_surprisePrizeReceive(loader, goldenEggId, joo.optString("receiveOrderId")));
                        if (joo.optBoolean("success")) {
                            Log.farm("收取金蛋，领取" + prizeName);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static void hireAnimal(ClassLoader loader) {
        try {
            for (Animal animal : animals) {
                if (!ownerAnimal.animalId.equals(animal.animalId)
                        && !AnimalInteractStatus.HIRING.name().equals(animal.animalInteractStatus)
                        && new JSONObject(AntFarmRpcCall.rpcCall_hireAnimal(loader, animal.masterFarmId, animal.animalId)).optBoolean("success")) {
                    Log.farm("消耗50g饲料，雇佣" + FriendIdMap.getNameById(animal.userId) + "的小鸡一起生产肥料");
                }
            }
            for (int count = animals.length; count < 3; ) {
                JSONObject jo = new JSONObject(AntFarmRpcCall.rpcCall_rankingList(loader));
                if (jo.optBoolean("success")) {
                    JSONArray ja = jo.getJSONArray("rankingList");
                    for (int i = ja.length() - 1; i >= 0; i--) {
                        JSONObject joo = ja.getJSONObject(i);
                        if (joo.optString("actionType").equals("starve_action")) {
                            String userId = joo.optString("userId");
                            joo = new JSONObject(AntFarmRpcCall.rpcCall_enterFarm(loader, "", userId))
                                    .getJSONObject("farmVO")
                                    .getJSONObject("subFarmVO");
                            String farmId = joo.getString("farmId");
                            JSONArray jaa = joo.getJSONArray("animals");
                            for (int j = 0; j < jaa.length(); j++) {
                                joo = jaa.getJSONObject(j);
                                if (joo.getString("masterFarmId").equals(farmId)) {
                                    if (new JSONObject(AntFarmRpcCall.rpcCall_hireAnimal(loader, farmId, joo.getString("animalId")))
                                            .optBoolean("success")) {
                                        Log.farm("消耗50g饲料，雇佣" + FriendIdMap.getNameById(userId) + "的小鸡一起生产肥料");
                                        count += 1;
                                    }
                                    break;
                                }
                            }
                            if (count >= 3) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void notifyFriend(ClassLoader loader) {
        if (foodStock >= foodStockLimit) return;
        try {
            boolean hasNext = false;
            int pageStartSum = 0;
            String s;
            JSONObject jo;
            do {
                s = AntFarmRpcCall.rpcCall_rankingList(loader, pageStartSum);
                jo = new JSONObject(s);
                String memo = jo.getString("memo");
                if (memo.equals("SUCCESS")) {
                    hasNext = jo.getBoolean("hasNext");
                    JSONArray jaRankingList = jo.getJSONArray("rankingList");
                    pageStartSum += jaRankingList.length();
                    for (int i = 0; i < jaRankingList.length(); i++) {
                        jo = jaRankingList.getJSONObject(i);
                        String userId = jo.getString("userId");
                        String userName = FriendIdMap.getNameById(userId);
                        if (Config.getDontNotifyFriendList().contains(userId)
                                || userId.equals(FriendIdMap.currentUid))
                            continue;
                        boolean starve = jo.has("actionType") && jo.getString("actionType").equals("starve_action");
                        if (jo.getBoolean("stealingAnimal") && !starve) {
                            s = AntFarmRpcCall.rpcCall_enterFarm(loader, "", userId);
                            jo = new JSONObject(s);
                            memo = jo.getString("memo");
                            if (memo.equals("SUCCESS")) {
                                jo = jo.getJSONObject("farmVO").getJSONObject("subFarmVO");
                                String friendFarmId = jo.getString("farmId");
                                JSONArray jaAnimals = jo.getJSONArray("animals");
                                boolean notified = !Config.getNotifyFriend();
                                for (int j = 0; j < jaAnimals.length(); j++) {
                                    jo = jaAnimals.getJSONObject(j);
                                    String animalId = jo.getString("animalId");
                                    String masterFarmId = jo.getString("masterFarmId");
                                    if (!masterFarmId.equals(friendFarmId) && !masterFarmId.equals(ownerFarmId)) {
                                        if (notified) continue;
                                        jo = jo.getJSONObject("animalStatusVO");
                                        notified = notifyFriend(loader, jo, friendFarmId, animalId, userName);
                                    }
                                }
                            } else {
                                Log.recordLog(memo, s);
                            }
                        }
                        if (foodStock < foodStockLimit) {
                            break;
                        }
                    }
                } else {
                    Log.recordLog(memo, s);
                }
                if (foodStock < foodStockLimit) {
                    break;
                }
            } while (hasNext);
            Log.recordLog("饲料剩余〔" + foodStock + "克〕", "");
        } catch (Throwable t) {
            Log.i(TAG, "notifyFriend err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void collectOrchardManure(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(AntFarmRpcCall.rpcCall_orchardIndex(loader));
            if (jo.optBoolean("success")) {
                if (new JSONObject(jo.optString("taobaoData"))
                        .getJSONObject("gameInfo")
                        .getJSONObject("dailyFuqiInfo")
                        .optBoolean("canCollect")) {
                    jo = new JSONObject(AntFarmRpcCall.rpcCall_orchardCollectDailyManure(loader));
                    if (jo.optBoolean("success")) {
                        jo = new JSONObject(jo.optString("taobaoData"));
                        int collectAmount = jo.optInt("collectAmount");
                        int amount = jo.getJSONObject("dailyFuqiInfo").getInt("amount");
                        Log.farm("芭芭农场领取每日肥料" + collectAmount + "g，明日7:00可领" + amount + "g");
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void spreadOrchardManure(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(AntFarmRpcCall.rpcCall_orchardSpreadManure(loader));
            if (jo.optBoolean("success")) {
                jo = new JSONObject(jo.optString("taobaoData", "{}"));
                int currentCost = jo.optInt("currentCost");
                int left = jo.optInt("leftHappyPoint");
                jo = jo.optJSONObject("currentStage");
                String seedName = jo.optString("seedName");
                String stageName = jo.optString("stageName");
                String stageText = jo.optString("stageText");
                Log.farm("给" + seedName + "施肥" + currentCost + "g，剩余" + left + "g，" + stageName + "，" + stageText);
                if (left > 600) {
                    Thread.sleep(RandomUtils.delay());
                    spreadOrchardManure(loader);
                }
            } else {
                Log.recordLog(jo.optString("memo"));
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void playGame(ClassLoader loader) {
        try {
            String[] games = new String[]{"starGame", "jumpGame"};
            String[] gameNames = new String[]{"星星球", "登山赛"};
            JSONObject jo;
            for (int i = 0; i < games.length; i++) {
                String s = AntFarmRpcCall.rpcCall_initFarmGame(loader, games[i]);
                if (!s.isEmpty()) {
                    jo = new JSONObject(s);
                    if (jo.optBoolean("success")) {
                        jo = jo.getJSONObject("gameAward");
                        if (!jo.optBoolean("level1Get") || !jo.optBoolean("level2Get") || !jo.optBoolean("level3Get")) {
                            s = AntFarmRpcCall.rpcCall_recordFarmGame(loader, games[i]);
                            if (!s.isEmpty()) {
                                jo = new JSONObject(s);
                                if (jo.optBoolean("success")) {
                                    int currentScore = jo.optInt("currentScore");
                                    JSONArray ja = jo.optJSONArray("awardInfos");
                                    StringBuilder sb = new StringBuilder("玩〔" + gameNames[i] + "〕，得分〔" + currentScore + "〕");
                                    if (ja != null && ja.length() > 0) {
                                        sb.append("，获得");
                                        for (int j = 0; j < ja.length(); j++) {
                                            jo = ja.getJSONObject(j);
                                            sb.append("〔" + jo.optString("awardName") + "x" + jo.optInt("receiveCount") + "〕");
                                        }
                                    }
                                    Log.farm(sb.toString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void receiveFarmTaskAward(ClassLoader loader) {
        try {
            String s = AntFarmRpcCall.rpcCall_listFarmTask(loader);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                JSONArray jaFarmTaskList = jo.getJSONArray("farmTaskList");
                for (int i = 0; i < jaFarmTaskList.length(); i++) {
                    jo = jaFarmTaskList.getJSONObject(i);
                    String taskTitle = null;
                    if (jo.has("title")) taskTitle = jo.getString("title");
                    switch (TaskStatus.valueOf(jo.getString("taskStatus"))) {
                        case TODO:
                            break;
                        case FINISHED:
                            int awardCount = jo.getInt("awardCount");
                            if (awardCount + foodStock > foodStockLimit) {
                                unreceiveTaskAward++;
                                Log.recordLog("领取" + awardCount + "克饲料后将超过〔" + foodStockLimit + "克〕上限，已终止领取", "");
                                break;
                            }

                            s = AntFarmRpcCall.rpcCall_receiveFarmTaskAward(loader, jo.getString("taskId"));
                            jo = new JSONObject(s);
                            memo = jo.getString("memo");
                            if (memo.equals("SUCCESS")) {
                                foodStock = jo.getInt("foodStock");
                                Log.farm("领取〔" + jo.getInt("haveAddFoodStock") + "克〕，来源：" + taskTitle);
                                if (unreceiveTaskAward > 0) unreceiveTaskAward--;
                            } else {
                                Log.recordLog(memo, s);
                            }
                        case RECEIVED:
                            if (taskTitle != null && taskTitle.equals("庄园小课堂")) {
                                Statistics.setQuestionHint(null);
                            }
                            break;
                    }
                }
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "receiveFarmTaskAward err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void receiveToolTaskReward(ClassLoader loader) {
        try {
            String s = AntFarmRpcCall.rpcCall_listToolTaskDetails(loader);
            JSONObject jo = new JSONObject(s);
            String memo = jo.getString("memo");
            if (memo.equals("SUCCESS")) {
                JSONArray jaList = jo.getJSONArray("list");
                for (int i = 0; i < jaList.length(); i++) {
                    jo = jaList.getJSONObject(i);
                    if (jo.has("taskStatus")
                            && TaskStatus.FINISHED.name().equals(jo.getString("taskStatus"))) {
                        int awardCount = jo.getInt("awardCount");
                        String awardType = jo.getString("awardType");
                        ToolType toolType = ToolType.valueOf(awardType);
                        String taskType = jo.getString("taskType");
                        jo = new JSONObject(jo.getString("bizInfo"));
                        String taskTitle = jo.getString("taskTitle");
                        s = AntFarmRpcCall.rpcCall_receiveToolTaskReward(loader, awardType, awardCount, taskType);
                        jo = new JSONObject(s);
                        memo = jo.getString("memo");
                        if (memo.equals("SUCCESS")) {
                            Log.farm("领取〔" + awardCount + "张〕〔" + toolType.nickName() + "〕，来源：" + taskTitle);
                        } else {
                            memo = memo.replace("道具", toolType.nickName());
                            Log.recordLog(memo, s);
                        }
                    }
                }
            } else {
                Log.recordLog(memo, s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "receiveToolTaskReward err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void rewardFriend(ClassLoader loader) {
        try {
            if (rewardList != null) {
                for (int i = 0; i < rewardList.length; i++) {
                    String s = AntFarmRpcCall.rpcCall_rewardFriend(loader, rewardList[i].consistencyKey, rewardList[i].friendId, rewardProductNum, rewardList[i].time);
                    JSONObject jo = new JSONObject(s);
                    String memo = jo.getString("memo");
                    if (memo.equals("SUCCESS")) {
                        double rewardCount = benevolenceScore - jo.getDouble("farmProduct");
                        benevolenceScore -= rewardCount;
                        Log.farm("打赏〔" + FriendIdMap.getNameById(rewardList[i].friendId) + "〕〔" + rewardCount + "颗〕爱心鸡蛋");
                    } else {
                        Log.recordLog(memo, s);
                    }
                }
                rewardList = null;
            }
        } catch (Throwable t) {
            Log.i(TAG, "rewardFriend err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void sendBackAnimal(ClassLoader loader) {
        try {
            boolean changed = false;
            for (int i = 0; i < animals.length; i++) {
                if (AnimalInteractStatus.STEALING.name().equals(animals[i].animalInteractStatus)
                        && !SubAnimalType.GUEST.name().equals(animals[i].subAnimalType)) {
                    // 赶鸡
                    String user = AntFarmRpcCall.farmId2UserId(animals[i].masterFarmId);
                    if (Config.getDontSendFriendList().contains(user))
                        continue;
                    SendType sendType = Config.getSendType();
                    user = FriendIdMap.getNameById(user);
                    String s = AntFarmRpcCall.rpcCall_sendBackAnimal(
                            loader, sendType.name(), animals[i].animalId,
                            animals[i].currentFarmId, animals[i].masterFarmId);
                    JSONObject jo = new JSONObject(s);
                    String memo = jo.getString("memo");
                    if (memo.equals("SUCCESS")) {
                        if (sendType == SendType.HIT) {
                            if (jo.has("hitLossFood")) {
                                s = "胖揍〔" + user + "〕小鸡，掉落〔" + jo.getInt("hitLossFood") + "克〕";
                                if (jo.has("finalFoodStorage"))
                                    foodStock = jo.getInt("finalFoodStorage");
                            } else
                                s = "〔" + user + "〕的小鸡躲开了攻击";
                        } else {
                            s = "赶走〔" + user + "〕的小鸡";
                        }
                        Log.farm(s);
                        changed = true;
                    } else {
                        Log.recordLog(memo, s);
                    }
                }
            }
            if (changed) {
                syncAnimalStatus(loader, ownerFarmId);
            }
        } catch (Throwable t) {
            Log.i(TAG, "sendBackAnimal err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void orchardSignInTask(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(AntFarmRpcCall.rpcCall_orchardListTask(loader));
            if (jo.optBoolean("success")) {
                JSONArray ja = jo.getJSONArray("taskList");
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    String taskId = jo.optString("taskId");
                    if (jo.optString("actionType").equals("TRIGGER") && jo.optString("taskStatus").equals("TODO")) {
                        jo = jo.optJSONObject("taobaoTaskParams");
                        String s = AntFarmRpcCall.rpcCall_triggerTbTask(loader, jo.optInt("activityId"), jo.optInt("deliveryId"), jo.optString("implId"), jo.optInt("sceneId"), taskId);
                        if (new JSONObject(s).optBoolean("success")) {
                            Log.farm("芭芭农场签到成功，获得600g肥料包");
                        }
                        return;
                    }
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    private static void giveFriendFood(ClassLoader loader) {
        try {
            if (foodStock >= 10) {
                int cost = 0;
                for (String userId : Config.getVisitFriendList()) {
                    if (!userId.equals(FriendIdMap.currentUid)) {
                        if (foodStock - cost >= 10) {
                            JSONObject jo = new JSONObject(AntFarmRpcCall.rpcCall_enterFarm(loader, "", userId));
                            if (jo.getString("memo").equals("SUCCESS")) {
                                jo = jo.getJSONObject("farmVO").getJSONObject("subFarmVO");
                                if (!jo.optBoolean("visitedToday")) {
                                    String farmId = jo.getString("farmId");
                                    int n = 0;
                                    for (int j = 0; j < 3; j++) {
                                        jo = new JSONObject(AntFarmRpcCall.rpcCall_visitFriend(loader, farmId));
                                        if (jo.optString("memo").equals("SUCCESS")) {
                                            n += jo.optInt("giveFoodNum");
                                            if (jo.optBoolean("isReachLimit") || foodStock - n < 10) {
                                                break;
                                            }
                                        }
                                    }
                                    cost += n;
                                    Log.farm("给〔" + FriendIdMap.getNameById(userId) + "〕种麦子〔" + n + "g〕");
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
                add2FoodStock(-cost);
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public enum SendType {
        HIT, NORMAL;
        public static final CharSequence[] names =
                {HIT.name(), NORMAL.name()};
        public static final CharSequence[] nickNames =
                {"攻击", "常规"};

        public SendType another() {
            return this == HIT ? NORMAL : HIT;
        }

        public CharSequence nickName() {
            return nickNames[ordinal()];
        }
    }

    public enum AnimalBuff {ACCELERATING, INJURED, NONE}

    public enum AnimalFeedStatus {HUNGRY, EATING}

    public enum AnimalInteractStatus {HOME, GOTOSTEAL, STEALING, HIRING}

    public enum SubAnimalType {NORMAL, GUEST, PIRATE}

    public enum TaskStatus {TODO, FINISHED, RECEIVED}

    public enum ToolType {
        STEALTOOL, ACCELERATETOOL, SHARETOOL, FENCETOOL, NEWEGGTOOL;
        public static final CharSequence[] nickNames =
                {"蹭饭卡", "加速卡", "救济卡", "篱笆卡", "新蛋卡"};

        public CharSequence nickName() {
            return nickNames[ordinal()];
        }
    }

    private static class Animal {
        public String animalId;
        public String currentFarmId;
        public String masterFarmId;
        public String animalBuff;
        public String subAnimalType;
        public String animalFeedStatus;
        public String animalInteractStatus;
        public String userId;
    }

    private static class RewardFriend {
        public String consistencyKey;
        public String friendId;
        public String time;
    }


}