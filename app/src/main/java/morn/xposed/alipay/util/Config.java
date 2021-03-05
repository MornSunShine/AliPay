package morn.xposed.alipay.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import morn.xposed.alipay.AntFarm;

public class Config {
    private static final String TAG = "pansong291.xposed.quickenergy.util.Config";
    public static final String
            /* application */
            jn_immediateEffect = "immediateEffect", jn_recordLog = "recordLog", jn_showToast = "showToast",
            jn_recordRuntimeLog = "recordRuntimeLog", jn_deleteStranger = "deleteStranger",
            jn_smallAccountList = "smallAccountList", jn_autoRestart = "autoRestart", jn_autoRestart2 = "autoRestart2",
            jn_pin = "pin", jn_restartService = "restartService", jn_sendXedgepro = "sendXedgepro",
            jn_xedgeproData = "xedgeproData", jn_originalMode = "originalMode", jn_stayAwake = "stayAwake",
    /* forest */
    jn_collectEnergy = "collectEnergy", jn_dontCollectList = "dontCollectList", jn_checkInterval = "checkInterval",
            jn_advanceTime = "advanceTime", jn_collectInterval = "collectInterval", jn_collectTimeout = "collectTimeout",
            jn_threadCount = "threadCount", jn_returnWater30 = "returnWater30", jn_returnWater20 = "returnWater20",
            jn_returnWater10 = "returnWater10", jn_helpFriendCollect = "helpFriendCollect",
            jn_delayHelpFriendCollect = "delayHelpFriendCollect", jn_dontHelpCollectList = "dontHelpCollectList",
            jn_collectWateringEnergy = "collectWateringEnergy", jn_helpFriendCollectWatering = "helpFriendCollectWatering",
            jn_cooperateWater = "cooperateWater", jn_cooperateWaterList = "cooperateWaterList",
            jn_waterFriendList = "waterFriendList", jn_grabPacket = "grabPacket", jn_receiveForestTaskAward = "receiveForestTaskAward",
    /* farm */
    jn_enableFarm = "enableFarm", jn_feedAnimal = "feedAnimal", jn_useAccelerateTool = "useAccelerateTool",
            jn_useNewEggTool = "useNewEggTool", jn_harvestProduce = "harvestProduce", jn_donation = "donation",
            jn_sendBackAnimal = "sendBackAnimal", jn_sendType = "sendType", jn_dontSendFriendList = "dontSendFriendList",
            jn_rewardFriend = "rewardFriend", jn_recallAnimalType = "recallAnimalType", jn_feedFriendAnimalList = "feedFriendAnimalList",
            jn_notifyFriend = "notifyFriend", jn_dontNotifyFriendList = "dontNotifyFriendList", jn_acceptGift = "acceptGift",
            jn_visitFriendList = "visitFriendList", jn_answerQuestion = "answerQuestion", jn_starGameHighScore = "starGameHighScore",
            jn_jumpGameHighScore = "jumpGameHighScore", jn_playFarmGame = "playFarmGame", jn_receiveFarmToolReward = "receiveFarmToolReward",
            jn_receiveFarmTaskAward = "receiveFarmTaskAward", jn_collectManurePot = "collectManurePot",
            jn_hireAnimal = "hireAnimal", jn_triggerTbTask = "triggerTbTask", jn_orchardSpreadManure = "orchardSpreadManure",
    /* other */
    jn_receivePoint = "receivePoint", jn_kbSignIn = "kbSignIn", jn_collectCreditFeedback = "collectCreditFeedback",
            jn_zmDonate = "zmDonate", jn_electricSignIn = "electricSignIn", jn_greatyouthReceive = "greatyouthReceive",
            jn_openTreasureBox = "openTreasureBox", jn_donateCharityCoin = "donateCharityCoin", jn_minExchangeCount = "minExchangeCount",
            jn_latestExchangeTime = "latestExchangeTime", jn_hookStep = "hookStep", jn_openDoorSignIn = "openDoorSignIn",
            jn_addFriendGroupList = "addFriendGroupList", jn_enable = "enable", jn_addFriend = "addFriend";
    public static boolean shouldReload;
    public static boolean hasChanged;
    private static Config config;
    /* application */
    private boolean immediateEffect;
    private boolean recordLog;
    private boolean recordRuntimeLog;
    private boolean showToast;
    private boolean deleteStranger;
    private List<String> smallAccountList;
    private RestartType autoRestart;
    private RestartType autoRestart2;
    private String pin;
    private boolean restartService;
    private boolean sendXedgepro;
    private String xedgeproData;
    private boolean originalMode;
    private boolean stayAwake;

    /* forest */
    private boolean collectEnergy;
    private List<String> dontCollectList;
    private int checkInterval;
    private int advanceTime;
    private int collectInterval;
    private int collectTimeout;
    private int threadCount;
    private int returnWater30;
    private int returnWater20;
    private int returnWater10;
    private boolean helpFriendCollect;
    private boolean delayHelpFriendCollect;
    private List<String> dontHelpCollectList;
    private boolean collectWateringEnergy;
    private boolean helpFriendCollectWatering;
    private boolean cooperateWater;
    private List<String> cooperateWaterList;
    private List<Integer> cooperateWaterNumList;
    private List<String> waterFriendList;
    private List<Integer> waterCountList;
    private boolean grabPacket;
    private boolean receiveForestTaskAward;

    /* farm */
    private boolean enableFarm;
    private boolean feedAnimal;
    private boolean useAccelerateTool;
    private boolean useNewEggTool;
    private boolean harvestProduce;
    private boolean donation;
    private boolean sendBackAnimal;
    private AntFarm.SendType sendType;
    private List<String> dontSendFriendList;
    private boolean rewardFriend;
    private RecallAnimalType recallAnimalType;
    private List<String> feedFriendAnimalList;
    private List<Integer> feedFriendAnimalNumList;
    private boolean notifyFriend;
    private List<String> dontNotifyFriendList;
    private boolean acceptGift;
    private List<String> visitFriendList;
    private boolean answerQuestion;
    private boolean starGameHighScore;
    private boolean jumpGameHighScore;
    private boolean playFarmGame;
    private boolean receiveFarmToolReward;
    private boolean receiveFarmTaskAward;
    private boolean collectManurePot;
    private boolean hireAnimal;
    private boolean triggerTbTask;
    private boolean orchardSpreadManure;

    /*others*/
    private boolean receivePoint;
    private boolean kbSignIn;
    private boolean collectCreditFeedback;
    private boolean zmDonate;
    private boolean electricSignIn;
    private boolean greatyouthReceive;
    private boolean openTreasureBox;
    private boolean donateCharityCoin;
    private int minExchangeCount;
    private int latestExchangeTime;
    private int hookStep;
    private boolean openDoorSignIn;
    private List<String> addFriendGroupList;

    private boolean enable;

    public static boolean getUseAccelerateTool() {
        return getConfig().useAccelerateTool;
    }

    public static boolean getUseNewEggTool() {
        return getConfig().useNewEggTool;
    }

    public static String getXedgeproData() {
        return getConfig().xedgeproData;
    }

    public static boolean getZmDonate() {
        return getConfig().zmDonate;
    }

    public static boolean getOrchardSpreadManure() {
        return getConfig().orchardSpreadManure;
    }

    public static boolean getOriginalMode() {
        return getConfig().originalMode;
    }

    public static String getPin() {
        return getConfig().pin;
    }

    public static boolean getPlayFarmGame() {
        return getConfig().playFarmGame;
    }

    public static RecallAnimalType getRecallAnimalType() {
        return getConfig().recallAnimalType;
    }

    public static boolean getReceiveFarmTaskAward() {
        return getConfig().receiveFarmTaskAward;
    }

    public static boolean getReceiveFarmToolReward() {
        return getConfig().receiveFarmToolReward;
    }

    public static boolean getReceiveForestTaskAward() {
        return getConfig().receiveForestTaskAward;
    }

    public static boolean getReceivePoint() {
        return getConfig().receivePoint;
    }

    public static boolean getRecordLog() {
        return getConfig().recordLog;
    }

    public static boolean getRecordRuntimeLog() {
        if (config == null) {
            return true;
        }
        return config.recordRuntimeLog;
    }

    public static boolean getRestartService() {
        return getConfig().restartService;
    }

    public static int getReturnWater10() {
        return getConfig().returnWater10;
    }

    public static int getReturnWater20() {
        return getConfig().returnWater20;
    }

    public static int getReturnWater30() {
        return getConfig().returnWater30;
    }

    public static boolean getRewardFriend() {
        return getConfig().rewardFriend;
    }

    public static boolean write2ConfigFile() {
        return FileUtils.write2File(config2Json(config), FileUtils.getConfigFile());
    }

    public static boolean getSendBackAnimal() {
        return getConfig().sendBackAnimal;
    }

    public static AntFarm.SendType getSendType() {
        return getConfig().sendType;
    }

    public static boolean getSendXedgepro() {
        return getConfig().sendXedgepro;
    }

    public static boolean getShowToast() {
        return getConfig().showToast;
    }

    public static List<String> getSmallAccountList() {
        return getConfig().smallAccountList;
    }

    public static boolean getStarGameHighScore() {
        return getConfig().starGameHighScore;
    }

    public static boolean getStayAwake() {
        return getConfig().stayAwake;
    }

    public static int getThreadCount() {
        return getConfig().threadCount;
    }

    public static boolean getTriggerTbTask() {
        return getConfig().triggerTbTask;
    }

    public static String formatJson(JSONObject jo, boolean removeQuote) {
        String formated = null;
        try {
            formated = jo.toString(4);
        } catch (Throwable t) {
            return jo.toString();
        }
        if (!removeQuote) return formated;
        StringBuilder sb = new StringBuilder(formated);
        char currentChar, lastNonSpaceChar = 0;
        for (int i = 0; i < sb.length(); i++) {
            currentChar = sb.charAt(i);
            switch (currentChar) {
                case '"':
                    switch (lastNonSpaceChar) {
                        case ':':
                        case '[':
                            sb.deleteCharAt(i);
                            i = sb.indexOf("\"", i);
                            sb.deleteCharAt(i);
                            if (lastNonSpaceChar != '[') lastNonSpaceChar = sb.charAt(--i);
                    }
                    break;

                case ' ':
                    break;

                default:
                    if (lastNonSpaceChar == '[' && currentChar != ']')
                        break;
                    lastNonSpaceChar = currentChar;
            }
        }
        formated = sb.toString();
        return formated;
    }

    public static String config2Json(Config config) {
        JSONObject jo = new JSONObject();
        JSONArray ja = null, jaa = null;
        try {
            if (config == null) config = Config.defInit();
            jo.put(jn_immediateEffect, config.immediateEffect);
            jo.put(jn_recordLog, config.recordLog);
            jo.put(jn_recordRuntimeLog, config.recordRuntimeLog);
            jo.put(jn_showToast, config.showToast);
            jo.put(jn_stayAwake, config.stayAwake);
            jo.put(jn_restartService, config.restartService);
            jo.put(jn_autoRestart, config.autoRestart.name());
            jo.put(jn_autoRestart2, config.autoRestart2.name());
            jo.put(jn_pin, config.pin);
            jo.put(jn_sendXedgepro, config.sendXedgepro);
            jo.put(jn_xedgeproData, config.xedgeproData);
            jo.put(jn_originalMode, config.originalMode);
            jo.put(jn_deleteStranger, config.deleteStranger);
            jo.put(jn_enable, config.enable);
            ja = new JSONArray();
            for (String s : config.smallAccountList) {
                ja.put(s);
            }
            jo.put(jn_smallAccountList, ja);

            jo.put(jn_collectEnergy, config.collectEnergy);
            jo.put(jn_collectWateringEnergy, config.collectWateringEnergy);
            jo.put(jn_checkInterval, config.checkInterval);
            jo.put(jn_threadCount, config.threadCount);
            jo.put(jn_advanceTime, config.advanceTime);
            jo.put(jn_collectInterval, config.collectInterval);
            jo.put(jn_collectTimeout, config.collectTimeout);
            jo.put(jn_returnWater30, config.returnWater30);
            jo.put(jn_returnWater20, config.returnWater20);
            jo.put(jn_returnWater10, config.returnWater10);
            jo.put(jn_helpFriendCollect, config.helpFriendCollect);
            jo.put(jn_delayHelpFriendCollect, config.delayHelpFriendCollect);
            jo.put(jn_helpFriendCollectWatering, config.helpFriendCollectWatering);
            ja = new JSONArray();
            for (String s : config.dontCollectList) {
                ja.put(s);
            }
            jo.put(jn_dontCollectList, ja);

            ja = new JSONArray();
            for (String s : config.dontHelpCollectList) {
                ja.put(s);
            }
            jo.put(jn_dontHelpCollectList, ja);

            jo.put(jn_receiveForestTaskAward, config.receiveForestTaskAward);
            ja = new JSONArray();
            for (int i = 0; i < config.waterFriendList.size(); i++) {
                jaa = new JSONArray();
                jaa.put(config.waterFriendList.get(i));
                jaa.put(config.waterCountList.get(i));
                ja.put(jaa);
            }
            jo.put(jn_waterFriendList, ja);

            jo.put(jn_cooperateWater, config.cooperateWater);

            ja = new JSONArray();
            for (int i = 0; i < config.cooperateWaterList.size(); i++) {
                jaa = new JSONArray();
                jaa.put(config.cooperateWaterList.get(i));
                jaa.put(config.cooperateWaterNumList.get(i));
                ja.put(jaa);
            }
            jo.put(jn_cooperateWaterList, ja);

            jo.put(jn_grabPacket, config.grabPacket);
            jo.put(jn_enableFarm, config.enableFarm);
            jo.put(jn_rewardFriend, config.rewardFriend);
            jo.put(jn_sendBackAnimal, config.sendBackAnimal);
            jo.put(jn_sendType, config.sendType.name());

            ja = new JSONArray();
            for (String s : config.dontSendFriendList) {
                ja.put(s);
            }
            jo.put(jn_dontSendFriendList, ja);

            jo.put(jn_recallAnimalType, config.recallAnimalType);
            jo.put(jn_receiveFarmToolReward, config.receiveFarmToolReward);
            jo.put(jn_useNewEggTool, config.useNewEggTool);
            jo.put(jn_harvestProduce, config.harvestProduce);
            jo.put(jn_donation, config.donation);
            jo.put(jn_answerQuestion, config.answerQuestion);
            jo.put(jn_receiveFarmTaskAward, config.receiveFarmTaskAward);
            jo.put(jn_feedAnimal, config.feedAnimal);
            jo.put(jn_useAccelerateTool, config.useAccelerateTool);

            ja = new JSONArray();
            for (int i = 0; i < config.feedFriendAnimalList.size(); i++) {
                jaa = new JSONArray();
                jaa.put(config.feedFriendAnimalList.get(i));
                jaa.put(config.feedFriendAnimalNumList.get(i));
                ja.put(jaa);
            }
            jo.put(jn_feedFriendAnimalList, ja);

            jo.put(jn_notifyFriend, config.notifyFriend);

            ja = new JSONArray();
            for (String s : config.dontNotifyFriendList) {
                ja.put(s);
            }
            jo.put(jn_dontNotifyFriendList, ja);

            jo.put(jn_starGameHighScore, config.starGameHighScore);
            jo.put(jn_jumpGameHighScore, config.jumpGameHighScore);
            jo.put(jn_playFarmGame, config.playFarmGame);
            jo.put(jn_acceptGift, config.acceptGift);

            ja = new JSONArray();
            for (String s : config.visitFriendList) {
                ja.put(s);
            }
            jo.put(jn_visitFriendList, ja);

            jo.put(jn_collectManurePot, config.collectManurePot);
            jo.put(jn_triggerTbTask, config.triggerTbTask);
            jo.put(jn_orchardSpreadManure, config.orchardSpreadManure);
            jo.put(jn_hireAnimal, config.hireAnimal);
            jo.put(jn_receivePoint, config.receivePoint);
            jo.put(jn_openTreasureBox, config.openTreasureBox);
            jo.put(jn_donateCharityCoin, config.donateCharityCoin);
            jo.put(jn_minExchangeCount, config.minExchangeCount);
            jo.put(jn_latestExchangeTime, config.latestExchangeTime);
            jo.put(jn_kbSignIn, config.kbSignIn);
            jo.put(jn_collectCreditFeedback, config.collectCreditFeedback);
            jo.put(jn_zmDonate, config.zmDonate);
            jo.put(jn_electricSignIn, config.electricSignIn);
            jo.put(jn_greatyouthReceive, config.greatyouthReceive);

            ja = new JSONArray();
            for (String s : config.addFriendGroupList) {
                ja.put(s);
            }
            jo.put(jn_addFriendGroupList, ja);

            jo.put(jn_openDoorSignIn, config.openDoorSignIn);
            jo.put(jn_hookStep, config.hookStep);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return formatJson(jo, false);
    }

    public static Config json2Config(String json) {
        Config config;
        try {
            JSONObject jo = new JSONObject(json);
            JSONArray ja = null, jaa = null;
            config = new Config();

            config.immediateEffect = jo.optBoolean(jn_immediateEffect, true);
            config.recordLog = jo.optBoolean(jn_recordLog, true);
            config.recordRuntimeLog = jo.optBoolean(jn_recordRuntimeLog, false);
            config.showToast = jo.optBoolean(jn_showToast, true);
            config.stayAwake = jo.optBoolean(jn_stayAwake, true);
            config.restartService = jo.optBoolean(jn_restartService, true);
            try {
                config.autoRestart = RestartType.valueOf(jo.optString(jn_autoRestart, RestartType.APP.name()));
            } catch (Exception e) {
                config.autoRestart = RestartType.APP;
            }
            try {
                config.autoRestart2 = RestartType.valueOf(jo.optString(jn_autoRestart2, RestartType.APP.name()));
            } catch (Exception e) {
                config.autoRestart2 = RestartType.APP;
            }
            config.pin = jo.optString(jn_pin, "");
            config.sendXedgepro = jo.optBoolean(jn_sendXedgepro, true);
            config.xedgeproData = jo.optString(jn_xedgeproData, "");
            config.originalMode = jo.optBoolean(jn_originalMode, false);
            config.deleteStranger = jo.optBoolean(jn_deleteStranger, false);
            config.enable = jo.optBoolean(jn_enable, true);
            config.smallAccountList = new ArrayList();
            if (jo.has(jn_smallAccountList)) {
                ja = jo.getJSONArray(jn_smallAccountList);
                for (int i = 0; i < ja.length(); i++) {
                    config.smallAccountList.add(ja.getString(i));
                }
            }
            config.collectEnergy = jo.optBoolean(jn_collectEnergy, true);
            config.collectWateringEnergy = jo.optBoolean(jn_collectWateringEnergy, true);
            config.checkInterval = jo.optInt(jn_checkInterval, 300000);
            config.threadCount = jo.optInt(jn_threadCount, 1);
            config.advanceTime = jo.optInt(jn_advanceTime, 150);
            config.collectInterval = jo.optInt(jn_collectInterval, 150);
            config.collectTimeout = jo.optInt(jn_collectTimeout, 2000);
            config.returnWater30 = jo.optInt(jn_returnWater30);
            config.returnWater20 = jo.optInt(jn_returnWater20);
            config.returnWater10 = jo.optInt(jn_returnWater10);
            config.helpFriendCollect = jo.optBoolean(jn_helpFriendCollect, true);
            config.delayHelpFriendCollect = jo.optBoolean(jn_delayHelpFriendCollect, false);
            config.helpFriendCollectWatering = jo.optBoolean(jn_helpFriendCollectWatering, true);
            config.dontCollectList = new ArrayList();
            if (jo.has(jn_dontCollectList)) {
                ja = jo.getJSONArray(jn_dontCollectList);
                for (int i = 0; i < ja.length(); i++) {
                    config.dontCollectList.add(ja.getString(i));
                }
            }
            config.dontHelpCollectList = new ArrayList();
            if (jo.has(jn_dontHelpCollectList)) {
                ja = jo.getJSONArray(jn_dontHelpCollectList);
                for (int i = 0; i < ja.length(); i++) {
                    config.dontHelpCollectList.add(ja.getString(i));
                }
            }
            config.receiveForestTaskAward = jo.optBoolean(jn_receiveForestTaskAward, true);
            config.waterFriendList = new ArrayList();
            config.waterCountList = new ArrayList();
            if (jo.has(jn_waterFriendList)) {
                ja = jo.getJSONArray(jn_waterFriendList);
                for (int i = 0; i < ja.length(); i++) {
                    if (ja.get(i) instanceof JSONArray) {
                        jaa = ja.getJSONArray(i);
                        config.waterFriendList.add(jaa.getString(0));
                        config.waterCountList.add(jaa.getInt(1));
                    } else {
                        config.waterFriendList.add(ja.getString(i));
                        config.waterCountList.add(3);
                    }
                }
            }
            config.cooperateWater = jo.optBoolean(jn_cooperateWater, true);
            config.cooperateWaterList = new ArrayList();
            config.cooperateWaterNumList = new ArrayList();
            if (jo.has(jn_cooperateWaterList)) {
                ja = jo.getJSONArray(jn_cooperateWaterList);
                for (int i = 0; i < ja.length(); i++) {
                    jaa = ja.getJSONArray(i);
                    config.cooperateWaterList.add(jaa.getString(0));
                    config.cooperateWaterNumList.add(jaa.getInt(1));
                }
            }
            config.grabPacket = jo.optBoolean(jn_grabPacket, true);
            config.enableFarm = jo.optBoolean(jn_enableFarm, true);
            config.rewardFriend = jo.optBoolean(jn_rewardFriend, true);
            config.sendBackAnimal = jo.optBoolean(jn_sendBackAnimal, true);
            config.sendType = AntFarm.SendType.valueOf(jo.optString(jn_sendType, AntFarm.SendType.HIT.name()));
            config.dontSendFriendList = new ArrayList();
            if (jo.has(jn_dontSendFriendList)) {
                ja = jo.getJSONArray(jn_dontSendFriendList);
                for (int i = 0; i < ja.length(); i++) {
                    config.dontSendFriendList.add(ja.getString(i));
                }
            }
            config.recallAnimalType = RecallAnimalType.valueOf(jo.optString(jn_recallAnimalType, RecallAnimalType.ALWAYS.name()));
            config.receiveFarmToolReward = jo.optBoolean(jn_receiveFarmToolReward, true);
            config.useNewEggTool = jo.optBoolean(jn_useNewEggTool, true);
            config.harvestProduce = jo.optBoolean(jn_harvestProduce, true);
            config.donation = jo.optBoolean(jn_donation, true);
            config.answerQuestion = jo.optBoolean(jn_answerQuestion, true);
            config.receiveFarmTaskAward = jo.optBoolean(jn_receiveFarmTaskAward, true);
            config.feedAnimal = jo.optBoolean(jn_feedAnimal, true);
            config.useAccelerateTool = jo.optBoolean(jn_useAccelerateTool, true);
            config.feedFriendAnimalList = new ArrayList();
            config.feedFriendAnimalNumList = new ArrayList();
            if (jo.has(jn_feedFriendAnimalList)) {
                ja = jo.getJSONArray(jn_feedFriendAnimalList);
                for (int i = 0; i < ja.length(); i++) {
                    if (ja.get(i) instanceof JSONArray) {
                        jaa = ja.getJSONArray(i);
                        config.feedFriendAnimalList.add(jaa.getString(0));
                        config.feedFriendAnimalNumList.add(jaa.getInt(1));
                    } else {
                        config.feedFriendAnimalList.add(ja.getString(i));
                        config.feedFriendAnimalNumList.add(1);
                    }
                }
            }
            config.notifyFriend = jo.optBoolean(jn_notifyFriend, true);
            config.dontNotifyFriendList = new ArrayList();
            if (jo.has(jn_dontNotifyFriendList)) {
                ja = jo.getJSONArray(jn_dontNotifyFriendList);
                for (int i = 0; i < ja.length(); i++) {
                    config.dontNotifyFriendList.add(ja.getString(i));
                }
            }
            config.starGameHighScore = jo.optBoolean(jn_starGameHighScore, true);
            config.jumpGameHighScore = jo.optBoolean(jn_jumpGameHighScore, true);
            config.playFarmGame = jo.optBoolean(jn_playFarmGame, true);
            config.acceptGift = jo.optBoolean(jn_acceptGift, true);
            config.visitFriendList = new ArrayList();
            ja = jo.optJSONArray(jn_visitFriendList);
            if (ja != null) {
                for (int i = 0; i < ja.length(); i++) {
                    config.visitFriendList.add(ja.getString(i));
                }
            }
            config.collectManurePot = jo.optBoolean(jn_collectManurePot, true);
            config.triggerTbTask = jo.optBoolean(jn_triggerTbTask, true);
            config.orchardSpreadManure = jo.optBoolean(jn_orchardSpreadManure, true);
            config.hireAnimal = jo.optBoolean(jn_hireAnimal, true);
            config.receivePoint = jo.optBoolean(jn_receivePoint, true);
            config.openTreasureBox = jo.optBoolean(jn_openTreasureBox, true);
            config.donateCharityCoin = jo.optBoolean(jn_donateCharityCoin, true);
            config.minExchangeCount = jo.optInt(jn_minExchangeCount);
            config.latestExchangeTime = jo.optInt(jn_latestExchangeTime, 21);
            config.kbSignIn = jo.optBoolean(jn_kbSignIn, true);
            config.collectCreditFeedback = jo.optBoolean(jn_collectCreditFeedback, true);
            config.zmDonate = jo.optBoolean(jn_zmDonate, true);
            config.electricSignIn = jo.optBoolean(jn_electricSignIn, true);
            config.greatyouthReceive = jo.optBoolean(jn_greatyouthReceive, true);
            config.addFriendGroupList = new ArrayList();
            ja = jo.optJSONArray(jn_addFriendGroupList);
            if (ja != null) {
                for (int i = 0; i < ja.length(); i++) {
                    config.addFriendGroupList.add(ja.getString(i));
                }
            }
            config.openDoorSignIn = jo.optBoolean(jn_openDoorSignIn, true);
            config.hookStep = jo.optInt(jn_hookStep, RandomUtils.nextInt(20000, 30000));
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
            if (json != null) {
                Log.i(TAG, "配置文件格式有误，已重置配置文件并备份原文件");
                FileUtils.write2File(json, FileUtils.getBackupFile(FileUtils.getConfigFile()));
            }
            config = defInit();
        }
        String formatted = config2Json(config);
        if (!formatted.equals(json)) {
            Log.i(TAG, "重新格式化 config.json");
            FileUtils.write2File(formatted, FileUtils.getConfigFile());
        }
        return config;
    }

    public static void setAdvanceTime(int advanceTime) {
        getConfig().advanceTime = advanceTime;
        hasChanged = true;
    }

    public static void setRecordRuntimeLog(Boolean recordRuntimeLog) {
        getConfig().recordRuntimeLog = recordRuntimeLog;
        hasChanged = true;
    }

    public static void setAutoRestart(RestartType autoRestart) {
        getConfig().autoRestart = autoRestart;
        hasChanged = true;
    }

    public static void setAcceptGift(boolean acceptGift) {
        getConfig().acceptGift = acceptGift;
        hasChanged = true;
    }

    public static boolean getAcceptGift() {
        return getConfig().acceptGift;
    }

    public static int getAdvanceTime() {
        return getConfig().advanceTime;
    }

    public static void setCheckInterval(int checkInterval) {
        getConfig().checkInterval = checkInterval;
        hasChanged = true;
    }

    public static void setPin(String pin) {
        getConfig().pin = pin;
        hasChanged = true;
    }

    public static void setAutoRestart2(RestartType autoRestart2) {
        getConfig().autoRestart2 = autoRestart2;
        hasChanged = true;
    }

    public static void setAnswerQuestion(boolean answerQuestion) {
        getConfig().answerQuestion = answerQuestion;
        hasChanged = true;
    }

    public static void setCollectInterval(int collectInterval) {
        getConfig().collectInterval = collectInterval;
        hasChanged = true;
    }

    public static void setXedgeproData(String xedgeproData) {
        getConfig().xedgeproData = xedgeproData;
        hasChanged = true;
    }

    public static void setCollectCreditFeedback(boolean collectCreditFeedback) {
        getConfig().collectCreditFeedback = collectCreditFeedback;
        hasChanged = true;
    }

    public static boolean getAnswerQuestion() {
        return getConfig().answerQuestion;
    }

    public static RestartType getAutoRestart() {
        return getConfig().autoRestart;
    }

    public static void setCollectTimeout(int collectTimeout) {
        getConfig().collectTimeout = collectTimeout;
        hasChanged = true;
    }

    public static void setCollectEnergy(boolean collectEnergy) {
        getConfig().collectEnergy = collectEnergy;
        hasChanged = true;
    }

    public static RestartType getAutoRestart2() {
        return getConfig().autoRestart2;
    }

    public static void setHookStep(int hookStep) {
        getConfig().hookStep = hookStep;
        hasChanged = true;
    }

    public static void setCollectManurePot(boolean collectManurePot) {
        getConfig().collectManurePot = collectManurePot;
        hasChanged = true;
    }

    public static int getCheckInterval() {
        Calendar calendar = Calendar.getInstance();
        int i1 = calendar.get(11);
        int i2 = calendar.get(12);
        if (((i1 == 6) && (i2 > 50)) || ((i1 == 7) && (i2 < 40))) {
            return Math.min(getConfig().checkInterval, 120000);
        }
        if ((i1 == 23) && (i2 + getConfig().checkInterval / 60000 >= 58)) {
            calendar.add(5, 1);
            calendar.set(11, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            return (int) (calendar.getTimeInMillis() - System.currentTimeMillis()) + 2000;
        }
        return getConfig().checkInterval;
    }

    public static void setLatestExchangeTime(int latestExchangeTime) {
        getConfig().latestExchangeTime = latestExchangeTime;
        hasChanged = true;
    }

    public static void setCollectWateringEnergy(boolean collectWateringEnergy) {
        getConfig().collectWateringEnergy = collectWateringEnergy;
        hasChanged = true;
    }

    public static void setMinExchangeCount(int minExchangeCount) {
        getConfig().minExchangeCount = minExchangeCount;
        hasChanged = true;
    }

    public static void setCooperateWater(boolean cooperateWater) {
        getConfig().cooperateWater = cooperateWater;
        hasChanged = true;
    }

    public static boolean getCollectCreditFeedback() {
        return getConfig().collectCreditFeedback;
    }

    public static void setRecallAnimalType(int index) {
        getConfig().recallAnimalType = RecallAnimalType.values()[index];
        hasChanged = true;
    }

    public static void setDelayHelpFriendCollect(boolean delayHelpFriendCollect) {
        getConfig().delayHelpFriendCollect = delayHelpFriendCollect;
        hasChanged = true;
    }

    public static boolean getCollectEnergy() {
        return getConfig().collectEnergy;
    }

    public static int getCollectInterval() {
        return getConfig().collectInterval;
    }

    public static void setReturnWater10(int returnWater10) {
        getConfig().returnWater10 = returnWater10;
        hasChanged = true;
    }

    public static void setDeleteStranger(boolean deleteStranger) {
        getConfig().deleteStranger = deleteStranger;
        hasChanged = true;
    }

    public static void setReturnWater20(int returnWater20) {
        getConfig().returnWater20 = returnWater20;
        hasChanged = true;
    }

    public static void setDonateCharityCoin(boolean donateCharityCoin) {
        getConfig().donateCharityCoin = donateCharityCoin;
        hasChanged = true;
    }

    public static boolean getCollectManurePot() {
        return getConfig().collectManurePot;
    }

    public static int getCollectTimeout() {
        return getConfig().collectTimeout;
    }

    public static void setReturnWater30(int returnWater30) {
        getConfig().returnWater30 = returnWater30;
        hasChanged = true;
    }

    public static void setDonation(boolean donation) {
        getConfig().donation = donation;
        hasChanged = true;
    }

    public static void setSendType(int index) {
        getConfig().sendType = AntFarm.SendType.values()[index];
        hasChanged = true;
    }

    public static void setElectricSignIn(boolean electricSignIn) {
        getConfig().electricSignIn = electricSignIn;
        hasChanged = true;
    }

    public static boolean getCollectWateringEnergy() {
        return getConfig().collectWateringEnergy;
    }

    public static void setThreadCount(int threadCount) {
        getConfig().threadCount = threadCount;
        hasChanged = true;
    }

    public static void setEnableFarm(boolean enableFarm) {
        getConfig().enableFarm = enableFarm;
        hasChanged = true;
    }

    public static boolean getCooperateWater() {
        return getConfig().cooperateWater;
    }

    public static Config defInit() {
        Config c = new Config();
        c.immediateEffect = true;
        c.recordLog = true;
        c.recordRuntimeLog = false;
        c.showToast = true;
        c.stayAwake = false;
        c.restartService = true;
        RestartType restartType = RestartType.APP;
        c.autoRestart = restartType;
        c.autoRestart2 = restartType;
        c.pin = "";
        c.sendXedgepro = true;
        c.xedgeproData = "";
        c.originalMode = false;
        c.deleteStranger = false;
        c.enable = true;
        if (c.smallAccountList == null) {
            c.smallAccountList = new ArrayList();
        }
        c.collectEnergy = true;
        c.collectWateringEnergy = true;
        c.checkInterval = 300000;
        c.threadCount = 1;
        c.advanceTime = 150;
        c.collectInterval = 150;
        c.collectTimeout = 2000;
        c.returnWater30 = 0;
        c.returnWater20 = 0;
        c.returnWater10 = 0;
        c.helpFriendCollect = true;
        c.delayHelpFriendCollect = false;
        c.helpFriendCollectWatering = true;
        if (c.dontCollectList == null) {
            c.dontCollectList = new ArrayList();
        }
        if (c.dontHelpCollectList == null) {
            c.dontHelpCollectList = new ArrayList();
        }
        c.receiveForestTaskAward = true;
        if (c.waterFriendList == null) {
            c.waterFriendList = new ArrayList();
        }
        if (c.waterCountList == null) {
            c.waterCountList = new ArrayList();
        }
        c.cooperateWater = true;
        if (c.cooperateWaterList == null) {
            c.cooperateWaterList = new ArrayList();
        }
        if (c.cooperateWaterNumList == null) {
            c.cooperateWaterNumList = new ArrayList();
        }
        c.grabPacket = true;
        c.enableFarm = true;
        c.rewardFriend = true;
        c.sendBackAnimal = true;
        c.sendType = AntFarm.SendType.HIT;
        if (c.dontSendFriendList == null) {
            c.dontSendFriendList = new ArrayList();
        }
        c.recallAnimalType = RecallAnimalType.ALWAYS;
        c.receiveFarmToolReward = true;
        c.useNewEggTool = true;
        c.harvestProduce = true;
        c.donation = true;
        c.answerQuestion = true;
        c.receiveFarmTaskAward = true;
        c.feedAnimal = true;
        c.useAccelerateTool = true;
        if (c.feedFriendAnimalList == null) {
            c.feedFriendAnimalList = new ArrayList();
        }
        if (c.feedFriendAnimalNumList == null) {
            c.feedFriendAnimalNumList = new ArrayList();
        }
        c.notifyFriend = true;
        if (c.dontNotifyFriendList == null) {
            c.dontNotifyFriendList = new ArrayList();
        }
        c.starGameHighScore = true;
        c.jumpGameHighScore = true;
        c.playFarmGame = true;
        c.acceptGift = true;
        if (c.visitFriendList == null) {
            c.visitFriendList = new ArrayList();
        }
        c.collectManurePot = true;
        c.triggerTbTask = true;
        c.orchardSpreadManure = true;
        c.hireAnimal = true;
        c.receivePoint = true;
        c.openTreasureBox = true;
        c.donateCharityCoin = true;
        c.kbSignIn = true;
        c.collectCreditFeedback = true;
        c.zmDonate = true;
        c.electricSignIn = true;
        c.greatyouthReceive = true;
        if (c.addFriendGroupList == null) {
            c.addFriendGroupList = new ArrayList();
        }
        c.openDoorSignIn = true;
        c.hookStep = RandomUtils.nextInt(20000, 30000);
        FileUtils.write2File(config2Json(c), FileUtils.getConfigFile());
        return c;
    }

    public static void setFeedAnimal(boolean feedAnimal) {
        getConfig().feedAnimal = feedAnimal;
        hasChanged = true;
    }

    public static void setGrabPacket(boolean grabPacket) {
        getConfig().grabPacket = grabPacket;
        hasChanged = true;
    }

    public static boolean getDelayHelpFriendCollect() {
        return getConfig().delayHelpFriendCollect;
    }

    public static void setGreatyouthReceive(boolean greatyouthReceive) {
        getConfig().greatyouthReceive = greatyouthReceive;
        hasChanged = true;
    }

    public static boolean getDeleteStranger() {
        return getConfig().deleteStranger;
    }

    public static void setHarvestProduce(boolean harvestProduce) {
        getConfig().harvestProduce = harvestProduce;
        hasChanged = true;
    }

    public static boolean getDonateCharityCoin() {
        return getConfig().donateCharityCoin;
    }

    public static void setHelpFriendCollect(boolean helpFriendCollect) {
        getConfig().helpFriendCollect = helpFriendCollect;
        hasChanged = true;
    }

    public static boolean getDonation() {
        return getConfig().donation;
    }

    public static void setHelpFriendCollectWatering(boolean helpFriendCollectWatering) {
        getConfig().helpFriendCollectWatering = helpFriendCollectWatering;
        hasChanged = true;
    }

    public static boolean getElectricSignIn() {
        return getConfig().electricSignIn;
    }

    public static void setHireAnimal(boolean hireAnimal) {
        getConfig().hireAnimal = hireAnimal;
        hasChanged = true;
    }

    public static boolean getEnableFarm() {
        return getConfig().enableFarm;
    }

    public static void setImmediateEffect(boolean immediateEffect) {
        getConfig().immediateEffect = immediateEffect;
        hasChanged = true;
    }

    public static boolean getFeedAnimal() {
        return getConfig().feedAnimal;
    }

    public static void setJumpGameHighScore(boolean jumpGameHighScore) {
        getConfig().jumpGameHighScore = jumpGameHighScore;
        hasChanged = true;
    }

    public static boolean enableOpenDoor() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return (hour == 6 && minute > 50) || (hour == 7 && minute < 40);
    }

    public static List<String> getAddFriendGroupList() {
        return getConfig().addFriendGroupList;
    }

    public static void setKbSignIn(boolean kbSignIn) {
        getConfig().kbSignIn = kbSignIn;
        hasChanged = true;
    }

    private static Config getConfig() {
        if (config == null || (shouldReload && config.immediateEffect)) {
            shouldReload = false;
            if (FileUtils.getConfigFile().exists()) {
                config = json2Config(FileUtils.readFromFile(FileUtils.getConfigFile()));
            } else {
                config = defInit();
            }
        }
        return config;
    }

    public static void setNotifyFriend(boolean notifyFriend) {
        getConfig().notifyFriend = notifyFriend;
        hasChanged = true;
    }

    public static List<String> getCooperateWaterList() {
        return getConfig().cooperateWaterList;
    }

    public static void setOpenDoorSignIn(boolean openDoorSignIn) {
        getConfig().openDoorSignIn = openDoorSignIn;
        hasChanged = true;
    }

    public static List<String> getDontCollectList() {
        return getConfig().dontCollectList;
    }

    public static void setOpenTreasureBox(boolean openTreasureBox) {
        getConfig().openTreasureBox = openTreasureBox;
        hasChanged = true;
    }

    public static List<String> getDontHelpCollectList() {
        return getConfig().dontHelpCollectList;
    }

    public static void setOrchardSpreadManure(boolean orchardSpreadManure) {
        getConfig().orchardSpreadManure = orchardSpreadManure;
        hasChanged = true;
    }

    public static List<String> getDontNotifyFriendList() {
        return getConfig().dontNotifyFriendList;
    }

    public static void setOriginalMode(boolean paramBoolean) {
        getConfig().originalMode = paramBoolean;
        hasChanged = true;
    }

    public static List<String> getDontSendFriendList() {
        return getConfig().dontSendFriendList;
    }

    public static void setPlayFarmGame(boolean playFarmGame) {
        getConfig().playFarmGame = playFarmGame;
        hasChanged = true;
    }

    public static List<String> getFeedFriendAnimalList() {
        return getConfig().feedFriendAnimalList;
    }

    public static void setReceiveFarmTaskAward(boolean paramBoolean) {
        getConfig().receiveFarmTaskAward = paramBoolean;
        hasChanged = true;
    }

    public static List<Integer> getFeedFriendAnimalNumList() {
        return getConfig().feedFriendAnimalNumList;
    }

    public static void setReceiveFarmToolReward(boolean receiveFarmToolReward) {
        getConfig().receiveFarmToolReward = receiveFarmToolReward;
        hasChanged = true;
    }

    public static List<String> getVisitFriendList() {
        return getConfig().visitFriendList;
    }

    public static void setReceiveForestTaskAward(boolean receiveForestTaskAward) {
        getConfig().receiveForestTaskAward = receiveForestTaskAward;
        hasChanged = true;
    }

    public static List<Integer> getWaterFriendNumList() {
        return getConfig().waterCountList;
    }

    public static void setReceivePoint(boolean receivePoint) {
        getConfig().receivePoint = receivePoint;
        hasChanged = true;
    }

    public static List<String> getWaterFriendList() {
        return getConfig().waterFriendList;
    }

    public static void setRecordLog(boolean recordLog) {
        getConfig().recordLog = recordLog;
        hasChanged = true;
    }

    public static List<Integer> getCooperateWaterNumList() {
        return getConfig().cooperateWaterNumList;
    }

    public static void setRestartService(boolean restartService) {
        getConfig().restartService = restartService;
        hasChanged = true;
    }

    public static void setRewardFriend(boolean rewardFriend) {
        getConfig().rewardFriend = rewardFriend;
        hasChanged = true;
    }

    public static boolean getGrabPacket() {
        return getConfig().grabPacket;
    }

    public static void setSendBackAnimal(boolean sendBackAnimal) {
        getConfig().sendBackAnimal = sendBackAnimal;
        hasChanged = true;
    }

    public static boolean getGreatyouthReceive() {
        return getConfig().greatyouthReceive;
    }

    public static void setSendXedgepro(boolean sendXedgepro) {
        getConfig().sendXedgepro = sendXedgepro;
        hasChanged = true;
    }

    public static boolean getHarvestProduce() {
        return getConfig().harvestProduce;
    }

    public static void setShowToast(boolean showToast) {
        getConfig().showToast = showToast;
        hasChanged = true;
    }

    public static boolean getHelpFriendCollect() {
        return getConfig().helpFriendCollect;
    }

    public static void setStarGameHighScore(boolean starGameHighScore) {
        getConfig().starGameHighScore = starGameHighScore;
        hasChanged = true;
    }

    public static boolean getHelpFriendCollectWatering() {
        return getConfig().helpFriendCollectWatering;
    }

    public static void setStayAwake(boolean stayAwake) {
        getConfig().stayAwake = stayAwake;
        hasChanged = true;
    }

    public static boolean getHireAnimal() {
        return getConfig().hireAnimal;
    }

    public static int getHookStep() {
        return getConfig().hookStep;
    }

    public static void setTriggerTbTask(boolean triggerTbTask) {
        getConfig().triggerTbTask = triggerTbTask;
        hasChanged = true;
    }

    public static void setUseAccelerateTool(boolean useAccelerateTool) {
        getConfig().useAccelerateTool = useAccelerateTool;
        hasChanged = true;
    }

    public static boolean getImmediateEffect() {
        return getConfig().immediateEffect;
    }

    public static void setUseNewEggTool(boolean useNewEggTool) {
        getConfig().useNewEggTool = useNewEggTool;
        hasChanged = true;
    }

    public static boolean getJumpGameHighScore() {
        return getConfig().jumpGameHighScore;
    }

    public static void setZmDonate(boolean zmDonate) {
        getConfig().zmDonate = zmDonate;
        hasChanged = true;
    }

    public static boolean getKbSignIn() {
        return getConfig().kbSignIn;
    }

    public static int getLatestExchangeTime() {
        return getConfig().latestExchangeTime;
    }

    public static int getMinExchangeCount() {
        return getConfig().minExchangeCount;
    }

    public static boolean getNotifyFriend() {
        return getConfig().notifyFriend;
    }

    public static boolean getOpenDoorSignIn() {
        return getConfig().openDoorSignIn;
    }

    public static boolean getOpenTreasureBox() {
        return getConfig().openTreasureBox;
    }

    public enum RecallAnimalType {
        ALWAYS, WHEN_THIEF, WHEN_HUNGRY, NEVER;
        public static final CharSequence[] names =
                {ALWAYS.name(), WHEN_THIEF.name(), WHEN_HUNGRY.name(), NEVER.name()};
        public static final CharSequence[] nickNames =
                {"始终召回", "作贼时召回", "饥饿时召回", "不召回"};

        public CharSequence nickName() {
            return nickNames[ordinal()];
        }
    }

    public enum RestartType {
        DISABLED, APP, ROOT, SERVICE;
    }
}