package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 8/10/2017.
 */
public class NameCommand extends Command {

    public NameCommand() {
        super(CommandType.MASTER, "<name>", "name");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (args.length >= 1) {
            StringBuilder b = new StringBuilder();
            for (String s : args) {
                b.append(s).append(" ");
            }
            MinehutBot.get().getDiscordClient().getSelfUser().getManager()
                    .setName(b.toString().length() > 32 ? b.toString().substring(0, 32) : b.toString()).complete();
        } else {
            return false;
        }

        return true;
    }

}
