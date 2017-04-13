package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Original idea and some code from FlareBot.
 * Created by MatrixTunnel on 2/7/2017.
 */
public class IDiscordClient {

    public boolean isReady() {
        return Core.getClient().getStatus().equals(JDA.Status.CONNECTED);
    }

    public List<VoiceChannel> getConnectedVoiceChannels() {
        return Core.getClient().getGuilds().stream()
                .map(c -> c.getAudioManager().getConnectedChannel())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public long getActiveVoiceChannels() {
        return getConnectedVoiceChannels().stream()
                .map(VoiceChannel::getGuild)
                .map(ISnowflake::getId)
                .filter(gid -> Core.getMusicManager().hasPlayer(gid))
                .map(g -> Core.getMusicManager().getPlayer(g))
                .filter(p -> p.getPlayingTrack() != null)
                .filter(p -> !p.getPaused()).count();
    }

    public List<Guild> getRolesForGuild(Guild guild) {
        return Core.getClient().getGuilds().stream().filter(g -> g.getRoles().equals(guild)).collect(Collectors.toList());
    }


    public boolean userHasRoleId(Guild guild, User user, String id) {
        return guild.getMember(user).getRoles().contains(Core.getClient().getRoleById(id));
    }

    public void streaming(String status, String url) {
        Core.getClient().getPresence().setGame(Game.of(status, url));
    }
}
