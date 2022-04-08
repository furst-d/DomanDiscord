package com.mens.doman.eventhandlers;

import com.mens.doman.DomanMain;
import com.mens.doman.discord.DiscordManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {
    private final DomanMain plugin;

    public OnPlayerQuitEvent(DomanMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DiscordManager manager = new DiscordManager(plugin);
        manager.sendInfoMessage(event.getQuitMessage());
    }
}
