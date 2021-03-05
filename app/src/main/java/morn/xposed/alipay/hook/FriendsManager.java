package morn.xposed.alipay.hook;

import android.app.Service;
import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.AntForest;
import morn.xposed.alipay.ui.AliFriend;
import morn.xposed.alipay.ui.ForestUser;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RandomUtils;

public class FriendsManager {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠇ";

    public static Object getCacheObj() {
        try {
            Class<?> userIndependentCache = XposedHook.getLoader().loadClass("com.alipay.mobile.socialcommonsdk.bizdata.UserIndependentCache"),
                    aliAccountDaoOp = XposedHook.getLoader().loadClass("com.alipay.mobile.socialcommonsdk.bizdata.contact.data.AliAccountDaoOp");
            return XposedHelpers.callStaticMethod(userIndependentCache, "getCacheObj", aliAccountDaoOp);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String getSelfId(ClassLoader loader, Service service) {
        try {
            Class<?> securityDbHelper = loader.loadClass("com.alipay.mobile.framework.service.ext.dbhelper.SecurityDbHelper");
            Method getInstance = securityDbHelper.getMethod("getInstance", Context.class);
            Object instance = getInstance.invoke(securityDbHelper, service);
            List list = (List) instance.getClass().getMethod("queryUserInfoList").invoke(instance);
            if (list != null && !list.isEmpty()) {
                String userId = (String) XposedHelpers.callMethod(list.get(0), "getUserId");
                String userName = (String) XposedHelpers.callMethod(list.get(0), "getUserName");
                String logonId = (String) XposedHelpers.callMethod(list.get(0), "getLogonId");
                String showName = (String) XposedHelpers.callMethod(list.get(0), "getShowName");
                try {
                    FriendIdMap.putMap(userId, showName, userName, logonId);
                    return userId;
                } catch (Throwable t) {
                    return userId;
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String toJSONString(ClassLoader loader, Object json) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.fastjson.JSON", loader), "toJSONString", json);
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static Map<String, AliFriend> farmUserList2Map(List<AliFriend> list) {
        HashMap<String, AliFriend> map = new HashMap<>();
        for (AliFriend e : list) {
            map.put(e.getUserId(), e);
        }
        return map;
    }

    private static AliFriend toAliFriend(Object o) {
        AliFriend farmUser = new AliFriend();
        int i;
        try {
            Field[] fields = o.getClass().getDeclaredFields();
            HashMap<String, Field> fieldMap = new HashMap<>();
            for (Field field : fields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                fieldMap.put(fieldName, field);
            }
            farmUser.setUserId((String) fieldMap.get("userId").get(o));
            farmUser.setUserName((String) fieldMap.get("name").get(o));
            farmUser.setShowName((String) fieldMap.get("displayName").get(o));
            farmUser.setLogonId((String) fieldMap.get("account").get(o));
            farmUser.setFriendStatus(fieldMap.get("friendStatus").getInt(o));
            farmUser.serVersion(fieldMap.get("version").getLong(o));
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        farmUser.id = farmUser.getUserId();
        farmUser.name = farmUser.getShowName() + "(" + farmUser.getLogonId() + ")";
        return farmUser;
    }

    public static void deleteEnemy(ClassLoader loader) {
        List<AliFriend> friends = filterFriend(loader);
        ArrayList<String> list = new ArrayList<>();
        for (AliFriend friend : friends) {
            list.add(friend.id);
            deleteFriend(loader, friend.id);
            FriendIdMap.friendMap.remove(friend.id);
            FriendIdMap.removeIdMap(friend.id);
            try {
                Thread.sleep(RandomUtils.delay());
            } catch (Throwable t) {
                Log.printStackTrace(TAG, t);
            }

        }
        FriendIdMap.saveIdMap();
        AntForest.removeIds(TextUtils.join(",", list));
        forceRefreshMyFriends(loader);
    }

    public static void deleteFriend(ClassLoader loader, String userId) {
        try {
            AliFriend friend = (AliFriend) FriendIdMap.friendMap.get(userId);
            String name = FriendIdMap.getNameById(userId);
            StringBuilder sb = new StringBuilder();
            if (friend != null && friend.getFriendStatus() == 2) {
                sb.append("删除单删好友：");
            } else {
                sb.append("删除好友：");
            }
            sb = new StringBuilder();
            sb.append(name);
            if (FriendIdMap.forestUserMap.isEmpty()) {
                FriendIdMap.updateForestInfo();
            }
            ForestUser forestUser = FriendIdMap.forestUserMap.get(userId);
            if (forestUser != null) {
                sb.append("（");
                sb.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date(forestUser.addTime)));
                sb.append("）");
                sb.append("周收");
                sb.append(Log.translate(forestUser.collectEnergyWeek));
                sb.append(" 总收");
                sb.append(Log.translate(forestUser.collectEnergyWeek + forestUser.collectEnergySum));
                sb.append(" 日收");
                sb.append(Log.translate(ForestUser.getAvgCollect(forestUser)));
                sb.append(" 周");
                sb.append(Log.translate(forestUser.energyWeek));
                sb.append(" 总");
                sb.append(Log.translate(forestUser.energySum));
            }
            String remarkName;
            if (friend != null) {
                remarkName = friend.getShowName();
            } else {
                int i = name.lastIndexOf("|");
                if (i > 0) {
                    remarkName = name.substring(0, i);
                } else {
                    remarkName = name;
                }
            }
            if (remarkName.startsWith("已加")) {
                remarkName = remarkName.substring(2);
            }
            if (!remarkName.startsWith("删除")) {
                remarkName = "删除" + remarkName;
            }
            setRemark(userId, remarkName);
            Thread.sleep(RandomUtils.nextInt(500, 1000));
            Object handleRelationReq = loader.loadClass("com.alipay.mobilerelation.biz.shared.req.HandleRelationReq").newInstance();
            XposedHelpers.setObjectField(handleRelationReq, "targetUserId", userId);
            XposedHelpers.setObjectField(handleRelationReq, "bizType", "2");
            XposedHelpers.setObjectField(handleRelationReq, "alipayAccount", "");
            Object rpcProxy = getRpcProxy(loader, "com.alipay.mobilerelation.biz.shared.rpc.AlipayRelationManageService");
            XposedHelpers.callMethod(rpcProxy, "handleRelation", handleRelationReq);
            Class<?> aliAccountDaoOp = loader.loadClass("com.alipay.mobile.socialcommonsdk.bizdata.contact.data.AliAccountDaoOp");
            Class<?> userIndependentCache = loader.loadClass("com.alipay.mobile.socialcommonsdk.bizdata.UserIndependentCache");
            Object cacheObj = XposedHelpers.callStaticMethod(userIndependentCache, "getCacheObj", aliAccountDaoOp);
            XposedHelpers.callMethod(cacheObj, "deleteAccountById", userId);
            Log.other(sb.toString());
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public static boolean setRemark(String targetUserId, String remarkName) {
        try {
            ClassLoader loader = XposedHook.getLoader();
            Object cacheObj = getCacheObj();
            Object accountInfo = XposedHelpers.callMethod(cacheObj, "getAccountById", targetUserId);
            String account = (String) XposedHelpers.getObjectField(accountInfo, "account");
            Object rpcProxy = getRpcProxy(loader, "com.alipay.mobilerelation.rpc.ScocialInfoManageRpc");
            Object remarkReq = XposedHelpers.newInstance(XposedHelpers.findClass("com.alipay.mobilerelation.rpc.protobuf.remark.RemarkReq", loader));
            XposedHelpers.setObjectField(remarkReq, "targetUserId", targetUserId);
            XposedHelpers.setObjectField(remarkReq, "remarkName", remarkName);
            XposedHelpers.setObjectField(remarkReq, "alipayAccount", account);
            Object result = XposedHelpers.callMethod(rpcProxy, "setRemark", remarkReq);
            if (result != null) {
                if ((Integer) XposedHelpers.getObjectField(targetUserId, "resultCode") == 100) {
                    XposedHelpers.setObjectField(accountInfo, "remarkName", remarkName);
                    XposedHelpers.callMethod(cacheObj, "createOrUpdateAccountInfo", accountInfo);
                    return true;
                }
                Log.i(TAG, "setRemark error:" + (String) XposedHelpers.getObjectField(result, "resultDesc"));
                return false;
            }
        } catch (Throwable t) {
            Log.i(TAG, "setRemark error:" + t.getMessage());
            Log.i(TAG, targetUserId);
        }
        return false;
    }

    public static Object findServiceByInterface(ClassLoader loader, String interfaceName) {
        Class<?> alipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", loader);
        Object instance = XposedHelpers.callStaticMethod(alipayApplication, "getInstance");
        Object context = XposedHelpers.callMethod(instance, "getMicroApplicationContext");
        return XposedHelpers.callMethod(context, "findServiceByInterface", interfaceName);
    }

    public static void forceRefreshMyFriends(ClassLoader loader) {
        Object service = findServiceByInterface(loader, "com.alipay.mobile.personalbase.service.SocialSdkContactService");
        XposedHelpers.callMethod(service, "forceRefreshMyFriends");
    }

    public static Object getRpcProxy(ClassLoader loader, String className) {
        Object service = findServiceByInterface(loader, "com.alipay.mobile.framework.service.common.RpcService");
        return XposedHelpers.callMethod(service, "getRpcProxy", XposedHelpers.findClass(className, loader));
    }

    public static List<AliFriend> getAllFriends(ClassLoader loader) {
        try {
            Object service = findServiceByInterface(loader, "com.alipay.mobile.personalbase.service.SocialSdkContactService");
            Object list = XposedHelpers.callMethod(service, "getAllFriends");
            ArrayList<AliFriend> result = new ArrayList<>();
            if (list instanceof List) {
                for (Object o : (List) list) {
                    result.add(toAliFriend(o));
                }
            }
            FriendIdMap.friendMap = farmUserList2Map(result);
            return result;
        } catch (Throwable ignored) {
        }
        return Collections.emptyList();
    }

    public static List<AliFriend> filterFriend(ClassLoader loader) {
        List<AliFriend> allFriends = getAllFriends(loader);
        ArrayList<AliFriend> result = new ArrayList<>();
        for (AliFriend user : allFriends) {
            if (user.getFriendStatus() == 2) {
                result.add(user);
            }
        }
        return result;
    }
}