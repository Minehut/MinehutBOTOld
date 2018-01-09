package com.minehut.discordbot.util;

import com.minehut.discordbot.MinehutBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

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
        ADMIN("Admins"),
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
                String rankName = getCurrentRank(Bot.getMainGuild().getMemberById(id).getRoles()).name();
                if (!object.optString("rank", "DEFAULT").equals(rankName)) object.put("rank", rankName);
                return object;
            }
        }
        return createUserObject();
    }

    private JSONObject createUserObject() {
        JSONObject object = new JSONObject().put("id", id).put("rank", getCurrentRank(Bot.getMainGuild().getMemberById(id).getRoles()).name()).put("discord_muted", false);
        userManager.getUserJson().put(object);
        return object;
    }

    public boolean hasRank(Rank rank) {
        return Rank.DEFAULT.compareTo(rank) >= 0;
    }

    public boolean isStaff() {
        return rank.ordinal() <= 3; // staff ranks (4) - 1 = 3
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

    private Rank getCurrentRank(List<Role> roles) {
        if (roles.isEmpty()) return Rank.DEFAULT;

        Rank rank = null;

        for (Role role : roles) {
            if (rank == null) {
                rank = getRankByRole(role);
            } else {
                if (getRankByRole(role).ordinal() > rank.ordinal()) {
                    rank = getRankByRole(role);
                }
            }
        }

        return rank;
    }

    private Rank getRankByRole(Role role) {
        return Arrays.stream(Rank.values()).filter(r -> r.getDisplayName().equals(role.getName())).findFirst().orElse(Rank.DEFAULT);
    }

}
