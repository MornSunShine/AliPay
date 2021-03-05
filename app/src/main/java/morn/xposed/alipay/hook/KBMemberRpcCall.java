package morn.xposed.alipay.hook;

import morn.xposed.alipay.util.Log;

public class KBMemberRpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠅ";

    public static String rpcCall_signIn(ClassLoader loader) {
        try {
            return RpcCall.invoke(loader, "alipay.kbmemberprod.action.signIn", "[{\"sceneCode\":\"KOUBEI_INTEGRAL\",\"source\":\"ALIPAY_TAB\",\"version\":\"2.0\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_signIn err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}