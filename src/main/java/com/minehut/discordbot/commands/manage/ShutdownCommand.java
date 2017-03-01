package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

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
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (args.length == 1 && args[0].equals("-r")) {
            Core.shutdown(true);
        } else {
            Core.shutdown(false);
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTRATIVE;
    }
}
