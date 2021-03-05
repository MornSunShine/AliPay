package morn.xposed.alipay.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import morn.xposed.alipay.AntFarm;
import morn.xposed.alipay.util.Config;

public class ChoiceDialog {
    private static AlertDialog sendTypeDialog;
    private static AlertDialog recallAnimalDialog;

    private static AlertDialog getRecallAnimalTypeDialog(Context context, CharSequence title) {
        if (recallAnimalDialog == null) {
            recallAnimalDialog = new Builder(context)
                    .setTitle(title)
                    .setSingleChoiceItems(Config.RecallAnimalType.nickNames, Config.getRecallAnimalType().ordinal(), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Config.setRecallAnimalType(i);
                        }
                    })
                    .setPositiveButton("确定", null)
                    .create();
        }
        return recallAnimalDialog;
    }

    private static AlertDialog getSendTypeDialog(Context context, CharSequence title) {
        if (sendTypeDialog == null) {
            sendTypeDialog = new Builder(context)
                    .setTitle(title)
                    .setSingleChoiceItems(AntFarm.SendType.nickNames, Config.getSendType().ordinal(), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Config.setSendType(i);
                        }
                    })
                    .setPositiveButton("确定", null)
                    .create();
        }
        return sendTypeDialog;
    }

    public static void showRecallAnimalDialog(Context context, CharSequence title) {
        try {
            getRecallAnimalTypeDialog(context, title).show();
        } catch (Exception e) {
            recallAnimalDialog = null;
            getRecallAnimalTypeDialog(context, title).show();
        }
    }

    public static void front(Context context, CharSequence title) {
        Builder builder = new Builder(context).setTitle(title);
        int i = Config.getAutoRestart().ordinal();
        builder.setSingleChoiceItems(new String[]{"关闭", "非root前台(需给予支付宝和秋风[自启动、锁屏显示、后台弹出界面]权限)", "root前台(需锁屏密码)", "服务重启(基本失效)"}, i, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Config.setAutoRestart(Config.RestartType.values()[i]);
            }
        }).setPositiveButton("确定", null).create().show();
    }

    public static void backend(Context context, CharSequence title) {
        Builder builder = new Builder(context).setTitle(title);
        int i = Config.getAutoRestart2().ordinal();
        builder.setSingleChoiceItems(new String[]{"关闭", "非root伪后台(需给予支付宝和秋风[自启动、锁屏显示、后台弹出界面]权限)", "root伪后台", "服务重启(基本失效)"}, i, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Config.setAutoRestart2(Config.RestartType.values()[i]);
            }
        }).setPositiveButton("确定", null).create().show();
    }

    public static void showSendTypeDialog(Context context, CharSequence title) {
        try {
            getSendTypeDialog(context, title).show();
        } catch (Exception e) {
            sendTypeDialog = null;
            getSendTypeDialog(context, title).show();
        }
    }
}