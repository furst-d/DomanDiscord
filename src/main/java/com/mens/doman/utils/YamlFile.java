package com.mens.doman.utils;

import com.mens.doman.DomanMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YamlFile {
    private final DomanMain plugin;
    private FileConfiguration conf;
    private final File file;

    public YamlFile(DomanMain plugin, String path) {
        this.plugin = plugin;
        this.file = new File(path);
        loadData();
    }

    public File getFile() {
        return file;
    }

    private void loadData() {
        try {
            conf = YamlConfiguration.loadConfiguration(file);
        } catch (IllegalArgumentException e) {
            conf = new YamlConfiguration();
        }
    }

    public void saveData() {
        try {
            getData().save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Chyba při ukládání do souboru " + file, e);
        }
    }

    public FileConfiguration getData() {
        if(conf == null) {
            loadData();
        }
        return conf;
    }

    public void reload() {
        loadData();
    }
}
