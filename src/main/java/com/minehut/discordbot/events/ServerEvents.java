package com.minehut.discordbot.events;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.management.ToggleMusicCommand;
import com.minehut.discordbot.commands.music.SkipCommand;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import com.minehut.discordbot.util.tasks.running.PunishmentChecker;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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

        Bot.nowPlaying = new ArrayList<>();
        SkipCommand.votes = new ArrayList<>();
        ChatEvents.messages = new HashMap<>();
        ChatEvents.amount = new HashMap<>();
        ToggleMusicCommand.canQueue = new HashMap<>();

        for (Guild guild : Core.getClient().getGuilds()) { //TODO Replace with roles command
            ToggleMusicCommand.canQueue.put(guild.getId(), true);
            for (Role role : guild.getRoles()) {
                Core.log.info(guild.getName() + " - \"" + role.getName() + "\" ID: \"" + role.getId() + "\" - " + role.getPermissions());
            }
        }

        PunishmentChecker.checkPuns();
        Core.log.info("Trackers Set");

        VoiceChannel channel = Core.getClient().getVoiceChannelById(Core.getConfig().getMainMusicChannelID());
        if (channel != null) {
            channel.getGuild().getAudioManager().openAudioConnection(channel);
        }

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Core.log.error("Error trusting cert", e);
        }


        Core.enabled = true;
        Core.log.info("Bot ready.");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        ToggleMusicCommand.canQueue.put(event.getGuild().getId(), true);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        ToggleMusicCommand.canQueue.remove(event.getGuild().getId());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild() == Bot.getMainGuild()) Bot.updateUsers();
        User user = event.getMember().getUser();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(user), null)
                .setThumbnail(user.getEffectiveAvatarUrl()).setDescription("*" + event.getMember().getAsMention() + " joined the server.*" +
                        "\n\n**Account Creation:** " + Bot.formatTime(LocalDateTime.from(user.getCreationTime())) +
                        "\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_GREEN).build()), event.getGuild());
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (event.getGuild() == Bot.getMainGuild()) Bot.updateUsers();
        User user = event.getMember().getUser();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(user), null)
                .setThumbnail(user.getEffectiveAvatarUrl()).setDescription("*" + event.getMember().getAsMention() + " left the server.*" +
                        "\n\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_RED).build()), event.getGuild());
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        if (event.getGuild() == Bot.getMainGuild()) Bot.updateUsers();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(event.getUser()), null)
                .setDescription("*was banned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.RED).build()), event.getGuild());
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        if (event.getGuild() == Bot.getMainGuild()) Bot.updateUsers();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(event.getUser()), null)
                .setDescription("*was unbanned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.PINK).build()), event.getGuild());
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        EmbedBuilder embed = Chat.getEmbed();
        StringBuilder roles = new StringBuilder();

        if (GuildSettings.getMutedRole(event.getGuild()) != null &&
                event.getRoles().contains(GuildSettings.getMutedRole(event.getGuild()))) {
            Role mutedRole = GuildSettings.getMutedRole(event.getGuild());

            for (Role role : event.getRoles()) {
                if (role != mutedRole) roles.append("`").append(role.getName()).append("`, ");
            }
        } else {
            for (Role role : event.getRoles()) {
                roles.append("`").append(role.getName()).append("`, ");
            }
        }

        if (roles.length() > 0) {
            embed.setDescription(event.getMember().getAsMention() + " gained the role(s):\n" + roles.substring(0, roles.length() - 2));

            Bot.logGuildMessage(new MessageBuilder().setEmbed(embed.setFooter("System time | " + Bot.getBotTime(), null)
                    .setColor(Chat.CUSTOM_ORANGE).build()), event.getGuild());
        }
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        EmbedBuilder embed = Chat.getEmbed();
        StringBuilder roles = new StringBuilder();

        if (GuildSettings.getMutedRole(event.getGuild()) != null &&
                event.getRoles().contains(GuildSettings.getMutedRole(event.getGuild()))) {
            Role mutedRole = GuildSettings.getMutedRole(event.getGuild());

            for (Role role : event.getRoles()) {
                if (role != mutedRole) roles.append("`").append(role.getName()).append("`, ");
            }
        } else {
            for (Role role : event.getRoles()) {
                roles.append("`").append(role.getName()).append("`, ");
            }
        }

        if (roles.length() > 0) {
            embed.setDescription(event.getMember().getAsMention() + " lost the role(s):\n" + roles.substring(0, roles.length() - 2));

            Bot.logGuildMessage(new MessageBuilder().setEmbed(embed.setFooter("System time | " + Bot.getBotTime(), null)
                    .setColor(Chat.CUSTOM_ORANGE).build()), event.getGuild());
        }
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) { //TODO Add Minecraft name ???
        User user = event.getMember().getUser();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed()
                .setDescription(Chat.getFullName(user) + " | " + event.getMember().getAsMention() + " Changed nick\n" +
                        "`" + (event.getPrevNick() == null ? "null" : event.getPrevNick()) +
                        "` -> `" + (event.getNewNick() == null ? "null" : event.getNewNick()) + "`")
                .setColor(Color.ORANGE).setFooter("System time | " + Bot.getBotTime(), null).build()), event.getGuild());
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
