package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.URLJson;
import com.minehut.discordbot.util.music.VideoThread;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

/**
 * Created by MatrixTunnel on 2/21/2017.
 */
public class RandomSongCommand implements Command {

    @Override
    public String getCommand() {
        return "randomsong";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rs", "random"};
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.setAutoDelete(message, 5);

        if (guild.getSelfMember().getVoiceState().getChannel() == null) {
            Chat.sendMessage(sender.getAsMention() + " The bot is not in a voice channel!", channel, 10);
            return;
        }
        if (!guild.getSelfMember().getVoiceState().getChannel().equals(member.getVoiceState().getChannel())) {
            Chat.sendMessage(sender.getAsMention() + " You must be in the music channel in order to play songs!", channel, 10);
            return;
        }

        EmbedBuilder embed = Chat.getEmbed();

        if (args.length == 0) {
            embed.setAuthor("Random Song Categories", "https://temp.discord.fm", null)
                    .setDescription("Be sure to spell the name as shown! (not cap sensitive)\n\n" +
                            //"[`all`](https://temp.discord.fm)\n" + //TODO Add "all" category
                            "[`Electro Hub`](https://temp.discord.fm/libraries/electro-hub)\n" +
                            "[`Chill Corner`](https://temp.discord.fm/libraries/chill-corner)\n" +
                            "[`Korean Madness`](https://temp.discord.fm/libraries/korean-madness)\n" +
                            "[`Japanese Lounge`](https://temp.discord.fm/libraries/japanese-lounge)\n" +
                            "[`Classical`](https://temp.discord.fm/libraries/classical)\n" +
                            "[`Retro Renegade`](https://temp.discord.fm/libraries/retro-renegade)\n" +
                            "[`Metal Mix`](https://temp.discord.fm/libraries/metal-mix)\n" +
                            "[`Hip hop`](https://temp.discord.fm/libraries/hip-hop)\n" +
                            "[`Electro Swing`](https://temp.discord.fm/libraries/electro-swing)\n" +
                            "[`Purely Pop`](https://temp.discord.fm/libraries/purely-pop)\n" +
                            "[`Rock n Roll`](https://temp.discord.fm/libraries/rock-n-roll)\n" +
                            "[`Coffee house Jazz`](https://temp.discord.fm/libraries/coffee-house-jazz)");
            Chat.sendMessage(embed, channel, 25);
        } else if (args.length >= 1) {
            Message mainMessage = Chat.sendMessage(embed.addField("Processing...", "This may take a few moments", true)
                    .setColor(Chat.CUSTOM_ORANGE), channel, 120);

            StringBuilder term = new StringBuilder();
            for (String s : args) {
                term.append(s).append("-");
            }

            try {
                JSONArray array = URLJson.readJsonArrayFromUrl("http://temp.discord.fm/libraries/" + term.toString().toLowerCase().substring(0, term.length() - 1) + "/json");
                Integer random = new Random().nextInt((array.length() - 1) + 1) + 1;
                JSONObject obj = array.getJSONObject(random);

                VideoThread.getThread(obj.getString("url"), channel, sender).start();
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Chat.editMessage(embed.clearFields().addField("Whoops! :banana: :monkey:", "The category `" + term + "` was not found. Please try again with a different term!", true)
                        .setColor(Chat.CUSTOM_RED), mainMessage);
                return;
            }

            Chat.removeMessage(mainMessage);
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
