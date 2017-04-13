package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/30/2017.
 */
public class ShutdownCommand implements Command {

    @Override
    public String getCommand() {
        return "shutdown";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stop", "exit", "end"};
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (args.length == 1 && args[0].equals("-r")) {
            Core.shutdown(true);
        } else {
            Core.shutdown(false);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MASTER;
    }
}
