package morn.xposed.alipay.hook;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.AntForestNotification;
import morn.xposed.alipay.AntForestToast;
import morn.xposed.alipay.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.RpcCall";
    private static Method rpcCallMethod;
    private static Method getResponseMethod;
    private static Object curH5PageImpl;
    public static volatile boolean b1;
    public static volatile boolean sendXEdgeProBroadcast;

    public static String invoke(ClassLoader loader, String args0, String args1) {
        if (rpcCallMethod == null) {
            try {
                Class<?> rpcClazz = loader.loadClass("com.alipay.mobile.nebulabiz.rpc.H5RpcUtil");
                Class<?> h5PageClazz = loader.loadClass("com.alipay.mobile.h5container.api.H5Page");
                Class<?> jsonClazz = loader.loadClass("com.alibaba.fastjson.JSONObject");
                rpcCallMethod = rpcClazz.getMethod(
                        "rpcCall", String.class, String.class, String.class,
                        boolean.class, jsonClazz, String.class, boolean.class, h5PageClazz,
                        int.class, String.class, boolean.class, int.class);
                Log.i(TAG, "get Old RpcCallMethod successfully");
            } catch (Throwable t) {
                Log.i(TAG, "get Old RpcCallMethod err:");
                //Log.printStackTrace(TAG, t);
            }

            if (rpcCallMethod == null)
                try {
                    Class<?> h5PageClazz = loader.loadClass("com.alipay.mobile.h5container.api.H5Page");
                    Class<?> jsonClazz = loader.loadClass("com.alibaba.fastjson.JSONObject");
                    Class<?> rpcClazz = loader.loadClass("com.alipay.mobile.nebulaappproxy.api.rpc.H5RpcUtil");
                    rpcCallMethod = rpcClazz.getMethod(
                            "rpcCall", String.class, String.class, String.class,
                            boolean.class, jsonClazz, String.class, boolean.class, h5PageClazz,
                            int.class, String.class, boolean.class, int.class, String.class);
                    Log.i(TAG, "get RpcCallMethod successfully");
                } catch (Throwable t) {
                    Log.i(TAG, "get RpcCallMethod err:");
                    //Log.printStackTrace(TAG, t);
                }
        }

        try {
            Object o = null;
            if (rpcCallMethod.getParameterTypes().length == 12) {
                o = rpcCallMethod.invoke(
                        null, args0, args1, "", true, null, null, false, curH5PageImpl, 0, "", false, -1);
            } else {
                o = rpcCallMethod.invoke(
                        null, args0, args1, "", true, null, null, false, curH5PageImpl, 0, "", false, -1, "");
            }
            String str = getResponse(o);
            Log.i(TAG, "argument: " + args0 + ", " + args1);
            Log.i(TAG, "response: " + str);
            if (!args0.isEmpty() && args0.contains("alipay.antmember.forest")) {
                AntForestNotification.setAutoStart(false);
            }
            return str;
        } catch (Throwable t) {
            Log.i(TAG, "invoke err:");
            Log.printStackTrace(TAG, t);
            if (t instanceof InvocationTargetException) {
                String eMessage = t.getCause().getMessage();
                if (sendXEdgeProBroadcast) {
                    Log.recordLog(eMessage + ",发送XposedEdgePro广播", "");
                }
                if (!eMessage.isEmpty() && eMessage.contains("超时")) {
                    AntForestNotification.setContentText(Log.getFormatTime() + "  登录超时，请重启支付宝");
                    AntForestNotification.setAutoStart(false);
                    if (System.currentTimeMillis() - XposedHook.startTime >= 60000) {
                        if (AntForestToast.context != null && Config.sendXedgepro() && b1) {
                            b1 = false;
                            Intent it = new Intent("com.jozein.xedgepro.PERFORM");
                            it.putExtra("data", Config.xedgeproData());
                            AntForestToast.context.sendBroadcast(it);
                            Log.recordLog(t.getCause().getMessage() + ",发送XposedEdgePro广播", "");
                        }
                        if (AntForestNotification.getAutoStart()) {
                            KeyguardManager manager = (KeyguardManager) XposedHook.getService().getSystemService(Context.KEYGUARD_SERVICE);
                            boolean initResult = manager.inKeyguardRestrictedInputMode();
                            if (initResult
                                    && Config.autoRestart() != Config.StateType.DISABLED
                                    && sendXEdgeProBroadcast) {
                                sendXEdgeProBroadcast = false;
                                Log.other("登录超时，开始重启(锁屏)");
                                XposedHook.startWithLock(false);
                            } else if (!initResult && Config.autoRestart2() != Config.StateType.DISABLED
                                    && sendXEdgeProBroadcast
                                    && !args0.isEmpty()
                                    && eMessage.contains("超时")) {
                                sendXEdgeProBroadcast = false;
                                Log.other("登录超时，开始重启(解锁)");
                                XposedHook.startNoLock(false);
                            } else if (!initResult
                                    && Config.autoRestart2() == Config.StateType.DISABLED
                                    && sendXEdgeProBroadcast
                                    && !args0.isEmpty()
                                    && eMessage.contains("超时")) {
                                sendXEdgeProBroadcast = false;
                                AntForestToast.show("登录超时，请重启支付宝");
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getResponse(Object resp) {
        try {
            if (getResponseMethod == null) {
                getResponseMethod = resp.getClass().getMethod("getResponse");
            }
            return (String) getResponseMethod.invoke(resp);
        } catch (Throwable t) {
            Log.i(TAG, "getResponse err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}