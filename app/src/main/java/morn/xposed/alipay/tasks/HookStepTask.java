package morn.xposed.alipay.tasks;

import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;

public class HookStepTask {
    private static final String TAG = XposedHook.class.getCanonicalName();
    private ClassLoader loader;

    public HookStepTask(ClassLoader loader) {
        this.loader = loader;
    }

    public void hook() {
        try {
            XposedHelpers.findAndHookMethod("com.alibaba.health.pedometer.intergation.rpc.SyncRequestBuilder", this.loader, "dailyCount", Integer.TYPE, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    int hookStep = Math.min(Config.getHookStep(), 10_0000);
                    if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 6 && (int) param.args[0] < hookStep) {
                        param.args[0] = hookStep;
                    }
                }
            });
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }
}