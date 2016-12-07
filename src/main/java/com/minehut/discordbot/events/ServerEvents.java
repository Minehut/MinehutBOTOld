package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.Status;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class ServerEvents {

    private static String disconnectReason = "UNKNOWN";

    @EventSubscriber
    public void handle(ReadyEvent event) {

        Core.discordConnection = true;
        Core.getDiscord().changeStatus(Status.game("with " + event.getClient().getGuilds().get(0).getUsers().size() + " users!"));

        Core.enabled = true;
        Core.log.info("Bot ready.");
    }

    @EventSubscriber
    public void handle(UserJoinEvent event) {
        Core.getDiscord().changeStatus(Status.game("with " + event.getGuild().getUsers().size() + " users!"));

        //TODO Test how old the account is when joining

        //if (event.getUser().getCreationDate().) {

        //}
        Chat.sendDiscordMessage(event.getUser().mention() + " _has joined the Discord server._");
        Core.log.info(event.getUser().getName() + " has joined the Discord server.");

        if (event.getGuild().getUsers().size() == 900) {
            Chat.sendDiscordMessage(event.getUser().toString() + " is the 900th Discord member!", event.getGuild().getChannelByID("239599059415859200"));
        }
    }

    @EventSubscriber
    public void handle(UserLeaveEvent event) {
        Core.getDiscord().changeStatus(Status.game("with " + event.getGuild().getUsers().size() + " users!"));

        Chat.sendDiscordMessage(event.getUser().mention() + " _has left the Discord server._");
        Core.log.info(event.getUser().getName() + " has left the Discord server.");
    }

    @EventSubscriber
    public void handle(UserBanEvent event) {
        Core.getDiscord().changeStatus(Status.game("with " + event.getGuild().getUsers().size() + " users!"));

        Chat.sendDiscordMessage(event.getUser().mention() + " **was banned from Discord.**");
        Core.log.info(event.getUser().getName() + " was banned from Discord.");
    }

    @EventSubscriber
    public void handle(UserPardonEvent event) {
        Core.getDiscord().changeStatus(Status.game("with " + event.getGuild().getUsers().size() + " users!"));

        Chat.sendDiscordMessage(event.getUser().mention() + " **was unbanned from Discord.**");
        Core.log.info(event.getUser().getName() + " was unbanned from Discord.");
    }

    @EventSubscriber
    public void handle(NickNameChangeEvent event) {
        Chat.sendDiscordMessage("`" + event.getOldNickname().orElseGet(() -> event.getUser().getName()) + "` is now known as `" + event.getNewNickname().orElseGet(() -> event.getUser().getName()) + "`");
        Core.log.info("\"" + event.getOldNickname().orElseGet(() -> event.getUser().getName()) + "\" is now known as \"" + event.getNewNickname().orElseGet(() -> event.getUser().getName()) + "\"");
    }

    //TODO Add voice channel join, move, and leave events

    @EventSubscriber
    public void handle(ReconnectSuccessEvent event) {
        Core.getDiscord().changeStatus(Status.game("with " + event.getClient().getGuilds().get(0).getUsers().size() + " users!"));
        Core.broadcast("Connection to Discord has been reestablished! Disconnect reason: " + disconnectReason);
        Core.discordConnection = true;
    }

    @EventSubscriber
    public void handle(DisconnectedEvent event) {
        if (event.getReason() != DisconnectedEvent.Reason.RECONNECT_OP) {
            disconnectReason = event.getReason().toString();
        }

        Core.discordConnection = false;
        Core.log.error("Disconnected from Discord. Reason: " + disconnectReason);
        //System.out.print("Disconnected from Discord. Reason: " + disconnectReason);
    }

}
