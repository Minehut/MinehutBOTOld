package com.minehut.discordbot.util;

import com.minehut.discordbot.MinehutBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

/**
 * Created by MatrixTunnel on 12/31/2017.
 */
public class UserClient {

    private UserManager userManager = MinehutBot.get().getUserManager();

    private JSONObject user;

    @Getter private String id;
    @Getter private Rank rank;
    @Getter private boolean muted;

    public enum Rank {
        DEVELOPER("Developers"),
        JR_DEVELOPER("Junior Developers"),
        SR_MOD("Senior Moderators"),
        MOD("Moderators"),
        JR_MOD("Junior Moderators"),

        YOUTUBE("Youtube"),
        ARTIST("Artist"),
        BUILDER("Builder"),

        PATRON("Patron"),
        LEGEND("Legend"),
        PRO("Pro"),
        VIP("Vip"),

        DEFAULT("Default");

        private String display;

        Rank(String display) {
            this.display = display;
        }

        public String getDisplayName() {
            return display;
        }
    }

    public UserClient(User user) {
        this(user.getId());
    }

    public UserClient(String id) {
        this.id = id;

        this.user = getUserObject();
        this.rank = Rank.valueOf(user.opt("rank").toString());
        this.muted = user.optBoolean("discord_muted", false);
    }

    public JSONObject getUserObject() {
        for (Object obj : userManager.getUserJson()) {
            JSONObject object = (JSONObject) obj;
            if (object.optString("id", "").equals(id)) {
                return object;
            }
        }
        return createUserObject();
    }

    private JSONObject createUserObject() {
        JSONObject object = new JSONObject().put("id", id).put("rank", Rank.DEFAULT).put("discord_muted", false);
        userManager.getUserJson().put(object);
        return object;
    }

    public boolean hasRank(Rank rank) {
        return Rank.DEFAULT.compareTo(rank) >= 0;
    }

    public boolean isStaff() {
        return rank.ordinal() <= 4; // staff ranks (5) - 1 = 4
    }

    public void toggleMute() {
        user.put("discord_muted", muted = !muted);
    }

    public User getUser() {
        return MinehutBot.get().getDiscordClient().getUserById(id);
    }

    public String toString() {
        return "{id=\"" + id + "\",rank=\"" + rank.name() + "\",discord_muted=\"" + String.valueOf(muted) + "\"}";
    }

}
