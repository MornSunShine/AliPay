package morn.xposed.alipay;

import android.content.Context;
import android.widget.Toast;

import morn.xposed.alipay.hook.XposedHook;
import morn.xposed.alipay.util.Config;
import morn.xposed.alipay.util.Log;

public class AntForestToast {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠅ";
    public static Context context;

    public static void show(CharSequence cs) {
        try {
            if (context != null && Config.getShowToast()) {
                XposedHook.handler.post(
                        new Runnable() {
                            CharSequence cs;

                            public Runnable setData(CharSequence c) {
                                cs = c;
                                return this;
                            }

                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(context, cs, Toast.LENGTH_SHORT).show();
                                } catch (Throwable t) {
                                    Log.i(TAG, "show.run err:");
                                    Log.printStackTrace(TAG, t);
                                }
                            }
                        }.setData(cs));
            }
        } catch (Throwable t) {
            Log.i(TAG, "show err:");
            Log.printStackTrace(TAG, t);
        }
    }
}