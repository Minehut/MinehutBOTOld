package com.minehut.discordbot.events;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.management.ToggleMusicCommand;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class ServerEvents extends ListenerAdapter {

    private MinehutBot bot = MinehutBot.get();

    @Override
    public void onReady(ReadyEvent event) {
        Bot.updateUsers();

        bot.getDiscordClient().getPresence().setStatus(OnlineStatus.ONLINE);

        ToggleMusicCommand.canQueue.put(Bot.getMainGuild().getId(), true);

        bot.getDiscordClient().getGuilds().forEach(guild -> {
            if (!guild.getId().equals(bot.getConfig().getMainGuildId())) {
                Chat.sendPM(guild.getOwner().getUser(), "The " + bot.getDiscordClient().getSelfUser().getName() + " Bot is for private use only. This incident has been reported");
                guild.leave().queue();
            }
        });

        String audioChannelId = MinehutBot.get().getConfig().getAudioChannelId();

        if (audioChannelId != null && !audioChannelId.isEmpty()) {
            VoiceChannel audioChannel = MinehutBot.get().getDiscordClient().getVoiceChannelById(MinehutBot.get().getConfig().getAudioChannelId());

            if (audioChannel != null) {
                audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);
            }
        }

        MinehutBot.enabled = true;
        MinehutBot.log.info("Bot ready.");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Bot.updateUsers();

        User user = event.getMember().getUser();
        EmbedBuilder embed = Chat.getEmbed();

        UserClient client = new UserClient(user.getId());

        if (Bot.getMutedRole() != null && client.isMuted()) {
            event.getGuild().getController().addRolesToMember(event.getMember(), Bot.getMutedRole()).queue();
        }

        embed.setDescription("*" + event.getMember().getAsMention() + " joined the server.*" +
                "\n\n**Account Creation:** " + Bot.formatTime(LocalDateTime.from(user.getCreationTime())));

        String isName;

        if (user.getName().length() < 15 && !Chat.hasRegex(Pattern.compile("([^_A-Za-z0-9])"), user.getName())) {
            try {
                ResponseBody body = MinehutBot.get().getHttpClient().newCall(new Request.Builder()
                        .url("https://api.mojang.com/users/profiles/minecraft/" + user.getName())
                        .header("Accept", "application/json").build())
                        .execute().body();

                if (body != null) {
                    isName = "True";
                } else {
                    isName = "False";
                }
            } catch (IOException e) {
                isName = "Unknown";
            }
        } else {
            isName = "False";
        }

        embed.appendDescription("\n**MC Name:** " + isName);

        event.getGuild().getMembers().forEach(member -> {
            if (!member.equals(event.getMember()) && member.getEffectiveName().equals(event.getMember().getEffectiveName())) {
                embed.appendDescription("\n\n**Same Name:** " + member.getAsMention());
            }
        });

        Bot.logGuildMessage(new MessageBuilder().setEmbed(embed.setTitle(Chat.getFullName(user), null)
                .setThumbnail(user.getEffectiveAvatarUrl()).setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_GREEN).build()));
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        Bot.updateUsers();
        User user = event.getMember().getUser();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(user), null)
                .setThumbnail(user.getEffectiveAvatarUrl()).setDescription("*" + event.getMember().getAsMention() + " left the server.*" +
                        "\n\n**Forums:** [`" + user.getName() + "`](https://minehut.com/" + user.getName().replace(" ", "") + ")")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Chat.CUSTOM_RED).build()));
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        Bot.updateUsers();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(event.getUser()), null)
                .setDescription("*was banned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.RED).build()));
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        Bot.updateUsers();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed().setTitle(Chat.getFullName(event.getUser()), null)
                .setDescription("*was unbanned from the server.*")
                .setFooter("System time | " + Bot.getBotTime(), null)
                .setColor(Color.PINK).build()));
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        EmbedBuilder embed = Chat.getEmbed();
        StringBuilder roles = new StringBuilder();

        if (Bot.getMutedRole() != null) {
            event.getRoles().stream().filter(role -> !role.getId().equals(Bot.getMutedRole().getId())).forEach(role -> roles.append("`").append(role.getName()).append("`, "));
        } else {
            event.getRoles().forEach(role -> roles.append("`").append(role.getName()).append("`, "));
        }

        if (roles.length() > 0) {
            embed.setDescription(event.getMember().getAsMention() + " gained the role(s):\n" + roles.substring(0, roles.length() - 2));

            Bot.logGuildMessage(new MessageBuilder().setEmbed(embed.setFooter("System time | " + Bot.getBotTime(), null)
                    .setColor(Chat.CUSTOM_ORANGE).build()));
        }
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        EmbedBuilder embed = Chat.getEmbed();
        StringBuilder roles = new StringBuilder();

        if (Bot.getMutedRole() != null) {
            event.getRoles().stream().filter(role -> !role.getId().equals(Bot.getMutedRole().getId())).forEach(role -> roles.append("`").append(role.getName()).append("`, "));
        } else {
            event.getRoles().forEach(role -> roles.append("`").append(role.getName()).append("`, "));
        }

        if (roles.length() > 0) {
            embed.setDescription(event.getMember().getAsMention() + " lost the role(s):\n" + roles.substring(0, roles.length() - 2));

            Bot.logGuildMessage(new MessageBuilder().setEmbed(embed.setFooter("System time | " + Bot.getBotTime(), null)
                    .setColor(Chat.CUSTOM_ORANGE).build()));
        }
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) { //TODO Add Minecraft name ???
        User user = event.getMember().getUser();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed()
                .setDescription(Chat.getFullName(user) + " | " + event.getMember().getAsMention() + " Changed nick\n" +
                        "`" + (event.getPrevNick() == null ? "null" : event.getPrevNick()) +
                        "` -> `" + (event.getNewNick() == null ? "null" : event.getNewNick()) + "`")
                .setColor(Color.ORANGE).setFooter("System time | " + Bot.getBotTime(), null).build()));
    }

    @Override
    public void onUserNameUpdate(UserNameUpdateEvent event) {
        User user = event.getUser();

        Bot.logGuildMessage(new MessageBuilder().setEmbed(Chat.getEmbed()
                .setDescription(Chat.getFullName(user) + " | " + event.getUser().getAsMention() + " Changed name\n" +
                        "`" + event.getOldName() + "` -> `" + user.getName() + "`")
                .setColor(Color.ORANGE).setFooter("System time | " + Bot.getBotTime(), null).build()));
    }

}
