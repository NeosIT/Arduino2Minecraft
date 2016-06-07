package de.neosit.minecraft.arduinointegration;

import java.util.TooManyListenersException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import gnu.io.PortInUseException;
/**
 * Hauptklasse zur Initialisierung des Plugin und dem Beenden des Plugin.
 *
 */
public class Main extends JavaPlugin {
	private Logger log = getLogger();

	private SerialInterface serial;
	
	@Override
	public void onEnable() {
		
		//Konfiguration des Plugin laden
		Configuration config = new Configuration(getConfig());
		config.setDefaults();
		saveConfig();
		
		
		CommandDispatcher command = new CommandDispatcher(config, getLogger());
		command.register(this);
		
		serial = new SerialInterface(command, getLogger());
		
		try {
			serial.connectToArduino();
		} catch (TooManyListenersException e) {
			log.warning(e.getMessage());
		} catch (PortInUseException e) {
			log.warning(e.getMessage());
		}
		
		PluginManager pluginManager = getServer().getPluginManager();
		
		PlayerHealthListener playerHealthListener = new PlayerHealthListener(serial, getLogger());
		pluginManager.registerEvents(playerHealthListener, this);
		
		log.info("Plugin aktiviert!");	
	}

	@Override
	public void onDisable() {
		serial.closeConnection();
		log.info("Plugin deaktiviert!");
	}
}
