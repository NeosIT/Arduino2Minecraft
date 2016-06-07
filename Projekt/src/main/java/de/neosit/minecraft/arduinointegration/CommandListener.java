package de.neosit.minecraft.arduinointegration;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import com.google.common.base.Objects;

public class CommandListener implements CommandExecutor{
	public static final String COMMAND = "sendToArduino";
	
	// Serielle Schnittstelle für den Arduino
	private SerialInterface serialInterface;
	
	public CommandListener(SerialInterface serialInterface) {
		this.serialInterface = serialInterface;
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (COMMAND.equalsIgnoreCase(arg2) == false) {
			return false;
		}
		
		String message = StringUtils.join(arg3, " ");
		serialInterface.sendData(message);
		return true;
	}

}
