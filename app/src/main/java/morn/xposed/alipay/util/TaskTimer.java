package morn.xposed.alipay.util;

public class TaskTimer implements Comparable<TaskTimer> {
    private Runnable runnable;
    private long startTime;
    private boolean a;

    public TaskTimer(Runnable runnable, long startTime) {
        this.runnable = runnable;
        this.startTime = startTime;
        this.a = false;
    }

    public TaskTimer(Runnable runnable, long startTime, boolean a) {
        this.runnable = runnable;
        this.startTime = startTime;
        this.a = a;
    }

    public String toString() {
        double d = this.startTime - System.currentTimeMillis();
        return String.valueOf(d / 1000.0D);
    }

    @Override
    public int compareTo(TaskTimer timer) {
        return Long.compare(this.startTime, timer.startTime);
    }

    public long getStartTime() {
        return this.startTime;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public boolean getA() {
        return this.a;
    }
}