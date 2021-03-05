package morn.xposed.alipay.util;

import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import de.robv.android.xposed.XposedBridge;

public class Log {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠊ.ࠄ";
    private static SimpleDateFormat sdf;

    public static void i(String tag, String s) {
        StringBuilder sb = new StringBuilder(tag + ": " + s);
        try {
            for (int i = 0; i < sb.length(); i += 2000) {
                if (sb.length() < i + 2000)
                    XposedBridge.log(sb.substring(i, sb.length()));
                else
                    XposedBridge.log(sb.substring(i, i + 2000));
            }
        } catch (Throwable t) {
            // when hooking self, this XposedBridge.class will
            // not be found, ignore it.
            android.util.Log.i(tag, s);
        }
        if (Config.getRecordRuntimeLog()) {
            FileUtils.append2RuntimeLogFile(sb.toString());
        }
    }

    public static void printStackTrace(String tag, Throwable t) {
        Log.i(tag, android.util.Log.getStackTraceString(t));
    }

    public static boolean forest(String s) {
        recordLog(s, "");
        return FileUtils.append2File(getFormatTime() + " " + s + "\n", FileUtils.getForestLogFile());
    }

    public static boolean farm(String s) {
        recordLog(s, "");
        return FileUtils.append2File(getFormatTime() + " " + s + "\n", FileUtils.getFarmLogFile());
    }

    public static boolean other(String s) {
        recordLog(s, "");
        return FileUtils.append2File(getFormatTime() + " " + s + "\n", FileUtils.getOtherLogFile());
    }

    public static boolean recordLog(String str, String str2) {
        Log.i(TAG, str + str2);
        if (!Config.getRecordLog()) return false;
        return FileUtils.append2SimpleLogFile(str);
    }

    public static boolean recordLog(String s) {
        return recordLog(s, "");
    }

    public static String getFormatDateTime() {
        if (sdf == null) sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String getFormatDate() {
        return getFormatDateTime().split(" ")[0];
    }

    public static String getFormatTime() {
        return getFormatDateTime().split(" ")[1];
    }

    public static int getWeekTimes(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(2);
        calendar.setMinimalDaysInFirstWeek(7);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static String translate(int g) {
        if (g < 1000) {
            return Integer.toString(g) + "g";
        }
        if (g < 1000_000) {
            return String.format("%.1f", g / 1000.0D) + "kg";
        } else {
            return String.format("%.1f", g / 1000_000.0D) + "t";
        }
    }

    public static int getWeekTimes() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(2);
        calendar.setMinimalDaysInFirstWeek(7);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}