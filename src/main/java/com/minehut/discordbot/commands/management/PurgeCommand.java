package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Made by the developers of FlareBot.
 * Changed by MatrixTunnel on 12/18/2016.
 */
public class PurgeCommand extends Command {

    public PurgeCommand() {
        super("purge", new String[]{"cut"}, "", CommandType.TRUSTED);
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        /*if (args.length == 1 && args[0].matches("\\d+")) {
            int count = Integer.parseInt(args[0]) + 1;
            if (count < 2 || count > 200) {
                Chat.sendMessage(Chat
                        .getEmbed().withDesc("Can't purge less than 2 messages or more than 200!"), channel, 5);
                return;
            }
            RequestBuffer.request(() -> {
                channel.getMessages().setCacheCapacity(count);
                boolean loaded = true;
                while (loaded && channel.getMessages().size() < count)
                    loaded = channel.getMessages().load(Math.min(count, 100));
                if (loaded) {
                    List<IMessage> list = channel.getMessages().stream().collect(Collectors.toList());
                    List<IMessage> toDelete = new ArrayList<>();
                    for (IMessage msg : list) {
                        if (toDelete.size() > 99) {
                            bulk(toDelete, channel);
                            toDelete.clear();
                        }
                        toDelete.add(msg);
                    }
                    Chat.logRemove = false;
                    bulk(toDelete, channel);
                    channel.getMessages().setCacheCapacity(0);

                        Chat.sendMessage(Chat.getEmbed().withDesc(":+1: Deleted!")
                                .appendField("Message Count: ", String.valueOf(count), true), channel, 5);
                } else {
                    Chat.sendMessage(Chat.getEmbed().withDesc("Could not load in messages!"), channel, 5);
                }
                Chat.logRemove = true;
            });
        } else {
            Chat.sendMessage(Chat.getEmbed().withDesc("Usage: `" + Command.getPrefix() + getCommand() + " <message count>`").withColor(Chat.CUSTOM_GREEN), channel, 5);
            //Chat.sendMessage(Chat.getEmbed().withDesc("Bad arguments!\n" + getDescription()), channel, 5); //TODO Make reaction
        }
        */

        return true;
    }

}
