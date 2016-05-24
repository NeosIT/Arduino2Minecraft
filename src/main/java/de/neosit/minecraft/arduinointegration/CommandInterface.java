package de.neosit.minecraft.arduinointegration;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CommandInterface {
	public static final String PREFIX = "arduino_";
	
	private Queue<String> queue = new LinkedList<String>();
	private Configuration config;
	
	public CommandInterface(Configuration config) {
		this.config = config;
	}

	public void addArduinoValue(String arduinoValue) {
		String command = config.getCommandForArduinoValue(arduinoValue);
		addCommand(command);
	}
	
	public void addCommand(String command) {
		queue.add(command);
	}
	
	public void register(JavaPlugin plugin) {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		Runnable run = new Runnable() {
			
			public void run() {
				if (queue.isEmpty()) {
					return;
				}
				
				String command = queue.remove();
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		};
		
		scheduler.scheduleSyncRepeatingTask(plugin, run, 0, config.getPeriod());
	}
}
