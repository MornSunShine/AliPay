package morn.xposed.alipay.tasks;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import morn.xposed.alipay.AntBroadcastReceiver;
import morn.xposed.alipay.AntForestNotification;
import morn.xposed.alipay.AntForestToast;
import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;

public class ࠄ extends XC_MethodHook {
    private static final String TAG = ࠄ.class.getCanonicalName();
    private ClassLoader loader;

    public ࠄ(ClassLoader loader) {
        this.loader = loader;
    }

    protected void afterHookedMethod(MethodHookParam param) {
        Service service = (Service) param.thisObject;
        AntForestNotification.stop(service, false);
        AntForestNotification.setContentText("支付宝服务被销毁");
        Log.other("支付宝服务被销毁");
        try {
            XposedHook.handler.removeCallbacks(XposedHook.runnable);
            AntBroadcastReceiver.unregister();
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }

        try {
            AlarmManager alarm = (AlarmManager) service.getSystemService(Context.ALARM_SERVICE);
            Intent it = new Intent();
            it.setClassName("com.eg.android.AlipayGphone", "com.alipay.android.launcher.service.LauncherService");
            PendingIntent pendingIntent = PendingIntent.getService(service, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 8000, pendingIntent);
            if (!XposedHook.a2 && AntForestToast.context != null && Config.getSendXedgepro()) {
                Intent intent = new Intent("com.jozein.xedgepro.PERFORM");
                intent.putExtra("data", Config.getXedgeproData());
                AntForestToast.context.sendBroadcast(intent);
                Log.recordLog("发送XposedEdgePro广播");
            }
            if (!XposedHook.a2) {
                boolean restricted = ((KeyguardManager) service.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode();
                if (restricted && Config.getAutoRestart() != Config.RestartType.DISABLED) {
                    XposedHook.startWithLock(true);
                } else if (!restricted && Config.getAutoRestart2() != Config.RestartType.DISABLED) {
                    XposedHook.startNoLock(true);
                }
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
    }

    public void hook() {
        try {
            XposedHelpers.findAndHookMethod("com.alipay.android.launcher.service.LauncherService", loader, "onDestroy", this);
            Log.i(TAG, "hook onDestroy successfully");
        } catch (Throwable t) {
            Log.i(TAG, "hook onDestroy err:");
            Log.printStackTrace(TAG, t);
        }
        try {
            XposedHelpers.findAndHookMethod("com.alipay.android.launcher.service.LauncherService", loader, "onStartCommand",
                    Intent.class, Integer.TYPE, Integer.TYPE, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            param.setResult(1);
                        }
                    });
        } catch (Throwable t) {
            Log.i(TAG, "hook onStartCommand err:");
            Log.printStackTrace(TAG, t);
        }
    }
}