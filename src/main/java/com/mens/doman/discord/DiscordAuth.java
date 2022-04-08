package com.mens.doman.discord;

import com.mens.doman.DomanMain;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscordAuth {
    private final DomanMain plugin;
    private static List<AuthInfo> keys;

    public DiscordAuth(DomanMain plugin) {
        this.plugin = plugin;
        keys = new ArrayList<>();
    }

    public static List<AuthInfo> getKeys() {
        return keys;
    }

    public boolean isAuthorized(String discordUUID) {
        int count = 0;
        try {
            if(!plugin.getDb().getCon().isValid(0)) {
                plugin.getDb().openConnection();
            }
            PreparedStatement stm = plugin.getDb().getCon().prepareStatement("SELECT COUNT(discordUUID) FROM discord_auth WHERE discordUUID = ?");
            stm.setString(1, discordUUID);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count != 0;
    }

    public AuthInfo checkKey(String myKey) {
        Optional<AuthInfo> info = keys.stream().filter(key -> key.getKey().equals(myKey)).findFirst();
        if(info.isPresent()) {
            keys.removeIf(key -> key.getKey().equals(myKey));
            return info.get();
        }
        return null;
    }

    public void authorize(AuthInfo info) {
        try {
            if(!plugin.getDb().getCon().isValid(0)) {
                plugin.getDb().openConnection();
            }
            PreparedStatement stm = plugin.getDb().getCon().prepareStatement("INSERT INTO discord_auth(userUUID, discordUUID) VALUES (?, ?)");
            stm.setString(1, info.getPlayer().getUniqueId().toString());
            stm.setString(2, info.getDiscordUser().getId());
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getUserUUID(User user) {
        String uuid = null;
        try {
            if(!plugin.getDb().getCon().isValid(0)) {
                plugin.getDb().openConnection();
            }
            PreparedStatement stm = plugin.getDb().getCon().prepareStatement("SELECT userUUID FROM discord_auth WHERE discordUUID = ?");
            stm.setString(1, user.getId());
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                uuid = rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return uuid;
    }
}
