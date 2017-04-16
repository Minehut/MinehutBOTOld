package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/27/2017.
 */
public class MuteCommand implements Command {

    @Override
    public String getCommand() {
        return "mute";
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (args.length == 1) {
            User muteUser = Core.getClient().getUserById(args[0].replace("<@", "").replace("!", "").replace(">", ""));
            Role muteRole = Core.getClient().getRoleById(Core.getConfig().getMutedRoleID());

            if (muteUser == null) {
                Chat.sendMessage(Chat.getEmbed().setDescription("Not a valid user!").setColor(Chat.CUSTOM_RED).build(), channel, 5);
                return;
            }

            if (guild.getMember(muteUser).getRoles().contains(muteRole)) {
                guild.getController().removeRolesFromMember(guild.getMember(muteUser), muteRole).queue();
                channel.sendMessage("User " + muteUser.getAsMention() + " has been unmuted by " + sender.getAsMention() + ".").queue();

                Bot.getLogChannel().sendMessage(Chat.getEmbed().setDescription(":loud_sound:  " + muteUser.getAsMention() + " | " + Chat.getFullName(muteUser) + " was unmuted.")
                        .addField("Staff Member", sender.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true) //TODO reason if args > 2
                        .setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()).queue();

                Core.log.info(Chat.getFullName(muteUser) + " was unmuted by " + Chat.getFullName(sender.getUser()) + ".");
            } else {
                guild.getController().addRolesToMember(guild.getMember(muteUser), muteRole).queue();
                channel.sendMessage("User " + muteUser.getAsMention() + " has been muted by " + sender.getAsMention() + ".").queue();

                Bot.getLogChannel().sendMessage(Chat.getEmbed().setDescription(":no_bell:  " + muteUser.getAsMention() + " | " + Chat.getFullName(muteUser) + " was muted.")
                        .addField("Staff Member", sender.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true) //TODO reason if args > 2
                        .setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()).queue();

                Core.log.info(Chat.getFullName(muteUser) + " was muted by " + Chat.getFullName(sender.getUser()) + ".");
            }
        } else {
            Chat.sendMessage(Chat.getEmbed().setDescription("Usage: `" + Command.getPrefix() + getCommand() + " <user id|user mention>`").setColor(Chat.CUSTOM_BLUE).build(), channel, 5);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.TRUSTED;
    }
}
