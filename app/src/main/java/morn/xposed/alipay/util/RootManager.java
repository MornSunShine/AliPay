package morn.xposed.alipay.util;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class RootManager {
    private static final String TAG = "pansong291.xposed.quickenergy.ࠊ.ࠇ";
    private static RootManager instance;
    private static final Object lock = new Object();
    String suCommand;
    OutputStream outputStream;
    Process process;

    private RootManager(String suCommand) {
        this.suCommand = suCommand;
        init();
    }

    public static int runCommand(Context context, String command, StringBuilder result) {
        MyThread thread = new MyThread(new File(context.getCacheDir(), "secopt.sh"), command, result);
        thread.start();
        try {
            thread.join(40000L);
            if (thread.isAlive()) {
                thread.interrupt();
                thread.join(150L);
                thread.destroy();
                thread.join(50L);
            }
        } catch (InterruptedException ignored) {
        }
        return thread.flag;
    }

    public static RootManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                try {
                    instance = new RootManager("su");
                } catch (Exception e1) {
                    try {
                        instance = new RootManager("/system/xbin/su");
                    } catch (Exception e2) {
                        try {
                            instance = new RootManager("/system/bin/su");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return instance;
    }

    public static boolean stop(Context context) {
        StringBuilder result = new StringBuilder();
        try {
            if (runCommand(context, "exit 0", result) == 0) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void init() {
        try {
            if (process != null && outputStream != null) {
                outputStream.flush();
                outputStream.close();
                process.destroy();
            }
            process = Runtime.getRuntime().exec(suCommand);
            outputStream = process.getOutputStream();
            runCommand("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runCommand(String command) {
        try {
            String s = command + "\n";
            outputStream.write(s.getBytes("ASCII"));
            outputStream.flush();
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
            try {
                init();
            } catch (Exception e1) {
                Log.printStackTrace(TAG, e1);
            }
        }
    }

    public void exec(String command) {
        runCommand(command);
    }

    private static final class MyThread extends Thread {
        private final File targetFile;
        private final String command;
        private final StringBuilder result;
        public int flag = -1;
        private Process process;

        public MyThread(File file, String s, StringBuilder result) {
            targetFile = file;
            this.command = s;
            this.result = result;
        }

        public void destroy() {
            try {
                if (this.process != null) {
                    this.process.destroy();
                }
                this.process = null;
            } catch (Exception ignored) {
            }
        }

        public void run() {
            try {
                targetFile.createNewFile();
                Process process = Runtime.getRuntime().exec("chmod 777 "
                        + targetFile.getAbsolutePath());
                process.waitFor();
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(targetFile));
                if (new File("/system/bin/sh").exists()) {
                    writer.write("#!/system/bin/sh\n");
                }
                writer.write(this.command);
                if (!this.command.endsWith("\n")) {
                    writer.write("\n");
                }
                writer.write("exit\n");
                writer.flush();
                writer.close();
                this.process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(this.process.getOutputStream());
                os.writeBytes(targetFile.getAbsolutePath());
                os.flush();
                os.close();
                InputStreamReader in = new InputStreamReader(this.process.getInputStream());
                char[] bytes = new char[1024];
                int count;
                while ((count = in.read(bytes)) != -1) {
                    if (this.result != null) {
                        this.result.append(bytes, 0, count);
                    }
                }
                in = new InputStreamReader(this.process.getErrorStream());
                while ((count = in.read(bytes)) != -1) {
                    if (this.result != null) {
                        this.result.append(bytes, 0, count);
                    }
                }
                if (this.process != null) {
                    this.flag = this.process.waitFor();
                }
            } catch (InterruptedException e) {
                if (this.result != null) {
                    this.result.append("\n").append(e);
                }
            } catch (Exception e) {
                if (this.result != null) {
                    this.result.append("\nOperation timed-out");
                }
            } finally {
                destroy();
            }
        }
    }
}