package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.extractors.YouTubeExtractor;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

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
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        Chat.setAutoDelete(message, 5);

        Player player = Core.getMusicManager().getPlayer(channel.getGuild().getID());

        if (Core.getMusicManager().getPlayer(guild.getID()).getPlayingTrack() != null) {
            Chat.sendMessage(Chat.getEmbed().appendField("Currently Playing: ", "[`" + player.getPlayingTrack().getTrack().getInfo().title + "`](" + //TODO Move to now playing command
                    YouTubeExtractor.WATCH_URL + player.getPlayingTrack().getTrack().getIdentifier() +
                    ") added by <@!" + player.getPlayingTrack().getMeta().get("requester") + ">", false)
                    .appendField("Volume: ", String.valueOf(player.getVolume()) + "%", true)
                    .appendField("Repeating: ", String.valueOf(player.getLooping()).toLowerCase().replace("true", ":white_check_mark:").replace("false", ":x:"), true)
                    .appendField("Paused: ", String.valueOf(player.getPaused()).toLowerCase().replace("true", ":white_check_mark:").replace("false", ":x:"), true), channel, 25);
        } else {
            Chat.sendMessage(Chat.getEmbed().withDesc("There are no songs playing!").withColor(Chat.CUSTOM_RED), channel, 20);
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
