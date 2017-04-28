package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandHandler;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class EnableCommand extends Command {

    private CommandHandler commandHandler;

    public EnableCommand(CommandHandler commandHandler) {
        super("enablecmd", CommandType.MASTER, "<command>", "enable");
        this.commandHandler = commandHandler;
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {

        if (args.length == 0) {
            return false;
        }

        Command cmd = commandHandler.getCommand(args[0]);

        if (cmd == null) {
            Chat.sendMessage(sender.getAsMention() + " The command provided is not valid.", channel, 20);
            return true;
        }

        StringBuilder response = new StringBuilder("The command ");

        //enable the command
        cmd.setEnabled(true);

        //send a response informing the user that the command was enabled
        response.append(cmd.getName()).append(" has been enabled!");

        Chat.sendMessage(sender.getAsMention() + response.toString(), channel, 20);

        return true;
    }

}
