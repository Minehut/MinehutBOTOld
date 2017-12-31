package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;


public class NowPlayingCommand extends Command {

    public NowPlayingCommand() {
        super(CommandType.MUSIC, null, "np", "nowplaying", "song", "playing", "whatisthis");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message, 5);

        Player player = MinehutBot.get().getMusicManager().getPlayer(guild.getId());

        if (player.getPlayingTrack() != null) {
            Chat.sendMessage(Chat.getEmbed().addField("Currently Playing", String.format("**[%s](%s)** `[%s]` | <@!%s>",
                    player.getPlayingTrack().getTrack().getInfo().title, player.getPlayingTrack().getTrack().getInfo().uri,
                    Bot.millisToTime(player.getPlayingTrack().getTrack().getDuration(), false), player.getPlayingTrack().getMeta().get("requester")), false)
                    .build(), channel, 25);
        } else { //TODO
            Chat.sendMessage(guild.getMember(sender.getUser()).getAsMention() + " There are no songs currently playing!", channel, 15);
            Chat.sendMessage(Chat.getEmbed().setDescription("There are no songs playing!").setColor(Chat.CUSTOM_RED).build(), channel, 20);
        }

        return true;
    }

}
