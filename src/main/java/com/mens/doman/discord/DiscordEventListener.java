package com.mens.doman.discord;

import com.mens.doman.DomanMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;


public class DiscordEventListener extends ListenerAdapter {
    private final DomanMain plugin;

    public DiscordEventListener(DomanMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        DiscordManager.getEmotes().add(new MyEmote(":arrow:", event.getGuild().getEmoteById(plugin.getConfig().getLong("Discord.Emotes.Arrow"))));
        DiscordManager manager = new DiscordManager(plugin);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("DSB se připojil!");
        builder.setColor(new Color(7, 180, 38));
        builder.setTimestamp(ZonedDateTime.now());
        manager.sendEmbedMessage(manager.getChannelByName(plugin.getConfig().getString("Discord.Rooms.MC chat")), builder.build());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!event.getAuthor().isBot()) {
            if(event.getMessage().getChannel().getName().equals(plugin.getConfig().getString("Discord.Rooms.MC chat"))) {
                String uuid = event.getAuthor().getId();
                DiscordAuth auth = new DiscordAuth(plugin);
                if(!auth.isAuthorized(uuid)) {
                    DiscordManager manager = new DiscordManager(plugin);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Váš účet není ověřen!");
                    builder.setDescription("Abyste mohli psát z Discordu do hry, je nejprve nutné Váš účet ověřit. Postupujte podle následujících instrukcí.");
                    builder.setColor(new Color(104, 64, 236));
                    builder.addField("Instrukce:", "Z vašeho herního účtu ve hře odešlete následující příkaz:", true);
                    builder.addField("Příkaz o odeslání (1/2):", "/dd auth uid:" + uuid, true);
                    manager.sendPrimaryEmbedMessage(event.getAuthor(), builder.build());
                } else {
                    if(!event.getMessage().getContentRaw().startsWith("/")) {
                        UUID playerUUID = UUID.fromString(auth.getUserUUID(event.getAuthor()));
                        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
                        Server server = Bukkit.getServer();
                        String group = Objects.requireNonNull(plugin.getPerms().getData().getString("users." + playerUUID + ".group")).replace("[", "").replace("]", "");
                        String prefix = Objects.requireNonNull(plugin.getPerms().getData().getString("groups." + group + ".options.prefix")).replace("&", "§");
                        String dcPrefix = plugin.getConfig().getString("Discord.Game prefix");
                        String op = player.isOp() ? "§4" : "";
                        server.broadcastMessage(dcPrefix + prefix + op + player.getName() + "§r: " + event.getMessage().getContentRaw());
                    }
                }
            }
        }
    }
}
