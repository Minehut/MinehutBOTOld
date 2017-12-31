package com.minehut.discordbot.events;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.CommandHandler;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

/**
 * Created by MatrixTunnel on 11/29/2016.
 */
public class ChatEvents extends ListenerAdapter {

    private static HashMap<String, String> messages = new HashMap<>();
    private static HashMap<String, Integer> amount = new HashMap<>();

    private MinehutBot bot = MinehutBot.get();
    private CommandHandler commandHandler = new CommandHandler();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        Member sender = event.getMember();
        User user = event.getAuthor();
        TextChannel channel = event.getChannel();

        if (!Bot.isReady() || user.isBot() || user.isFake()) {
            return;
        }

        UserClient client = new UserClient(sender.getUser());

        if (message.getContentRaw().startsWith(bot.getConfig().getCommandPrefix())) {
            commandHandler.execute(client, guild, message, sender, channel);
            return;
        }

        if (!client.isStaff() && !message.getContentRaw().startsWith(bot.getConfig().getCommandPrefix())) {
            if (Bot.getMutedRole() != null) {
                if (message.getContentRaw().equalsIgnoreCase(messages.get(user.getId()))) {
                    amount.put(user.getId(), (amount.get(user.getId()) + 1));
                } else {
                    messages.put(user.getId(), message.getContentRaw());
                    amount.put(user.getId(), 0);
                }

                if (amount.get(user.getId()) != null && ((amount.get(user.getId()) + 1) == 3/* || (amount.get(user.getId()) + 1) == 4*/)) {
                    Chat.removeMessage(event.getMessage());
                    channel.sendMessage(user.getAsMention() + " Please do not repeat the same message!").queue();
                    return;
                } else if ((amount.get(user.getId()) + 1) >= 4) {
                    Chat.removeMessage(event.getMessage());

                    client.toggleMute();
                    guild.getController().addRolesToMember(sender, Bot.getMutedRole()).queue();
                    channel.sendMessage(user.getAsMention() + " has been auto muted for spam").queue();

                    EmbedBuilder builder = Chat.getEmbed().setDescription(":no_bell:  " + user.getAsMention() + " | " + Chat.getFullName(user) + " was auto muted for spam!")
                            .addField("Channel", channel.getAsMention(), true);
                    if (event.getMessage().getContentDisplay().length() >= 1024) {
                        builder.addField("Message", "```" + event.getMessage().getContentDisplay().substring(0, 1014).replace("`", "\\`") + "...```", false);
                    } else {
                        builder.addField("Message", "```" + event.getMessage().getContentDisplay().replace("`", "\\`") + "```", false);
                    }

                    Bot.logGuildMessage(new MessageBuilder().setEmbed(builder.setFooter("System time | " + Bot.getBotTime(), null)
                            .setColor(Chat.CUSTOM_PURPLE).build()));
                    return;
                }
            }

            filterMessage(message);

            if (message.getContentDisplay().length() >= bot.getConfig().getMessageAlertLength()) {
                EmbedBuilder builder = Chat.getEmbed().setDescription(":exclamation: Possible message spam - **" + Chat.getFullName(user) + "**")
                        .addField("User", user.getAsMention(), true)
                        .addField("Length", String.valueOf(message.getContentDisplay().length()), true)
                        .addField("Channel", channel.getAsMention(), true);
                if (message.getContentDisplay().length() >= 1024) {
                    builder.addField("Message", "```" + message.getContentDisplay().replace("`", "\\`").substring(0, 1014) + "...```", false);
                } else {
                    builder.addField("Message", "```" + message.getContentDisplay().replace("`", "\\`") + "```", false);
                }

                Bot.logGuildMessage(new MessageBuilder().setEmbed(builder.setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()));
            }
        }
    }

    private void filterMessage(Message message) {
        User user = message.getAuthor();
        TextChannel channel = message.getTextChannel();

        if (Chat.hasInvite(message)) {
            Chat.removeMessage(message);

            channel.sendMessage(user.getAsMention() + " Please do not advertise Discord servers, thanks!").queue();
            EmbedBuilder builder = com.minehut.discordbot.util.Chat.getEmbed().setDescription(":exclamation: Discord server advertisement - **" + Chat.getFullName(user) + "**")
                    .addField("User", user.getAsMention(), true)
                    .addField("Channel", channel.getAsMention(), true);
            if (message.getContentDisplay().length() >= 1024) {
                builder.addField("Message", "```" + message.getContentDisplay().substring(0, 1014).replace("`", "\\`") + "...```", false);
            } else {
                builder.addField("Message", "```" + message.getContentDisplay().replace("`", "\\`") + "```", false);
            }

            Bot.logGuildMessage(new MessageBuilder().setEmbed(builder.setFooter(Bot.getBotTime(), null)
                    .setColor(Chat.CUSTOM_PURPLE).build()));
            return;
        }

        if (message.getContentRaw().contains("▔╲▂▂▂▂╱▔╲▂")) {
            Chat.removeMessage(message);
            Chat.sendMessage(user.getAsMention() + " Please do not post cooldog on this server.", channel, 30);
        }
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        if (!Bot.isReady() || event.getMember().getUser().isBot() || event.getMember().getUser().isFake()) {
            return;
        }

        if (!new UserClient(event.getMember().getUser().getId()).isStaff()) {
            filterMessage(event.getMessage());
        }
    }

}
