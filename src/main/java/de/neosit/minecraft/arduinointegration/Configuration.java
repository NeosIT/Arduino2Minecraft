package de.neosit.minecraft.arduinointegration;

import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
	public static final String PORT = "port";
	public static final String TIMEOUT = "timeout";
	public static final String DATA_RATE = "data_rate";
	public static final String PERIOD = "period";
	public static final String HEALTHBAR_PERIOD = "healthbar_period";
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
		config.addDefault(HEALTHBAR_PERIOD, 10);
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
	
	public String getCommandForArduinoValue(String arduinoValue) {
		String key = ARDUINO_PREFIX + arduinoValue;
		return (String) config.getString(key);
	}
	
	public int getPeriod() {
		return (int) config.getInt(PERIOD);
	}
	
	public int getHealthbarPeriod() {
		return (int) config.getInt(HEALTHBAR_PERIOD);
	}
}
