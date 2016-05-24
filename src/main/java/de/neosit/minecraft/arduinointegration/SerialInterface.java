package de.neosit.minecraft.arduinointegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.bukkit.Bukkit;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialInterface {

	private CommandInterface command;
	
	public SerialInterface(CommandInterface command) {
		this.command = command;
	}

	public void findPort() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			System.out.println(portEnum.nextElement());
		}
	}

	public SerialPort serialPort = null;
	public BufferedReader input2 = null;
	
	private static final int TIME_OUT = 1000; // Port open timeout
	private static final int DATA_RATE = 9600; // Arduino serial port
	
	public void a() throws TooManyListenersException, PortInUseException {
		String appName = null;
		BufferedReader input;
		OutputStream output;

		String PORT_NAMES[] = { "/dev/tty.usbmodem", // Mac OS X
        "/dev/usbdev", // Linux
       "/dev/tty", // Linux
       "/dev/serial", // Linux
       "COM3", // Windows
		};

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// Enumerate system ports and try connecting to Arduino over each
		//
		System.out.println("Trying:");
		while (portId == null && portEnum.hasMoreElements()) {
			// Iterate through your host computer's serial port IDs
			//
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println("   port" + currPortId.getName());
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName) || currPortId.getName().startsWith(portName)) {

					// Try to connect to the Arduino on this port
					//
					// Open serial port
					serialPort = (SerialPort) currPortId.open(appName, TIME_OUT);
					portId = currPortId;
					System.out.println("Connected on port" + currPortId.getName());
					break;
				}
			}
		}

		if (portId == null || serialPort == null) {
			System.out.println("Oops... Could not connect to Arduino");
		}

		serialPort.addEventListener(new SerialPortEventListener() {

			public void serialEvent(SerialPortEvent oEvent) {
				try {
					switch (oEvent.getEventType()) {
					case SerialPortEvent.DATA_AVAILABLE:
						if (null == input2) {
							input2 = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
						}
						String inputLine = input2.readLine();
						command.addArduinoValue(inputLine);
						break;

					default:
						break;
					}
				} catch (Exception e) {
					System.err.println(e.toString());
				}

			}
		});
		serialPort.notifyOnDataAvailable(true);
	}
	
	public void sendData(String s) {
		try {
			s = s + "\n";
			OutputStream output = serialPort.getOutputStream();
			output.write(s.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		if (null == serialPort) {
			return;
		}
		
		serialPort.close();
	}
}
