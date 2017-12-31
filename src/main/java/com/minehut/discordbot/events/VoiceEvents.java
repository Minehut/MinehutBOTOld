package com.minehut.discordbot.events;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
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
        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            if (MinehutBot.get().getMusicManager().hasPlayer(event.getGuild().getId())) {
                MinehutBot.get().getMusicManager().getPlayer(event.getGuild().getId()).getPlaylist().clear();
                MinehutBot.get().getMusicManager().getPlayer(event.getGuild().getId()).skip();
            }
        }

        new BotTask("RejoinVoice") {
            @Override
            public void run() {
                if (event.getChannelLeft() != null) event.getChannelLeft().getGuild().getAudioManager().openAudioConnection(event.getChannelLeft());
            }
        }.delay(5000);
    }

    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            //TODO Unmute lol event.getVoiceState().
        }
    }
}
