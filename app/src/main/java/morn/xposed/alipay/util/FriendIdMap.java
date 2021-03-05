package morn.xposed.alipay.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import morn.xposed.alipay.ui.AliFriend;
import morn.xposed.alipay.ui.ForestUser;

public class FriendIdMap {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠊ.ࠃ";
    public static volatile boolean shouldReload = false;
    public static volatile String currentUid;

    private static Map<String, UserCard> idMap;
    private static volatile boolean hasChanged = false;
    private static final Pattern pattern = Pattern.compile("([^#]*)#([^#]*)#([^#]*)#([^#]*)");
    public static Map<String, AliFriend> friendMap = Collections.emptyMap();
    public static Map<String, ForestUser> forestUserMap = Collections.emptyMap();

    private static String a(String paramString) {
        String str;
        if (paramString != null) {
            str = paramString;
            if (!paramString.equals("null")) {
            }
        } else {
            str = "";
        }
        return str;
    }

    public static Map<String, UserCard> getIdMap() {
        if (idMap == null || shouldReload) {
            int i = 0;
            shouldReload = false;
            idMap = new TreeMap<>();
            String str = FileUtils.readFromFile(FileUtils.getFriendIdMapFile());
            if (str.length() > 0) {
                try {
                    String[] idSet = str.split("\n");
                    Matcher matcher;
                    UserCard card;
                    for (String s : idSet) {
                        matcher = pattern.matcher(s);
                        if (matcher.find() && matcher.groupCount() == 4) {
                            card = new UserCard();
                            card.userId = matcher.group(1);
                            card.nickName = matcher.group(2);
                            card.userName = matcher.group(3);
                            card.logonId = matcher.group(4);
                            if (card.userId != null) {
                                idMap.put(card.userId, card);
                            }
                        }
                    }
                } catch (ThreadDeath t) {
                    Log.printStackTrace(TAG, t);
                    idMap.clear();
                }
            }
        }
        return idMap;
    }

    public static void putMap(String id, String nickName, String userName, String logonId) {
        try {
            UserCard card = new UserCard();
            card.userId = id;
            card.nickName = nickName;
            card.userName = userName;
            card.logonId = logonId;
            if (friendMap.containsKey(id)) {
                AliFriend user = friendMap.get(id);
                card.nickName = user.getShowName();
                if (user.getUserName() != null) {
                    card.userName = user.getUserName();
                }
                if (logonId == null) {
                    card.logonId = user.getLogonId();
                }
            }
            UserCard user = (UserCard) getIdMap().get(id);
            if (nickName != null) {
                if (card.nickName.isEmpty()) {
                    card.nickName = user.nickName;
                }
                if (card.userName.isEmpty() || (card.userName.contains("*") && !user.userName.isEmpty())) {
                    card.userName = user.userName;
                }
                if (card.logonId.isEmpty() || (!user.logonId.isEmpty() && card.logonId.replaceAll("\\*", "").length() < user.logonId.replaceAll("\\*", "").length())) {
                    card.logonId = user.logonId;
                }
            }
            if (card.nickName != null) {
                card.nickName = card.nickName.replace("#", "");
            }
            getIdMap().put(id, card);
            hasChanged = true;
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static String getNameById(String id) {
        try {
            UserCard card = getIdMap().get(id);
            if (card != null) {
                if (card.nickName != null && card.userName != null) {
                    return card.nickName + "|" + card.userName;
                }
                if (card.nickName != null) {
                    return card.nickName;
                }
                if (card.userName != null) {
                    return card.userName;
                }
                if (card.logonId != null) {
                    return card.logonId;
                }
                return card.userId;
            }
            AliFriend user = friendMap.get(id);
            if (user != null) {
                putMap(id, user.getShowName(), user.getUserName(), user.getLogonId());
            } else {
                putMap(id, null, null, null);
            }
            hasChanged = true;
            return getNameById(id);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return id;
    }

    public static void updateForestInfo() {
        try {
            String data = FileUtils.readFromFile(FileUtils.getRankFile());
            if (data.isEmpty()) {
                data = "{}";
            }
            JSONObject jo = new JSONObject(data);
            if (!currentUid.isEmpty()) {
                if (!jo.has(currentUid)) {
                    return;
                }
                jo = jo.getJSONObject(currentUid);
            } else {
                if (!jo.has(Statistics.getUserId())) {
                    return;
                }
                jo = jo.getJSONObject(Statistics.getUserId());
            }
            JSONArray ja = jo.getJSONArray("rank");
            forestUserMap = Collections.synchronizedMap(new LinkedHashMap());
            ForestUser user;
            JSONObject jaa;
            for (int i = 0; i < ja.length(); i++) {
                jaa = ja.getJSONObject(i);
                user = new ForestUser();
                user.id = jaa.optString("id");
                user.name = jaa.optString("name");
                user.friendStatus = jaa.optInt("friendStatus");
                user.energyWeek = jaa.optInt("energyWeek");
                user.energySum = jaa.optInt("energySum");
                user.collectEnergyWeek = jaa.optInt("collectEnergyWeek");
                user.collectEnergySum = jaa.optInt("collectEnergySum");
                user.week = jaa.optInt("week");
                user.addTime = jaa.optLong("addTime");
                forestUserMap.put(user.id, user);
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static String getRealNameById(String id) {
        if (friendMap.containsKey(id)) {
            AliFriend user = (AliFriend) friendMap.get(id);
            if (!user.getUserName().isEmpty()) {
                return user.getUserName();
            }
        }
        UserCard user = getIdMap().get(id);
        if (user != null && !user.userName.isEmpty()) {
            return user.userName;
        }
        return "";
    }

    public static boolean saveIdMap() {
        if (hasChanged) {
            StringBuilder sb = new StringBuilder();
            for (UserCard card : getIdMap().values()) {
                sb.append(card.userId)
                        .append("#")
                        .append(card.nickName)
                        .append("#")
                        .append(card.userName)
                        .append("#")
                        .append(card.logonId)
                        .append("\n");
            }
            hasChanged = !FileUtils.write2File(sb.toString(), FileUtils.getFriendIdMapFile());
        }
        return hasChanged;
    }

    public static void saveFarmMap() {
        for (AliFriend user : friendMap.values()) {
            putMap(user.getUserId(), user.getShowName(), user.getUserName(), user.getLogonId());
        }
        hasChanged = true;
        saveIdMap();
    }

    public static void removeIdMap(String key) {
        if (key == null || key.isEmpty()) return;
        if (getIdMap().containsKey(key)) {
            getIdMap().remove(key);
            hasChanged = true;
        }
    }

    public static class UserCard {
        public String userId;
        public String nickName;
        public String userName;
        public String logonId;

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof UserCard)) {
                return false;
            } else {
                UserCard c = (UserCard) o;
                return c.userId.equals(this.userId)
                        && c.nickName.equals(this.nickName)
                        && c.userName.equals(this.userName)
                        && c.logonId.equals(this.logonId);
            }
        }

        public int hashCode() {
            String str1 = this.userId;
            String str2 = this.nickName;
            int j = 1;
            String str3 = this.userName;
            String str4 = this.logonId;
            int i = 0;
            while (i < 4) {
                Object localObject = new Object[]{str1, str2, str3, str4}[i];
                int k;
                if (localObject == null) {
                    k = 0;
                } else {
                    k = localObject.hashCode();
                }
                j = j * 31 + k;
                i += 1;
            }
            return j;
        }
    }
}