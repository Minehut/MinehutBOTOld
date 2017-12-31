package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 3/17/2017.
 */
public class SayCommand extends Command {

    public SayCommand() {
        super(CommandType.MASTER, "<channelId|channelId-r|here> <message>", "say");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (args.length >= 2) {
            StringBuilder msg = new StringBuilder();
            for (String s : args) {
                msg.append(s).append(" ");
            }

            if (args[0].equals("here")) {
                MinehutBot.log.info(msg.toString().replace(args[0], "[BOT]"));
                channel.sendMessage(msg.toString().replace(args[0], "")).queue();
                return true;
            }

            MessageChannel textChannel = MinehutBot.get().getDiscordClient().getTextChannelById(args[0].replace("-r", ""));

            if (args[0].contains("-r")) {
                MinehutBot.log.info(msg.toString().replace(args[0], "[BOT]"));
                textChannel.sendMessage(msg.toString().replace(args[0], "")).queue();
            } else {
                MinehutBot.log.info(msg.toString().replace(args[0], "[BOT]"));
                textChannel.sendMessage(msg.toString().replace(args[0], "**[BOT]**")).queue();
            }
        } else {
            return false;
        }

        return true;
    }

}
