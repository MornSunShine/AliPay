package morn.xposed.alipay.hook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.preference.PreferenceManager;

import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import morn.xposed.alipay.QiufengBroadcastReceiver;
import morn.xposed.alipay.ui.MainActivity;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;
import morn.xposed.alipay.util.RootManager;
import morn.xposed.alipay.tasks.FarmGameTask;
import morn.xposed.alipay.tasks.LauncherTask;
import morn.xposed.alipay.tasks.HookTask;
import morn.xposed.alipay.tasks.MainTask;
import morn.xposed.alipay.tasks.HookStepTask;

public class XposedHook implements IXposedHookLoadPackage {
    public static final String TAG = XposedHook.class.getCanonicalName();
    public static Service service;
    public static Handler handler;
    public static Runnable runnable;
    public static int times = 0;
    public static volatile boolean a1 = false;
    public static ClassLoader loader;
    public static volatile boolean unlock = false;
    public static volatile boolean a2 = false;
    public static long startTime = 0L;

    public static WakeLock getWakeLock() {
        return null;
    }

    public static void setWakeLock(WakeLock lock) {
        if (lock != null) {
            try {
                if (lock.isHeld()) {
                    lock.release();
                }
            } catch (Throwable t) {
                Log.printStackTrace(TAG, t);
            }
        }
    }

    private void hookRpcCall(ClassLoader loader) {
        try {
            XposedHelpers.findAndHookMethod(loader.loadClass("com.alipay.mobile.nebulaappproxy.api.rpc.H5AppRpcUpdate"), "matchVersion", new Object[]{loader.loadClass("com.alipay.mobile.h5container.api.H5Page"), Map.class, String.class, XC_MethodReplacement.returnConstant(Boolean.valueOf(false))});
            Log.i(TAG, "hook matchVersion successfully");
        } catch (Throwable t) {
            Log.i(TAG, "hook matchVersion err:");
            Log.printStackTrace(TAG, t);
        }
    }

    public static void saveRestartLog(String s) {
        try {
            Intent intent = new Intent(QiufengBroadcastReceiver.TAG);
            intent.putExtra("action", "restart");
            intent.putExtra("msg", s);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            service.sendBroadcast(intent);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(service).edit();
            editor.putLong("xqe", System.currentTimeMillis());
            editor.apply();
            editor.commit();
        } catch (Throwable t) {
            Log.other(t.getMessage() + ":" + android.util.Log.getStackTraceString(t));
        }
        if (s == null || !s.contains("服务")) {
            Process.killProcess(Process.myPid());
        }
    }

    public static void startWithLock(boolean... args) {
        boolean flag = false;
        if (args != null && args.length > 0) {
            flag = args[0];
        }
        String msg;
        if (flag) {
            msg = "支付宝服务被销毁，重启成功(锁屏)";
        } else {
            msg = "登录超时，重启成功(锁屏)";
        }
        switch (Config.getAutoRestart()) {
            case APP:
                saveRestartLog(msg);
                break;
            case ROOT:
                if (!unlock) {
                    saveRestartLog(msg);
                } else {
                    acquirePowerManager();
                    StringBuilder sb = new StringBuilder();
                    sb.append("input keyevent 224 \n ");
                    sb.append("sleep 1 \n ");
                    sb.append("input swipe 400 1500 400 200 \n ");
                    sb.append("input swipe 400 1000 400 200 \n ");
                    sb.append("sleep 1 \n ");
                    if (!Config.getXedgeproData().isEmpty()) {
                        sb.append("input text ");
                        sb.append(Config.getXedgeproData());
                        sb.append(" \n ");
                        sb.append("sleep 1 \n ");
                        sb.append("input keyevent 66 \n ");
                        sb.append("sleep 1 \n ");
                    }
                    sb.append("am force-stop ");
                    sb.append("com.eg.android.AlipayGphone");
                    sb.append(" \n ");
                    sb.append("sleep 1 \n ");
                    sb.append("am start -n ");
                    sb.append("com.eg.android.AlipayGphone");
                    sb.append("/");
                    sb.append("com.eg.android.AlipayGphone.AlipayLogin");
                    sb.append(" \n ");
                    sb.append("sleep 8 \n ");
                    sb.append("input keyevent 3 \n input keyevent 223 \n ");
                    RootManager.getInstance().exec(sb.toString());
                }
                break;
            case SERVICE:
                ࠄ();
                break;
            default:
                break;
        }
    }

    public static ClassLoader getLoader() {
        return loader;
    }

    public static void startNoLock(boolean... args) {
        boolean flag = false;
        if (args != null && args.length > 0) {
            flag = args[0];
        }
        String msg;
        if (flag) {
            msg = "支付宝服务被销毁，重启成功(解锁)";
        } else {
            msg = "登录超时，重启成功(解锁)";
        }
        switch (Config.getAutoRestart2()) {
            case APP:
                saveRestartLog(msg);
                break;
            case ROOT:
                if (!unlock) {
                    saveRestartLog(msg);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("am force-stop ");
                    sb.append("com.eg.android.AlipayGphone");
                    sb.append(" \n sleep 1 \n ");
                    sb.append("am start -n ");
                    sb.append("com.eg.android.AlipayGphone");
                    sb.append("/");
                    sb.append("com.eg.android.AlipayGphone.AlipayLogin");
                    sb.append(" --es qiufeng \"1\" \n ");
                    RootManager.getInstance().exec(sb.toString());
                }
                break;
            case SERVICE:
                ࠄ();
                break;
            default:
                break;
        }
    }

    public static Service getService() {
        return service;
    }

    private static void acquirePowerManager() {
        ((PowerManager) service.getSystemService(Context.POWER_SERVICE)).newWakeLock(268435462, TAG).acquire();
    }

    private static void ࠄ() {
        if (!unlock) {
            Intent intent = new Intent();
            intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.android.launcher.service.LauncherService");
            PendingIntent pendingIntent = PendingIntent.getService(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ((AlarmManager) service.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000L, pendingIntent);
            if (Build.VERSION.SDK_INT >= 26) {
                service.startForegroundService(intent);
            } else {
                service.startService(intent);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("am force-stop ");
            sb.append("com.eg.android.AlipayGphone");
            sb.append(" \n sleep 2 \n ");
            if (Build.VERSION.SDK_INT >= 26) {
                sb.append("am startforegroundservice ");
            } else {
                sb.append("am startservice ");
            }
            sb.append("com.eg.android.AlipayGphone");
            sb.append("/");
            sb.append("com.alipay.android.launcher.service.LauncherService");
            sb.append(" \n ");
            RootManager.getInstance().exec(sb.toString());
        }
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if ("pansong291.xposed.quickenergy.qiufeng".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(MainActivity.class.getName(), lpparam.classLoader, "setModuleActive", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = true;
                }
            });
        }
        if ("com.eg.android.AlipayGphone".equals(lpparam.packageName)
                && "com.eg.android.AlipayGphone".equals(lpparam.processName)) {
            Log.i(TAG, lpparam.packageName);
            new HookTask(lpparam.classLoader).hookOnCreate();
            new morn.xposed.alipay.tasks.ࠄ(lpparam.classLoader).hook();
            hookRpcCall(lpparam.classLoader);
            new MainTask(lpparam.classLoader).hook();
            new FarmGameTask(lpparam.classLoader).hook();
            new HookStepTask(lpparam.classLoader).hook();
        }
        if ("com.eg.android.AlipayGphone".equals(lpparam.packageName)) {
            new LauncherTask(lpparam.classLoader).start();
        }
    }

}