package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.Status;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class Bot {

    public static void updateUsers() {
        Core.getDiscord().changeStatus(Status.game("with " + Core.getDiscord().getGuilds().get(0).getUsers().size() + " users!"));
    }

}
