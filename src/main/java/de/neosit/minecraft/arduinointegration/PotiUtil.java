package de.neosit.minecraft.arduinointegration;

public final class PotiUtil {
	
	public static Boolean checkForPotiInput(String input) {
		return input.startsWith("poti:");
	}
	
	public static String createPotiCommand(String input, Configuration config) {
		if (checkForPotiInput(input)) {
			String value = input.substring(input.lastIndexOf(":") + 1);
			String command = config.getCommandForArduinoValue("poti");
			command = command.replace("?", value);
			return command;
		}
		return null;
	}
}
