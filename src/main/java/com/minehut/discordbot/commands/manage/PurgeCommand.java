package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 12/18/2016.
 */
public class PurgeCommand implements Command {

    @Override
    public String getCommand() {
        return "purge";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cut"};
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        if (args.length == 1 && args[0].matches("\\d+")) {
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
            Chat.sendMessage(Chat.getEmbed().withDesc("Usage: `" + Command.getPrefix() + getCommand() + getArgs() + "`").withColor(Chat.CUSTOM_GREEN), channel, 5);
            //Chat.sendMessage(Chat.getEmbed().withDesc("Bad arguments!\n" + getDescription()), channel, 5); //TODO Make reaction
        }
    }

    @Override
    public String getArgs() {
        return " <messages to remove>";
    }

    private void bulk(List<IMessage> toDelete, IChannel channel) {
        RequestBuffer.request(() -> {
            try {
                channel.getMessages().bulkDelete(toDelete);
            } catch (DiscordException e) {
                Core.log.error("Could not bulk delete!", e);
                    Chat.sendMessage(Chat.getEmbed()
                            .withDesc("Could not bulk delete! Error occured!"), channel, 10);
            } catch (MissingPermissionsException e) {
                    Chat.sendMessage(Chat.getEmbed()
                            .withDesc("I do not have the `Manage Messages` permission!"), channel, 10);
            }
        });
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTRATIVE;
    }
}
