package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by MatrixTunnel on 4/16/2017.
 */
public class VoiceEvents extends ListenerAdapter {
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            event.getGuild().getAudioManager().setSelfDeafened(true);
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getGuild().equals(Core.getClient().getGuildById(Core.getConfig().getMainGuildID()))) return;

        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            if (Core.getMusicManager().hasPlayer(event.getGuild().getId())) {
                Core.getMusicManager().getPlayer(event.getGuild().getId()).getPlaylist().clear();
                Core.getMusicManager().getPlayer(event.getGuild().getId()).skip();
            }
        } else {
            if (event.getChannelLeft().getMembers().contains(event.getGuild().getMember(event.getJDA().getSelfUser()))
                    && event.getChannelLeft().getMembers().size() < 2) {
                event.getChannelLeft().getGuild().getAudioManager().closeAudioConnection();
            }
        }
    }
}
