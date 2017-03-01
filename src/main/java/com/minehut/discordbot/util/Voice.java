package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Created by MatrixTunnel on 12/5/2016.
 */
public class Voice {

    public static void clearPlaylist(Guild guild) {
        Core.getMusicManager().getPlayer(guild.getId()).getPlaylist().clear();
        Core.getMusicManager().getPlayer(guild.getId()).skip();
    }

}
