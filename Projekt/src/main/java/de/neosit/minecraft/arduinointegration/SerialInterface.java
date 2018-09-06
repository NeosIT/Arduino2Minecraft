package de.neosit.minecraft.arduinointegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Logger;

import gnu.io.*;

/**
 * Diese Klasse empfängt und sendet Befehle an den Arduino.
 */
public class SerialInterface {
    private Logger log;
    private CommandDispatcher command;
    private List<String> ports;

    public SerialInterface(CommandDispatcher command, Logger log, List<String> ports) {
        this.command = command;
        this.log = log;
        this.ports = ports;
    }

    public SerialPort serialPort = null;
    public BufferedReader input2 = null;

    private static final int TIME_OUT = 1000;

    public void connectToArduino() throws TooManyListenersException {
        CommPortIdentifier portId = null;
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        List<String> availablePortNames = new ArrayList<String>();

        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier portIdentifier = (CommPortIdentifier) portIdentifiers.nextElement();
            log.info("Port available: " + portIdentifier.getName());

            if (ports.contains(portIdentifier.getName())) {
                log.info("Port available and in config: " + portIdentifier.getName());
                availablePortNames.add(portIdentifier.getName());
            }
        }

        // Enumerate system ports and try connecting to Arduino over each
        log.info("Versuche Verbindung zum Arduino herzustellen...");
        for (String available : availablePortNames) {
            log.info("Versuche device " + available);
            try {
                CommPortIdentifier currPortId = CommPortIdentifier.getPortIdentifier(available);

                // Open serial port
                serialPort = (SerialPort) currPortId.open(this.getClass().getName(), TIME_OUT);
                portId = currPortId;
                log.info("Verbindung zum Arduino hergestellt auf Port " + available);
                break;
            } catch (Exception e) {
                log.warning("Could not connect to device " + available);
                log.warning(e.getClass().getName() + " :" + e.getMessage());
            }
        }

        if (portId == null || serialPort == null) {
            log.warning("Verbindung zum Arduino fehlgeschlagen!");
            return;
        }

        serialPort.addEventListener(new SerialPortEventListener() {
            public synchronized void serialEvent(SerialPortEvent oEvent) {
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
                    log.warning(e.getClass().getName() + ": " + e.toString());
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
            log.warning(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
}
