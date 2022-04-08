package com.mens.doman.eventhandlers;

import com.mens.doman.DomanMain;
import com.mens.doman.discord.DiscordManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnAsyncPlayerChatEvent implements Listener {
    private final DomanMain plugin;

    public OnAsyncPlayerChatEvent(DomanMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        DiscordManager manager = new DiscordManager(plugin);
        manager.sendChatMessage(event.getPlayer(), event.getMessage());
    }
}
