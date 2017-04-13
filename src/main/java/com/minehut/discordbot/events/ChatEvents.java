package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
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

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        Guild guild = event.getGuild();
        Member sender = event.getMember();
        User user = message.getAuthor();
        TextChannel channel = event.getChannel();

        if (guild == null || !Core.getDiscord().isReady() || user.equals(Core.getClient().getSelfUser()) || user.isBot() || user.isFake()) {
            return;
        }

        if (guild == Bot.getMainGuild()) {
            if (guild.getMember(user).getNickname() == null) {
                Core.log.info(Chat.getChannelName(channel) + Chat.getFullName(user) + ": " + message.getContent());
            } else {
                Core.log.info(Chat.getChannelName(channel) + Chat.getFullName(user) + " (" + guild.getMember(user).getNickname() + "): " + message.getContent());
            }

            if (!Bot.isTrusted(user) && !message.getContent().startsWith(Command.getPrefix())) {
                if (message.getRawContent().equalsIgnoreCase(messages.get(user.getId()))) {
                    amount.put(user.getId(), (amount.get(user.getId()) + 1));
                } else {
                    messages.put(user.getId(), message.getRawContent());
                    amount.put(user.getId(), 0);
                }

                if (amount.get(user.getId()) != null && ((amount.get(user.getId()) + 1) == 3 || (amount.get(user.getId()) + 1) == 4)) {
                    Chat.removeMessage(event.getMessage());
                    channel.sendMessage(user.getAsMention() + ", please do not repeat the same message!").queue();
                    return;
                } else if ((amount.get(user.getId()) + 1) >= 5) {
                    Chat.removeMessage(event.getMessage());

                    guild.getController().addRolesToMember(sender, Core.getClient().getRoleById(Core.getConfig().getMutedRoleID())).queue();
                    channel.sendMessage(user.getAsMention() + " has been auto muted for spam").queue();

                    EmbedBuilder builder = Chat.getEmbed().setDescription(":no_bell:  " + user.getAsMention() + " | " + Chat.getFullName(user) + " was auto muted for spam!")
                            .addField("Channel", channel.getAsMention(), true);
                    if (event.getMessage().getContent().length() >= 1024) {
                        builder.addField("Message", "```" + event.getMessage().getContent().substring(0, 1014) + "...```", false);
                    } else {
                        builder.addField("Message", "```" + event.getMessage().getContent() + "```", false);
                    }

                    Bot.getLogChannel().sendMessage(builder.setFooter("System time | " + Bot.getBotTime(), null)
                            .setColor(Chat.CUSTOM_PURPLE).build());
                    return;
                }
            }

            if (message.getContent().length() >= Core.getConfig().getMaxMessageLength()) {
                EmbedBuilder builder = Chat.getEmbed().setDescription(":exclamation: Possible message spam - **" + Chat.getFullName(user) + "**")
                        .addField("User", user.getAsMention(), true)
                        .addField("Length", String.valueOf(message.getContent().length()), true)
                        .addField("Channel", channel.getAsMention(), true);
                if (event.getMessage().getContent().length() >= 1024) {
                    builder.addField("Message", "```" + event.getMessage().getContent().substring(0, 1014) + "...```", false);
                } else {
                    builder.addField("Message", "```" + event.getMessage().getContent() + "```", false);
                }

                Bot.getLogChannel().sendMessage(builder.setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build());
            }

            if (Bot.hasInvite(message) && !Bot.isTrusted(user)) {
                Chat.removeMessage(message);

                channel.sendMessage(user.getAsMention() + ", please do not advertise Discord servers. Thanks!");
                EmbedBuilder builder = Chat.getEmbed().setDescription(":exclamation: Discord server advertisement - **" + Chat.getFullName(user) + "**")
                        .addField("User", user.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true);
                if (event.getMessage().getContent().length() >= 1024) {
                    builder.addField("Message", "```" + event.getMessage().getContent().substring(0, 1014) + "...```", false);
                } else {
                    builder.addField("Message", "```" + event.getMessage().getContent() + "```", false);
                }

                Bot.getLogChannel().sendMessage(builder.setFooter(Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build());
                return;
            }

            if (!channel.getName().contains("meme") && message.getRawContent().contains("▔╲▂▂▂▂╱▔╲▂")) {

                Chat.removeMessage(message);
                Chat.sendMessage(user.getAsMention() + ", please do not post cooldog in this channel.", channel, 30);
                return;
            }

            //if (channel.getId().equals("284833888482754561") && message.getMentionedUsers().contains(jda.getSelfUser()) && !message.getRawContent().substring(14).equals("")) {
            //    Core.log.info("test");
//
            //    try {
            //        MonkeyLearn ml = new MonkeyLearn("YOUR API KEY HERE");
//
            //        // Classify some texts
            //        String[] textList = {message.getRawContent().substring(14)};
            //        String moduleId = "cl_hS9wMk9y";
            //        MonkeyLearnResponse res = ml.classifiers.classify(moduleId, textList, true);
//
            //        Chat.sendMessage(res.arrayResult.toString(), channel);
            //    } catch (MonkeyLearnException e) {
            //        e.printStackTrace();
            //    }
            //    return;
            //}
        }

                return;
            }
            */
        }

        if (message.getRawContent() != null && message.getContent().startsWith(Command.getPrefix())) {

            for (String id : Core.getConfig().getBlockedUsers()) {
                if (user.getId().equals(id) && !Bot.isTrusted(user)) {
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

                    if (cmd.getType() == CommandType.TRUSTED && !Bot.isTrusted(user)) {
                        return;
                    }

                    if (guild.equals(Bot.getMainGuild()) && cmd.getType() == CommandType.MUSIC &&
                            !Arrays.asList(Core.getConfig().getMusicCommandChannels()).contains(channel.getId())) {
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

                            if (cmd.getType() == CommandType.TRUSTED && !Bot.isTrusted(user)) {
                                return;
                            }

                            if (guild.equals(Bot.getMainGuild()) && cmd.getType() == CommandType.MUSIC &&
                                    !Arrays.asList(Core.getConfig().getMusicCommandChannels()).contains(channel.getId())) {
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
