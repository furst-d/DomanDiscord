package com.mens.doman.discord;

import com.mens.doman.DomanMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscordManager {
    private static JDA discordBot;
    private final DomanMain plugin;
    private static final List<MyEmote> emotes = new ArrayList<>();

    public DiscordManager(DomanMain plugin) {
        this.plugin = plugin;
    }

    public static List<MyEmote> getEmotes() {
        return emotes;
    }

    public static void startBot(DomanMain plugin) {
        try {
            discordBot = JDABuilder.createDefault(plugin.getConfig().getString("Discord.Token"))
                    .addEventListeners(new DiscordEventListener(plugin))
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void stopBot(DomanMain plugin) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("DSB se odpojil!");
        builder.setColor(new Color(255, 0, 0));
        builder.setTimestamp(ZonedDateTime.now());
        discordBot.getTextChannelsByName(Objects.requireNonNull(plugin.getConfig().getString("Discord.Rooms.MC chat")), true)
                .get(0).sendMessageEmbeds( builder.build()).queue();
        discordBot.shutdown();
    }

    public void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public void sendEmbedMessage(MessageChannel channel, MessageEmbed message) {
        channel.sendMessageEmbeds(message).queue();
    }

    public void sendPrimaryEmbedMessage(User user, MessageEmbed message) {
        user.openPrivateChannel().queue((channel) ->
                channel.sendMessageEmbeds(message).queue());
    }

    public void sendChatMessage(Player player, String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String arrow = DiscordManager.getEmotes().get(0).getEmote().getAsMention();
        String displayName = replaceCodes(player.getDisplayName());
        String formattedMessage = "[" + time + "] " + arrow + "**" + displayName + "** » " + message;
        sendMessage(getChannelByName(plugin.getConfig().getString("Discord.Rooms.MC chat")), formattedMessage);
    }

    public void sendInfoMessage(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        sendMessage(getChannelByName(plugin.getConfig().getString("Discord.Rooms.MC chat")),
                "```ARM\n[" + time + "] " + replaceCodes(message) + "\n```");
    }

    public MessageChannel getChannelByName(String name) {
        return discordBot.getTextChannelsByName(name, true).get(0);
    }

    public User getUserByUUID(String uuid) {
        return discordBot.retrieveUserById(uuid).complete();
    }

    private String replaceCodes(String message) {
        String[] codes = new String[] {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§g", "§r", "§o", "§n", "§m", "§l", "§k"};
        for(String code: codes) {
            message = message.replace(code, "");
        }
        return message;
    }
}
