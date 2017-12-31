package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.io.IOException;


public class ReloadCommand extends Command {

    private MinehutBot bot = MinehutBot.get();

    public ReloadCommand() {
        super(CommandType.MASTER, null, "reload", "rl");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        try {
            bot.getConfig().load();
            MinehutBot.log.info("Config reloaded!");
        } catch (IOException e) {
            MinehutBot.log.error("Error reloading config", e);
            throw new CommandException("Error reloading config");
        }

        if (Bot.getMainGuild().getAudioManager().getConnectedChannel() == null) {
            String audioChannelId = MinehutBot.get().getConfig().getAudioChannelId();

            if (audioChannelId != null && !audioChannelId.isEmpty()) {
                VoiceChannel audioChannel = MinehutBot.get().getDiscordClient().getVoiceChannelById(MinehutBot.get().getConfig().getAudioChannelId());

                if (audioChannel != null) {
                    audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);
                }
            }
        }

        return true;
    }

}
