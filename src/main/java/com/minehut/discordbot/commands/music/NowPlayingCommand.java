package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.extractors.YouTubeExtractor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

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
        return new String[]{"nowplaying", "current", "song"};
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.setAutoDelete(message, 5);

        Player player = Core.getMusicManager().getPlayer(channel.getGuild().getId());

        if (Core.getMusicManager().getPlayer(guild.getId()).getPlayingTrack() != null) {
            Chat.sendMessage(Chat.getEmbed().addField("Currently Playing: ", "[`" + player.getPlayingTrack().getTrack().getInfo().title + "`](" + //TODO Move to now playing command
                    YouTubeExtractor.WATCH_URL + player.getPlayingTrack().getTrack().getIdentifier() +
                    ") added by <@!" + player.getPlayingTrack().getMeta().get("requester") + ">", false)
                    .addField("Volume: ", String.valueOf(player.getVolume()) + "%", true)
                    .addField("Repeating: ", String.valueOf(player.getLooping()).toLowerCase().replace("true", ":white_check_mark:").replace("false", ":x:"), true)
                    .addField("Paused: ", String.valueOf(player.getPaused()).toLowerCase().replace("true", ":white_check_mark:").replace("false", ":x:"), true), channel, 25);
        } else {
            Chat.sendMessage(Chat.getEmbed().setDescription("There are no songs playing!").setColor(Chat.CUSTOM_RED), channel, 20);
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
