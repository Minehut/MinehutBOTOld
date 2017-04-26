package com.minehut.discordbot.commands.general.minehut;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.exceptions.CommandException;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
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

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by MatrixTunnel on 4/14/2017.
 * API by ReduxRedstone.
 */
public class UserCommand extends Command {

    public UserCommand() {
        super("user", new String[]{}, "<username>", CommandType.GENERAL);
    }

    private JSONObject user;
    private JSONArray profile;
    private JSONObject minehutProfile;
    private boolean valid = true;

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        EmbedBuilder embed = Chat.getEmbed();

        if (args.length == 1) {
            Message mainMsg = channel.sendMessage(new MessageBuilder().setEmbed(embed.addField("Gathering Information...", "This may take a few moments", true)
                    .setColor(Chat.CUSTOM_ORANGE).build()).build()).complete();

            try { //if user name is valid, continue.  If not, skip network check.  If offline, check network.
                user = new URLJson("https://api.mojang.com/users/profiles/minecraft/" + args[0]).getJsonObject();
            } catch (JSONException e) {
                //Not valid
                e.printStackTrace();
                valid = false;
            } catch (IOException e) {
                //Down?
                e.printStackTrace();
                user = null;
            }

            if (user != null) {
                try {
                    profile = new URLJson("https://api.mojang.com/user/profiles/" + user.getString("id") + "/names").getJsonArray();
                } catch (JSONException e) {
                    //Not valid
                    e.printStackTrace();
                    valid = false;
                } catch (IOException e) {
                    //Down?
                    profile = null;
                }
            }


            try {
                if (user != null) {
                    minehutProfile = new URLJson("http://mctoolbox.me/minehut/user/?user=" + user.get("name")).getJsonObject();
                    valid = true;
                } else {
                    minehutProfile = new URLJson("http://mctoolbox.me/minehut/user/?user=" + args[0]).getJsonObject();
                    valid = true;
                }
                if (minehutProfile.optBoolean("error")) {
                    minehutProfile = null;
                }
            } catch (JSONException e) {
                //Not valid
                e.printStackTrace();
                valid = false;
            } catch (IOException e) {
                //Down?
                minehutProfile = null;
            }

            if (!valid) {
                Core.log.info("Invalid"); //The username "" is invalid. Please try again with a different username
                //Invalid
            } else if (user == null && minehutProfile == null) {
                if (user == null && valid) {
                    Core.log.info("Mojang down and never joined minehut"); //That user has never joined Minehut and the Mojang servers are down so the user info could not be displayed. Please try again later
                    return true;
                }
                Core.log.info("Both APIs down"); //Either something went wrong or both Mojang and Minehut are down. Please try again later
                //Apis down
            } else {
                embed.clearFields();
                StringBuilder decription = new StringBuilder();

                if (minehutProfile == null) {
                    //Minehut down (stats, server name, online status)
                    decription.append("**Unable to get Minehut stats.**\n\n");
                    embed.setColor(Chat.CUSTOM_RED);
                } else {
                    if (minehutProfile.getString("about").length() == 0) {
                        decription.append("```Nothing has been written here yet. (probably just html)```\n");
                    } else {
                        decription.append("```").append(minehutProfile.getString("about").length() > 50 ?
                                minehutProfile.getString("about").substring(0, 50) : minehutProfile.getString("about")).append("...```\n");
                    }

                    embed.addField("Profile", "[`" + minehutProfile.getString("name") + "`](https://minehut.com/" + minehutProfile.getString("name") + ")", true)
                            .addField("Friend Count", minehutProfile.getJSONObject("friends").getString("total"), true)
                            .addField("Total Online Time", minehutProfile.getJSONObject("stats").getString("time_online").replace(" of online time.", ""), true)
                            .addField("Server", "`" + minehutProfile.getJSONObject("server").optString("name", "Not created yet") + "`", true)
                            .addField("Rank", minehutProfile.getString("rank").replace("\u00e2\u009d\u00a4", ":heart:"), true)
                            .setColor(isOnlineColor(minehutProfile));

                    if (!isOnline(minehutProfile)) {
                        embed.addField("Last Online", minehutProfile.getString("last_seen").replace("Last seen ", ""), true);
                    }

                    embed.addField("First Joined", minehutProfile.getJSONObject("stats").getString("date_joined"), false);
                }

                if (user == null) {
                    //Mojang down (name history, uuid)
                    embed.setImage(minehutProfile.getJSONObject("icons").getString("body"))
                            .setThumbnail(minehutProfile.getJSONObject("icons").getString("face"))
                            .setAuthor(minehutProfile.getString("name"), "https://minehut.com/" + minehutProfile.getString("name"), Bot.getLogo());
                } else {
                    embed.setImage("https://crafatar.com/renders/body/" + user.get("id") + "?overlay")
                            .setThumbnail("https://crafatar.com/avatars/" + user.get("id") + "?overlay")
                            .setAuthor(user.getString("name"), "https://minehut.com/" + user.getString("name"), Bot.getLogo());

                    StringBuilder str = new StringBuilder();
                    int nameChanges = profile.length();

                    if (nameChanges > 1) {
                        for (Object obj : Bot.reverseJsonArray(profile)) {
                            JSONObject object = (JSONObject) obj;
                            if (object.optLong("changedToAt") == 0) {
                                str.append("`").append(object.getString("name")).append("`\n");
                            } else {
                                str.append("`").append(object.getString("name")).append("` | ")
                                        .append(Bot.formatTime(LocalDateTime.ofInstant(new Date(object.optLong("changedToAt"))
                                                .toInstant(), ZoneId.systemDefault()))).append("\n");
                            }
                        }
                    }

                    if (nameChanges > 1) {
                        embed.addField("Past Names (" + (nameChanges - 1) + ")", str.toString(), false);
                    }

                    decription.append("UUID: `").append(user.getString("id")).append("`")
                            .append("\nSkin: **[Click](https://crafatar.com/skins/").append(user.getString("id")).append(")**")
                            .append("\nNameMC: **[Click](https://namemc.com/profile/").append(user.getString("id")).append(")**");
                }

                Chat.editMessage(embed.setDescription(decription.toString()).build(), mainMsg, 20);
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean isOnline(JSONObject json) {
        String status;

        try {
            status = json.getString("status");
        } catch (JSONException e1) {
            status = "Offline";
        }

        switch (status) {
            case "Online":
                return true;
            case "Offline":
                return false;
            default:
                return false;
        }
    }

    private Color isOnlineColor(JSONObject json) {
        return isOnline(json) ? Chat.CUSTOM_GREEN : Chat.CUSTOM_RED;
    }

}
