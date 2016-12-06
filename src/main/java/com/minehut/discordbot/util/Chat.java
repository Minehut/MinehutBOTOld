package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class Chat {

    public static void sendDiscordMessage(String message) {
        try {
            Core.getDiscord().getChannelByID(Core.discordLogChatID).sendMessage(message);
        } catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
            Core.log.error("Sending messages too quickly!");
            e.printStackTrace();
        } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
            Core.log.error(e.getErrorMessage());
            e.printStackTrace();
        } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
            Core.log.error("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    public static void sendDiscordMessage(String message, IChannel channel) {
        try {
            Core.getDiscord().getChannelByID(channel.getID()).sendMessage(message);
        } catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
            Core.log.error("Sending messages too quickly!");
            e.printStackTrace();
        } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
            Core.log.error(e.getErrorMessage());
            e.printStackTrace();
        } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
            Core.log.error("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    public static void removeMessage(IMessage message) {
        try {
            message.delete();
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }
    }

    public static String fixDiscordMentions(IMessage message) {
        String workingText = message.getContent();

        List<IUser> mentionedUsersList = message.getMentions();
        List<IChannel> mentionedChannelsList = message.getChannelMentions();
        List<IRole> mentionedRolesList = message.getRoleMentions();

        if (mentionedUsersList != null) {
            for (IUser user : mentionedUsersList) {
                workingText = workingText.replace("<@!" + user.getID() + ">", "@" + user.getDisplayName(message.getGuild()));
                workingText = workingText.replace("<@" + user.getID() + ">", "@" + user.getDisplayName(message.getGuild()));
            }
        }

        if (mentionedChannelsList != null) {
            for (IChannel channel : mentionedChannelsList) {
                workingText = workingText.replace("<#" + channel.getID() + ">", "#" + channel.getName());
            }
        }

        if (mentionedRolesList != null) {
            for (IRole role : mentionedRolesList) {
                workingText = workingText.replace("<@&" + role.getID() + ">", "@" + role.getName());
            }
        }

        return workingText;
    }

    public static String getChannelName(IChannel channel) {
        return "[" + channel.getName() + "] ";
    }

}
