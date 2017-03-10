package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.VideoThread;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 1/8/2017.
 */
public class PlayCommand implements Command {

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.setAutoDelete(message, 5);

        if (args.length == 0) {
            Chat.sendMessage(sender.getAsMention() + " Usage: `" + Command.getPrefix() + getCommand() + getArgs() + "`", channel, 15);
        } else if (args.length >= 1) {
            if (guild.getSelfMember().getVoiceState().getChannel() == null) {
                Chat.sendMessage(sender.getAsMention() + " The bot is not in a voice channel!", channel, 10);
                return;
            }
            if (!guild.getSelfMember().getVoiceState().getChannel().equals(member.getVoiceState().getChannel())) {
                Chat.sendMessage(sender.getAsMention() + " You must be in the music channel in order to play songs!", channel, 10);
                return;
            }

            if (args[0].startsWith("http") || args[0].startsWith("www.")) {
                VideoThread.getThread(args[0], channel, sender).start();
            } else {
                String term = "";
                for (String s : args) {
                    term += s + " ";
                }
                term = term.trim();
                VideoThread.getSearchThread(term, channel, sender).start(); //YouTube only
            }
        }

    }

    @Override
    public String getArgs() {
        return " <term>";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
