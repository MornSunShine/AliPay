package morn.xposed.alipay.util;

import morn.xposed.alipay.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class Statistics {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠊ.ࠈ";
    private static final String
            jn_year = "year", jn_month = "month", jn_week = "week", jn_latWeek = "lastWeek", jn_day = "day",
            jn_collected = "collected", jn_helped = "helped", jn_watered = "watered",
            jn_dayHis = "dayHis", jn_userId = "userId",
            jn_answerQuestionList = "answerQuestionList",
            jn_questionHint = "questionHint",
            jn_memberSignIn = "memberSignIn", jn_exchange = "exchange";
    private static Statistics statistics;
    private TimeStatistics year;
    private TimeStatistics month;
    private TimeStatistics week;
    private TimeStatistics lastWeek;
    private TimeStatistics day;
    private LinkedList<TimeStatistics> dayHis = new LinkedList();
    private String userId;
    private ArrayList<WaterFriendLog> waterFriendLogList;
    private ArrayList<String> cooperateWaterList;
    private ArrayList<String> answerQuestionList;
    private String questionHint;
    private ArrayList<FeedFriendLog> feedFriendLogList;
    private JSONObject memberSignIn = new JSONObject();
    private JSONObject exchange = new JSONObject();
    private JSONObject kbSignIn = new JSONObject();
    private JSONObject zmDonate = new JSONObject();
    private JSONObject electricSignIn = new JSONObject();
    private JSONObject addFriend = new JSONObject();

    public static void addData(DataType dt, int collected) {
        Statistics stat = getStatistics();
        resetToday();
        switch (dt) {
            case COLLECTED:
                stat.day.collected += collected;
                stat.week.collected += collected;
                stat.month.collected += collected;
                stat.year.collected += collected;
                break;
            case HELPED:
                stat.day.helped += collected;
                stat.week.helped += collected;
                stat.month.helped += collected;
                stat.year.helped += collected;
                break;
            case WATERED:
                stat.day.watered += collected;
                stat.week.watered += collected;
                stat.month.watered += collected;
                stat.year.watered += collected;
                break;
        }
        save();
    }

    public static int getData(TimeType tt, DataType dt) {
        Statistics stat = getStatistics();
        int data = 0;
        TimeStatistics ts = null;
        switch (tt) {
            case YEAR:
                ts = stat.year;
                break;
            case MONTH:
                ts = stat.month;
                break;
            case WEEK:
                ts = stat.week;
                break;
            case DAY:
                ts = stat.day;
                break;
        }
        if (ts != null)
            switch (dt) {
                case TIME:
                    data = ts.time;
                    break;
                case COLLECTED:
                    data = ts.collected;
                    break;
                case HELPED:
                    data = ts.helped;
                    break;
                case WATERED:
                    data = ts.watered;
                    break;
            }
        return data;
    }

    public static String getText() {
        statistics = null;
        Statistics stat = getStatistics();
        StringBuilder sb = new StringBuilder((getData(TimeType.YEAR, DataType.TIME) - 2000)).
                append("年 : 收 ").
                append(getData(TimeType.YEAR, DataType.COLLECTED)).
                append(",   帮 ").
                append(getData(TimeType.YEAR, DataType.HELPED)).
                append(",   浇 ").
                append(getData(TimeType.YEAR, DataType.WATERED)).
                append("\n").
                append(getData(TimeType.MONTH, DataType.TIME)).
                append("月 : 收 ").
                append(getData(TimeType.MONTH, DataType.COLLECTED)).
                append(",   帮 ").
                append(getData(TimeType.MONTH, DataType.HELPED)).
                append(",   浇 ").
                append(getData(TimeType.MONTH, DataType.WATERED));
        if (stat.lastWeek.collected > 0) {
            sb.append("\n").
                    append(stat.lastWeek.time).
                    append("周 : 收 ").
                    append(stat.lastWeek.collected).
                    append(",   帮 ").
                    append(stat.lastWeek.helped).
                    append(",   浇 ").
                    append(stat.lastWeek.watered);
        }
        sb.append("\n").
                append(getData(TimeType.DAY, DataType.TIME)).
                append("周 : 收 ").
                append(getData(TimeType.WEEK, DataType.COLLECTED)).
                append(",   帮 ").
                append(getData(TimeType.WEEK, DataType.HELPED)).
                append(",   浇 ").
                append(getData(TimeType.WEEK, DataType.WATERED));
        if (stat.dayHis.isEmpty()) {
            TimeStatistics t = stat.dayHis.get(stat.dayHis.size() - 1);
            sb.append("\n").
                    append(t.time).
                    append("日 : 收 ").
                    append(t.collected).
                    append(",   帮 ").
                    append(t.helped).
                    append(",   浇 ").
                    append(t.watered);
        }
        sb.append("\n").
                append(getData(TimeType.DAY, DataType.TIME)).
                append("日 : 收 ").
                append(getData(TimeType.DAY, DataType.COLLECTED)).
                append(",   帮 ").
                append(getData(TimeType.DAY, DataType.HELPED)).
                append(",   浇 ").
                append(getData(TimeType.DAY, DataType.WATERED));
        return sb.toString();
    }

    public static boolean canWaterFriendToday(String id, int count) {
        Statistics stat = getStatistics();
        int index = -1;
        for (int i = 0; i < stat.waterFriendLogList.size(); i++)
            if (stat.waterFriendLogList.get(i).userId.equals(id)) {
                index = i;
                break;
            }
        if (index < 0) return true;
        WaterFriendLog wfl = stat.waterFriendLogList.get(index);
        return wfl.waterCount < count;
    }

    public static void waterFriendToday(String id, int count) {
        Statistics stat = getStatistics();
        WaterFriendLog wfl;
        int index = -1;
        for (int i = 0; i < stat.waterFriendLogList.size(); i++)
            if (stat.waterFriendLogList.get(i).userId.equals(id)) {
                index = i;
                break;
            }
        if (index < 0) {
            wfl = new WaterFriendLog(id);
            stat.waterFriendLogList.add(wfl);
        } else {
            wfl = stat.waterFriendLogList.get(index);
        }
        wfl.waterCount = count;
        save();
    }

    private static String statistics2Json(Statistics stat) {
        JSONObject jo = new JSONObject();
        JSONArray ja = null;
        try {
            if (stat == null) stat = Statistics.defInit();
            JSONObject joo = new JSONObject();
            joo.put(jn_year, stat.year.time);
            joo.put(jn_collected, stat.year.collected);
            joo.put(jn_helped, stat.year.helped);
            joo.put(jn_watered, stat.year.watered);
            jo.put(jn_year, joo);

            joo = new JSONObject();
            joo.put(jn_month, stat.month.time);
            joo.put(jn_collected, stat.month.collected);
            joo.put(jn_helped, stat.month.helped);
            joo.put(jn_watered, stat.month.watered);
            jo.put(jn_month, joo);

            joo = new JSONObject();
            joo.put(jn_week, stat.week.time);
            joo.put(jn_collected, stat.week.collected);
            joo.put(jn_helped, stat.week.helped);
            joo.put(jn_watered, stat.week.watered);
            jo.put(jn_week, joo);

            joo = new JSONObject();
            joo.put(jn_latWeek, stat.lastWeek.time);
            joo.put(jn_collected, stat.lastWeek.collected);
            joo.put(jn_helped, stat.lastWeek.helped);
            joo.put(jn_watered, stat.lastWeek.watered);
            jo.put(jn_latWeek, joo);

            joo = new JSONObject();
            joo.put(jn_day, stat.day.time);
            joo.put(jn_collected, stat.day.collected);
            joo.put(jn_helped, stat.day.helped);
            joo.put(jn_watered, stat.day.watered);
            jo.put(jn_day, joo);

            ja = new JSONArray();
            for (TimeStatistics t : stat.dayHis) {
                joo = new JSONObject();
                joo.put(jn_day, t.time);
                joo.put(jn_collected, t.collected);
                joo.put(jn_helped, t.helped);
                joo.put(jn_watered, t.watered);
                ja.put(joo);
            }
            jo.put(jn_dayHis, ja);

            jo.put(jn_userId, stat.userId);

            ja = new JSONArray();
            for (WaterFriendLog w : stat.waterFriendLogList) {
                JSONArray jaa = new JSONArray();
                jaa.put(w.userId);
                jaa.put(w.waterCount);
                ja.put(jaa);
            }
            jo.put(Config.jn_waterFriendList, ja);

            ja = new JSONArray();
            for (String s : stat.cooperateWaterList) {
                ja.put(s);
            }
            jo.put(Config.jn_cooperateWaterList, ja);

            ja = new JSONArray();
            for (String s : stat.answerQuestionList) {
                ja.put(s);
            }
            jo.put(jn_answerQuestionList, ja);

            if (stat.questionHint != null)
                jo.put(jn_questionHint, stat.questionHint);

            ja = new JSONArray();
            for (FeedFriendLog ffl : stat.feedFriendLogList) {
                JSONArray jaa = new JSONArray();
                jaa.put(ffl.userId);
                jaa.put(ffl.feedCount);
                ja.put(jaa);
            }
            jo.put(Config.jn_feedFriendAnimalList, ja);

            jo.put(jn_memberSignIn, stat.memberSignIn);

            jo.put(jn_exchange, stat.exchange);

            jo.put(Config.jn_kbSignIn, stat.kbSignIn);
            jo.put(Config.jn_zmDonate, stat.zmDonate);
            jo.put(Config.jn_electricSignIn, stat.electricSignIn);
            jo.put(Config.jn_addFriend, stat.addFriend);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return Config.formatJson(jo, false);
    }

    public static void kbSignInToday() {
        Statistics stat = getStatistics();
        try {
            stat.kbSignIn.put(FriendIdMap.currentUid, Log.getFormatTime());
            save();
        } catch (JSONException e) {
            Log.printStackTrace(TAG, e);
        }
    }

    public static void answerQuestionToday(String uid) {
        Statistics stat = getStatistics();
        if (!stat.answerQuestionList.contains(uid)) {
            stat.answerQuestionList.add(uid);
            save();
        }
    }

    private static void addDay(TimeStatistics day) {
        Statistics stat = getStatistics();
        if (stat.dayHis.size() >= 6) {
            stat.dayHis.pop();
        }
        stat.getClass();
        TimeStatistics t = new TimeStatistics(day.time);
        t.collected = day.collected;
        t.helped = day.helped;
        t.watered = day.watered;
        stat.dayHis.add(t);
    }

    public static boolean canFeedFriendToday(String id, int count) {
        Statistics stat = getStatistics();
        int index = -1;
        for (int i = 0; i < stat.feedFriendLogList.size(); i++)
            if (stat.feedFriendLogList.get(i).userId.equals(id)) {
                index = i;
                break;
            }
        if (index < 0) return true;
        FeedFriendLog ffl = stat.feedFriendLogList.get(index);
        return ffl.feedCount < count;
    }

    public static boolean canCooperateWaterToday(String uid, String coopId) {
        Statistics stat = getStatistics();
        return !stat.cooperateWaterList.contains(uid + "_" + coopId);
    }

    public static void cooperateWaterToday(String uid, String coopId) {
        Statistics stat = getStatistics();
        String v = uid + "_" + coopId;
        if (!stat.cooperateWaterList.contains(v)) {
            stat.cooperateWaterList.add(v);
            save();
        }
    }

    private static void addWeek(TimeStatistics week) {
        Statistics stat = getStatistics();
        TimeStatistics t = new TimeStatistics(week.time);
        t.collected = week.collected;
        t.helped = week.helped;
        t.watered = week.watered;
        stat.lastWeek = t;
    }

    public static boolean canElectricSignIn() {
        Statistics stat = getStatistics();
        return !Log.getFormatTime().equals(stat.electricSignIn.optString(FriendIdMap.currentUid));
    }

    public static boolean canAnswerQuestionToday(String uid) {
        Statistics stat = getStatistics();
        return !stat.answerQuestionList.contains(uid);
    }

    public static void feedFriendToday(String id) {
        Statistics stat = getStatistics();
        FeedFriendLog ffl;
        int index = -1;
        for (int i = 0; i < stat.feedFriendLogList.size(); i++)
            if (stat.feedFriendLogList.get(i).userId.equals(id)) {
                index = i;
                break;
            }
        if (index < 0) {
            ffl = new FeedFriendLog(id);
            stat.feedFriendLogList.add(ffl);
        } else {
            ffl = stat.feedFriendLogList.get(index);
        }
        ffl.feedCount++;
        save();
    }

    public static boolean canExchangeToday() {
        Statistics stat = getStatistics();
        return !Log.getFormatTime().equals(stat.exchange.optString(FriendIdMap.currentUid));
    }

    private static Statistics json2Statistics(String json) {
        Statistics stat;
        try {
            JSONObject jo = new JSONObject(json);
            Log.i(TAG, "statistics.json:" + jo.toString());

            JSONObject joo = null;
            stat = new Statistics();

            joo = jo.getJSONObject(jn_year);
            stat.year = new TimeStatistics(joo.getInt(jn_year));
            stat.year.collected = joo.getInt(jn_collected);
            stat.year.helped = joo.getInt(jn_helped);
            stat.year.watered = joo.getInt(jn_watered);

            joo = jo.getJSONObject(jn_month);
            stat.month = new TimeStatistics(joo.getInt(jn_month));
            stat.month.collected = joo.getInt(jn_collected);
            stat.month.helped = joo.getInt(jn_helped);
            stat.month.watered = joo.getInt(jn_watered);

            if (jo.has(jn_week)) {
                joo = jo.getJSONObject(jn_week);
                stat.week = new TimeStatistics(joo.getInt(jn_week));
                stat.week.collected = joo.getInt(jn_collected);
                stat.week.helped = joo.getInt(jn_helped);
                stat.week.watered = joo.getInt(jn_watered);
            } else {
                stat.week = new TimeStatistics(Log.getWeekTimes());
            }

            if (jo.has(jn_latWeek)) {
                joo = jo.getJSONObject(jn_latWeek);
                stat.lastWeek = new TimeStatistics(joo.getInt(jn_week));
                stat.lastWeek.collected = joo.getInt(jn_collected);
                stat.lastWeek.helped = joo.getInt(jn_helped);
                stat.lastWeek.watered = joo.getInt(jn_watered);
            } else {
                stat.lastWeek = new TimeStatistics(0);
            }

            joo = jo.getJSONObject(jn_day);
            stat.day = new TimeStatistics(joo.getInt(jn_day));
            stat.day.collected = joo.getInt(jn_collected);
            stat.day.helped = joo.getInt(jn_helped);
            stat.day.watered = joo.getInt(jn_watered);

            stat.dayHis = new LinkedList<>();
            if (jo.has(jn_dayHis)) {
                JSONArray ja = jo.getJSONArray(jn_dayHis);
                for (int i = 0; i < ja.length(); i++) {
                    joo = ja.getJSONObject(i);
                    TimeStatistics timeStatistics = new TimeStatistics(joo.getInt(jn_day));
                    timeStatistics.collected = joo.getInt(jn_collected);
                    timeStatistics.helped = joo.getInt(jn_helped);
                    timeStatistics.watered = joo.getInt(jn_watered);
                    stat.dayHis.add(timeStatistics);
                }
            }

            stat.userId = jo.optString(jn_userId);
            stat.waterFriendLogList = new ArrayList<>();
            if (jo.has(Config.jn_waterFriendList)) {
                JSONArray ja = jo.getJSONArray(Config.jn_waterFriendList);
                for (int i = 0; i < ja.length(); i++) {
                    JSONArray jaa = ja.getJSONArray(i);
                    WaterFriendLog waterFriendLog = new WaterFriendLog(jaa.getString(0));
                    waterFriendLog.waterCount = jaa.getInt(1);
                    stat.waterFriendLogList.add(waterFriendLog);
                }
            }

            stat.cooperateWaterList = new ArrayList<>();
            if (jo.has(Config.jn_cooperateWaterList)) {
                JSONArray ja = jo.getJSONArray(Config.jn_cooperateWaterList);
                for (int i = 0; i < ja.length(); i++) {
                    stat.cooperateWaterList.add(ja.getString(i));
                }
            }

            stat.answerQuestionList = new ArrayList<>();
            if (jo.has(jn_answerQuestionList)) {
                JSONArray ja = jo.getJSONArray(jn_answerQuestionList);
                for (int i = 0; i < ja.length(); i++) {
                    stat.answerQuestionList.add(ja.getString(i));
                }
            }

            if (jo.has(jn_questionHint)) {
                stat.questionHint = jo.getString(jn_questionHint);
            }
            stat.feedFriendLogList = new ArrayList<>();
            if (jo.has(Config.jn_feedFriendAnimalList)) {
                JSONArray ja = jo.getJSONArray("feedFriendAnimalList");
                for (int i = 0; i < ja.length(); i++) {
                    JSONArray jaa = ja.getJSONArray(i);
                    FeedFriendLog feedFriendLog = new FeedFriendLog(jaa.getString(0));
                    feedFriendLog.feedCount = jaa.getInt(1);
                    stat.feedFriendLogList.add(feedFriendLog);
                }
            }

            joo = jo.optJSONObject(jn_memberSignIn);
            if (joo != null) {
                stat.memberSignIn = (joo);
            }
            joo = jo.optJSONObject(jn_exchange);
            if (joo != null) {
                stat.exchange = (joo);
            }
            joo = jo.optJSONObject(Config.jn_kbSignIn);
            if (joo != null) {
                stat.kbSignIn = (joo);
            }
            joo = jo.optJSONObject(Config.jn_zmDonate);
            if (joo != null) {
                stat.zmDonate = (joo);
            }
            joo = jo.optJSONObject(Config.jn_electricSignIn);
            if (joo != null) {
                stat.electricSignIn = (joo);
            }
            jo = jo.optJSONObject(Config.jn_addFriend);
            if (jo != null) {
                stat.addFriend = jo;
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
            if (json != null) {
                Log.i(TAG, "统计文件格式有误，已重置统计文件并备份原文件");
                FileUtils.write2File(json, FileUtils.getBackupFile(FileUtils.getStatisticsFile()));
            }
            stat = defInit();
        }
        String formated = statistics2Json(stat);
        if (!formated.equals(json)) {
            Log.i(TAG, "重新格式化 statistics.json");
            FileUtils.write2File(formated, FileUtils.getStatisticsFile());
        }
        return stat;
    }

    public static boolean canKbSignInToday() {
        Statistics stat = getStatistics();
        return !Log.getFormatTime().equals(stat.kbSignIn.optString(FriendIdMap.currentUid));
    }

    public static void setQuestionHint(String s) {
        Statistics stat = getStatistics();
        if (stat.questionHint == null) {
            stat.questionHint = s;
            save();
        }
    }

    public static boolean canMemberSignInToday() {
        Statistics stat = getStatistics();
        return !Log.getFormatTime().equals(stat.memberSignIn.optString(FriendIdMap.currentUid));
    }

    public static boolean canZmDonate() {
        Statistics stat = getStatistics();
        return !Log.getFormatTime().equals(stat.zmDonate.optString(FriendIdMap.currentUid));
    }

    private static void dayClear() {
        Statistics stat = getStatistics();
        stat.waterFriendLogList.clear();
        stat.cooperateWaterList.clear();
        stat.answerQuestionList.clear();
        stat.feedFriendLogList.clear();
        stat.questionHint = null;
        stat.memberSignIn = new JSONObject();
        stat.exchange = new JSONObject();
        stat.kbSignIn = new JSONObject();
        stat.zmDonate = new JSONObject();
        stat.electricSignIn = new JSONObject();
        stat.addFriend = new JSONObject();
        save();
        FileUtils.getForestLogFile().delete();
        FileUtils.getFarmLogFile().delete();
        FileUtils.getOtherLogFile().delete();
    }

    private static Statistics defInit() {
        Statistics stat = new Statistics();
        String[] date = Log.getFormatDate().split("-");
        if (stat.year == null) {
            stat.year = new TimeStatistics(Integer.parseInt(date[0]));
        }
        if (stat.month == null) {
            stat.month = new TimeStatistics(Integer.parseInt(date[1]));
        }
        if (stat.week == null) {
            stat.week = new TimeStatistics(Log.getWeekTimes());
        }
        if (stat.lastWeek == null) {
            stat.lastWeek = new TimeStatistics(0);
        }
        if (stat.day == null) {
            stat.day = new TimeStatistics(Integer.parseInt(date[2]));
        }
        if (stat.cooperateWaterList == null) {
            stat.cooperateWaterList = new ArrayList<>();
        }
        if (stat.answerQuestionList == null) {
            stat.answerQuestionList = new ArrayList<>();
        }
        if (stat.feedFriendLogList == null) {
            stat.feedFriendLogList = new ArrayList<>();
        }
        return stat;
    }

    public static void electricSignInToday() {
        Statistics stat = getStatistics();
        try {
            stat.electricSignIn.put(FriendIdMap.currentUid, Log.getFormatTime());
            save();
        } catch (JSONException e) {
            Log.printStackTrace(TAG, e);
        }
    }

    public static void exchangeToday() {
        Statistics stat = getStatistics();
        try {
            stat.exchange.put(FriendIdMap.currentUid, Log.getFormatTime());
            save();
        } catch (JSONException e) {
            Log.printStackTrace(TAG, e);
        }
    }

    public static String getQuestionHint() {
        Statistics stat = getStatistics();
        if (stat.questionHint.isEmpty()) {
            return "";
        } else {
            return "答题提示 : " + stat.questionHint;
        }
    }

    private static Statistics getStatistics() {
        if (statistics == null) {
            String str = null;
            if (FileUtils.getStatisticsFile().exists()) {
                str = FileUtils.readFromFile(FileUtils.getStatisticsFile());
            }
            statistics = json2Statistics(str);
        }
        return statistics;
    }


    public static String getUserId() {
        return getStatistics().userId;
    }

    public static void memberSignInToday() {
        Statistics stat = getStatistics();
        try {
            stat.memberSignIn.put(FriendIdMap.currentUid, Log.getFormatTime());
            save();
        } catch (JSONException e) {
            Log.printStackTrace(TAG, e);
        }
    }

    public static void resetToday() {
        Statistics stat = getStatistics();
        String[] dataStr = Log.getFormatDate().split("-");
        int ye = Integer.parseInt(dataStr[0]);
        int mo = Integer.parseInt(dataStr[1]);
        int da = Integer.parseInt(dataStr[2]);
        int week = Log.getWeekTimes();
        if (ye != stat.year.time) {
            stat.year.reset(ye);
        }
        if (mo != stat.month.time) {
            stat.month.reset(mo);
        }
        if (week != stat.week.time) {
            addWeek(stat.week);
            stat.week.reset(week);
        }
        if (da != stat.day.time) {
            addDay(stat.day);
            stat.day.reset(da);
            dayClear();
        }
        stat.userId = FriendIdMap.currentUid;
        save();
    }

    private static boolean save() {
        return FileUtils.write2File(statistics2Json(getStatistics()), FileUtils.getStatisticsFile());
    }

    public static void zmDonateToday() {
        Statistics stat = getStatistics();
        try {
            stat.zmDonate.put(FriendIdMap.currentUid, Log.getFormatTime());
            save();
        } catch (JSONException e) {
            Log.printStackTrace(TAG, e);
        }
    }

    public enum DataType {
        TIME, COLLECTED, HELPED, WATERED
    }

    public static class FeedFriendLog {
        String userId;
        int feedCount = 0;

        public FeedFriendLog(String userId) {
            this.userId = userId;
        }
    }

    private static class TimeStatistics {
        int time;
        int collected;
        int helped;
        int watered;

        TimeStatistics(int time) {
            reset(time);
        }

        public void reset(int time) {
            this.time = time;
            this.collected = 0;
            this.helped = 0;
            this.watered = 0;
        }
    }

    public enum TimeType {YEAR, MONTH, WEEK, DAY}

    public static class WaterFriendLog {
        String userId;
        int waterCount = 0;

        public WaterFriendLog(String userId) {
            this.userId = userId;
        }
    }
}