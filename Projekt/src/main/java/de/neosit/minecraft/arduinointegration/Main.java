package de.neosit.minecraft.arduinointegration;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
/**
 * Hauptklasse zur Initialisierung des Plugin und dem Beenden des Plugin.
 *
 */
public class Main extends JavaPlugin {
	private Logger log = getLogger();

	private SerialInterface serial;
	
	@Override
	public void onEnable() {
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM1");

		//Konfiguration des Plugin laden
		Configuration config = new Configuration(getConfig());
		config.setDefaults();
		saveConfig();
		
		CommandDispatcher command = new CommandDispatcher(config, getLogger());
		command.register(this);
		
		serial = new SerialInterface(command, getLogger(), config.getPorts());
		
		try {
			serial.connectToArduino();
		} catch (Exception e) {
			log.warning(e.getClass().getName() + ": " + e.getMessage());
		}
		
		PluginManager pluginManager = getServer().getPluginManager();
		
		PlayerHealthListener playerHealthListener = new PlayerHealthListener(serial, getLogger());
		pluginManager.registerEvents(playerHealthListener, this);
		
		getCommand(CommandListener.COMMAND).setExecutor(new CommandListener(serial));
		
		log.info("Plugin aktiviert!");	
	}

	@Override
	public void onDisable() {
		serial.closeConnection();
		log.info("Plugin deaktiviert!");
	}
}
