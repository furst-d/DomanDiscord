package com.mens.doman;

import com.mens.doman.commands.dd.DomanDiscord;
import com.mens.doman.database.Database;
import com.mens.doman.discord.DiscordManager;
import com.mens.doman.eventhandlers.OnAsyncPlayerChatEvent;
import com.mens.doman.eventhandlers.OnPlayerJoinEvent;
import com.mens.doman.eventhandlers.OnPlayerQuitEvent;
import com.mens.doman.utils.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DomanMain extends JavaPlugin {
    private PluginManager pm;
    private Database db;

    private YamlFile perms;

    @Override
    public void onEnable() {
        getLogger().info("Plugin spusten!");
        loadConfig();
        pm = Bukkit.getPluginManager();
        db = new Database(this);
        db.openFirstConnection();
        loadEvents();
        loadFiles();
        DiscordManager.startBot(this);
        loadCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin vypnut!");
        DiscordManager.stopBot(this);
    }

    public Database getDb() {
        return db;
    }

    public YamlFile getPerms() {
        return perms;
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void loadCommands() {
        getCommand("dd").setExecutor(new DomanDiscord(this));
    }

    private void loadEvents() {
        pm.registerEvents(new OnAsyncPlayerChatEvent(this), this);
        pm.registerEvents(new OnPlayerJoinEvent(this), this);
        pm.registerEvents(new OnPlayerQuitEvent(this), this);
    }

    private void loadFiles() {
        perms = new YamlFile(this, getDataFolder().getParentFile().getPath() + "/PermissionsEx/permissions.yml");
    }
}
