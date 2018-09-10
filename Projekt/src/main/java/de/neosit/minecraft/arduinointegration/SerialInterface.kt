package de.neosit.minecraft.arduinointegration

import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import gnu.io.SerialPortEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Logger

/**
 * Diese Klasse empfängt und sendet Befehle an den Arduino.
 */
class SerialInterface(private val command: CommandDispatcher, private val log: Logger, private val ports: Array<String>) {

    var serialPort: SerialPort? = null

    @Throws(TooManyListenersException::class)
    fun connectToArduino() {
        var portId: CommPortIdentifier? = null
        val portIdentifiers = CommPortIdentifier.getPortIdentifiers()
        val availablePortNames = ArrayList<String>()

        while (portIdentifiers.hasMoreElements()) {
            val portIdentifier = portIdentifiers.nextElement() as CommPortIdentifier

            if (ports.contains(portIdentifier.name)) {
                log.info("Port available and in config: " + portIdentifier.name)
                availablePortNames.add(portIdentifier.name)
            } else {
                log.info("Port available but not in config: " + portIdentifier.name)
            }
        }

        // Enumerate system ports and try connecting to Arduino over each
        log.info("Versuche Verbindung zum Arduino herzustellen...")
        for (available in availablePortNames) {
            log.info("Versuche device $available")
            try {
                val currPortId = CommPortIdentifier.getPortIdentifier(available)

                // Open serial port
                serialPort = currPortId.open(this.javaClass.name, TIME_OUT) as SerialPort
                portId = currPortId
                log.info("Verbindung zum Arduino hergestellt auf Port $available")
                break
            } catch (e: Exception) {
                log.warning("Could not connect to device $available")
                log.warning(e.javaClass.name + " :" + e.message)
            }

        }

        if (portId == null || serialPort == null) {
            log.warning("Verbindung zum Arduino fehlgeschlagen!")
            return
        }

        serialPort?.addEventListener { oEvent ->
            try {
                when (oEvent.eventType) {
                    SerialPortEvent.DATA_AVAILABLE -> {
                        val input2 = BufferedReader(InputStreamReader(serialPort?.inputStream))
                        val inputLine = input2.readLine()
                        command.addArduinoValue(inputLine)
                    }
                }
            } catch (e: Exception) {
                log.warning(e.javaClass.name + ": " + e.message)
            }
        }
        serialPort?.notifyOnDataAvailable(true)
    }

    fun sendData(s: String) {
        if(serialPort == null) {
            log.warning("SerialPort is currently null.")
            return
        }
        val str: String = s + "\n"
        try {
            val output = serialPort?.outputStream
            output?.write(str.toByteArray())
            log.info("Sende folgende Daten an den Arduino $s")
        } catch (e: IOException) {
            log.warning(e.javaClass.name + ": " + e.message)
        }
    }

    fun closeConnection() {
        if (serialPort != null) {
            serialPort?.removeEventListener()
            serialPort?.close()
        }
    }
}
private const val TIME_OUT = 1000
