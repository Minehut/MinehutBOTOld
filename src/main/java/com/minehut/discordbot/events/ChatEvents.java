package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by MatrixTunnel on 11/29/2016.
 * Some code used from FlareBot.
 */
public class ChatEvents extends ListenerAdapter {

    public static HashMap<String, String> messages;
    public static HashMap<String, Integer> amount;

    private static boolean filteredMessage(Message message) {
        Guild guild = message.getGuild();
        User user = message.getAuthor();
        TextChannel channel = message.getTextChannel();

        if (!GuildSettings.isTrusted(guild.getMember(user))) {
            if (message.getContent().length() >= Core.getConfig().getMaxMessageLength()) {
                EmbedBuilder builder = Chat.getEmbed().setDescription(":exclamation: Possible message spam - **" + Chat.getFullName(user) + "**")
                        .addField("User", user.getAsMention(), true)
                        .addField("Length", String.valueOf(message.getContent().length()), true)
                        .addField("Channel", channel.getAsMention(), true);
                if (message.getContent().length() >= 1024) {
                    builder.addField("Message", "```" + message.getContent().replace("`", "\\`").substring(0, 1014) + "...```", false);
                } else {
                    builder.addField("Message", "```" + message.getContent().replace("`", "\\`") + "```", false);
                }

                Bot.logGuildMessage(new MessageBuilder().setEmbed(builder.setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()), guild);
            }

            if (Bot.hasInvite(message)) {
                Chat.removeMessage(message);

                channel.sendMessage(user.getAsMention() + " Please do not advertise Discord servers. Thanks!").queue();
                EmbedBuilder builder = Chat.getEmbed().setDescription(":exclamation: Discord server advertisement - **" + Chat.getFullName(user) + "**")
                        .addField("User", user.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true);
                if (message.getContent().length() >= 1024) {
                    builder.addField("Message", "```" + message.getContent().substring(0, 1014).replace("`", "\\`") + "...```", false);
                } else {
                    builder.addField("Message", "```" + message.getContent().replace("`", "\\`") + "```", false);
                }

                Bot.logGuildMessage(new MessageBuilder().setEmbed(builder.setFooter(Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()), guild);
                return true;
            }

            if (!channel.getName().contains("meme") && message.getRawContent().contains("▔╲▂▂▂▂╱▔╲▂")) {
                Chat.removeMessage(message);
                Chat.sendMessage(user.getAsMention() + " Please do not post cooldog in this channel.", channel, 30);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (event.getGuild() == null || !Core.getDiscord().isReady() || event.getMember().getUser().equals(Core.getClient().getSelfUser()) ||
                event.getMember().getUser().isBot()) {
            return;
        }

        if (filteredMessage(event.getMessage())) {
            return;
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        Guild guild = event.getGuild();
        Member sender = event.getMember();
        User user = message.getAuthor();
        TextChannel channel = event.getChannel();

        if (guild == null || !Core.getDiscord().isReady() || user.equals(Core.getClient().getSelfUser()) || user.isBot()) {
            return;
        }

        if (!GuildSettings.isTrusted(sender) && !message.getContent().startsWith(GuildSettings.getPrefix(guild))) {
            if (GuildSettings.filterSpam(guild)) {
                if (GuildSettings.getMutedRole(guild) != null) {
                    if (message.getRawContent().equalsIgnoreCase(messages.get(user.getId()))) {
                        amount.put(user.getId(), (amount.get(user.getId()) + 1));
                    } else {
                        messages.put(user.getId(), message.getRawContent());
                        amount.put(user.getId(), 0);
                    }

                    if (amount.get(user.getId()) != null && ((amount.get(user.getId()) + 1) == 3 || (amount.get(user.getId()) + 1) == 4)) {
                        Chat.removeMessage(event.getMessage());
                        channel.sendMessage(user.getAsMention() + " Please do not repeat the same message!").queue();
                        return;
                    } else if ((amount.get(user.getId()) + 1) >= 5) {
                        Chat.removeMessage(event.getMessage());

                        guild.getController().addRolesToMember(sender, GuildSettings.getMutedRole(guild)).queue();
                        channel.sendMessage(user.getAsMention() + " has been auto muted for spam").queue();

                        EmbedBuilder builder = Chat.getEmbed().setDescription(":no_bell:  " + user.getAsMention() + " | " + Chat.getFullName(user) + " was auto muted for spam!")
                                .addField("Channel", channel.getAsMention(), true);
                        if (event.getMessage().getContent().length() >= 1024) {
                            builder.addField("Message", "```" + event.getMessage().getContent().substring(0, 1014).replace("`", "\\`") + "...```", false);
                        } else {
                            builder.addField("Message", "```" + event.getMessage().getContent().replace("`", "\\`") + "```", false);
                        }

                        Bot.logGuildMessage(new MessageBuilder().setEmbed(builder.setFooter("System time | " + Bot.getBotTime(), null)
                                .setColor(Chat.CUSTOM_PURPLE).build()), guild);
                        return;
                    }
                }

                if (filteredMessage(message)) {
                    return;
                }
            }
        }

        if (message.getRawContent() != null && message.getContent().startsWith(GuildSettings.getPrefix(guild))) {

            for (String id : Core.getConfig().getBlockedUsers()) {
                if (user.getId().equals(id) && !GuildSettings.isTrusted(sender)) {
                    Chat.removeMessage(message);
                    Chat.sendMessage(user.getAsMention() + " You are blacklisted from using bot commands. If you believe this is an error, please contact MatrixTunnel.", channel, 10);
                    return;
                }
            }

            String msg = event.getMessage().getRawContent();
            String command = msg.substring(1);
            String[] args = new String[0];
            if (msg.contains(" ")) {
                command = command.substring(0, msg.indexOf(" ") - 1);
                args = msg.substring(msg.indexOf(" ") + 1).split(" ");
            }

            for (Command cmd : Core.getCommands()) {
                if (cmd.getCommand().equalsIgnoreCase(command)) {
                    if (cmd.getType() == CommandType.MASTER && !user.getId().equals("118088732753526784")) {
                        return;
                    }

                    if (cmd.getType() == CommandType.TRUSTED && !GuildSettings.isTrusted(sender)) {
                        return;
                    }

                    if (cmd.getType() == CommandType.MUSIC && !GuildSettings.getMusicCommandChannels().contains(channel.getId())) {
                        return;
                    }

                    try {
                        cmd.onCommand(guild, channel, sender, message, args);
                    } catch (Exception ex) {
                        Core.log.error("Exception in guild " + "!\n" + '\'' + cmd.getCommand() + "' "
                                + Arrays.toString(args) + " in " + channel + "! Sender: " +
                                user.getName() + '#' + user.getDiscriminator(), ex);
                        ex.printStackTrace();
                    }
                    return;
                } else {
                    for (String alias : cmd.getAliases()) {
                        if (alias.equalsIgnoreCase(command)) {
                            if (cmd.getType() == CommandType.MASTER && !user.getId().equals("118088732753526784")) {
                                return;
                            }

                            if (cmd.getType() == CommandType.TRUSTED && !GuildSettings.isTrusted(sender)) {
                                return;
                            }

                            if (cmd.getType() == CommandType.MUSIC && !GuildSettings.getMusicCommandChannels().contains(channel.getId())) {
                                return;
                            }

                            try {
                                cmd.onCommand(guild, channel, sender, message, args);
                            } catch (Exception ex) {
                                Core.log.error("Exception in guild " + "!\n" + '\'' + cmd.getCommand() + "' "
                                        + Arrays.toString(args) + " in " + channel + "! Sender: " +
                                        user.getName() + '#' + user.getDiscriminator(), ex);
                                ex.printStackTrace();
                            }
                            return;
                        } else {
                            //not a valid command
                            Chat.removeMessage(message, 120);
                        }
                    }
                }
            }
        }
    }

}
