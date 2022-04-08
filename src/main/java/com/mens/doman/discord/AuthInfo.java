package com.mens.doman.discord;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;

public class AuthInfo {
    private final Player player;
    private final User discordUser;
    private final String key;

    public AuthInfo(Player player, User discordUser, String key) {
        this.player = player;
        this.discordUser = discordUser;
        this.key = key;
    }

    public Player getPlayer() {
        return player;
    }

    public User getDiscordUser() {
        return discordUser;
    }

    public String getKey() {
        return key;
    }
}
