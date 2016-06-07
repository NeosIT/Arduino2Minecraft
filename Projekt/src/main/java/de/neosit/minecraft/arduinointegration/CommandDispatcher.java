package de.neosit.minecraft.arduinointegration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Diese Klasse behandelt die Verarbeitung von Befehlen, die in Minecraft ausgeführt werden.
 *
 */
public class CommandDispatcher {
	private Logger log;
	
	//Prefix für die Werte in der Konfigurationsdatei (config.yml)
	public static final String PREFIX = "arduino_";
	
	//Warteschlange für die zu bearbeitenden Befehle
	private Queue<String> queue = new LinkedList<String>();
	
	private Configuration config;

	//Laden der Konfiguration
	public CommandDispatcher(Configuration config, Logger log) {
		this.config = config;
		this.log = log;
	}

	public void addArduinoValue(String arduinoValue) {
		try {
			String command = "";
			
			if(PotiUtil.checkForPotiInput(arduinoValue)){
				command = PotiUtil.createPotiCommand(arduinoValue, config);
			} else {
				command = config.getCommandForArduinoValue(arduinoValue);

			}
			
			log.info(arduinoValue + " --> " + command);
			
			if (null == command) {
				command = arduinoValue;
				log.info("Verwende den Wert vom Arduino " + arduinoValue + " als Befehl.");
			}
			
			addCommand(command);
		} catch (Exception e) {
			log.warning(e.getMessage());
		}

	}
	
	/**
	 * Fügt ein Befehl der Warteschlange hinzu.
	 * 
	 * @param command Der Befehl, der hinzugefügt werden soll.
	 */
	public void addCommand(String command) {
		queue.add(command);
	}

	/**
	 * Diese Methode sorgt dafür, dass immer wieder überprüft wird ob Befehle in der 
	 * Warteschlange vorhanden sind. 
	 * Sollten Befehle vorhanden sein, werden diese an den Minecraft Server gesendet.
	 * 
	 * @param plugin
	 */
	public void register(JavaPlugin plugin) {
		BukkitScheduler scheduler = Bukkit.getScheduler();
		
		//Periodische Überprüfung der Warteschlange
		Runnable run = new Runnable() {

			public void run() {
				if (queue.isEmpty()) {
					return;
				}
				
				//Schreibe den Befehl in die command Variable
				//und lösche den Befehl aus der Warteschlange
				String command = queue.remove();
				
				// Verhindere einen Fehler durch einen Null-Wert
				if (null == command) {
					return;
				}
				
				//Sende den Befehl an den Minecraft Server
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		};
		
		//Hier wird eine, sich imme wiederholende, Aufgabe definiert.
		//run steht für die Aufgabe, die wiederholt werden soll.
		//config.getPeriod ist der Wert aus unserer Konfiguration und gibt an,
		//in welchem Abstand, die Aufgabe wiederholt werden soll.
		scheduler.scheduleSyncRepeatingTask(plugin, run, 0, config.getPeriod());
	}
}
