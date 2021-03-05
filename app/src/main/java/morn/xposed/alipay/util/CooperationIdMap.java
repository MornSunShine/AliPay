package morn.xposed.alipay.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CooperationIdMap {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠊ.ࠁ";
    public static boolean shouldReload = false;
    private static TreeMap<String, String> idMap;
    private static boolean hasChanged = false;

    public static void putIdMap(String key, String value) {
        if (key == null || key.isEmpty()) return;
        if (getIdMap().containsKey(key)) {
            if (!getIdMap().get(key).equals(value)) {
                getIdMap().remove(key);
                getIdMap().put(key, value);
                hasChanged = true;
            }
        } else {
            getIdMap().put(key, value);
            hasChanged = true;
        }
    }

    public static boolean saveIdMap() {
        if (hasChanged) {
            StringBuilder sb = new StringBuilder();
            Set<Entry<String, String>> idSet = getIdMap().entrySet();
            for (Map.Entry<String, String> entry : idSet) {
                sb.append(entry.getKey());
                sb.append(':');
                sb.append(entry.getValue());
                sb.append('\n');
            }
            hasChanged = !FileUtils.write2File(sb.toString(), FileUtils.getCooperationIdMapFile());
        }
        return hasChanged;
    }

    public static void remove(String key) {
        if (key == null || key.isEmpty()) return;
        if (getIdMap().containsKey(key)) {
            getIdMap().remove(key);
            hasChanged = true;
        }

    }

    public static TreeMap<String, String> getIdMap() {
        if (idMap == null || shouldReload) {
            shouldReload = false;
            idMap = new TreeMap<String, String>();
            String str = FileUtils.readFromFile(FileUtils.getCooperationIdMapFile());
            if (str.length() > 0) {
                try {
                    String[] idSet = str.split("\n");
                    for (String s : idSet) {
                        Log.i(TAG, s);
                        int ind = s.indexOf(":");
                        idMap.put(s.substring(0, ind), s.substring(ind + 1));
                    }
                } catch (Throwable t) {
                    Log.printStackTrace(TAG, t);
                    idMap.clear();
                }
            }
        }
        return idMap;
    }

}