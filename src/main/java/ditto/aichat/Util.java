package ditto.aichat;

import org.bukkit.ChatColor;

public class Util {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
