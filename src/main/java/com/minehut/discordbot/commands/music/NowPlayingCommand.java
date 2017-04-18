package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/8/2017.
 */
public class NowPlayingCommand implements Command {

    @Override
    public String getCommand() {
        return "np";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nowplaying", "current", "song", "playing"};
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message, 5);

        Player player = Core.getMusicManager().getPlayer(guild.getId());

        if (Core.getMusicManager().getPlayer(guild.getId()).getPlayingTrack() != null) {
            Chat.sendMessage(Chat.getEmbed().addField("Currently Playing", String.format("**[%s](%s)** `[%s]` | <@!%s>",
                    player.getPlayingTrack().getTrack().getInfo().title, player.getPlayingTrack().getTrack().getInfo().uri,
                    Bot.millisToTime(player.getPlayingTrack().getTrack().getDuration(), false), player.getPlayingTrack().getMeta().get("requester")), false)
                    .addField("Volume", player.getVolume() + "%", true)
                    .addField("Repeating", player.getLooping() ? ":white_check_mark:" : ":x:", true)
                    .addField("Paused", player.getPaused() ? ":white_check_mark:" : ":x:", true).build(), channel, 25);
        } else {
            Chat.sendMessage(Chat.getEmbed().setDescription("There are no songs playing!").setColor(Chat.CUSTOM_RED).build(), channel, 20);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
