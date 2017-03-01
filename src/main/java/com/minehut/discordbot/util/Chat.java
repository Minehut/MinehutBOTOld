package com.minehut.discordbot.util;

import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class Chat {

    public static boolean logRemove = true;

    public static final Color CUSTOM_BLUE = new Color(66, 173, 244);
    public static final Color CUSTOM_RED = new Color(244, 78, 66);
    public static final Color CUSTOM_ORANGE = new Color(236, 150, 43);
    public static final Color CUSTOM_GREEN = new Color(64, 192, 61);
    public static final Color CUSTOM_GRAY = new Color(98, 98, 98);
    public static final Color CUSTOM_PURPLE = new Color(151, 74, 191);

    public static Message sendMessage(EmbedBuilder embed, MessageChannel channel) {
        return channel.sendMessage(new MessageBuilder().setEmbed(embed.build()).build()).complete();
    }

    public static Message sendMessage(EmbedBuilder embed, MessageChannel channel, int removeTime) {
        return setAutoDelete(sendMessage(embed, channel), removeTime);
    }

    public static Message sendMessage(String message, EmbedBuilder embed, MessageChannel channel) {
        return channel.sendMessage(new MessageBuilder().append(message).setEmbed(embed.build()).build()).complete();
    }

    public static Message sendMessage(String message, EmbedBuilder embed, MessageChannel channel, int removeTime) {
        return setAutoDelete(sendMessage(message, embed, channel), removeTime);
    }

    public static String getFullName(User user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    public static EmbedBuilder getEmbed() {
        return new EmbedBuilder().setColor(CUSTOM_BLUE); //Default blue
    }

    public static Message sendMessage(CharSequence message, MessageChannel channel) {
        return channel.sendMessage(message.toString().substring(0, Math.min(message.length(), 1999))).complete();
    }

    public static Message sendMessage(CharSequence message, MessageChannel channel, int removeTime) {
        return setAutoDelete(sendMessage(message, channel), removeTime);
    }

    public static void editMessage(String s, EmbedBuilder embed, Message message, int removeTime) {
        message.editMessage(new MessageBuilder().append(s).setEmbed(embed.build()).build()).queue(m -> setAutoDelete(m, removeTime));
    }

    public static void editMessage(String s, EmbedBuilder embed, Message message) {
        if (message != null) {
            message.editMessage(new MessageBuilder().append(s).setEmbed(embed.build()).build()).queue();
        }
    }

    public static void editMessage(EmbedBuilder embed, Message message) {
        editMessage(message.getRawContent(), embed, message);
    }

    public static void editMessage(EmbedBuilder embed, Message message, int removeTime) {
        message.editMessage(new MessageBuilder().append(message.getRawContent()).setEmbed(embed.build()).build()).queue(m -> setAutoDelete(m, removeTime));
    }

    public static void editMessage(Message message, String content) {
        message.editMessage(content).queue();
    }

    public static void editMessage(Message message, String content, int removeTime) {
        message.editMessage(content).queue(m -> setAutoDelete(m, removeTime));
    }

    public static void removeMessage(Message message) {
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }

        logRemove = true;
    }

    public static Message setAutoDelete(Message message, int time) {
        new BotTask("Delete message " + message.getChannel().toString()) {
            @Override
            public void run() {
                removeMessage(message);
            }
        }.delay(time * 1000);
        return message;
    }

    /*
    public static String fixDiscordMentions(Message message) {
        String workingText = message.getContent();

        List<User> mentionedUsersList = message.getMentionedUsers();
        List<TextChannel> mentionedChannelsList = message.getMentionedChannels();
        List<Role> mentionedRolesList = message.getMentionedRoles();

        if (mentionedUsersList != null) {
            for (User user : mentionedUsersList) {
                workingText = workingText.replace("<@!" + user.getId() + ">", "@" + user.getDisplayName(message.getGuild()));
                workingText = workingText.replace("<@" + user.getId() + ">", "@" + user.getDisplayName(message.getGuild()));
            }
        }

        if (mentionedChannelsList != null) {
            for (Channel channel : mentionedChannelsList) {
                workingText = workingText.replace("<#" + channel.getId() + ">", "#" + channel.getName());
            }
        }

        if (mentionedRolesList != null) {
            for (Role role : mentionedRolesList) {
                workingText = workingText.replace("<@&" + role.getId() + ">", "@" + role.getName());
            }
        }

        return workingText;
    }
    */

    public static String getChannelName(Channel channel) {
        return "[" + channel.getName() + "] ";
    }

    public static String getContent(String[] args, int start, int end) {
        String content = "";
        for (int i = start; i < Math.min(end, args.length); i++) {
            content += args[i];
            if (i != Math.min(end, args.length) - 1) {
                content += " ";
            }
        }

        return content;
    }

}
