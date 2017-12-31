package com.minehut.discordbot.util.tasks;

import com.minehut.discordbot.MinehutBot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * ayy it repeats
 * <br>
 * Created by Arsen on 20.9.16.
 * Changed by MatrixTunnel on 2/8/2017.
 */
public class Scheduler {

    private static final ThreadGroup commands = new ThreadGroup("Command Threads");
    private static final ExecutorService command = Executors.newCachedThreadPool(r -> new Thread(commands, r, "Command Pool-" + commands.activeCount()));

    private static final ScheduledExecutorService timer = Executors.newScheduledThreadPool(10, r -> new Thread(r, "BotTask"));
    private static final Map<String, ScheduledFuture<?>> tasks = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(timer::shutdownNow));
        Runtime.getRuntime().addShutdownHook(new Thread(command::shutdownNow));
    }

    public static boolean scheduleRepeating(Runnable task, String taskName, long delay, long interval) {
        if (tasks.containsKey(taskName)) {
            return false;
        }
        tasks.put(taskName,
                timer.scheduleAtFixedRate(() -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        MinehutBot.log.error("Task \"" + taskName + "\" error!", e);
                    }
                }, delay, interval, TimeUnit.MILLISECONDS));
        return true;
    }

    public static void delayTask(Runnable task, long delay) {
        timer.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                MinehutBot.log.error("Error executing task", e);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public static void commandTask(Runnable task) {
        command.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                MinehutBot.log.error("Error executing command!", e);
            }
        });
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