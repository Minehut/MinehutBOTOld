package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class Bot {

    public static List<Message> nowPlaying;

    public static String getLogo() {
        return Core.getClient().getSelfUser().getAvatarUrl();
    }

    public static String getMb(long bytes) {
        return (bytes / 1024 / 1024) + " MB";
    }

    public static void updateUsers() {
        if (Core.getClient().getGuilds().contains(Core.getClient().getGuildById("239599059415859200"))) { //Minehut
            Core.getDiscord().streaming(getMainGuild().getMembers().size() + " Discord users!", "https://minehut.com");
        } else {
            Core.getDiscord().streaming("with my code", "https://minehut.com");
        }
    }

    public static Guild getMainGuild() {
        return Core.getClient().getGuildById(Core.getConfig().getMainGuildID());
    }

    public static TextChannel getLogChannel() {
        return Core.getClient().getTextChannelById(Core.getConfig().getLogChannelID());
    }

    public static boolean isTrusted(User user) {
        for (String role : Core.getConfig().getTrustedRoles()) {
            if (Core.getDiscord().userHasRoleId(getMainGuild(), user, role)) {
                return true;
            }
        }
        return false;
    }

    public static String getBotTime() {
        return Bot.formatTime(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
    }

    public static String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("MMMM dd")) + getDayOfMonthSuffix(time.getDayOfMonth()) +
                " (" + time.format(DateTimeFormatter.ofPattern("EE")) + ") " + time.format(DateTimeFormatter.ofPattern("yyyy HH:mm:ss"));
    }

    private static String getDayOfMonthSuffix(final int n) {
        if (n < 1 || n > 31) throw new IllegalArgumentException("illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String getMCOwnerName(String UUID) {
        try {
            JSONArray json = URLJson.readJsonArrayFromUrl("https://api.mojang.com/user/profiles/" + UUID.replaceAll("-", "") + "/names");
            return json.getJSONObject(json.length() - 1).getString("name");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "null";
    }

    public static boolean hasInvite(Message message) {
        return Pattern.compile("(?:https?://)?discord(?:app\\.com/invite|\\.gg|\\.io)/(\\S+?)").matcher(message.getRawContent()).find();
    }

    public static String millisToTime(long millis, boolean format) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(format ? "d " : ":");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(format ? "h " : ":");
        }
        sb.append(format ? minutes : minutes < 10 && hours < 1 ? minutes : "0" + minutes);
        sb.append(format ? "m " : ":");

        sb.append(format ? seconds : seconds < 10 ? "0" + seconds : seconds);
        sb.append(format ? "s" : "");

        return (sb.toString());
    }

    public static JSONArray reverseJsonArray(JSONArray array) {
        List<Object> reverse = array.toList();
        Collections.reverse(reverse);
        return new JSONArray(reverse);
    }

}
