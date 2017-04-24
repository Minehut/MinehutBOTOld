package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

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
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        Chat.sendMessage(new MessageBuilder().append(sender.getAsMention()).setEmbed(Chat.getEmbed().setColor(Chat.CUSTOM_DARK_GREEN)
                .addField("Music",
                        "`" + Command.getPrefix() + "play <term>` Replace \"term\" with a YouTube/SoundCloud url or a search query to play music\n" +
                        "`" + Command.getPrefix() + "queue` Lists the current music playlist of queued messages\n" +
                        "`" + Command.getPrefix() + "skip` Casts your vote to skip the song that is currently playing\n" +
                        "`" + Command.getPrefix() + "random <playlist>` Plays a random song from the specified category", false)
                .addField("Minehut",
                        "`" + Command.getPrefix() + "status <network|bot>` See Minehut's network or bot status\n" +
                        "`" + Command.getPrefix() + "user <username>` Shows username changes and Minehut stats\n" +
                        "`" + Command.getPrefix() + "server <name>` Shows about for the player server", false).build()).build(), channel, 20);
        )
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
