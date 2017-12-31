package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/27/2017.
 */
public class MuteCommand extends Command {

    public MuteCommand() {
        super(CommandType.TRUSTED, "<user id|user mention>", "mute");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);
        Member member = guild.getMember(sender.getUser());

        if (args.length == 1) {
            Member muteMember = guild.getMember(MinehutBot.get().getDiscordClient().getUserById(args[0].replace("<@", "").replace("!", "").replace(">", "")));
            Role muteRole = Bot.getMutedRole();

            if (muteRole == null) {
                Chat.sendMessage(member.getAsMention() + " The mute command is not enabled on this server!", channel, 10);
                return true;
            }

            if (muteMember == null) {
                Chat.sendMessage(member.getAsMention() + " Not a valid user!", channel, 5);
                return true;
            }

            new UserClient(muteMember.getUser().getId()).toggleMute();
            if (muteMember.getRoles().contains(muteRole)) {
                guild.getController().removeRolesFromMember(muteMember, muteRole).queue();
                channel.sendMessage("User " + muteMember.getAsMention() + " has been unmuted by " + member.getAsMention() + ".").queue();

                Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setDescription(":loud_sound:  " + muteMember.getAsMention() + " | " + Chat.getFullName(muteMember.getUser()) + " was unmuted.")
                        .addField("Staff Member", member.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true) //TODO reason if args > 2
                        .setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()));

                MinehutBot.log.info(Chat.getFullName(muteMember.getUser()) + " was unmuted by " + Chat.getFullName(sender.getUser()) + ".");
            } else {
                guild.getController().addRolesToMember(muteMember, muteRole).queue();
                channel.sendMessage("User " + muteMember.getAsMention() + " has been muted by " + member.getAsMention() + ".").queue();

                Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setDescription(":no_bell:  " + muteMember.getAsMention() + " | " + Chat.getFullName(muteMember.getUser()) + " was muted.")
                        .addField("Staff Member", member.getAsMention(), true)
                        .addField("Channel", channel.getAsMention(), true) //TODO reason if args > 2
                        .setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_PURPLE).build()));

                MinehutBot.log.info(Chat.getFullName(muteMember.getUser()) + " was muted by " + Chat.getFullName(sender.getUser()) + ".");
            }
        } else {
            return false;
        }

        return true;
    }

}
