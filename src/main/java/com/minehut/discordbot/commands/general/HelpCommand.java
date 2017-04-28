package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 12/18/2016.
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", CommandType.GENERAL, null, "commands", "howdoido", "howdoidothis");
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        Chat.sendMessage(new MessageBuilder().append(sender.getAsMention()).setEmbed(Chat.getEmbed().setColor(Chat.CUSTOM_DARK_GREEN)
                .addField("Music",
                        "`play <term>` Replace \"term\" with a YouTube/SoundCloud url or a search query to play music\n" +
                        "`queue` Lists the current music playlist of queued messages\n" +
                        "`skip` Casts your vote to skip the song that is currently playing\n" +
                        "`random <playlist>` Plays a random song from the specified category\n" +
                        "`nowplaying` Displays information about the playing song.", false)
                .addField("Minehut",
                        "`status <network|bot>` See Minehut's network or bot status\n" +
                        "`user <username>` Shows username changes and Minehut stats\n" +
                        "`server <name>` Shows about for the player server", false).build()).build(), channel, 20);

        return true;
    }

}
