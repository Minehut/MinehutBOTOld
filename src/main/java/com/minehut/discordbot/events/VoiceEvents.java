package com.minehut.discordbot.events;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.obj.IChannel;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class VoiceEvents {

    //TODO Add voice channel stuff here

    @EventSubscriber
    public void handle(UserVoiceChannelJoinEvent event) {
        IChannel channel = event.getChannel();



    }

}
