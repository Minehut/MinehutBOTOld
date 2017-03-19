package com.minehut.discordbot.util.tasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ayy it repeats
 * <br>
 * Created by Arsen on 20.9.16.
 * Changed by MatrixTunnel on 2/8/2017.
 */
public class Scheduler {
    private static final ScheduledExecutorService timer = Executors.newScheduledThreadPool(10, r -> new Thread(r, "Bot Scheduled Task"));
    private static final Map<String, ScheduledFuture<?>> tasks = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(timer::shutdownNow));
    }

    public static boolean scheduleRepeating(Runnable task, String taskName, long delay, long interval) {
        if (tasks.containsKey(taskName)) {
            return false;
        }
        tasks.put(taskName,
                timer.scheduleAtFixedRate(task, delay, interval, TimeUnit.MILLISECONDS));
        return true;
    }

    public static void delayTask(Runnable task, long delay) {
        timer.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public static boolean cancelTask(String taskName) {
        Iterator<Map.Entry<String, ScheduledFuture<?>>> i = tasks.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, ScheduledFuture<?>> next = i.next();
            if (next.getKey().equals(taskName)) {
                next.getValue().cancel(false);
                i.remove();
                return true;
            }
        }
        return false;
    }
}