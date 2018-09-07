package de.neosit.minecraft.arduinointegration.commands;

import de.neosit.minecraft.arduinointegration.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;

public final class CommandRegistrationManager {

    private static List<String> commands = Arrays.asList("/arduino help", "/arduino send", "/arduino reload");

    public static void registerCommands(Main main) {
        registerArduinoCommands(main);
    }

    private static void registerArduinoCommands(Main main) {
        PluginCommand cmd = main.getCommand("arduino");
        cmd.setDescription("Show a brief description of Arduino2Minecraft");
        cmd.setUsage("Available commands are:" + "\n" + ChatColor.BLUE + StringUtils.join(commands, "\n" + ChatColor.BLUE));
        cmd.setExecutor(new ArduinoCommand(main));
    }
}
