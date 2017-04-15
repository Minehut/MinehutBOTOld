package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/29/2017.
 * Matcher code/regex made by MeowingTwurtle.
 * Info help from Draem and ReduxRedstone.
 */
public class InfoCommand implements Command {

    private String minehutLogo = "https://cdn.discordapp.com/attachments/233737506955329538/292430804246855681/NEW_Minehut_logo.jpg";

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message, 5);

        if (args.length == 0) {
            Chat.sendMessage("That command has been moved. Please use the commands `user, status, or server` instead", channel, 10);
        } else if (args.length >= 1) {
            switch (args[0]) {
                case "server":
                    Chat.sendMessage("That command has been moved. Please use `" + Command.getPrefix() + "server <server_name>` instead", channel, 10);
                    break;
                case "user":
                    Chat.sendMessage("That command has been moved. Please use `" + Command.getPrefix() + "user <username>` instead", channel, 10);
                    break;
                case "network":
                    Chat.sendMessage("That command has been moved. Please use `" + Command.getPrefix() + "status network` instead", channel, 10);
                    break;
                case "bot":
                    Chat.sendMessage("That command has been moved. Please use `" + Command.getPrefix() + "status bot` instead", channel, 10);
                    break;
                default:
                    Chat.sendMessage("That command has been moved. Please use the commands `user, status, or server` instead", channel, 10);
                    break;
            }
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

}
