package com.mens.doman.database;

import com.mens.doman.DomanMain;
import com.mens.doman.utils.Prefix;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private Connection con;
    private final DomanMain plugin;
    private final String FILENAME = "database.db";
    private final Prefix prefix;

    public Database(DomanMain plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
    }

    public Connection getCon() {
        return con;
    }

    public void openFirstConnection() {
        File file = new File(plugin.getDataFolder(), FILENAME);
        final String URL = "jdbc:sqlite:" + file;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(URL);
            createTables();
            Bukkit.getConsoleSender().sendMessage(prefix.getPrefix() + "Databáze §3SQLite §7připojena!");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(prefix.getPrefix() + "K databázi §3SQLite §7se nepodařilo připojit!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public void openConnection() {
        File file = new File(plugin.getDataFolder(), FILENAME);
        final String URL = "jdbc:sqlite:" + file;
        try {
            if(con != null) {
                con.close();
            }
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(URL);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private void createTables() {
        try {
            if(!con.isValid(0)) {
                openConnection();
            }
            PreparedStatement stm = con.prepareStatement("CREATE TABLE IF NOT EXISTS discord_auth(id INTEGER PRIMARY KEY AUTOINCREMENT, userUUID VARCHAR(36), discordUUID VARCHAR(36))");
            Bukkit.getConsoleSender().sendMessage(prefix.getPrefix() + "Chybějící tabulky vytvořeny!");
            stm.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
