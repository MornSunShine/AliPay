package morn.xposed.alipay.tasks;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;

public class LauncherTask {
    private static final String TAG = XposedHook.class.getCanonicalName();
    private ClassLoader loader;

    public LauncherTask(ClassLoader loader) {
        this.loader = loader;
    }

    public void start() {
        try {
            XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", this.loader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Activity activity = (Activity) param.thisObject;
                    if (Config.getAutoRestart() == Config.RestartType.APP || Config.getAutoRestart2() == Config.RestartType.APP) {
                        activity.getWindow().addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    }
                    Intent it = activity.getIntent();
                    it.getStringExtra("msg");
                    boolean restricted = ((KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode();
                    String status = it.getStringExtra("qiufeng");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    long xqe = preferences.getLong("xqe", 0L);
                    if (!restricted && (status.equals("1") || Math.abs(System.currentTimeMillis() - xqe) < 40000L)) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
                        activity.moveTaskToBack(true);
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("xqe", 1L);
                    editor.apply();
                    editor.commit();
                }
            });
            Log.i(TAG, "hook    onCreate successfully");
        } catch (Throwable t) {
            Log.i(TAG, "hook    onCreate err:");
            Log.printStackTrace(TAG, t);
        }
    }
}