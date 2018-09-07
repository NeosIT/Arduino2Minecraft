package de.neosit.minecraft.arduinointegration

import de.neosit.minecraft.arduinointegration.commands.CommandRegistrationManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Hauptklasse zur Initialisierung des Plugin und dem Beenden des Plugin.
 */
class Main : JavaPlugin() {
    private val log = logger

    lateinit var serial: SerialInterface
        private set
    lateinit var configuration: Configuration
        private set

    override fun onEnable() {
        //Konfiguration des Plugin laden
        configuration = Configuration(config)
        configuration.setDefaults()
        saveConfig()

        val command = CommandDispatcher(configuration, logger)
        command.register(this)

        serial = SerialInterface(command, logger, configuration.ports)

        try {
            serial.connectToArduino()
        } catch (e: Exception) {
            log.warning(e.javaClass.name + ": " + e.message)
        }

        val pluginManager = server.pluginManager

        val playerHealthListener = PlayerHealthListener(serial, logger)
        pluginManager.registerEvents(playerHealthListener, this)

        CommandRegistrationManager.registerCommands(this)

        log.info("Plugin aktiviert!")
    }

    override fun onDisable() {
        serial.closeConnection()
        log.info("Plugin deaktiviert!")
    }
}
