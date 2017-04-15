package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 3/17/2017.
 */
public class SayCommand implements Command {

    @Override
    public String getCommand() {
        return "say";
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (args.length >= 2) {
            StringBuilder msg = new StringBuilder();
            for (String s : args) {
                msg.append(s).append(" ");
            }

            if (args[0].equals("here")) {
                Core.log.info(msg.toString().replace(args[0], "[BOT]"));
                channel.sendMessage(msg.toString().replace(args[0], "")).queue();
                return;
            }

            MessageChannel textChannel = Core.getClient().getTextChannelById(args[0].replace("-r", ""));

            if (args[0].contains("-r")) {
                Core.log.info(msg.toString().replace(args[0], "[BOT]"));
                textChannel.sendMessage(msg.toString().replace(args[0], "")).queue();
            } else {
                Core.log.info(msg.toString().replace(args[0], "[BOT]"));
                textChannel.sendMessage(msg.toString().replace(args[0], "**[BOT]**")).queue();
            }
        } else {
            //Command format
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MASTER;
    }
}
