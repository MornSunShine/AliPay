package morn.xposed.alipay.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import morn.xposed.alipay.util.FriendIdMap;

public class AlipayUser extends AlipayId implements Comparable<AlipayUser> {
    private static List<AlipayUser> list;

    public AlipayUser(String i, String n) {
        this.id = i;
        this.name = n;
    }

    public static List<AlipayUser> getList() {
        if (list == null || FriendIdMap.shouldReload) {
            list = new ArrayList<AlipayUser>();
            Set<Map.Entry<String, FriendIdMap.UserCard>> idSet = FriendIdMap.getIdMap().entrySet();
            StringBuilder sb;
            FriendIdMap.UserCard card;
            for (Map.Entry<String, FriendIdMap.UserCard> entry:idSet){
                card=entry.getValue();
                sb=new StringBuilder();
                if (card.nickName !=null&&card.userName !=null){
                    sb.append(card.nickName);
                    sb.append("|");
                    sb.append(card.userName);
                }else if(card.nickName !=null){
                    sb.append(card.nickName);
                }else if (card.userName !=null){
                    sb.append(card.userName);
                }
               if(card.logonId !=null){
                   sb.append("(").append(card.logonId).append(")");
               }
               list.add(new AlipayUser(card.userId,sb.toString()));
            }
        }
        Collections.sort(list);
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

    @Override
    public int compareTo(AlipayUser user) {
        return this.name.compareTo(user.name);
    }
}