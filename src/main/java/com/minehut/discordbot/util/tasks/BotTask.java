package com.minehut.discordbot.util.tasks;

/**
 * ayy it repeats
 * <br>
 * Created by Arsen on 20.9.16.
 * Changed by MatrixTunnel on 2/8/2017.
 */
public abstract class BotTask implements Runnable {

    private String taskName;

    private BotTask() {
    }

    public BotTask(String taskName) {
        this.taskName = taskName;
    }

    public boolean repeat(long delay, long interval) {
        return Scheduler.scheduleRepeating(this, taskName, delay, interval);
    }

    public void delay(long delay) {
        Scheduler.delayTask(this, delay);
    }

    public void command() {
        Scheduler.commandTask(this);
    }

    public boolean cancel() {
        return Scheduler.cancelTask(taskName);
    }
}
