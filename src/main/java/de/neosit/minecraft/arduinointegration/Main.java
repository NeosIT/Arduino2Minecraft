package de.neosit.minecraft.arduinointegration;

import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import gnu.io.PortInUseException;

public class Main extends JavaPlugin {
	private Logger log;

	private SerialInterface serial;
	
	@Override
	public void onEnable() {
		log = getLogger();
		
		Configuration config = new Configuration(getConfig());
		config.setDefaults();
		saveConfig();
		
		CommandInterface command = new CommandInterface(config);
		command.register(this);
		
		serial = new SerialInterface(command);
		
		
		try {
			serial.a();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PluginManager pluginManager = getServer().getPluginManager();
		
		PlayerHealthListener playerHealthListener = new PlayerHealthListener(serial, config);
		playerHealthListener.registerSchedule(this);
		pluginManager.registerEvents(playerHealthListener, this);
		
		log.log(Level.INFO, "is enabled!");	
	}

	@Override
	public void onDisable() {
		serial.closeConnection();
		log.log(Level.INFO, "is disabled!");
	}
}
