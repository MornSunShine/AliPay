package morn.xposed.alipay.ui;

import java.util.*;
import java.util.Map.Entry;

import morn.xposed.alipay.util.CooperationIdMap;

public class AlipayCooperate extends AlipayId {
    private static List<AlipayCooperate> list;

    public AlipayCooperate(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<AlipayCooperate> getList() {
        if (list == null || CooperationIdMap.shouldReload) {
            list = new ArrayList<>();
            Set<Entry<String, String>> idSet = CooperationIdMap.getIdMap().entrySet();
            for (Map.Entry<String, String> entry : idSet) {
                list.add(new AlipayCooperate(entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }

    public static void remove(String id) {
        getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id.equals(id)) {
                list.remove(i);
                break;
            }
        }
    }
}