package morn.xposed.alipay.util;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileUtils {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠊ.ࠂ";
    private static File androidDirectory;
    private static File configDirectory;
    private static File configFile;
    private static File friendIdMapFile;
    private static File cooperationIdMapFile;
    private static File exportedStatisticsFile;
    private static File statisticsFile;
    private static File forestLogFile;
    private static File farmLogFile;
    private static File otherLogFile;
    private static File friendLogFile;
    private static File simpleLogFile;
    private static File runtimeLogFile;

    public static File getAndroidDirectoryFile() {
        if (androidDirectory == null) {
            androidDirectory = new File(Environment.getExternalStorageDirectory(), "Android");
            if (!androidDirectory.exists()) {
                androidDirectory.mkdirs();
            }
        }
        return androidDirectory;
    }

    public static File getBackupFile(File f) {
        return new File(f.getAbsolutePath() + ".bak");
    }

    public static void close(Closeable c, File f) {
        try {
            if (c != null) c.close();
        } catch (Throwable t) {
            if (!f.equals(getRuntimeLogFile()))
                Log.printStackTrace(TAG, t);
        }
    }

    public static boolean copyTo(File f1, File f2) {
        return write2File(readFromFile(f1), f2);
    }

    public static boolean append2RuntimeLogFile(String s) {
        if (getRuntimeLogFile().length() > 31_457_280) // 30MB
            getRuntimeLogFile().delete();
        return append2File(Log.getFormatDateTime() + "  " + s + "\n", getRuntimeLogFile());
    }

    public static boolean append2File(String s, File f) {
        boolean success = false;
        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
            fw.append(s);
            fw.flush();
            success = true;
        } catch (Throwable t) {
            if (!f.equals(getRuntimeLogFile()))
                Log.printStackTrace(TAG, t);
        }
        close(fw, f);
        return success;
    }

    public static File getConfigDirectoryFile() {
        if (configDirectory == null) {
            configDirectory = new File(getAndroidDirectoryFile(), "data/pansong291.xposed.quickenergy.qiufeng");
            if (configDirectory.exists()) {
                if (configDirectory.isFile()) {
                    configDirectory.delete();
                    configDirectory.mkdirs();
                }
            } else {
                configDirectory.mkdirs();
            }
        }
        return configDirectory;
    }

    public static String readFromFile(File f) {
        StringBuilder result = new StringBuilder();
        FileReader fr = null;
        try {
            fr = new FileReader(f);
            char[] chs = new char[1024];
            int len = 0;
            while ((len = fr.read(chs)) >= 0) {
                result.append(chs, 0, len);
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG, t);
        }
        close(fr, f);
        return result.toString();
    }

    public static boolean append2SimpleLogFile(String s) {
        if (getSimpleLogFile().length() > 31_457_280) // 30MB
            getSimpleLogFile().delete();
        return append2File(Log.getFormatDateTime() + "  " + s + "\n", getSimpleLogFile());
    }

    public static boolean write2File(String s, File f) {
        boolean success = false;
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write(s);
            fw.flush();
            success = true;
        } catch (Throwable t) {
            if (!f.equals(getRuntimeLogFile()))
                Log.printStackTrace(TAG, t);
        }
        close(fw, f);
        return success;
    }

    public static File getConfigFile() {
        if (configFile == null) {
            configFile = new File(getConfigDirectoryFile(), "config.json");
            if (configFile.exists() && configFile.isDirectory())
                configFile.delete();
        }
        return configFile;
    }

    public static File getCooperationIdMapFile() {
        if (cooperationIdMapFile == null) {
            cooperationIdMapFile = new File(getConfigDirectoryFile(), "cooperationId.list");
            if (cooperationIdMapFile.exists() && cooperationIdMapFile.isDirectory())
                cooperationIdMapFile.delete();
        }
        return cooperationIdMapFile;
    }

    public static File getStatisticsFile() {
        if (statisticsFile == null) {
            statisticsFile = new File(getConfigDirectoryFile(), "statistics.json");
            if (statisticsFile.exists() && statisticsFile.isDirectory())
                statisticsFile.delete();
        }
        return statisticsFile;
    }

    public static File getFarmLogFile() {
        if (farmLogFile == null) {
            farmLogFile = new File(getConfigDirectoryFile(), "farm.txt");
            if (farmLogFile.exists()) {
                if (farmLogFile.isDirectory()) {
                    farmLogFile.delete();
                }
            } else {
                try {
                    farmLogFile.createNewFile();
                } catch (Throwable ignored) {
                }
            }
        }
        return farmLogFile;
    }

    public static File getForestLogFile() {
        if (forestLogFile == null) {
            forestLogFile = new File(getConfigDirectoryFile(), "forest.txt");
            if (forestLogFile.exists()) {
                if (forestLogFile.isDirectory())
                    forestLogFile.delete();
            } else {
                try {
                    forestLogFile.createNewFile();
                } catch (Throwable ignored) {
                }
            }
        }
        return forestLogFile;
    }

    public static File getFriendIdMapFile() {
        if (friendIdMapFile == null) {
            friendIdMapFile = new File(getConfigDirectoryFile(), "friendId.list");
            if (friendIdMapFile.exists() && friendIdMapFile.isDirectory())
                friendIdMapFile.delete();
        }
        return friendIdMapFile;
    }

    public static File getFriendFile() {
        if (friendLogFile == null) {
            friendLogFile = new File(getConfigDirectoryFile(), "friend.txt");
            if (friendLogFile.exists()) {
                if (friendLogFile.isDirectory())
                    friendLogFile.delete();
            } else {
                try {
                    friendLogFile.createNewFile();
                } catch (Throwable ignored) {
                }
            }
        }
        return friendLogFile;
    }

    public static File getOtherLogFile() {
        if (otherLogFile == null) {
            otherLogFile = new File(getConfigDirectoryFile(), "other.txt");
            if (otherLogFile.exists()) {
                if (otherLogFile.isDirectory())
                    otherLogFile.delete();
            } else {
                try {
                    otherLogFile.createNewFile();
                } catch (Throwable t) {
                }
            }
        }
        return otherLogFile;
    }

    public static File getRankFile() {
        File file = new File(getConfigDirectoryFile(), "rank.json");
        if ((file.exists()) && (file.isDirectory())) {
            file.delete();
        }
        return file;
    }

    public static File getRuntimeLogFile() {
        if (runtimeLogFile == null) {
            runtimeLogFile = new File(getConfigDirectoryFile(), "runtime.txt");
            if (runtimeLogFile.exists() && runtimeLogFile.isDirectory())
                runtimeLogFile.delete();
        }
        return runtimeLogFile;
    }

    public static File getSimpleLogFile() {
        if (simpleLogFile == null) {
            simpleLogFile = new File(getConfigDirectoryFile(), "simple.txt");
            if (simpleLogFile.exists() && simpleLogFile.isDirectory())
                simpleLogFile.delete();
        }
        return simpleLogFile;
    }

    public static File getExportedStatisticsFile() {
        if (exportedStatisticsFile == null) {
            exportedStatisticsFile = new File(getAndroidDirectoryFile(), "statistics.json");
            if (exportedStatisticsFile.exists() && exportedStatisticsFile.isDirectory())
                exportedStatisticsFile.delete();
        }
        return exportedStatisticsFile;
    }
}