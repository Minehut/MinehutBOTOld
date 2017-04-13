package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.URLJson;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by MatrixTunnel on 3/20/2017.
 */
public class CheckNameCommand implements Command {

    @Override
    public String getCommand() {
        return "checkname";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"namehistory", "cn", "namecheck"};
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        EmbedBuilder embed = Chat.getEmbed();

        if (args.length >= 1 && args[0].length() <= 16) { //TODO Add support for uuid lookup
            try {
                JSONObject user = URLJson.readJsonObjectFromUrl("https://api.mojang.com/users/profiles/minecraft/" + args[0]);
                JSONArray profile = URLJson.readJsonArrayFromUrl("https://api.mojang.com/user/profiles/" + user.getString("id") + "/names");

                String username = profile.getJSONObject(profile.length() - 1).getString("name");
                String uuid = user.getString("id");

                StringBuilder str = new StringBuilder();
                int nameChanges = profile.length();

                if (nameChanges > 1) {
                    for (Object obj : Bot.reverseJsonArray(profile)) {
                        JSONObject object = (JSONObject) obj;
                        if (object.optLong("changedToAt") == 0) {
                            str.append("`").append(object.getString("name")).append("`\n");
                        } else {
                            str.append("`").append(object.getString("name"))
                                    .append("` | ").append(Bot.formatTime(LocalDateTime.ofInstant(new Date(object.optLong("changedToAt")).toInstant(), ZoneId.systemDefault()))).append("\n");
                        }
                    }
                }

                if (nameChanges > 1) {
                    embed.addField("Past Names (" + (nameChanges - 1) + ")", str.toString(), true);
                }

                Chat.sendMessage(embed.setAuthor(username, null, null).setDescription("UUID: `" + uuid + "`" +
                        "\nSkin: **[Click](https://crafatar.com/skins/" + uuid + ")**" +
                        "\nNameMC: **[Click](https://namemc.com/profile/" + uuid + ")**" +
                        "\nMinehut: **[Click](https://minehut.com/" + username + ")**")
                        .setImage("https://crafatar.com/renders/body/" + uuid + "?overlay")
                        .setThumbnail("https://crafatar.com/avatars/" + uuid + "?overlay").build(), channel, 20);
            } catch (JSONException e) {
                //Invalid user
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //format
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
