package com.minehut.discordbot.util;

import net.dv8tion.jda.core.utils.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author sedmelluq
 */
public class LoggerAdapter implements SimpleLog.LogListener {

    private static final Map<SimpleLog, Logger> logs = new WeakHashMap<>();

    public static void set() {
        SimpleLog.addListener(new LoggerAdapter());
        SimpleLog.LEVEL = SimpleLog.Level.OFF;
    }

    @Override
    public void onLog(SimpleLog simpleLog, SimpleLog.Level level, Object message) {
        Logger log = convert(simpleLog);
        switch (level) {
            case TRACE:
                if (log.isTraceEnabled()) log.trace(message.toString());
                break;
            case DEBUG:
                if (log.isDebugEnabled()) log.debug(message.toString());
                break;
            case INFO:
                log.info(message.toString());
                break;
            case WARNING:
                log.warn(message.toString());
                break;
            case FATAL:
                log.error(message.toString());
                break;
        }
    }

    @Override
    public void onError(SimpleLog simpleLog, Throwable throwable) {
        convert(simpleLog).error("An exception occurred", throwable);
    }

    private Logger convert(SimpleLog log) {
        return logs.computeIfAbsent(log, ignored -> LoggerFactory.getLogger(log.name));
    }
}
