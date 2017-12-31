package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;


public class RandomCommand extends Command { //TODO

    public RandomCommand() {
        super(CommandType.MUSIC, "<playlist>", "random", "randomsong", "rs");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) {
        Chat.removeMessage(message);
        //Member member = guild.getMember(sender.getUser());
//
        //if (!ToggleMusicCommand.canQueue.get(guild.getId()) && !sender.isStaff()) {
        //    Chat.sendMessage(member.getAsMention() + " Music commands are currently disabled. " +
        //            "If you believe this is an error, please contact a staff member", channel, 10);
        //    return true;
        //}
        //if (guild.getSelfMember().getVoiceState().getChannel() == null) {
        //    Chat.sendMessage(member.getAsMention() + " The bot is not in a voice channel!", channel, 10);
        //    return true;
        //}
        //if (!guild.getSelfMember().getVoiceState().getChannel().equals(member.getVoiceState().getChannel())) {
        //    Chat.sendMessage(member.getAsMention() + " You must be in the music channel in order to play songs!", channel, 10);
        //    return true;
        //}
//
        //EmbedBuilder embed = Chat.getEmbed();
//
        //if (args.length == 0) {
        //    embed.setAuthor("Random Song Categories", "https://temp.discord.fm", null)
        //            .setDescription("Be sure to spell the name as shown! (not cap sensitive)\n\n" + //TODO Add playlist by number
        //                    //"[`all`](https://temp.discord.fm)\n" + //TODO Add "all" category
        //                    "[`Electro Hub`](https://temp.discord.fm/libraries/electro-hub)\n" +
        //                    "[`Chill Corner`](https://temp.discord.fm/libraries/chill-corner)\n" +
        //                    "[`Korean Madness`](https://temp.discord.fm/libraries/korean-madness)\n" +
        //                    "[`Japanese Lounge`](https://temp.discord.fm/libraries/japanese-lounge)\n" +
        //                    "[`Classical`](https://temp.discord.fm/libraries/classical)\n" +
        //                    "[`Retro Renegade`](https://temp.discord.fm/libraries/retro-renegade)\n" +
        //                    "[`Metal Mix`](https://temp.discord.fm/libraries/metal-mix)\n" +
        //                    "[`Hip hop`](https://temp.discord.fm/libraries/hip-hop)\n" +
        //                    "[`Electro Swing`](https://temp.discord.fm/libraries/electro-swing)\n" +
        //                    "[`Purely Pop`](https://temp.discord.fm/libraries/purely-pop)\n" +
        //                    "[`Rock n Roll`](https://temp.discord.fm/libraries/rock-n-roll)\n" +
        //                    "[`Coffee house Jazz`](https://temp.discord.fm/libraries/coffee-house-jazz)");
        //    Chat.sendMessage(Chat.respondMessage(member, embed.build()), channel, 25);
        //} else {
        //    Message mainMessage = channel.sendMessage(new MessageBuilder().setEmbed(embed.addField("Processing...", "This may take a few moments", true)
        //            .setFooter("Requested by @" + Chat.getFullName(sender.getUser()), null).setColor(Chat.CUSTOM_ORANGE).build()).build()).complete();
        //    Chat.removeMessage(mainMessage, 30);
//
        //    StringBuilder term = new StringBuilder();
        //    for (String s : args) {
        //        term.append(s).append("-");
        //    }
//
        //    try {
//
        //        JSONArray array = new JSONArray(MinehutBot.getHttpClient().newCall(new Request.Builder()
        //                .url("https://temp.discord.fm/libraries/" + term.toString().toLowerCase().substring(0, term.length() - 1) + "/json")
        //                .header("accept", "application/json").build())
        //                .execute().body().string());
        //        JSONObject obj = array.getJSONObject(new Random().nextInt(array.length()) + 1); // .nextInt(max) + min
//
        //        if (obj.getString("service").equals("YouTubeVideo")) {
        //            VideoThread.getThread("https://youtu.be/" + obj.getString("identifier"), channel, member).start();
        //        } else {
        //            VideoThread.getThread(obj.getString("url"), channel, member).start(); //Might break if new SoundCloud tracks don't have a url :(
        //        }
        //    } catch (JSONException | IOException e) {
        //        e.printStackTrace();
        //        Chat.editMessage(Chat.respondMessage(member, embed.clearFields().addField("Whoops! :banana: :monkey:",
        //                "The category `" + term.toString().replace("-", " ").substring(0, term.length() - 1) + "` was not found. Please try again with a different term!", true)
        //                .setColor(Chat.CUSTOM_RED).build()), mainMessage, 15);
        //        return true;
        //    }
//
        //    Chat.removeMessage(mainMessage);
        //}

        return true;
    }

}
