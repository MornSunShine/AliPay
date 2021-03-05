package morn.xposed.alipay.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import morn.xposed.alipay.util.Config;

public class EditDialog {
    private static AlertDialog dialog;
    private static EditText edt;
    private static EditMode mode;

    private static AlertDialog getEditDialog(Context c) {
        if (dialog == null) {
            edt = new EditText(c);
            dialog = new Builder(c).setTitle("title").setView(edt).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        String text = edt.getText().toString();
                        int val = Integer.parseInt(text);
                        switch (mode) {
                            case PIN:
                                Config.setPin(text);
                                break;
                            case XEDGEPRO_DATA:
                                Config.setXedgeproData(text);
                                break;
                            case CHECK_INTERVAL:
                                if (val > 0)
                                    Config.setCheckInterval(val * 60_000);
                                break;
                            case THREAD_COUNT:
                                if (val >= 0)
                                    Config.setThreadCount(val);
                                break;
                            case ADVANCE_TIME:
                                Config.setAdvanceTime(val);
                                break;
                            case COLLECT_INTERVAL:
                                if (val >= 0)
                                    Config.setCollectInterval(val);
                                break;
                            case COLLECT_TIMEOUT:
                                if (val > 0)
                                    Config.setCollectTimeout(val * 1_000);
                                break;
                            case RETURN_WATER_30:
                                if (val >= 0)
                                    Config.setReturnWater30(val);
                                break;
                            case RETURN_WATER_20:
                                if (val >= 0)
                                    Config.setReturnWater20(val);
                                break;
                            case RETURN_WATER_10:
                                if (val >= 0)
                                    Config.setReturnWater10(val);
                                break;
                            case MIN_EXCHANGE_COUNT:
                                if (val >= 0)
                                    Config.setMinExchangeCount(val);
                                break;
                            case LATEST_EXCHANGE_TIME:
                                if (val >= 0 && val < 24)
                                    Config.setLatestExchangeTime(val);
                                break;
                            case HOOK_STEP:
                                Config.setHookStep(Math.min(val, 100000));
                                break;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }).create();
        }
        String str = "";
        switch (mode) {
            case PIN:
                str = Config.getPin();
                break;
            case XEDGEPRO_DATA:
                str = Config.getXedgeproData();
                break;
            case CHECK_INTERVAL:
                str = String.valueOf(Config.getCheckInterval() / 60_000);
                break;

            case THREAD_COUNT:
                str = String.valueOf(Config.getThreadCount());
                break;

            case ADVANCE_TIME:
                str = String.valueOf(Config.getAdvanceTime());
                break;

            case COLLECT_INTERVAL:
                str = String.valueOf(Config.getCollectInterval());
                break;

            case COLLECT_TIMEOUT:
                str = String.valueOf(Config.getCollectTimeout() / 1_000);
                break;

            case RETURN_WATER_30:
                str = String.valueOf(Config.getReturnWater30());
                break;

            case RETURN_WATER_20:
                str = String.valueOf(Config.getReturnWater20());
                break;

            case RETURN_WATER_10:
                str = String.valueOf(Config.getReturnWater10());
                break;

            case MIN_EXCHANGE_COUNT:
                str = String.valueOf(Config.getMinExchangeCount());
                break;

            case LATEST_EXCHANGE_TIME:
                str = String.valueOf(Config.getLatestExchangeTime());
                break;
            case HOOK_STEP:
                str = String.valueOf(Config.getHookStep());
                break;
        }
        edt.setText(str);
        return dialog;
    }

    public static void showEditDialog(Context c, CharSequence title, EditMode em) {
        mode = em;
        try {
            getEditDialog(c).show();
        } catch (Throwable t) {
            dialog = null;
            getEditDialog(c).show();
        }
        dialog.setTitle(title);
    }

    public enum EditMode {
        PIN, XEDGEPRO_DATA, CHECK_INTERVAL, THREAD_COUNT, ADVANCE_TIME,
        COLLECT_INTERVAL, COLLECT_TIMEOUT, RETURN_WATER_30, RETURN_WATER_20, RETURN_WATER_10,
        MIN_EXCHANGE_COUNT, LATEST_EXCHANGE_TIME, HOOK_STEP, ENERGY_DOUBLE_CLICK
    }
}