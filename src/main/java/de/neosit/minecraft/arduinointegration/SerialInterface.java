package de.neosit.minecraft.arduinointegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Logger;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * Diese Klasse empfängt und sendet Befehle an den Arduino.
 *
 */
public class SerialInterface {
	private Logger log;
	private CommandDispatcher command;
	
	public SerialInterface(CommandDispatcher command, Logger log) {
		this.command = command;
		this.log = log;
	}

	public void findPort() {
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			System.out.println(portEnum.nextElement());
		}
	}

	public SerialPort serialPort = null;
	public BufferedReader input2 = null;
	
	private static final int TIME_OUT = 1000; // Port open timeout
	
	public void connectToArduino() throws TooManyListenersException, PortInUseException {
		String appName = null;

		String PORT_NAMES[] = { "/dev/tty.usbmodem", // Mac OS X
        "/dev/usbdev", // Linux
       "/dev/tty", // Linux
       "/dev/serial", // Linux
       "COM3", // Windows
		};

		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		// Enumerate system ports and try connecting to Arduino over each
		log.info("Versuche Verbindung zum Arduino herzustellen...");
		while (portId == null && portEnum.hasMoreElements()) {
			// Iterate through your host computer's serial port IDs
			//
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			log.info("Versuche device " + currPortId.getName());
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName) || currPortId.getName().startsWith(portName)) {

					// Try to connect to the Arduino on this port
					//
					// Open serial port
					serialPort = (SerialPort) currPortId.open(appName, TIME_OUT);
					portId = currPortId;
					log.info("Verbindung zum Arduino hergestellt auf Port " + currPortId.getName());
					break;
				}
			}
		}

		if (portId == null || serialPort == null) {
			log.warning("Verbindung zum Arduino fehlgeschlagen!");
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
					log.warning(e.toString());
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
			log.info("Sende folgende Daten an den Arduino " + s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warning(e.getMessage());
		}
	}
	
	public void closeConnection() {
		if (null == serialPort) {
			return;
		}
		
		serialPort.close();
	}
}
