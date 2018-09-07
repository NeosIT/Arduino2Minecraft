package de.neosit.minecraft.arduinointegration

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.logging.Logger

/**
 * Diese Klasse behandelt die Verarbeitung von Befehlen, die in Minecraft ausgeführt werden.
 */
class CommandDispatcher(private val config: Configuration, private val log: Logger) {

    //Warteschlange für die zu bearbeitenden Befehle
    private val queue = LinkedList<String>()

    fun addArduinoValue(arduinoValue: String) {
        try {
            var command: String?

            if (PotiUtil.checkForPotiInput(arduinoValue)) {
                command = PotiUtil.createPotiCommand(arduinoValue, config)
            } else {
                command = config.getCommandForArduinoValue(arduinoValue)
            }

            log.info("$arduinoValue --> $command")

            if (null == command) {
                command = arduinoValue
                log.info("Verwende den Wert vom Arduino $arduinoValue als Befehl.")
            }

            addCommand(command)
        } catch (e: Exception) {
            log.warning("Command could not be executed: $arduinoValue")
        }
    }

    /**
     * Fügt ein Befehl der Warteschlange hinzu.
     *
     * @param command Der Befehl, der hinzugefügt werden soll.
     */
    private fun addCommand(command: String) {
        queue.add(command)
    }

    /**
     * Diese Methode sorgt dafür, dass immer wieder überprüft wird ob Befehle in der
     * Warteschlange vorhanden sind.
     * Sollten Befehle vorhanden sein, werden diese an den Minecraft Server gesendet.
     *
     * @param plugin
     */
    fun register(plugin: JavaPlugin) {
        val scheduler = Bukkit.getScheduler()

        //Periodische Überprüfung der Warteschlange
        val run = Runnable {
            if (queue.isEmpty()) {
                return@Runnable
            }

            //Schreibe den Befehl in die command Variable
            //und lösche den Befehl aus der Warteschlange
            val command = queue.remove() ?: return@Runnable

            // Verhindere einen Fehler durch einen Null-Wert

            //Sende den Befehl an den Minecraft Server
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)
        }

        //Hier wird eine, sich imme wiederholende, Aufgabe definiert.
        //run steht für die Aufgabe, die wiederholt werden soll.
        //config.getPeriod ist der Wert aus unserer Konfiguration und gibt an,
        //in welchem Abstand, die Aufgabe wiederholt werden soll.
        scheduler.scheduleSyncRepeatingTask(plugin, run, 0, config.period.toLong())
    }
}
