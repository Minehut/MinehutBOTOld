package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.management.ToggleMusicCommand;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import com.minehut.discordbot.util.URLJson;
import com.minehut.discordbot.util.music.VideoThread;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

/**
 * Created by MatrixTunnel on 2/21/2017.
 */
public class RandomCommand implements Command {

    @Override
    public String getCommand() {
        return "random";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"randomsong", "rs"};
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (!ToggleMusicCommand.canQueue.get(guild.getId()) && !GuildSettings.isTrusted(sender)) {
            Chat.sendMessage(sender.getAsMention() + " Music commands are currently disabled. " +
                    "If you believe this is an error, please contact a staff member", channel, 10);
            return;
        }
        if (guild.getSelfMember().getVoiceState().getChannel() == null) {
            Chat.sendMessage(sender.getAsMention() + " The bot is not in a voice channel!", channel, 10);
            return;
        }
        if (!guild.getSelfMember().getVoiceState().getChannel().equals(sender.getVoiceState().getChannel())) {
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
            Chat.sendMessage(embed.build(), channel, 25);
        } else if (args.length >= 1) {
            Message mainMessage = channel.sendMessage(new MessageBuilder().setEmbed(embed.addField("Processing...", "This may take a few moments", true)
                    .setColor(Chat.CUSTOM_ORANGE).build()).build()).complete();
            //TODO Make auto remove if nothing found

            StringBuilder term = new StringBuilder();
            for (String s : args) {
                term.append(s).append("-");
            }

            try {
                JSONArray array = new URLJson("http://temp.discord.fm/libraries/" + term.toString().toLowerCase().substring(0, term.length() - 1) + "/json").getJsonArray();
                JSONObject obj = array.getJSONObject(new Random().nextInt(array.length()) + 1); // .nextInt(max) + min

                if (obj.getString("service").equals("YouTubeVideo")) {
                    VideoThread.getThread("https://youtu.be/" + obj.getString("identifier"), channel, sender).start();
                } else {
                    VideoThread.getThread(obj.getString("url"), channel, sender).start(); //Might break if new SoundCloud tracks don't have a url :(
                }


            } catch (JSONException | IOException e) {
                e.printStackTrace();
                Chat.editMessage(embed.clearFields().addField("Whoops! :banana: :monkey:",
                        "The category `" + term.toString().replace("-", " ").substring(0, term.length() - 1) + "` was not found. Please try again with a different term!", true)
                        .setColor(Chat.CUSTOM_RED).build(), mainMessage, 15);
                return;
            }

            Chat.removeMessage(mainMessage);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
