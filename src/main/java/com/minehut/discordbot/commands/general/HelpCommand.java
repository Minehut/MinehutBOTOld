package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 12/18/2016.
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super(CommandType.GENERAL, null, "help", "commands", "howdoido", "howdoidothis");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        Chat.sendMessage(new MessageBuilder().append(guild.getMember(sender.getUser()).getAsMention()).setEmbed(Chat.getEmbed().setColor(Chat.CUSTOM_DARK_GREEN)
                .addField("Music",
                        "np Shows the currently playing song\n" +
                        "`play <term>` Replace \"term\" with a YouTube/SoundCloud url or a search query to play music\n" +
                        "`queue` Lists the current music playlist of queued messages\n" +
                        "`skip` Casts your vote to skip the song that is currently playing", false)
                        //"`random <playlist>` Plays a random song from the specified category"
                .addField("Minehut",
                        "`status <bot|servers>` See the current bot/network status\n" +
                        "`help` Guess what this command does :thinking:", false).build()).build(), channel, 20);

        return true;
    }

}
