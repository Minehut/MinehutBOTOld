package com.minehut.discordbot.util;

import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

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

    public static EmbedBuilder getEmbed() {
        return new EmbedBuilder().setColor(CUSTOM_BLUE); //Default blue
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

    public static void editMessage(String content, Message message, long time) {
        if (message != null) message.editMessage(content).queue(m -> removeMessage(m, time));
    }

    public static void editMessage(EmbedBuilder builder, Message message) {
        if (message != null) message.editMessage(builder.build()).queue();
    }

    public static void editMessage(EmbedBuilder builder, Message message, long time) {
        if (message != null) message.editMessage(builder.build()).queue(m -> removeMessage(m, time));
    }

    public static void sendMessage(CharSequence message, MessageChannel channel, long time) {
        sendMessage(new MessageBuilder().append(message.toString()).build(), channel, time);
    }

    public static void sendMessage(MessageEmbed embed, MessageChannel channel, long time) {
        sendMessage(new MessageBuilder().setEmbed(embed).build(), channel, time);
    }

    public static void sendMessage(Message message, MessageChannel channel, long time) {
        channel.sendMessage(message).queue(m -> removeMessage(m, time));
    }

    public static String getChannelName(Channel channel) {
        return "[#" + channel.getName() + "] ";
    }

}
