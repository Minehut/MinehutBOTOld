package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.music.SkipCommand;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.tasks.running.PunishmentChecker;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class ServerEvents extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        Core.latch.countDown();
        Bot.updateUsers();
        Core.getClient().getPresence().setStatus(OnlineStatus.ONLINE);

        for (Role role : Bot.getMainGuild().getRoles()) {
            Core.log.info("Name: \"" + role.getName() + "\" ID: \"" + role.getId() + "\" - " + role.getPermissions());
        }

        Bot.nowPlaying = new ArrayList<>();
        SkipCommand.votes = new ArrayList<>();
        ChatEvents.messages = new HashMap<>();
        ChatEvents.amount = new HashMap<>();
        Core.log.info("Trackers set.");

        for (String id : Core.getConfig().getMusicVoiceChannels()) {
            VoiceChannel channel = Core.getClient().getVoiceChannelById(id);
            if (channel != null) {
                channel.getGuild().getAudioManager().openAudioConnection(channel);
                if (Core.getClient().getGuilds().contains(Bot.getMainGuild())) break; //Only join main guild else everything else
            }
        }


        Core.enabled = true;
        Core.log.info("Bot ready.");
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            event.getGuild().getAudioManager().setSelfDeafened(true);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getMember().getUser();
        Bot.updateUsers();

        Bot.getLogChannel().sendMessage(Chat.getEmbed().setTitle(Chat.getFullName(user), null).setThumbnail(user.getEffectiveAvatarUrl()) // https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png
                .setDescription("*" + user.getAsMention() + " joined the server.*" +
                "\n\n**Account Creation:** " + Bot.formatTime(LocalDateTime.from(user.getCreationTime())) +
                "\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_GREEN).build());

        Core.log.info(Chat.getFullName(user) + " joined the Discord server.");
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getMember().getUser();
        Bot.updateUsers();

        Bot.getLogChannel().sendMessage(Chat.getEmbed().setTitle(Chat.getFullName(user), null).setThumbnail(user.getEffectiveAvatarUrl()) // https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png
                .setDescription("*" + user.getAsMention() + " left the server.*" +
                "\n\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_RED).build());

        Core.log.info(Chat.getFullName(user) + " left the Discord server.");
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getUser();

        Bot.getLogChannel().sendMessage(Chat.getEmbed().setTitle(Chat.getFullName(user), null).setDescription("*was banned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.RED).build());

        //Chat.sendDiscordMessage(event.getUser().mention() + " **was banned from Discord.**");
        Core.log.info(Chat.getFullName(user) + " was banned from Discord.");
        Bot.updateUsers();
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getUser();

        Bot.getLogChannel().sendMessage(Chat.getEmbed().setTitle(Chat.getFullName(user), null).setDescription("*was unbanned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.PINK).build());

        //Chat.sendDiscordMessage(event.getUser().mention() + " **was banned from Discord.**");
        Core.log.info(Chat.getFullName(user) + " was unbanned from Discord.");
        Bot.updateUsers();
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        EmbedBuilder embed = Chat.getEmbed();

        if (event.getRoles().contains(Core.getClient().getRoleById(Core.getConfig().getMutedRoleID()))) return;

        if (event.getRoles().size() == 1) {
            embed.setDescription(event.getMember().getAsMention() + " gained the role:\n");
            embed.appendDescription("`" + event.getRoles().get(0).getName() + "`");
        } else {
            embed.setDescription(event.getMember().getAsMention() + " gained the roles:\n");

            for (Role role : event.getRoles()) {
                embed.appendDescription("`" + role.getName() + "`, ");
            }
        }

        Bot.getLogChannel().sendMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_ORANGE).build());
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        EmbedBuilder embed = Chat.getEmbed();

        if (event.getRoles().contains(Core.getClient().getRoleById(Core.getConfig().getMutedRoleID()))) return;

        if (event.getRoles().size() == 1) {
            embed.setDescription(event.getMember().getAsMention() + " lost the role:\n");
            embed.appendDescription("`" + event.getRoles().get(0).getName() + "`");
        } else {
            embed.setDescription(event.getMember().getAsMention() + " lost the roles:\n");

            for (Role role : event.getRoles()) {
                embed.appendDescription("`" + role.getName() + "`, ");
            }
        }

        Bot.getLogChannel().sendMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_ORANGE).build());
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        if (event.getGuild() != Bot.getMainGuild()) return;
        User user = event.getMember().getUser();

        if (event.getPrevNick() == null) {
            Bot.getLogChannel().sendMessage(Chat.getEmbed().setDescription(user.getAsMention() + " changed their name from `" +
                    user.getName() + "` to `" + event.getNewNick() + "`")
                    .setColor(Color.ORANGE).build());
            return;
        }

        if (event.getNewNick() == null) {
            Bot.getLogChannel().sendMessage(Chat.getEmbed().setDescription(user.getAsMention() + " reset their name to `" + user.getName() + "`")
                    .setColor(Color.ORANGE).build());
            return;
        }

        if (!event.getNewNick().equals(user.getName())) {
            Bot.getLogChannel().sendMessage(Chat.getEmbed().setDescription(user.getAsMention() + " changed their name from `" +
                    event.getPrevNick() + "` to `" + event.getNewNick() + "`")
                    .setColor(Color.ORANGE).build());
        }

        Core.log.info("\"" + event.getPrevNick() + "\" is now known as \"" + event.getNewNick() + "\"");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        Core.log.info("Connection to Discord has been reestablished!");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        Core.log.warn("Disconnected from Discord.");
    }

}
