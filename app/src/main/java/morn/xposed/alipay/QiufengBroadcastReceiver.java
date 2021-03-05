package morn.xposed.alipay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import morn.xposed.alipay.util.Log;

public class QiufengBroadcastReceiver extends BroadcastReceiver {
    public static String TAG = "qiufeng";

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getStringExtra("action");
        final String msg = intent.getStringExtra("msg");
        Log.recordLog("Qiufeng接收到广播，action：" + action + "，msg：" + msg);
        if (!TextUtils.isEmpty(action) && action.equals("restart")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Intent it = new Intent("android.intent.action.VIEW");
                        it.setClassName("com.eg.android.AlipayGphone", "com.eg.android.AlipayGphone.AlipayLogin");
                        it.putExtra("qiufeng", "1");
                        if (msg != null) {
                            it.putExtra("msg", msg);
                        }
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(it);
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
        }
    }
}