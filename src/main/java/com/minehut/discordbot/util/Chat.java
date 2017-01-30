package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class Chat {

    public static boolean logRemove = true;

    public static final Color CUSTOM_BLUE = new Color(66, 173, 244);
    public static final Color CUSTOM_RED = new Color(244, 78, 66);
    public static final Color CUSTOM_GREEN = new Color(64, 192, 61);

    public static IMessage sendMessage(EmbedBuilder embed, IChannel channel) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(Core.getDiscord()).withEmbed(embed.build())
                        .withChannel(channel).send();
            } catch (DiscordException | MissingPermissionsException e) {
                Core.log.error("Something went wrong!", e);
            }
            return null;
        });
        return future.get();
    }

    public static IMessage sendMessage(EmbedBuilder embed, IChannel channel, int removeTime) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return setAutoDelete(new MessageBuilder(Core.getDiscord()).withEmbed(embed.build())
                        .withChannel(channel).send(), removeTime);
            } catch (DiscordException | MissingPermissionsException e) {
                Core.log.error("Something went wrong!", e);
            }
            return null;
        });
        return future.get();
    }

    public static String getFullName(IUser user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    public static EmbedBuilder getEmbed() {
        return new EmbedBuilder().withColor(CUSTOM_BLUE).setLenient(true); //Default blue
    }

    public static IMessage sendMessage(CharSequence message, IChannel channel) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return channel.sendMessage(message.toString().substring(0, Math.min(message.length(), 1999)));
            } catch (DiscordException | MissingPermissionsException e) {
                Core.log.error("Something went wrong!", e);
            }
            return null;
        });
        return future.get();
    }

    public static IMessage sendMessage(CharSequence message, IChannel channel, int removeTime) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return setAutoDelete(channel.sendMessage(message.toString().substring(0, Math.min(message.length(), 1999))), removeTime);
            } catch (DiscordException | MissingPermissionsException e) {
                Core.log.error("Something went wrong!", e);
            }
            return null;
        });
        return future.get();
    }

    public static void editMessage(String s, EmbedBuilder embed, IMessage message, int removeTime) {
        RequestBuffer.request(() -> {
            try {
                setAutoDelete(message.edit(s, embed.build()), removeTime);
            } catch (MissingPermissionsException | DiscordException e) {
                Core.log.error("Could not edit own message + embed!", e);
            }
        });

    }

    public static void editMessage(String s, EmbedBuilder embed, IMessage message) {
        RequestBuffer.request(() -> {
            try {
                message.edit(s, embed.build());
            } catch (MissingPermissionsException | DiscordException e) {
                Core.log.error("Could not edit own message + embed!", e);
            }
        });

    }

    public static void editMessage(EmbedBuilder embed, IMessage message) {
        RequestBuffer.request(() -> {
            try {
                message.edit(message.getContent(), embed.build());
            } catch (MissingPermissionsException | DiscordException e) {
                Core.log.error("Could not edit own message + embed!", e);
            }
        });

    }

    public static void editMessage(EmbedBuilder embed, IMessage message, int removeTime) {
        RequestBuffer.request(() -> {
            try {
                setAutoDelete(message.edit(message.getContent(), embed.build()), removeTime);
            } catch (MissingPermissionsException | DiscordException e) {
                Core.log.error("Could not edit own message + embed!", e);
            }
        });

    }

    public static void editMessage(IMessage message, String content) {
        RequestBuffer.request(() -> {
            try {
                return message.edit(content);
            } catch (MissingPermissionsException | DiscordException e) {
                Core.log.error("Could not edit own message!", e);
            }
            return message;
        }).get();
    }

    public static void editMessage(IMessage message, String content, int removeTime) {
        RequestBuffer.request(() -> {
            try {
                return setAutoDelete(message.edit(content), removeTime);
            } catch (MissingPermissionsException | DiscordException e) {
                Core.log.error("Could not edit own message!", e);
            }
            return message;
        }).get();
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

    public static Timer timer = new Timer();

    public static IMessage setAutoDelete(IMessage message, int time) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeMessage(message);
            }
        }, time * 1000);
        return message;
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
