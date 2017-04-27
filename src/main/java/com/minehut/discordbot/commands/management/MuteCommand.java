package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.exceptions.CommandException;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/27/2017.
 */
public class MuteCommand extends Command {

    public MuteCommand() {
        super("mute", new String[]{}, " <user id|user mention>", CommandType.TRUSTED);
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (args.length == 1) {
            User muteUser = Core.getClient().getUserById(args[0].replace("<@", "").replace("!", "").replace(">", ""));
            Role muteRole = GuildSettings.getMutedRole(guild);

            if (muteRole == null) {
                Chat.sendMessage(sender.getAsMention() + " The mute command is not setup for this server!", channel, 10);
                return true;
            }

            if (muteUser == null) {
                Chat.sendMessage(sender.getAsMention() + " Not a valid user!", channel, 5);
                return true;
            }

            if (guild.getMember(muteUser).getRoles().contains(muteRole)) {
                guild.getController().removeRolesFromMember(guild.getMember(muteUser), muteRole).queue();
                channel.sendMessage("User " + muteUser.getAsMention() + " has been unmuted by " + sender.getAsMention() + ".").queue();

                Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setDescription(":loud_sound:  " + muteUser.getAsMention() + " | " + Chat.getFullName(muteUser) + " was unmuted.")
                        .addField("Staff Member", sender.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true) //TODO reason if args > 2
                        .setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()), guild);

                Core.log.info(Chat.getFullName(muteUser) + " was unmuted by " + Chat.getFullName(sender.getUser()) + ".");
            } else {
                guild.getController().addRolesToMember(guild.getMember(muteUser), muteRole).queue();
                channel.sendMessage("User " + muteUser.getAsMention() + " has been muted by " + sender.getAsMention() + ".").queue();

                Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setDescription(":no_bell:  " + muteUser.getAsMention() + " | " + Chat.getFullName(muteUser) + " was muted.")
                        .addField("Staff Member", sender.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true) //TODO reason if args > 2
                        .setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()), guild);

                Core.log.info(Chat.getFullName(muteUser) + " was muted by " + Chat.getFullName(sender.getUser()) + ".");
            }
        } else {
            return false;
        }

        return true;
    }

}
