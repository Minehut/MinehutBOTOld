package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.Date;

/**
 * Created by MatrixTunnel on 1/27/2017.
 */
public class MuteCommand implements Command {

    @Override
    public String getCommand() {
        return "mute";
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        User muteUser = Core.getDiscord().getUserByID(args[0].replace("<@", "").replace("!", "").replace(">", ""));
        Role muteRole = Core.getDiscord().getRoleByID(Core.getConfig().getMutedRoleID());

        if (args.length == 1) {
            if (muteUser == null) {
                Chat.sendMessage(Chat.getEmbed().setDescription("Not a valid user!").setColor(Chat.CUSTOM_RED), channel, 5);
                return;
            }

            if (guild.getMember(muteUser).getRoles().contains(muteRole)) {
                guild.getController().removeRolesFromMember(guild.getMember(muteUser), muteRole).queue();
                Chat.sendMessage("User " + muteUser.getAsMention() + " has been unmuted by " + sender.getAsMention() + ".", channel);

                Chat.sendMessage(Chat.getEmbed().setDescription(":loud_sound:  *" + muteUser.getAsMention() + " was unmuted*")
                        .addField("Staff Member", sender.getAsMention() + " | " + Chat.getFullName(sender), true)
                        .addField("Channel", channel.getAsMention(), true)
                        .setFooter("System time | " + new Date().toString(), null)
                        .setColor(Chat.CUSTOM_PURPLE), Bot.getLogChannel());

                Core.log.info(Chat.getFullName(muteUser) + " was unmuted by " + Chat.getFullName(sender) + ".");
            } else {
                guild.getController().addRolesToMember(guild.getMember(muteUser), muteRole).queue();
                Chat.sendMessage("User " + muteUser.getAsMention() + " has been muted by " + sender.getAsMention() + ".", channel);

                Chat.sendMessage(Chat.getEmbed().setDescription(":no_bell:  *" + muteUser.getAsMention() + " was muted*")
                        .addField("Staff Member", sender.getAsMention() + " | " + Chat.getFullName(sender), true)
                        .addField("Channel", channel.getAsMention(), true)
                        .setFooter("System time | " + new Date().toString(), null)
                        .setColor(Chat.CUSTOM_PURPLE), Bot.getLogChannel());

                Core.log.info(Chat.getFullName(muteUser) + " was muted by " + Chat.getFullName(sender) + ".");
            }
        } else {
            Chat.sendMessage(Chat.getEmbed().setDescription("Usage: `" + Command.getPrefix() + getCommand() + getArgs() + "`").setColor(Chat.CUSTOM_BLUE), channel, 5);
            //Chat.sendMessage(Chat.getEmbed().withDesc("Bad arguments!\n" + getDescription()), channel, 5); //TODO Make reaction

        }
    }

    @Override
    public String getArgs() {
        return " <user id|user mention>";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTRATIVE;
    }
}
