package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.awt.*;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class ServerEvents {

    private static String disconnectReason = "UNKNOWN";

    @EventSubscriber
    public void handle(ReadyEvent event) {

        Core.discordConnection = true;
        Bot.updateUsers();

        Core.enabled = true;
        Core.log.info("Bot ready.");
    }

    @EventSubscriber
    public void handle(UserJoinEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        IUser user = event.getUser();
        IGuild guild = event.getGuild();

        Bot.updateUsers();

        //TODO Test how old the account is when joining

        //if (event.getUser().getCreationDate().) {

        //}

        MessageBuilder builder = new MessageBuilder(event.getClient()).withChannel(Core.discordLogChatID);

        EmbedBuilder embed = new EmbedBuilder().ignoreNullEmptyFields().withFooterText("joined the Discord server.");
        embed.withAuthorIcon(user.getAvatarURL()).withAuthorName(user.getDisplayName(guild)).withAuthorUrl("https://minehut.com/u/" + user.getDisplayName(guild));
        embed.withColor(Color.GREEN);

        builder.withEmbed(embed.build()).send();
        //Chat.sendDiscordMessage(event.getUser().mention() + " _has joined the Discord server._");
        Core.log.info(event.getUser().getName() + " joined the Discord server.");

        if (event.getGuild().getUsers().size() == 1000) {
            Chat.sendDiscordMessage(event.getUser().toString() + " is the 1000th Discord member! Be sure and give him a big warm welcome! Maybe even buy him Legend! :D",
                    event.getGuild().getChannelByID("239599059415859200"));
        }
    }

    @EventSubscriber
    public void handle(UserLeaveEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        IUser user = event.getUser();
        IGuild guild = event.getGuild();

        MessageBuilder builder = new MessageBuilder(event.getClient()).withChannel(Core.discordLogChatID);

        EmbedBuilder embed = new EmbedBuilder().ignoreNullEmptyFields().withFooterText("left the Discord server.");
        embed.withAuthorIcon(user.getAvatarURL()).withAuthorName(user.getDisplayName(guild)).withAuthorUrl("https://minehut.com/u/" + user.getDisplayName(guild));
        embed.withColor(Color.YELLOW);

        builder.withEmbed(embed.build()).send();
        //Chat.sendDiscordMessage(event.getUser().mention() + " _has left the Discord server._");
        Core.log.info(event.getUser().getName() + " left the Discord server.");
        Bot.updateUsers();
    }

    @EventSubscriber
    public void handle(UserBanEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        IUser user = event.getUser();
        IGuild guild = event.getGuild();

        MessageBuilder builder = new MessageBuilder(event.getClient()).withChannel(Core.discordLogChatID);

        EmbedBuilder embed = new EmbedBuilder().ignoreNullEmptyFields().withFooterText("was banned from the Discord server.");
        embed.withAuthorIcon(user.getAvatarURL()).withAuthorName(user.getDisplayName(guild)).withAuthorUrl("https://minehut.com/u/" + user.getDisplayName(guild));
        embed.withColor(Color.RED);

        builder.withEmbed(embed.build()).send();
        //Chat.sendDiscordMessage(event.getUser().mention() + " **was banned from Discord.**");
        Core.log.info(event.getUser().getName() + " was banned from Discord.");
        Bot.updateUsers();
    }

    @EventSubscriber
    public void handle(UserPardonEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        IUser user = event.getUser();
        IGuild guild = event.getGuild();

        MessageBuilder builder = new MessageBuilder(event.getClient()).withChannel(Core.discordLogChatID);

        EmbedBuilder embed = new EmbedBuilder().ignoreNullEmptyFields().withFooterText("was unbanned from the Discord server.");
        embed.withAuthorIcon(user.getAvatarURL()).withAuthorName(user.getDisplayName(guild)).withAuthorUrl("https://minehut.com/u/" + user.getDisplayName(guild));
        embed.withColor(Color.YELLOW);

        builder.withEmbed(embed.build()).send();
        //Chat.sendDiscordMessage(event.getUser().mention() + " **was unbanned from Discord.**");
        Core.log.info(event.getUser().getName() + " was unbanned from Discord.");
        Bot.updateUsers();
    }

    @EventSubscriber
    public void handle(NickNameChangeEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        IUser user = event.getUser();

        MessageBuilder builder = new MessageBuilder(event.getClient()).withChannel(Core.discordLogChatID);

        EmbedBuilder embed = new EmbedBuilder().ignoreNullEmptyFields().withTitle("**[INFO]** " + user).withDescription("`" +
                event.getOldNickname().orElseGet(user::getName) + "` is now known as `" + event.getNewNickname().orElseGet(user::getName) + "`");
        embed.withColor(Color.YELLOW);

        builder.withEmbed(embed.build()).send();
        //Chat.sendDiscordMessage("`" + event.getOldNickname().orElseGet(user::getName) + "` is now known as `" + event.getNewNickname().orElseGet(user::getName) + "`");
        Core.log.info("\"" + event.getOldNickname().orElseGet(user::getName) + "\" is now known as \"" + event.getNewNickname().orElseGet(user::getName) + "\"");
    }

    @EventSubscriber
    public void handle(ReconnectSuccessEvent event) {
        Bot.updateUsers();
        Core.broadcast("Connection to Discord has been reestablished! Disconnect reason: " + disconnectReason);
        Core.discordConnection = true;
    }

    @EventSubscriber
    public void handle(DisconnectedEvent event) throws DiscordException {
        if (event.getReason() != DisconnectedEvent.Reason.RECONNECT_OP) {
            disconnectReason = event.getReason().toString();
        }

        Core.discordConnection = false;
        Core.log.error("Disconnected from Discord. Reason: " + disconnectReason);
        //System.out.print("Disconnected from Discord. Reason: " + disconnectReason);
    }

}
