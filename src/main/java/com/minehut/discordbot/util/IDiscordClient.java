package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Original idea and most code from FlareBot.
 * Created by MatrixTunnel on 2/7/2017.
 */
public class IDiscordClient {

    public boolean isReady() {
        return Core.getClient().getStatus().equals(JDA.Status.CONNECTED);
    }

    public VoiceChannel getVoiceChannelByID(String id) {
        return Core.getClient().getGuilds().stream()
                .map(g -> g.getVoiceChannelById(id))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
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

    public Role getRoleByID(String id) {
        for (Guild guild : Core.getClient().getGuilds()) {
            for (Role role : guild.getRoles()) {
                if (role.getId().equals(id)) {
                    return role;
                }
            }
        }
        return null;
    }

    public TextChannel getChannelByID(String id) {
        for (Guild guild : Core.getClient().getGuilds()) {
            for (TextChannel channel : guild.getTextChannels()) {
                if (channel.getId().equals(id)) {
                    return channel;
                }
            }
        }
        return null;
    }

    public User getUserByID(String id) {
        for (Guild guild : Core.getClient().getGuilds()) {
            for (Member member : guild.getMembers()) {
                if (member.getUser().getId().equals(id)) {
                    return member.getUser();
                }
            }
        }
        return null;
    }


    public boolean userHasRoleId(Guild guild, User user, String id) { //TODO Check if this is working??
        return guild.getMember(user).getRoles().contains(Core.getDiscord().getRoleByID(id));

        //return Arrays.stream(Core.getClients()).map(jda -> jda.getGuildById(guild.getId()).getMember(user).getRoles()
        //        .contains(Core.getDiscord().getGuildByID(guild.getId()).getRoleById(id))).filter(Objects::nonNull)
        //        .findFirst().orElse(null);
    }


    public void streaming(String status, String url) {
        Core.getClient().getPresence().setGame(Game.of(status, url));
    }

}
