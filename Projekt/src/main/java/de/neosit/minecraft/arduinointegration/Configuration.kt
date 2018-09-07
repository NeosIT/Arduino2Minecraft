package de.neosit.minecraft.arduinointegration

import java.util.Arrays

import org.bukkit.configuration.file.FileConfiguration

/**
 * Konfigurationsklasse.
 * Stellt Zugriff auf die Konfigurationsdatei sicher.
 *
 */
class Configuration(private var config: FileConfiguration) {

    val ports: Array<String>
        get() = config.getStringList(PORT).toTypedArray()

    val timeout: Int
        get() = config.getInt(TIMEOUT)

    val dataRate: Int
        get() = config.getInt(DATA_RATE)

    val period: Int
        get() = config.getInt(PERIOD)

    fun setDefaults() {
        config.addDefault(PORT, Arrays.asList("/dev/tty.usbmodem", "/dev/usbdev", "/dev/tty", "/dev/serial", "COM3"))
        config.addDefault(TIMEOUT, 1000)
        config.addDefault(DATA_RATE, 9600)
        config.addDefault(PERIOD, 4)
        config.options().copyDefaults(true)
    }

    fun reload(config: FileConfiguration) {
        this.config = config
    }

    /**
     * Gibt den Befehl zurück, der zu einem bestimmten
     * Element in der Konfigurationsdatei hinterlegt ist.
     *
     * @param arduinoValue Ist der Wert, der direkt vom Arduino gesendet wird.
     * @return Gibt den Befehl zurück, der in der Konfiguration hinterlegt ist.
     */
    fun getCommandForArduinoValue(arduinoValue: String): String {
        val key = ARDUINO_PREFIX + arduinoValue
        return config.getString(key) as String
    }
}

const val PORT = "port"
const val TIMEOUT = "timeout"
const val DATA_RATE = "data_rate"
const val PERIOD = "period"
const val ARDUINO_PREFIX = "arduino_"
