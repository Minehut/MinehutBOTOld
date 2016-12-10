package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class Chat {

    public static boolean logRemove = true;

    public static void sendDiscordMessage(String message) {
        try {
            Core.getDiscord().getChannelByID(Core.discordLogChatID).sendMessage(message);
        } catch (RateLimitException e) {
            Core.log.error("Sending messages too quickly!");
            e.printStackTrace();
        } catch (DiscordException e) {
            Core.log.error(e.getErrorMessage());
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            Core.log.error("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    public static void sendDiscordMessage(String message, IChannel channel) {
        try {
            Core.getDiscord().getChannelByID(channel.getID()).sendMessage(message);
        } catch (RateLimitException e) {
            Core.log.error("Sending messages too quickly!");
            e.printStackTrace();
        } catch (DiscordException e) {
            Core.log.error(e.getErrorMessage());
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            Core.log.error("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    public static void removeMessage(IMessage message) {
        RequestBuffer.request(() -> {
            try {
                message.delete();
            } catch (MissingPermissionsException e) {
                // Ignore
            } catch (DiscordException e) {
                Core.log.error("Could not erase message!", e);
            }
        });

        logRemove = true;
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
