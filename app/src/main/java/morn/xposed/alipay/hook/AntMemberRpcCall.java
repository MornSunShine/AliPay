package morn.xposed.alipay.hook;

import morn.xposed.alipay.util.Log;

public class AntMemberRpcCall {
    private static final String TAG = "pansong291.xposed.quickenergy.hook.ࠃ";

    public static String rpcCall_familyHomepage(ClassLoader classLoader) {
        try {
            return RpcCall.invoke(classLoader, "alipay.peerpayprod.family.homepage", "[{\"appVersion\": \"3.0.0\",\"clientTraceId\": \"\",\"source\": \"JTHYJGW\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_familyHomepage err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryPointCert(ClassLoader classLoader, int page, int pageSize) {
        try {
            String sb = "[{\"page\":" +
                    page +
                    ",\"pageSize\":" +
                    pageSize +
                    "}]";
            return RpcCall.invoke(classLoader, "alipay.antmember.biz.rpc.member.h5.queryPointCert", sb);
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryPointCert err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_claimFamilyPointCert(ClassLoader classLoader, long certId, String familyId) {
        try {
            return RpcCall.invoke(classLoader,
                    "com.alipay.alipaymember.biz.rpc.family.h5.claimFamilyPointCert",
                    "[{\"certId\":" + certId + ",\"familyId\":\"" + familyId + "\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_claimFamilyPointCert err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_queryFamilyPointCert(ClassLoader classLoader, String familyId) {
        try {
            return RpcCall.invoke(classLoader,
                    "com.alipay.alipaymember.biz.rpc.family.h5.queryFamilyPointCert",
                    "[{\"familyId\":\"" + familyId + "\",\"limit\":20,\"needQueryOtherMemberCert\":false}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_queryFamilyPointCert err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_familySignin(ClassLoader classLoader) {
        try {
            return RpcCall.invoke(classLoader, "alipay.peerpayprod.family.signin",
                    "[{\"appVersion\": \"3.0.0\",\"clientTraceId\": \"\",\"source\": \"JTHYJGW\"}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_familySignin err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_receivePointByUser(ClassLoader classLoader, String certId) {
        try {
            return RpcCall.invoke(classLoader, "alipay.antmember.biz.rpc.member.h5.receivePointByUser",
                    "[{\"certId\":" + certId + "}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_receivePointByUser err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }

    public static String rpcCall_memberSigin(ClassLoader classLoader) {
        try {
            return RpcCall.invoke(classLoader, "alipay.antmember.biz.rpc.member.h5.memberSignin", "[{}]");
        } catch (Throwable t) {
            Log.i(TAG, "rpcCall_memberSignin err:");
            Log.printStackTrace(TAG, t);
        }
        return null;
    }
}
