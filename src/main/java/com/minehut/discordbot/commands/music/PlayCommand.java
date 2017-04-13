package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.VideoThread;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Made by the developers of FlareBot.
 * Changed by MatrixTunnel on 1/8/2017.
 */
public class PlayCommand implements Command {

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message, 5);

        if (args.length == 0) {
            Chat.sendMessage(sender.getAsMention() + " Usage: `" + Command.getPrefix() + getCommand() + " <term>`", channel, 15);
        } else if (args.length >= 1) {
            if (guild.getSelfMember().getVoiceState().getChannel() == null) {
                Chat.sendMessage(sender.getAsMention() + " The bot is not in a voice channel!", channel, 10);
                return;
            }
            if (!guild.getSelfMember().getVoiceState().getChannel().equals(sender.getVoiceState().getChannel())) {
                Chat.sendMessage(sender.getAsMention() + " You must be in the music channel in order to play songs!", channel, 10);
                return;
            }

            if (args[0].startsWith("http") || args[0].startsWith("www.")) {
                VideoThread.getThread(args[0], channel, sender.getUser()).start();
            } else {
                StringBuilder term = new StringBuilder();
                for (String s : args) {
                    term.append(s).append(" ");
                }
                term = new StringBuilder(term.toString().trim());
                VideoThread.getSearchThread(term.toString(), channel, sender.getUser()).start(); //YouTube only
            }
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
