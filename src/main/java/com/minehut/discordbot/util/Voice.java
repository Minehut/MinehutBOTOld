package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by MatrixTunnel on 12/5/2016.
 */
public class Voice {

    public static void clearPlaylist(IGuild guild) {
        Core.getMusicManager().getPlayer(guild.getID()).getPlaylist().clear();
        Core.getMusicManager().getPlayer(guild.getID()).skip();
    }

}
