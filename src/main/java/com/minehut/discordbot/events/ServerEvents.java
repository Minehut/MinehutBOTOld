package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class ServerEvents extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        Core.latch.countDown();
        Bot.updateUsers();

        for (Role role : Bot.getMainGuild().getRoles()) {
            Core.log.info("Name: \"" + role.getName() + "\" ID: \"" + role.getId() + "\" - " + role.getPermissions());
        }

        for (String id : Core.getConfig().getMusicVoiceChannels()) {
            VoiceChannel channel = Core.getDiscord().getVoiceChannelByID(id);
            if (channel != null) {
                channel.getGuild().getAudioManager().openAudioConnection(channel);
                if (Core.getClient().getGuilds().contains(Bot.getMainGuild())) break; //Only join main guild else everything else
            }
        }

        //for (Guild guild : Core.getClient().getGuilds()) {
        //    Core.getMusicManager().getPlayer(guild.getId()).setVolume(25);
        //}

        Core.enabled = true;
        Core.log.info("Bot ready.");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getMember().getUser();
        Bot.updateUsers();

        Chat.sendMessage(Chat.getEmbed().setTitle(Chat.getFullName(user), null).setThumbnail(user.getEffectiveAvatarUrl()) // https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png
                .setDescription("*" + user.getAsMention() + " joined the server.*" +
                "\n\n**Account Creation:** " + Bot.formatTime(LocalDateTime.from(user.getCreationTime())) +
                "\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_GREEN), Bot.getLogChannel());

        Core.log.info(Chat.getFullName(user) + " joined the Discord server.");

        /*
        if (event.getGuild().getUsers().size() == 2000) {
            Chat.sendMessage(event.getUser().toString() + " is the 2000th Discord member! Be sure and give them a big warm welcome and maybe even buy him Legend! :D",
                    guild.getChannelByID("239599059415859200"));
        }
        */
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getMember().getUser();

        Chat.sendMessage(Chat.getEmbed().setTitle(Chat.getFullName(user), null).setThumbnail(user.getEffectiveAvatarUrl()) // https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png
                .setDescription("*" + user.getAsMention() + " left the server.*" +
                "\n\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_RED), Bot.getLogChannel());

        Core.log.info(Chat.getFullName(user) + " left the Discord server.");
        Bot.updateUsers();
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getUser();

        Chat.sendMessage(Chat.getEmbed().setAuthor(Chat.getFullName(user), "https://minehut.com/" + user.getName(), null).setDescription("*was banned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.RED), Bot.getLogChannel());

        //Chat.sendDiscordMessage(event.getUser().mention() + " **was banned from Discord.**");
        Core.log.info(Chat.getFullName(user) + " was banned from Discord.");
        Bot.updateUsers();
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getUser();

        Chat.sendMessage(Chat.getEmbed().setAuthor(Chat.getFullName(user), "https://minehut.com/" + user.getName(), null).setDescription("*was unbanned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.PINK), Bot.getLogChannel());

        //Chat.sendDiscordMessage(event.getUser().mention() + " **was banned from Discord.**");
        Core.log.info(Chat.getFullName(user) + " was unbanned from Discord.");
        Bot.updateUsers();
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        EmbedBuilder embed = Chat.getEmbed();

        if (event.getRoles().contains(Core.getDiscord().getRoleByID(Core.getConfig().getMutedRoleID()))) return;

        if (event.getRoles().size() == 1) {
            embed.setDescription(event.getMember().getAsMention() + " gained the role:\n");
            embed.appendDescription("`" + event.getRoles().get(0).getName() + "`");
        } else {
            embed.setDescription(event.getMember().getAsMention() + " gained the roles:\n");

            for (Role role : event.getRoles()) {
                embed.appendDescription("`" + role.getName() + "`, ");
            }
        }

        Chat.sendMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_ORANGE), Bot.getLogChannel());
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        EmbedBuilder embed = Chat.getEmbed();

        if (event.getRoles().contains(Core.getDiscord().getRoleByID(Core.getConfig().getMutedRoleID()))) return;

        if (event.getRoles().size() == 1) {
            embed.setDescription(event.getMember().getAsMention() + " lost the role:\n");
            embed.appendDescription("`" + event.getRoles().get(0).getName() + "`");
        } else {
            embed.setDescription(event.getMember().getAsMention() + " lost the roles:\n");

            for (Role role : event.getRoles()) {
                embed.appendDescription("`" + role.getName() + "`, ");
            }
        }

        Chat.sendMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_ORANGE), Bot.getLogChannel());
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getMember().getUser();

        if (event.getPrevNick() == null) {
            Chat.sendMessage(Chat.getEmbed().setDescription(user.getAsMention() + " changed their name from `" +
                    user.getName() + "` to `" + event.getNewNick() + "`")
                    .setColor(Color.ORANGE), Bot.getLogChannel());
            return;
        }

        if (!event.getNewNick().equals(user.getName())) {
            Chat.sendMessage(Chat.getEmbed().setDescription(user.getAsMention() + " changed their name from `" +
                    event.getPrevNick() + "` to `" + event.getNewNick() + "`")
                    .setColor(Color.ORANGE), Bot.getLogChannel());
        } else {
            Chat.sendMessage(Chat.getEmbed().setDescription(user.getAsMention() + " reset their name to `" + event.getNewNick() + "`")
                    .setColor(Color.ORANGE), Bot.getLogChannel());
        }

        Core.log.info("\"" + event.getPrevNick() + "\" is now known as \"" + event.getNewNick() + "\"");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        //Bot.updateUsers(); <--- Causing bad stuff to happen
        //Core.broadcast("Connection to Discord has been reestablished! Disconnect reason: " + disconnectReason);
        Core.log.info("Connection to Discord has been reestablished!");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        Core.log.error("Disconnected from Discord.");
    }

}
