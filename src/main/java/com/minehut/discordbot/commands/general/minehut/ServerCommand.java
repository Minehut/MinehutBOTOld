package com.minehut.discordbot.commands.general.minehut;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.URLJson;
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

/**
 * Created by MatrixTunnel on 4/14/2017.
 */
public class ServerCommand implements Command {

    @Override
    public String getCommand() {
        return "server";
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        EmbedBuilder embed = Chat.getEmbed();

        if (args.length == 1) {
            Message mainMsg = channel.sendMessage(new MessageBuilder().setEmbed(embed.addField("Gathering Information...", "This may take a few moments", true)
                    .setColor(Chat.CUSTOM_ORANGE).build()).build()).complete();

            try {
                JSONArray servers = URLJson.readJsonArrayFromUrl("https://minehut.com/api/servers/");

                for (Object server : servers) {
                    JSONObject obj = (JSONObject) server;

                    if (obj.get("name").equals(args[0])) {
                        embed.clearFields().setDescription("`" + String.valueOf(obj.getString("motd")) + "`");

                        try {
                            JSONArray json = URLJson.readJsonArrayFromUrl("https://api.mojang.com/user/profiles/" + obj.getString("owner").replaceAll("-", "") + "/names");
                            String name = json.getJSONObject(json.length() - 1).getString("name");

                            embed.setAuthor(obj.getString("name") + " Info", "https://minehut.com/" + name, Bot.getLogo())
                                    .addField("Owner", "[`" + name + "`](https://minehut.com/" + name + ")", true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        embed.addField("Players Online", obj.getInt("player_count") + "/" + obj.getInt("max_players"), true)
                                .addField("Total Joins", String.valueOf(obj.getInt("total_joins")), true)
                                .addField("Unique Joins", String.valueOf(obj.getJSONArray("user_joins").length()), true)
                                .addField("Total Server Starts", String.valueOf(obj.getInt("starts")), true)
                                .addField("Server Host", obj.getString("host"), true);

                        Chat.editMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                                .setColor(Chat.CUSTOM_GREEN).build(), mainMsg, 20);
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Chat.editMessage(embed.clearFields().setAuthor("Minehut Network Status", "https://minehut.com", Bot.getLogo())
                        .setDescription("\nThe network is down at this time. Please try again later\n")
                        .setColor(Chat.CUSTOM_RED).build(), mainMsg, 10);
                return;
            }

            // Offline?
            try {
                JSONObject server = URLJson.readJsonObjectFromUrl("http://mctoolbox.me/minehut/serverowner/?server=" + args[0]);

                embed.clearFields().setAuthor(server.getJSONObject("server").optString("name") + " Info",
                        "https://minehut.com/" + server.getString("name"), Bot.getLogo())
                        .addField("Owner", "[`" + server.getString("name") + "`](https://minehut.com/" + server.getString("name") + ")", true)
                        .addField("Total Joins", String.valueOf(server.getJSONObject("server").getString("total")), true)
                        .addField("Unique Joins", String.valueOf(server.getJSONObject("server").getString("unique")), true);

                Chat.editMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                        .setColor(Chat.CUSTOM_RED).build(), mainMsg, 20);
            } catch (JSONException e) {
                Chat.editMessage(embed.clearFields()
                        .addField("Whoops! :banana: :monkey:", "The server `" + args[0] + "` was not found. Please try again with a different name!", true)
                        .setColor(Chat.CUSTOM_RED).build(), mainMsg, 10);
            } catch (IOException e) {
                e.printStackTrace();
                Chat.editMessage(embed.clearFields().setAuthor("Minehut Network Status", "https://minehut.com", Bot.getLogo())
                        .setDescription("\nThe network is down at this time. Please try again later\n")
                        .setColor(Chat.CUSTOM_RED).build(), mainMsg, 10);
            }
        } else {
            Chat.sendMessage(sender.getAsMention() + " Usage: ```" + getCommandUsage() + "```", channel, 10);
        }
    }

    @Override
    public String getCommandUsage() {
        return Command.getPrefix() + getCommand() + " <server_name>";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
