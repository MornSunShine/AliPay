package morn.xposed.alipay.tasks;

import org.json.JSONArray;
import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.AntForestToast;
import morn.xposed.alipay.hook.AntFarmRpcCall;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;

public class FarmGameTask {
    private static final String TAG = XposedHook.class.getCanonicalName();
    private ClassLoader loader;

    public FarmGameTask(ClassLoader loader) {
        this.loader = loader;
    }

    public void hook() {
        try {
            Class<?> h5RpcUtil = this.loader.loadClass("com.alipay.mobile.nebulaappproxy.api.rpc.H5RpcUtil");
            Class<?> h5Page = this.loader.loadClass("com.alipay.mobile.h5container.api.H5Page");
            Class<?> jsonObject = this.loader.loadClass("com.alibaba.fastjson.JSONObject");
            XposedHelpers.findAndHookMethod(h5RpcUtil, "rpcCall",
                    String.class, String.class, String.class, Boolean.TYPE, jsonObject,
                    String.class, Boolean.TYPE, h5Page, Integer.TYPE, String.class,
                    Boolean.TYPE, Integer.TYPE, String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if (param.args != null) {
                                if (param.args[0].equals("com.alipay.antfarm.recordFarmGame")) {
                                    JSONArray ja = new JSONArray((String) param.args[1]);
                                    JSONObject jo = ja.getJSONObject(0);
                                    String gameType = jo.optString("gameType");
                                    int score = jo.optInt("score");
                                    if ((Config.getStarGameHighScore() && gameType.equals("starGame"))
                                            || (Config.getJumpGameHighScore() && gameType.equals("jumpGame"))) {
                                        score = AntFarmRpcCall.getScore(gameType, score);
                                        AntForestToast.show("修改分数为" + score + "分.");
                                    }
                                    jo.put("score", score);
                                    param.args[1] = ja.toString();
                                } else if (param.args[0].equals("com.alipay.antfarm.recordFarmGameQiufeng")) {
                                    param.args[0] = "com.alipay.antfarm.recordFarmGame";
                                }
                            }
                        }
                    });
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }
}