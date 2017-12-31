package com.minehut.discordbot.util;

import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.util.regex.Pattern;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class Chat {

    public static final Color CUSTOM_BLUE = new Color(66, 173, 244);
    public static final Color CUSTOM_RED = new Color(244, 78, 66);
    public static final Color CUSTOM_ORANGE = new Color(236, 150, 43);
    public static final Color CUSTOM_DARK_ORANGE = new Color(255, 111, 0);
    public static final Color CUSTOM_GREEN = new Color(64, 192, 61);
    public static final Color CUSTOM_DARK_GREEN = new Color(56, 128, 53);
    public static final Color CUSTOM_GRAY = new Color(98, 98, 98);
    public static final Color CUSTOM_PURPLE = new Color(151, 74, 191);

    public static String getFullName(User user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    public static boolean hasInvite(Message message) {
        return Pattern.compile("(?:https?://)?discord(?:app\\.com/invite|\\.gg|\\.io)/(\\S+?)").matcher(message.getContentRaw()).find();
    }

    public static boolean hasRegex(Pattern regex, String imput) {
        return regex.matcher(imput).find();
    }

    public static EmbedBuilder getEmbed() {
        return new EmbedBuilder().setColor(CUSTOM_BLUE); //Default blue
    }

    public static void sendPM(User user, String content) {
        user.openPrivateChannel().queue(c -> c.sendMessage(content).queue(m -> m.getPrivateChannel().close().queue()));
    }

    public static Message respondMessage(Member sender, MessageEmbed embed) {
        return new MessageBuilder().append(sender.getAsMention()).setEmbed(new EmbedBuilder(embed).setFooter(null, null).build()).build();
    }

    public static void removeMessage(Message message) {
        if (message != null) message.delete().queue();
    }

    public static void removeMessage(Message message, long time) {
        if (message != null) {
            new BotTask("DeleteMessageTask") {
                @Override
                public void run() {
                    removeMessage(message);
                }
            }.delay(time * 1000);
        }
    }

    public static void editMessage(String content, Message message) {
        if (message != null) message.editMessage(content).queue();
    }

    public static void editMessage(CharSequence string, Message message, long time) {
        if (message != null) editMessage(new MessageBuilder().append(string.toString()).build(), message, time);
    }

    public static void editMessage(MessageEmbed embed, Message message, long time) {
        if (message != null) editMessage(new MessageBuilder().setEmbed(embed).build(), message, time);
    }

    public static void editMessage(Message msg, Message message, long time) {
        if (message != null) message.editMessage(msg).queue(m -> removeMessage(m, time));
    }

    public static void sendMessage(CharSequence string, MessageChannel channel, long time) {
        sendMessage(new MessageBuilder().append(string.toString()).build(), channel, time);
    }

    public static void sendMessage(MessageEmbed embed, MessageChannel channel, long time) {
        sendMessage(new MessageBuilder().setEmbed(embed).build(), channel, time);
    }

    public static void sendMessage(Message message, MessageChannel channel, long time) {
        channel.sendMessage(message).queue(m -> removeMessage(m, time));
    }

}
