package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MatrixTunnel on 12/8/2016.
 */
public class Bot {

    public static void updateUsers() {
        Core.getDiscord().streaming(getMainGuild().getUsers().size() + " Minehut users!", "https://www.minehut.com");
    }

    public static IGuild getMainGuild() {
        return Core.getDiscord().getGuildByID("239599059415859200");
    }

    public static List<String> getMusicTextChannels() { //TODO Config/fix :P
        List<String> textChannels = new ArrayList<>();
        textChannels.add("250849094208192512"); //Minehut
        //textChannels.add("244744966839074816"); //Matrix Spam
        //textChannels.add("263087009805893632"); //Informer's Home
        //textChannels.add("266232237597523968"); //Toro's Discord (bot-spam)
        return textChannels;
    }

    public static List<String> getMusicVoiceChannels() { //TODO Config/fix :P
        List<String> musicChannels = new ArrayList<>();
        musicChannels.add("256321559872929792"); //Minehut
        //musicChannels.add("258402255391293450"); //Matrix Spam
        //musicChannels.add("262017609870868481"); //Informer's Home
        //musicChannels.add("266673502604492800"); //Toro's Discord
        return musicChannels;
    }

    public static IChannel getMusicChannel() {
        return getMainGuild().getChannelByID("250849094208192512");
    }

    public static IChannel getLogChannel() {
        return getMainGuild().getChannelByID("253063123781550080");
    }

    public static boolean isTrusted(IUser user) {
        return userHasRoleId(getMainGuild(), user, "240228183985618954") || userHasRoleId(getMainGuild(), user, "246487117000212480");
    }

    public static boolean userHasRoleId(IGuild guild, IUser user, String id) {
        return user.getRolesForGuild(guild).stream().map(IDiscordObject::getID).anyMatch(s -> s.equals(id));
    }

    public static String getContent(String[] args, int start, int end) {
        String content = "";
        for (int i = start; i < Math.min(end, args.length); i++) {
            content += args[i];
            if (i != Math.min(end, args.length) - 1) {
                content += " ";
            }
        }

        return content;
    }

}
