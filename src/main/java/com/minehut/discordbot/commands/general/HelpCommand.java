package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 12/18/2016.
 */
public class HelpCommand implements Command {

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"commands", "howdoido"};
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        Chat.sendMessage("What do you need help with? I'm still making this command so you might have to wait a bit... Ask a staff member instead", channel, 15);
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
