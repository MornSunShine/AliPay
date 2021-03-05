package morn.xposed.alipay;

import android.os.PowerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import morn.xposed.alipay.hook.AntForestRpcCall;
import morn.xposed.alipay.hook.FriendsManager;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.ui.AliFriend;
import morn.xposed.alipay.ui.ForestUser;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.FileUtils;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;
import morn.xposed.alipay.util.Statistics;

public class AntForest {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠃ";
    private static String selfId;
    private static int collectedEnergy = 0;
    private static int helpCollectedEnergy = 0;
    private static int totalCollected = 0;
    private static int totalHelpCollected = 0;
    private static int collectTaskCount = 0;
    private static long serverTime = -1;
    private static long offsetTime = -1;
    private static long laterTime = -1;
    private static ConcurrentHashMap<String, Long> timemap1 = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, Long> timemap2 = new ConcurrentHashMap();
    private static volatile boolean abc = false;
    private static Set<String> bubbleSet = new HashSet<>();

    private static int forFriendCollectEnergy(ClassLoader loader, String targetUserId, long bubbleId, String bizeType, String userName) {
        int helped = 0;
        try {
            String s = AntForestRpcCall.rpcCall_forFriendCollectEnergy(loader, targetUserId, bubbleId, bizeType);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                JSONArray jaBubbles = jo.getJSONArray("bubbles");
                for (int i = 0; i < jaBubbles.length(); i++) {
                    jo = jaBubbles.getJSONObject(i);
                    helped += jo.getInt("collectedEnergy");
                }
                if (helped > 0) {
                    Log.forest("帮【" + userName + "】收取【" + helped + "克】");
                    helpCollectedEnergy += helped;
                    totalHelpCollected += helped;
                    Statistics.addData(Statistics.DataType.HELPED, helped);
                } else {
                    Log.recordLog("帮【" + userName + "】收取失败", "，UserID：" + targetUserId + "，BubbleId" + bubbleId);
                }
            } else {
                Log.recordLog("【" + userName + "】" + jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "forFriendCollectEnergy err:");
            Log.printStackTrace(TAG, t);
        }
        return helped;
    }

    private static int returnFriendWater(ClassLoader loader, String userId, String userName, String bizNo, int energyId, int count, String packetNo) {
        if (bizNo == null || bizNo.isEmpty()) return 0;
        int wateredTimes = 0;
        try {
            String s;
            JSONObject jo;
            for (int waterCount = 1; waterCount <= count; waterCount++) {
                s = AntForestRpcCall.rpcCall_transferEnergy(loader, userId, bizNo, waterCount, energyId, packetNo);
                jo = new JSONObject(s);
                if (jo.getString("resultCode").equals("SUCCESS")) {
                    s = jo.getJSONObject("treeEnergy").getString("currentEnergy");
                    Log.forest("给【" + userName + "】浇水成功，剩余能量【" + s + "克】");
                    wateredTimes++;
                    int collected;
                    switch (energyId) {
                        case 42:
                            collected = 66;
                            break;
                        case 41:
                            collected = 33;
                            break;
                        case 40:
                            collected = 18;
                            break;
                        case 39:
                            collected = 10;
                            break;
                        case 38:
                            collected = 5;
                            break;
                        case 37:
                            collected = 1;
                            break;
                        default:
                            collected = 0;
                    }
                    Statistics.addData(Statistics.DataType.WATERED, collected);
                    if (packetNo != null) {
                        if (jo.has("postHandlerVOMap")) {
                            double redPacket = jo.optJSONObject("postHandlerVOMap")
                                    .optJSONObject("redPacket")
                                    .getJSONObject("openVO")
                                    .getDouble("amount");
                            Log.other("给【" + userName + "】浇水" + collected + "克，获得" + redPacket + "元红包");
                        }
                    }
                } else if (s.equals("WATERING_TIMES_LIMIT")) {
                    Log.recordLog("今日给【" + userName + "】浇水已达上限", "");
                    wateredTimes = 3;
                    break;
                } else {
                    Log.recordLog(jo.getString("resultDesc"), jo.toString());
                }
                Thread.sleep(2000);
            }
        } catch (Throwable t) {
            Log.i(TAG, "returnFriendWater err:");
            Log.printStackTrace(TAG, t);
        }
        return wateredTimes;
    }

    private static void setLaterTime(long time) {
        Log.i(TAG, "能量成熟时间：" + time);
        if (time > serverTime && serverTime > 0
                && (laterTime < 0 || time < laterTime)) {
            laterTime = time;
            Log.i(TAG, laterTime - serverTime + "ms 后能量成熟");
        }
    }

    private static void waterFriendEnergy(ClassLoader loader, String userId, int count) {
        try {
            String s = AntForestRpcCall.rpcCall_queryFriendHomePage(loader, userId);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                String bizNo = jo.getString("bizNo");
                String userName = FriendIdMap.getNameById(userId);
                jo.getJSONObject("userEnergy");
                count = returnFriendWater(loader, userId, userName, bizNo, 42, count, null);
                if (count > 0) {
                    Statistics.waterFriendToday(userId, count);
                }
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "waterFriendEnergy err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public static void execute(ClassLoader loader, String userName, String userId, String bizNo, long bubbleId, long produceTime) {
        Config.getThreadCount();
        int count = 1;
        if (bizNo == null) {
            count = 2;
        }
        for (int i = 0; i < count; i++) {
            BubbleTimerTask bbt = new BubbleTimerTask(loader, userName, userId, bizNo, bubbleId, produceTime, i);
            long delay = bbt.calculateDelayTime();
            if (Config.getOriginalMode()) {
                bbt.start();
            } else {
                AntBroadcastReceiver.offerTask(bbt, delay);
            }
            collectTaskCount++;
            Log.recordLog("【" + userName + "】" + delay / 1000L + "秒后尝试收取能量", "");
        }
    }

    private static void canCollectEnergy(ClassLoader loader, String userId, boolean laterCollect) {
        try {
            long time = timemap1.get(userId);
            if (System.currentTimeMillis() < time - 60000L) {
                return;
            }
            timemap2.remove(userId);
            long start = System.currentTimeMillis();
            String s = AntForestRpcCall.rpcCall_queryFriendHomePage(loader, userId);
            JSONObject jo = new JSONObject(s);
            long end = System.currentTimeMillis();
            if (jo.getString("resultCode").equals("SUCCESS")) {
                serverTime = jo.getLong("now");
                offsetTime = (start + end) / 2 - serverTime;
                Log.i(TAG, "服务器时间：" + serverTime + "，本地减服务器时间差：" + offsetTime);
                String bizNo = jo.getString("bizNo");
                JSONArray jaProps = jo.getJSONArray("usingUserProps");
                JSONArray jaBubbles;
                if (jo.has("bubbles")) {
                    jaBubbles = jo.getJSONArray("bubbles");
                } else {
                    jaBubbles = new JSONArray();
                }
                JSONArray jaWateringBubbles;
                if (jo.has("wateringBubbles")) {
                    jaWateringBubbles = jo.getJSONArray("wateringBubbles");
                } else {
                    jaWateringBubbles = new JSONArray();
                }
                jo = jo.getJSONObject("userEnergy");
                FriendIdMap.putMap(userId, null, jo.optString("displayName"), jo.optString("loginId"));
                String userName = FriendIdMap.getNameById(userId);
                Log.recordLog("进入【" + userName + "】的蚂蚁森林", "");
                FriendIdMap.saveIdMap();
                if (Config.getGrabPacket() && Statistics.canWaterFriendToday(userId, 3) && jo.has("redPacketVO")) {
                    String packNo = jo.getJSONObject("redPacketVO").optString("packetNo");
                    if (packNo != null) {
                        returnFriendWater(loader, userId, userName, bizNo, 39, 1, packNo);
                    }
                }
                for (int i = 0; i < jaProps.length(); i++) {
                    JSONObject joProps = jaProps.getJSONObject(i);
                    if ("energyShield".equals(joProps.getString("type"))) {
                        if (joProps.getLong("endTime") > serverTime) {
                            Log.recordLog("【" + userName + "】被能量罩保护着哟", "");
                            return;
                        }
                    }
                }
                int collected = 0;
                int helped = 0;
                for (int i = 0; i < jaBubbles.length(); i++) {
                    jo = jaBubbles.getJSONObject(i);
                    long bubbleId = jo.getLong("id");
                    switch (CollectStatus.valueOf(jo.getString("collectStatus"))) {
                        case AVAILABLE:
                            if (Config.getDontCollectList().contains(userId))
                                Log.recordLog("不偷取【" + userName + "】", ", userId=" + userId);
                            else
                                collected += collectEnergy(loader, userId, bubbleId, userName, bizNo, null);
                            break;
                        case WAITING:
                            if (!laterCollect || Config.getDontCollectList().contains(userId))
                                break;
                            long produceTime = jo.getLong("produceTime");
                            if (produceTime - serverTime < Config.getCheckInterval() * 2)
                                execute(loader, userName, userId, bizNo, bubbleId, produceTime);
                            else
                                setLaterTime(produceTime);
                            break;
                    }
                    if (jo.getBoolean("canHelpCollect")) {
                        boolean flag = false;
                        long overTime = jo.optLong("overTime");
                        if (Config.getHelpFriendCollect()) {
                            if (Config.getDontHelpCollectList().contains(userId))
                                Log.recordLog("不帮收【" + userName + "】", ", userId=" + userId);
                            else {
                                if (!Config.getDelayHelpFriendCollect()) {
                                    helped += forFriendCollectEnergy(loader, userId, bubbleId, null, userName);
                                } else {
                                    Calendar overTimeC = Calendar.getInstance();
                                    overTimeC.setTimeInMillis(overTime);
                                    if (Calendar.getInstance().get(Calendar.DATE) == overTimeC.get(Calendar.DATE)) {
                                        helped += forFriendCollectEnergy(loader, userId, bubbleId, null, userName);
                                        flag = true;
                                    }
                                }
                            }
                        } else {
                            Log.recordLog("不帮收【" + userName + "】", ", userId=" + userId);
                        }
                        if (Config.getDelayHelpFriendCollect() && !flag) {
                            long time1 = timemap2.get(userId);
                            if (time1 != 0) {
                                timemap2.put(userId, overTime);
                            } else {
                                timemap2.put(userId, Math.min(overTime, time1));
                            }
                        }
                    }
                }
                if (Config.getHelpFriendCollectWatering()) {
                    for (int i = 0; i < jaWateringBubbles.length(); i++) {
                        JSONObject joBubble = jaWateringBubbles.getJSONObject(i);
                        if (joBubble.optBoolean("canHelpCollect")) {
                            joBubble.getLong("id");
                            if (Config.getDontHelpCollectList().contains(userId)) {
                                Log.recordLog("不帮收【" + userName + "】", ", userId=" + userId);
                            } else {
                                helped += forFriendCollectEnergy(loader, userId, start, "jiaoshui", userName);
                                Thread.sleep(RandomUtils.delay());
                            }
                        }
                    }
                }
                Thread.sleep(RandomUtils.nextInt(80, 150));
                if (helped > 0) {
                    canCollectEnergy(loader, userId, false);
                }
                collectedEnergy += collected;
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "canCollectEnergy err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public static void removeIds(String ids) {
        File rankFile = FileUtils.getRankFile();
        String s = FileUtils.readFromFile(rankFile);
        if (s != null) {
            try {
                JSONObject jo = new JSONObject(s);
                JSONArray ja = jo.getJSONObject(FriendIdMap.currentUid).getJSONArray("rank");
                HashSet<String> set = new HashSet<>();
                Collections.addAll(set, ids.split(","));
                for (int i = 0; i < ja.length(); i++) {
                    if (set.contains(ja.getJSONObject(i).getString("id"))) {
                        ja.remove(i);
                        i -= 1;
                    }
                }
                FileUtils.write2File(jo.toString(), rankFile);
                for (String id : set) {
                    FriendIdMap.removeIdMap(id);
                }
                FriendIdMap.saveIdMap();
            } catch (Throwable t) {
                Log.i(TAG, ids);
            }
        }
    }

    private static int collectEnergy(ClassLoader loader, String userId, long bubbleId, String userName, String bizNo, String bizType) {
        int collected = 0;
        try {
            String s = AntForestRpcCall.rpcCall_collectEnergy(loader, userId, bubbleId, bizType);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                JSONArray jaBubbles = jo.getJSONArray("bubbles");
                for (int i = 0; i < jaBubbles.length(); i++) {
                    jo = jaBubbles.getJSONObject(i);
                    collected += jo.getInt("collectedEnergy");
                }
                if (collected > 0) {
                    totalCollected += collected;
                    Statistics.addData(Statistics.DataType.COLLECTED, collected);
                    String str = "偷取【" + userName + "】的能量【" + collected + "克】";
                    Log.forest(str);
                    AntForestToast.show(str);
                } else {
                    Log.recordLog("偷取【" + userName + "】的能量失败", "，UserID：" + userId + "，BubbleId：" + bubbleId);
                }
                if (bizNo == null || bizNo.isEmpty())
                    return collected;
                int returnCount = 0;
                if (Config.getReturnWater30() > 0 && collected >= Config.getReturnWater30())
                    returnCount = 3;
                else if (Config.getReturnWater20() > 0 && collected >= Config.getReturnWater20())
                    returnCount = 2;
                else if (Config.getReturnWater10() > 0 && collected >= Config.getReturnWater10())
                    returnCount = 1;
                if (returnCount > 0)
                    returnFriendWater(loader, userId, userName, bizNo, 39, returnCount, null);
            } else {
                Log.recordLog("【" + userName + "】" + jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "collectEnergy err:");
            Log.printStackTrace(TAG, t);
        }
        return collected;
    }

    private static void canCollectSelfEnergy(ClassLoader loader, int times) {
        try {
            long start = System.currentTimeMillis();
            String s = AntForestRpcCall.rpcCall_queryHomePage(loader);
            long end = System.currentTimeMillis();
            if (s == null) {
                Thread.sleep(RandomUtils.delay());
                start = System.currentTimeMillis();
                s = AntForestRpcCall.rpcCall_queryHomePage(loader);
                end = System.currentTimeMillis();
            }
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                serverTime = jo.getLong("now");
                offsetTime = (start + end) / 2L - serverTime;
                Log.i(TAG, "服务器时间：" + serverTime + "，本地减服务器时间差：" + offsetTime);
                JSONArray jaBubbles = jo.getJSONArray("bubbles");
                JSONArray wateringBubbles;
                if (jo.has("wateringBubbles")) {
                    wateringBubbles = jo.getJSONArray("wateringBubbles");
                } else {
                    wateringBubbles = new JSONArray();
                }
                boolean more = jo.optBoolean("waterBubbleHasMore");
                jo = jo.getJSONObject("userEnergy");
                selfId = jo.getString("userId");
                String selfName = jo.getString("displayName");
                FriendIdMap.putMap(selfId, null, selfName, null);
                String str = FriendIdMap.getNameById(selfId);
                Log.recordLog("进入【" + str + "】的蚂蚁森林", "");
                FriendIdMap.saveIdMap();
                if (Config.getCollectEnergy()) {
                    if (!Config.getSmallAccountList().contains(FriendIdMap.currentUid)) {
                        for (int i = 0; i < jaBubbles.length(); i++) {
                            jo = jaBubbles.getJSONObject(i);
                            long bubbleId = jo.getLong("id");
                            switch (CollectStatus.valueOf(jo.getString("collectStatus"))) {
                                case AVAILABLE:
                                    if (Config.getDontCollectList().contains(selfId))
                                        Log.recordLog("不偷取【" + selfName + "】", ", userId=" + selfId);
                                    else
                                        collectedEnergy += collectEnergy(loader, selfId, bubbleId, selfName, null, null);
                                    break;

                                case WAITING:
                                    if (Config.getDontCollectList().contains(selfId) || times == -1)
                                        break;
                                    long produceTime = jo.getLong("produceTime");
                                    if (produceTime - serverTime < Config.getCheckInterval())
                                        execute(loader, selfName, selfId, null, bubbleId, produceTime);
                                    else
                                        setLaterTime(produceTime);
                                    break;
                            }
                        }
                    } else {
                        for (int i = 0; i < wateringBubbles.length(); i++) {
                            long id = wateringBubbles.getJSONObject(i).getLong("id");
                            collectedEnergy += collectEnergy(loader, selfId, start, str, null, "jiaoshui");
                        }
                        if (more) {
                            canCollectSelfEnergy(loader, -1);
                        }
                    }
                }
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
            }
            if (times == 0) {
                receiveTaskAward(loader);
                for (int i = 0; i < Config.getWaterFriendList().size(); i++) {
                    String uid = Config.getWaterFriendList().get(i);
                    if (selfId.equals(uid)) continue;
                    int waterCount = Config.getWaterFriendNumList().get(i);
                    if (waterCount <= 0) continue;
                    if (waterCount > 3) waterCount = 3;
                    if (Statistics.canWaterFriendToday(uid, waterCount))
                        waterFriendEnergy(loader, uid, waterCount);
                }
                praiseEnergy(loader);
            }
        } catch (Throwable t) {
            Log.i(TAG, "canCollectSelfEnergy err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static void onForestEnd(ClassLoader loader) {
        FriendIdMap.saveIdMap();
        collectedEnergy = 0;
        helpCollectedEnergy = 0;
        if (Config.getSmallAccountList().contains(FriendIdMap.currentUid)) {
            AntForestNotification.setContentText("小号不收取能量");
        } else if (Config.getCollectEnergy()) {
            Log.recordLog(
                    "收【" + collectedEnergy + "克】，帮【"
                            + helpCollectedEnergy + "克】，"
                            + collectTaskCount + "个蹲点任务", "");
            StringBuilder sb = new StringBuilder();
            sb.append("  收：" + totalCollected + "，帮：" + totalHelpCollected);
            if (laterTime > 0L) {
                sb.append("，下个：");
                long second = (laterTime - serverTime) / 1000;
                long minute = second / 60;
                second %= 60;
                long hour = minute / 60;
                minute %= 60;
                if (hour > 0) sb.append(hour + "时");
                if (minute > 0) sb.append(minute + "分");
                sb.append(second + "秒");
            }
            sb.append("\n");
            Log.recordLog(sb.toString(), "");
            AntForestNotification.setContentText(Log.getFormatTime() + " " + sb.toString());
        } else {
            AntForestNotification.setContentText("未打开收取能量");
        }
        laterTime = -1;
    }

    public static void checkEnergyRanking(ClassLoader loader, int times) {
        Log.recordLog("定时检测开始", "");
        AntForestNotification.setContentText("正在检测森林...");
        Thread thread = new Thread() {
            ClassLoader loader;
            int times;

            @Override
            public void run() {
                PowerManager.WakeLock wakeLock = XposedHook.getWakeLock();
                canCollectSelfEnergy(loader, times);
                if (Config.getCollectEnergy() && !Config.getSmallAccountList().contains(FriendIdMap.currentUid)) {
                    queryEnergyRanking(loader);
                }
                onForestEnd(loader);
                if (!abc && !AntForestNotification.getAutoStart() && !Config.enableOpenDoor() && kkk() && times % 2 == 1) {
                    abc = true;
                    friendAnalysis(loader);
                    abc = false;
                }
                XposedHook.setWakeLock(wakeLock);
            }

            public Thread setData(ClassLoader loader, int i) {
                this.loader = loader;
                this.times = i;
                return this;
            }
        }.setData(loader, times);
        if (Config.getOriginalMode()) {
            thread.start();
        } else {
            AntBroadcastReceiver.execute(thread);
        }
    }

    private static void praiseEnergy(ClassLoader loader) {
        try {
            JSONObject jo = new JSONObject(AntForestRpcCall.rpcCall_pageQueryDynamics(loader));
            if ("SUCCESS".equals(jo.getString("resultCode"))) {
                JSONArray ja = jo.getJSONArray("dynamics");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject joo = ja.getJSONObject(i);
                    if (joo.getBoolean("allowOperate") && "FRIEND_COLLECT".equals(joo.getString("type"))) {
                        JSONObject properties = joo.getJSONObject("properties");
                        if ((!properties.has("alreadyOperate") || !"true".equals(properties.getString("alreadyOperate")))) {
                            long bizDt = joo.getLong("bizDt");
                            String bizNo = joo.getString("bizNo");
                            String userId = joo.getJSONObject("fromUser").getString("userId");
                            String s = AntForestRpcCall.rpcCall_operateDynamic(loader, bizDt, bizNo, userId);
                            joo = new JSONObject(s);
                            if ("SUCCESS".equals(joo.getString("resultCode"))) {
                                Log.forest("打赏【" + FriendIdMap.getNameById(userId) + "】能量【" + joo.getLong("praiseEnergy") + "克】");
                            } else {
                                Log.recordLog("打赏失败，" + s);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "praiseEnergy err:");
            Log.printStackTrace(TAG, t);
        }
    }

    //*****
    private static void queryEnergyRanking(ClassLoader loader) {
        try {
            ArrayList<String> ids = new ArrayList(queryEnergyRankingWeek(loader).keySet());
            int checkInterval = Config.getCheckInterval();
            int start = 0;
            if (ids.size() > 0) {
                int end = Math.min(ids.size(), start + 20);
                String s = AntForestRpcCall.rpcCall_fillUserRobFlag(loader, ids.subList(start, end));
                JSONObject jo = new JSONObject(s);
                if (jo.getString("resultCode").equals("SUCCESS")) {
                    JSONArray ja = jo.getJSONArray("friendRanking");
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        String userId = jo.getString("userId");
                        boolean bool = true;
                        if (Config.getDelayHelpFriendCollect()) {
                            long time = timemap2.get(userId);
                            if (time != 0) {
                                Calendar instance = Calendar.getInstance();
                                instance.setTimeInMillis(time);
                                if (instance.get(Calendar.DAY_OF_MONTH) !=
                                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                    bool = false;
                                }
                            }
                        }
                        boolean v3 = false;
                        long canCollectLaterTime = jo.getLong("canCollectLaterTime");
                        if (canCollectLaterTime > 0 && System.currentTimeMillis() + checkInterval * 2 > canCollectLaterTime) {
                            v3 = true;
                        }
                        if (jo.getBoolean("canCollectEnergy") || v3) {
                            v3 = !Config.getDontCollectList().contains(userId);
                        }
                        boolean canHelpCollect = jo.getBoolean("canHelpCollect")
                                && (Config.getHelpFriendCollect() || Config.getHelpFriendCollectWatering())
                                && !Config.getDontHelpCollectList().contains(userId) && bool;
                        boolean canGrabPacket = Config.getGrabPacket() && jo.getBoolean("canGrabPacket");
                        if (canGrabPacket) {
                            timemap1.remove(userId);
                        }
                        v3 = v3 || canHelpCollect || canGrabPacket;
                        if (Config.getCollectEnergy() && v3 && !selfId.equals(userId)) {
                            canCollectEnergy(loader, userId, true);
                        } else {
                            FriendIdMap.putMap(userId, null, null, null);
                        }
                    }
                } else {
                    Log.recordLog(jo.getString("resultDesc"), s);
                }
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryEnergyRanking err:");
            Log.printStackTrace(TAG, t);
        }
    }

    private static Map<String, Integer> queryEnergyRankingSum(ClassLoader loader) {
        HashMap<String, Integer> result = new HashMap<>();
        try {
            String s = AntForestRpcCall.rpcCall_queryEnergyRankingTotal(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                if (jo.has("totalDatas")) {
                    JSONArray ja = jo.getJSONArray("totalDatas");
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        result.put(jo.getString("userId"), jo.getInt("energySummation"));
                    }
                }
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
                return result;
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryEnergyRankingSum err:");
            Log.printStackTrace(TAG, t);
        }
        return result;
    }

    private static Map<String, Integer> queryEnergyRankingWeek(ClassLoader loader) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        try {
            String s = AntForestRpcCall.rpcCall_queryEnergyRankingWeek(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                if (jo.has("totalDatas")) {
                    JSONArray ja = jo.getJSONArray("totalDatas");
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        result.put(jo.getString("userId"), jo.getInt("energySummation"));
                    }
                }
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "queryEnergyRankingWeek err:");
            Log.printStackTrace(TAG, t);
        }
        return result;
    }

    public static void friendAnalysis(ClassLoader loader) {
        new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File rankFile = FileUtils.getRankFile();
        HashMap<String, ForestUser> map = new HashMap<>();
        int weekTimes = Log.getWeekTimes();
        JSONObject jo;
        try {
            jo = new JSONObject(FileUtils.readFromFile(rankFile));
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
            jo = new JSONObject();
        }
        try {
            JSONObject self = jo.getJSONObject(FriendIdMap.currentUid);
            Date date = dateFormat.parse(self.getString("time"));
            if (System.currentTimeMillis() - date.getTime() < 18000000)
                return;
            int weekTimes2 = Log.getWeekTimes(date);
            JSONArray ja = self.getJSONArray("rank");
            for (int i = 0; i < ja.length(); i++) {
                ForestUser user = ForestUser.parseJSONObject(ja.getJSONObject(i));
                map.put(user.id, user);
            }
            if (FriendIdMap.friendMap.isEmpty()) {
                return;
            }
            Log.other("好友分析开始");
            Map<String, Integer> v0 = queryEnergyRankingWeek(loader);
            Map<String, Integer> v10 = queryEnergyRankingSum(loader);
            ArrayList<ForestUser> v11 = new ArrayList<>();
            for (Map.Entry<String, AliFriend> entry : FriendIdMap.friendMap.entrySet()) {
                String id = entry.getKey();
                AliFriend v14 = entry.getValue();
                String response = AntForestRpcCall.rpcCall_queryPKRecord(loader, id);

                jo = new JSONObject(response);
                if (jo.getString("resultCode").equals("SUCCESS")) {
                    ForestUser v4 = new ForestUser();
                    v11.add(v4);
                    v4.id = id;
                    if (v14.getFriendStatus() == 1 && !v0.containsKey(id)) {
                        v4.friendStatus = 3;
                    } else {
                        v4.friendStatus = v14.getFriendStatus();
                    }
                    v4.energyWeek = v0.get(id);
                    v4.energySum = v10.get(id);
                    JSONObject record = jo.optJSONObject("myRecord");
                    v4.collectEnergyWeek = record.optInt("collectEnergy");
                    String realName = FriendIdMap.getRealNameById(id);
                    String s = "";
                    if (!realName.isEmpty()) {
                        s = "|" + realName;
                    }
                    s = v14.getShowName() + s;
                    v4.name = s;
                    v4.addTime = v14.getVersion();
                    ForestUser v8 = map.get(id);
                    if (v8 != null) {
                        v4.addTime = v8.addTime;
                        if (weekTimes != 0) {
                            v4.collectEnergySum = v8.collectEnergySum + v8.collectEnergyWeek;
                            v4.week = v8.week + 1;
                        } else {
                            v4.collectEnergySum = v8.collectEnergySum;
                            v4.week = v8.week;
                            if (v4.collectEnergyWeek < v8.collectEnergyWeek) {
                                v4.collectEnergyWeek = v8.collectEnergyWeek;
                            }
                            if (v4.energyWeek < v8.energyWeek) {
                                v4.energyWeek = v8.energyWeek;
                            }
                        }
                        if (v4.energySum < v8.energySum) {
                            v4.energySum = v8.energySum;
                        }
                    }
                    Thread.sleep(RandomUtils.delay());
                } else {
                    Log.recordLog(jo.optString("resultDesc"), response);
                    Log.other("好友分析出错");
                }

            }
            v11.sort(ForestUser.getAvgCollectComparator());
            ja = new JSONArray(FriendsManager.toJSONString(loader, v11));
            jo = new JSONObject();
            jo.put("time", dateFormat.format(new Date()));
            jo.put("rank", ja);
            FileUtils.write2File(jo.toString(), rankFile);
            Log.other("好友分析结束");
            FriendIdMap.updateForestInfo();
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
            Log.other("好友分析出错");
        }
    }

    private static void receiveTaskAward(ClassLoader loader) {
        try {
            String s = AntForestRpcCall.rpcCall_queryTaskList(loader);
            JSONObject jo = new JSONObject(s);
            if (jo.getString("resultCode").equals("SUCCESS")) {
                JSONArray jaForestTaskVOList = jo.getJSONArray("forestTaskVOList");
                for (int i = 0; i < jaForestTaskVOList.length(); i++) {
                    jo = jaForestTaskVOList.getJSONObject(i);
                    if (AntFarm.TaskStatus.FINISHED.name().equals(jo.getString("taskStatus"))) {
                        String taskAwardTypeStr = jo.getString("awardType");
                        String awardName = null;
                        if (taskAwardTypeStr.endsWith(TaskAwardType.DRESS.name())) {
                            awardName = TaskAwardType.DRESS.nickName().toString();
                        } else if (TaskAwardType.BUBBLE_BOOST.name().equals(taskAwardTypeStr)) {
                            awardName = TaskAwardType.BUBBLE_BOOST.nickName().toString();
                        }
                        int awardCount = jo.getInt("awardCount");
                        s = AntForestRpcCall.rpcCall_receiveTaskAward(loader, jo.getString("taskType"));
                        jo = new JSONObject(s);
                        s = jo.getString("desc");
                        if (s.equals("SUCCESS"))
                            Log.forest("已领取【" + awardCount + "个】【" + awardName + "】");
                        else
                            Log.recordLog("领取失败，" + s, jo.toString());
                    }
                }
            } else {
                Log.recordLog(jo.getString("resultDesc"), s);
            }
        } catch (Throwable t) {
            Log.i(TAG, "receiveTaskAward err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public static boolean kkk() {
        Calendar instance = Calendar.getInstance();
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int minute = instance.get(Calendar.MINUTE);
        if (hour != 23 || minute <= 50) {
            return hour < 1 || hour >= 8;
        }
        return false;
    }

    public static class BubbleTimerTask extends Thread {
        ClassLoader loader;
        String userName;
        String userId;
        String bizNo;
        long bubbleId;
        long produceTime;
        long sleep = 0L;
        boolean a = true;
        int times = 0;

        BubbleTimerTask(ClassLoader loader, String userName, String userId, String bizNo, long bubbleId, long produceTime, int times) {
            this.loader = loader;
            this.userName = userName;
            this.bizNo = bizNo;
            this.userId = userId;
            this.bubbleId = bubbleId;
            this.produceTime = produceTime;
            this.times = times;
        }

        @Override
        public void run() {
            String bubbleKey = this.userId + "|" + this.bubbleId;
            int collected = 0;
            try {
                calculateDelayTime();
                if (sleep > 0) sleep(sleep);
                if (this.a) {
                    Log.recordLog("【" + userName + "】蹲点收取开始" + collectTaskCount + "，提前" + (produceTime - System.currentTimeMillis()) + "毫秒", "");
                } else {
                    Log.recordLog("【" + userName + "】蹲点收取开始" + collectTaskCount + "，提前" + (produceTime - System.currentTimeMillis()) + "毫秒，xxx", "");
                }
                collectTaskCount--;
                long time = System.currentTimeMillis();

                while (System.currentTimeMillis() - time < Config.getCollectTimeout()) {
                    collected = collectEnergy(loader, userId, bubbleId, userName, bizNo, null);
                    if (collected > 0) {
                        bubbleSet.add(bubbleKey);
                        break;
                    }
                    if (a && bubbleSet.contains(bubbleKey)) {
                        break;
                    }
                    if (Config.getCollectInterval() > 0) {
                        sleep(Config.getCollectInterval());
                    }
                }
                if (a && (System.currentTimeMillis() - time) - Config.getCollectTimeout() > 0 && !bubbleSet.contains(bubbleKey)) {
                    bubbleSet.add(bubbleKey);
                    BubbleTimerTask task = new BubbleTimerTask(loader, userName, userId, bizNo, bubbleId, produceTime + 10000, 0);
                    task.setA(false);
                    if (Config.getOriginalMode()) {
                        task.start();
                    } else {
                        AntBroadcastReceiver.offerTask(task, 10000);
                    }
                    collectTaskCount++;
                }
            } catch (Throwable t) {
                Log.i(TAG, "BubbleTimerTask.run err:");
                Log.printStackTrace(TAG, t);
            }
            String s = "【" + userName + "】蹲点收取结束，本次收" + collected + "，总收：" + totalCollected + "，帮：" + totalHelpCollected;
            Log.recordLog(s, "");
            AntForestNotification.setContentText(Log.getFormatTime() + s);
        }

        public long calculateDelayTime() {
            int advanceTime = Config.getAdvanceTime();
            if (this.bizNo == null) {
                if (Config.getAdvanceTime() < 300) {
                    advanceTime = 400;
                }
            }
            this.sleep = this.produceTime + offsetTime - System.currentTimeMillis() - advanceTime + this.times * 100;
            return sleep;
        }

        public void setA(boolean paramBoolean) {
            this.a = paramBoolean;
        }
    }

    public enum CollectStatus {AVAILABLE, WAITING, INSUFFICIENT, ROBBED}

    public enum TaskAwardType {
        BUBBLE_BOOST, DRESS;
        public static final CharSequence[] nickNames =
                {"时光加速器", "装扮"};

        public CharSequence nickName() {
            return nickNames[ordinal()];
        }
    }
}