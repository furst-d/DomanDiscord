package com.mens.doman.commands.dd;

import com.mens.doman.DomanMain;
import com.mens.doman.discord.DiscordAuth;
import com.mens.doman.discord.DiscordManager;
import com.mens.doman.discord.AuthInfo;
import com.mens.doman.utils.KeyGenerator;
import com.mens.doman.utils.Prefix;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomanDiscord implements CommandExecutor, TabCompleter {
    private final DomanMain plugin;
    private final Prefix prefix;
    private final KeyGenerator keyGenerator;
    private final DiscordAuth auth;
    private final DiscordManager manager;

    public DomanDiscord(DomanMain plugin) {
        this.plugin = plugin;
        prefix = new Prefix();
        keyGenerator = new KeyGenerator();
        manager = new DiscordManager(plugin);
        auth = new DiscordAuth(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(command.getName().equals("dd")) {
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("auth")) {
                        if(player.hasPermission("dd.auth")) {
                            if (args[1].startsWith("uid:")) {
                                String uuid = args[1].substring(4);
                                System.out.println(uuid);
                                String key = keyGenerator.generateKey(12);
                                User user = manager.getUserByUUID(uuid);
                                if(user == null) {
                                    player.sendMessage(prefix.getPrefix() + "Zadaný identifikátor nebyl nalezen. Zkontrolujte, zdali se příkaz shoduje s tím, který Vám byl zaslán na discord.");
                                    return false;
                                }
                                DiscordAuth.getKeys().add(new AuthInfo(player, user, key));
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setTitle("Žádost o ověření");
                                builder.setDescription("Byl přijat požadavek na ověření. Pokud nebyl vytvořen Vámi, můžete tuto zprávu ignorovat. V opačném případě pokračujte dle instrukcí.");
                                builder.setColor(new Color(104, 64, 236));
                                builder.addField("Instrukce:", "Z vašeho herního účtu ve hře odešlete následující příkaz:", true);
                                builder.addField("Příkaz o odeslání (2/2):", "/dd auth key:" + key, true);
                                manager.sendPrimaryEmbedMessage(user, builder.build());
                                player.sendMessage(prefix.getPrefix() + "Na Discord Vám byly zaslány další pokyny");
                            } else if (args[1].startsWith("key:")) {
                                String key = args[1].substring(4);
                                AuthInfo info = auth.checkKey(key);
                                if(info == null) {
                                    player.sendMessage(prefix.getPrefix() + "Zadaný klíč nebyl nalezen. Zkontrolujte, zdali se příkaz shoduje s tím, který Vám byl zaslán na discord.");
                                    return false;
                                }
                                auth.authorize(info);
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setTitle("Ověření proběhlo úspěšně!");
                                builder.setDescription("Váš účet byl ověřen. Nyní můžete psát do hry z discordu");
                                builder.setColor(new Color(104, 64, 236));
                                manager.sendPrimaryEmbedMessage(info.getDiscordUser(), builder.build());
                                player.sendMessage(prefix.getPrefix() + "Váš účet byl ověřen");
                            }
                        } else {
                            player.sendMessage(prefix.getPrefix() + "Na použití tohoto příkazu nemáte dostatečná oprávnění!");
                            return false;
                        }
                    }else if(args[0].equalsIgnoreCase("remove")) {
                        String user = args[1];
                        if(player.hasPermission("dd.remove")) {
                            removeUser(user, player);
                        } else {
                            player.sendMessage(prefix.getPrefix() + "Na použití tohoto příkazu nemáte dostatečná oprávnění!");
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void removeUser(String username, Player sender) {
        try {
            if(!plugin.getDb().getCon().isValid(0)) {
                plugin.getDb().openConnection();
            }
            try {
                UUID uuid = Bukkit.getPlayer(username).getUniqueId();
                PreparedStatement stm = plugin.getDb().getCon().prepareStatement("DELETE FROM discord_auth WHERE userUUID = ?");
                stm.setString(1, uuid.toString());
                stm.execute();
                sender.sendMessage(prefix.getPrefix() + "Hráči " + username + " bylo sebráno oprávnění!");
            } catch (NullPointerException e) {
                sender.sendMessage(prefix.getPrefix() + "Hráč " + username + " nebyl nalezen!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        if(sender.hasPermission("dd.remove")) {
            if(args.length == 1) {
                if("remove".contains(args[0])) {
                    arguments.add("remove");
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("remove")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(onlinePlayer.getName().contains(args[1])) {
                            arguments.add(onlinePlayer.getName());
                        }
                    }
                }
            }
        }
        return arguments;
    }
}
