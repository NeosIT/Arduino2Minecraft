package de.neosit.minecraft.arduinointegration;

import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Konfigurationsklasse.
 * Stellt Zugriff auf die Konfigurationsdatei sicher.
 *
 */
public class Configuration {
	public static final String PORT = "port";
	public static final String TIMEOUT = "timeout";
	public static final String DATA_RATE = "data_rate";
	public static final String PERIOD = "period";
	public static final String ARDUINO_PREFIX = "arduino_";
	
	private FileConfiguration config;

	public Configuration(FileConfiguration config) {
		this.config = config;
	}
	
	public void setDefaults() {
		config.addDefault(PORT, Arrays.asList("COM3"));
		config.addDefault(TIMEOUT, 1000);
		config.addDefault(DATA_RATE, 9600);
		config.addDefault(PERIOD, 4);
		config.options().copyDefaults(true);
	}
	
	public String getPort() {
		return (String) config.get(PORT);
	}
	
	public int getTimeout() {
		return (int) config.getInt(TIMEOUT);
	}
	
	public int getDataRate() {
		return (int) config.getInt(DATA_RATE);
	}
	
	/**
	 * Gibt den Befehl zurück, der zu einem bestimmten 
	 * Element in der Konfigurationsdatei hinterlegt ist.
	 * 
	 * @param arduinoValue Ist der Wert, der direkt vom Arduino gesendet wird.
	 * @return Gibt den Befehl zurück, der in der Konfiguration hinterlegt ist.
	 */
	public String getCommandForArduinoValue(String arduinoValue) {
		String key = ARDUINO_PREFIX + arduinoValue;
		return (String) config.getString(key);
	}
	
	public int getPeriod() {
		return (int) config.getInt(PERIOD);
	}
}
