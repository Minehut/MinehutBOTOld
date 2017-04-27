package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/29/2017.
 */
public class InfoCommand extends Command {

    public InfoCommand() {
        super("info", CommandType.GENERAL, null);
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message, 5);

        if (args.length == 0) {
            Chat.sendMessage("That command has been moved. Please use the commands `user, status, or server` instead", channel, 10);
        } else if (args.length >= 1) {
            switch (args[0]) {
                case "server":
                    Chat.sendMessage("That command has been moved. Please use `" + GuildSettings.getPrefix(guild) + "server <server_name>` instead", channel, 10);
                    break;
                case "user":
                    Chat.sendMessage("That command has been moved. Please use `" + GuildSettings.getPrefix(guild) + "user <username>` instead", channel, 10);
                    break;
                case "network":
                    Chat.sendMessage("That command has been moved. Please use `" + GuildSettings.getPrefix(guild) + "status network` instead", channel, 10);
                    break;
                case "bot":
                    Chat.sendMessage("That command has been moved. Please use `" + GuildSettings.getPrefix(guild) + "status bot` instead", channel, 10);
                    break;
                default:
                    Chat.sendMessage("That command has been moved. Please use the commands `user, status, or server` instead", channel, 10);
                    break;
            }
        }

        return true;
    }

}
