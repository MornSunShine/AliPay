package morn.xposed.alipay.tasks;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.AntBroadcastReceiver;
import morn.xposed.alipay.AntForestNotification;
import morn.xposed.alipay.AntForestToast;
import morn.xposed.alipay.hook.FriendsManager;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.FriendIdMap;
import morn.xposed.alipay.util.Log;

public class MainTask {
    private static final String TAG = XposedHook.class.getCanonicalName();
    ClassLoader loader;

    public MainTask(ClassLoader loader) {
        this.loader = loader;
    }

    private void init(ClassLoader loader, String currentUid, String nickName, String userName, String loginId) {
        XposedHook.handler.removeCallbacks(XposedHook.runnable);
        AntForestNotification.stop(XposedHook.service, false);
        AntBroadcastReceiver.register(XposedHook.service);
        XposedHook.times = 0;
        XposedHook.a1 = true;
        XposedHook.a2 = false;
        FriendsManager.getAllFriends(loader);
        FriendIdMap.saveFarmMap();
        FriendIdMap.currentUid = currentUid;
        FriendIdMap.updateForestInfo();
        FriendIdMap.putMap(currentUid, nickName, userName, loginId);
        AntForestNotification.start(XposedHook.service);
        XposedHook.handler.post(XposedHook.runnable);
        AntForestToast.show("秋风加载成功");
    }

    public void hook() {
        try {
            Class<?> baseLoginActivity = loader.loadClass("com.ali.user.mobile.login.ui.BaseLoginActivity");
            Class<?> unifyLoginRes = loader.loadClass("com.ali.user.mobile.rpc.vo.mobilegw.login.UnifyLoginRes");
            XposedHelpers.findAndHookMethod(baseLoginActivity, "onLoginResponseSuccess", unifyLoginRes, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        super.afterHookedMethod(param);
                        if (param.args.length >= 1) {
                            Class<?> aClass = param.args[0].getClass();
                            String userId = (String) aClass.getField("userId").get(param.args[0]);
                            String alipayLoginId = (String) aClass.getField("alipayLoginId").get(param.args[0]);
                            JSONObject data = new JSONObject((String) aClass.getField("data").get(param.args[0]));
                            String nickName = data.optJSONObject("extResAttrs").optString("nickName");
                            String userName = data.optString("userName");
                            init(loader, userId, nickName, userName, alipayLoginId);
                        }
                    } catch (Throwable t) {
                        Log.printStackTrace(TAG, t);
                    }
                }
            });
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        try {
            Class<?> accountManagerActivity = loader.loadClass("com.alipay.mobile.security.accountmanager.ui.AccountManagerActivity");
            Class<?> userInfo = loader.loadClass("com.alipay.mobile.framework.service.ext.security.bean.UserInfo");
            XposedHelpers.findAndHookMethod(accountManagerActivity, "a", userInfo, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        super.afterHookedMethod(param);
                        if (param.args.length >= 1) {
                            Class<?> aClass = param.args[0].getClass();
                            String userId = (String) aClass.getMethod("getUserId").invoke(param.args[0]);
                            String nick = (String) aClass.getMethod("getNick").invoke(param.args[0]);
                            String logonId = (String) aClass.getMethod("getLogonId").invoke(param.args[0]);
                            String userName = (String) aClass.getMethod("getUserName").invoke(param.args[0]);
                            init(loader, userId, nick, userName, logonId);
                        }
                    } catch (Throwable t) {
                        Log.printStackTrace(TAG, t);
                    }
                }
            });
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }
}