package com.minehut.discordbot.util;

import com.minehut.discordbot.MinehutBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONArray;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class Bot {

    private static MinehutBot bot = MinehutBot.get();
    private static Config config = bot.getConfig();

    public static List<Message> nowPlaying = new ArrayList<>();

    public static Message getLoadingMessage() {
        return new MessageBuilder().setEmbed(Chat.getEmbed().addField("Gathering Information...", "This may take a few moments", true)
                .setColor(Chat.CUSTOM_ORANGE).build()).build();
    }

    public static void logGuildMessage(MessageBuilder message) {
        if (config.getLogChannelId() != null && !config.getLogChannelId().isEmpty())
            bot.getDiscordClient().getTextChannelById(config.getLogChannelId()).sendMessage(message.build()).queue();
    }

    public static User getCreator() {
        return bot.getDiscordClient().getUserById(118088732753526784L); //MatrixTunnel#7348
    }

    public static String getLogoUrl() {
        return bot.getDiscordClient().getSelfUser().getAvatarUrl();
    }

    public static String getMb(long bytes) {
        return (bytes / 1024 / 1024) + " MB";
    }

    public static void updateUsers() {
        if (bot.getDiscordClient().getGuilds().contains(getMainGuild())) {
            setGame(Game.of(Game.GameType.WATCHING, getMainGuild().getMembers().size() + " discord users!", "https://minehut.com"));
        } else {
            setGame(Game.of(Game.GameType.STREAMING, "my code", "https://www.twitch.tv/p/security"));
        }
    }

    public static Role getMutedRole() {
        return config.isMuteEnabled() ? getMainGuild().getRolesByName(config.getMuteRoleName(), true).isEmpty() ? null : getMainGuild().getRolesByName(config.getMuteRoleName(), true).get(0) : null;
    }

    public static Guild getMainGuild() {
        return bot.getDiscordClient().getGuildById(config.getMainGuildId());
    }

    public static String getBotTime() { //TODO getFormattedBotTime
        return formatTime(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
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

    public static String getMinecraftUsername(UUID minecraftUuid) {
        try {
            ResponseBody body = bot.getHttpClient().newCall(new Request.Builder()
                    .url("https://api.mojang.com/user/profiles/" + minecraftUuid.toString().replaceAll("-", "") + "/names")
                    .header("Accept", "application/json").build()).execute().body();

            if (body != null) {
                JSONArray json = new JSONArray(body.string());
                return json.getJSONObject(json.length() - 1).getString("name");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "null";
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
        if (days > 0)
            sb.append(days).append(format ? "d " : ":");

        if (hours > 0)
            sb.append(hours).append(format ? "h " : ":");

        sb.append(format ? minutes : minutes < 10 ? "0" + minutes : minutes)
                .append(format ? "m " : ":");

        sb.append(format ? seconds : seconds < 10 ? "0" + seconds : seconds)
                .append(format ? "s" : "");

        return (sb.toString());
    }

    public static VoiceChannel getConnectedVoiceChannel() {
        return getMainGuild().getAudioManager().isConnected() ? getMainGuild().getAudioManager().getConnectedChannel() : null;
    }

    public static boolean isReady() {
        return bot.getDiscordClient().getStatus().equals(JDA.Status.CONNECTED);
    }

    public static boolean userHasRoleId(Member member, String id) {
        Role role = bot.getDiscordClient().getRoleById(id);
        return role != null && member.getRoles().contains(role);
    }

    public static void setGame(Game game) {
        bot.getDiscordClient().getPresence().setGame(game);
    }

    public static JSONArray reverseJsonArray(JSONArray array) {
        List<Object> reverseArray = array.toList();
        Collections.reverse(reverseArray);
        return new JSONArray(reverseArray);
    }

}
