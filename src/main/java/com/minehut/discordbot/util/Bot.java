package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class Bot {

    public static void updateUsers() {
        if (Core.getClient().getGuilds().contains(Core.getClient().getGuildById("239599059415859200"))) { //Minehut
            Core.getDiscord().streaming(getMainGuild().getMembers().size() + " Minehut users!", "https://minehut.com");
        } else {
            Core.getDiscord().streaming("the with my code", "https://minehut.com");
        }
    }

    public static Guild getMainGuild() {
        return Core.getClient().getGuildById(Core.getConfig().getMainGuildID());
    }

    public static TextChannel getLogChannel() {
        return Core.getDiscord().getChannelByID(Core.getConfig().getLogChannelID());
    }

    public static boolean isTrusted(User user) {
        for (String role : Core.getConfig().getTrustedRoles()) {
            if (Core.getDiscord().userHasRoleId(getMainGuild(), user, role)) {
                return true;
            }
        }
        return false;
    }

    public static String millisToString(long millis) {
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
            sb.append(":");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(":");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(":");
        }
        sb.append(seconds);

        return (sb.toString());
    }

}
