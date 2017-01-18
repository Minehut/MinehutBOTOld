package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Voice;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class VoiceEvents extends AudioEventAdapter {

    public static List<IMessage> playing = new ArrayList<>();

    /*
    @Override
    public void onTrackStart(com.sedmelluq.discord.lavaplayer.player.AudioPlayer player, com.sedmelluq.discord.lavaplayer.track.AudioTrack track) {

        for (String id : Bot.getMusicTextChannels()) {
            Core.log.info("loop");

            IChannel channel = Core.getDiscord().getChannelByID(id);
            com.sedmelluq.discord.lavaplayer.player.AudioPlayer song = Core.getMusicManager().getPlayer(channel.getGuild().getID()).getPlayer();

            if (song.getPlayingTrack() == player.getPlayingTrack()) {
                Core.log.info("yes");
                IMessage msg = Chat.sendMessage("Now Playing: **" + track.getInfo().title + "**", channel);

                SkipCommand.votes.clear();
                playing.add(msg);
            } else {
                Core.log.info("no");
            }

        }
    }

    @Override
    public void onTrackEnd(com.sedmelluq.discord.lavaplayer.player.AudioPlayer player, com.sedmelluq.discord.lavaplayer.track.AudioTrack track, AudioTrackEndReason endReason) {

        //Chat.removeMessage(msg);
        SkipCommand.votes.clear();
        if (playing == null) return;

        for (IMessage msg : playing) { //Has thrown a npe a few times. I think I fixed it with the break
            String content = msg.getContent().toLowerCase();
            if (content.contains(track.getInfo().title.toLowerCase())) {
                Chat.removeMessage(msg);
                playing.remove(msg);
                break;
            }
        }

    }
    */

    //TODO Chat.sendMessage("Playlist has been cleared since no one is in the Music channel", channel, 45);

    @EventSubscriber
    public void handle(UserVoiceChannelJoinEvent event) throws MissingPermissionsException {
        if (!Bot.getMusicVoiceChannels().contains(event.getVoiceChannel().getID())) return;
        if (!event.getVoiceChannel().getConnectedUsers().contains(Core.getDiscord().getOurUser())) {
            Voice.clearPlaylist(event.getGuild());
            Core.getDiscord().getVoiceChannelByID(event.getVoiceChannel().getID()).join();
        }
    }

    @EventSubscriber
    public void handle(UserVoiceChannelLeaveEvent event) throws MissingPermissionsException {
        if (!Bot.getMusicVoiceChannels().contains(event.getVoiceChannel().getID())) return;
        if (event.getVoiceChannel().getConnectedUsers().contains(Core.getDiscord().getOurUser()) && event.getVoiceChannel().getConnectedUsers().size() <= 1) {
            Voice.clearPlaylist(event.getGuild());
            Core.getDiscord().getVoiceChannelByID(event.getVoiceChannel().getID()).leave();
        }
    }

    @EventSubscriber
    public void handle(UserVoiceChannelMoveEvent event) throws MissingPermissionsException {
        if (event.getOldChannel().getConnectedUsers().contains(Core.getDiscord().getOurUser()) && event.getOldChannel().getConnectedUsers().size() <= 1) {
            Voice.clearPlaylist(event.getGuild());
            Core.getDiscord().getVoiceChannelByID(event.getOldChannel().getID()).leave();
            return;
        }

        if (Bot.getMusicVoiceChannels().contains(event.getVoiceChannel().getID()) && !event.getNewChannel().getConnectedUsers().contains(Core.getDiscord().getOurUser())) {
            //Voice.clearPlaylist(event.getGuild());
            Core.getDiscord().getVoiceChannelByID(event.getVoiceChannel().getID()).join();
        }

    }

    //public void trackStart(TrackStartEvent event) {

      //Core.log.info("testestestetestsetset");

      //EmbedBuilder embed = new EmbedBuilder().withColor(new Color(66, 173, 244));
      //


      //embed.withDesc("Now playing: **" +  + "** " + event.getTrack().getTotalTrackTime());

      //if (!Core.getDiscord().getVoiceChannelByID("256321559872929792").isConnected()) {
      //    Core.getDiscord().getVoiceChannelByID("256321559872929792").join();
      //}

      //embed.withDesc("");

      //Chat.setAutoDelete(Chat.sendDiscordMessage(event.getClient().getChannelByID("250849094208192512"))
      // .withEmbed(embed.build()).send(), (int) event.getTrack().getTotalTrackTime() * 1000);

}
