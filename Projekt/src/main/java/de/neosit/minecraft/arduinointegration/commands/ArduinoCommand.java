package de.neosit.minecraft.arduinointegration.commands;

import de.neosit.minecraft.arduinointegration.Main;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ArduinoCommand implements CommandExecutor {
    private Main main;

    public ArduinoCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> helpCmds = Arrays.asList("-h", "--help", "?");
        if (args.length == 1 && helpCmds.contains(args[0])) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        if (args.length > 0) {
            switch (args[0]) {
                case "send":
                    if (args.length == 1) {
                        sender.sendMessage("Use " + ChatColor.BLUE + "/arduino send [ARGS]" + ChatColor.RESET + " to send a message to the arduino.");
                        return true;
                    }
                    String message = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
                    main.getSerial().sendData(message);
                    return true;
                case "reload":
                    if (args.length == 1) {
                        sender.sendMessage(
                                "Use " + ChatColor.BLUE + "/arduino reload [CONFIG]" + ChatColor.RESET + " to reload a specific configuration file of this plugin.\n" +
                                        "Available configration files are:\n" +
                                        ChatColor.BLUE + "config"
                        );
                        return true;
                    }
                    if (args.length == 2 && args[1].equals("config")) {
                        main.reloadConfig();
                        main.getConfiguration().reload(main.getConfig());
                        return true;
                    }
            }
        }

        // all other cases
        sender.sendMessage(
                "Unknown command \"" + StringUtils.join(args, " ") + "\". " +
                        "Type " + ChatColor.BLUE + "/arduino --help" + ChatColor.RESET + " for all available commands.");
        return true;
    }
}
