package de.neosit.minecraft.arduinointegration;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class CommandListener implements CommandExecutor {
    public static final String ARDUINO = "arduino";

    // Serielle Schnittstelle für den Arduino
    private Main main;
    private Logger log;

    private CommandListener(Main main, Logger log) {
        this.main = main;
        this.log = log;
    }

    public static void installCommands(Main main, Logger log) {
        CommandListener commandListener = new CommandListener(main, log);

        main.getCommand(ARDUINO).setExecutor(commandListener);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "send":
                String message = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
                main.getSerial().sendData(message);
                return true;
            case "reload":
                if (args.length == 2 && args[1].equals("config")) {
                    main.reloadConfig();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
}
