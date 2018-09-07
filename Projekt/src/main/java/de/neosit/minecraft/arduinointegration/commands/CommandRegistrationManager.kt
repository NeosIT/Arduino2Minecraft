package de.neosit.minecraft.arduinointegration.commands

import de.neosit.minecraft.arduinointegration.Main
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import org.bukkit.command.PluginCommand

import java.util.Arrays

object CommandRegistrationManager {

    private val commands = listOf("/arduino help", "/arduino send", "/arduino reload")

    fun registerCommands(main: Main) {
        registerArduinoCommands(main)
    }

    private fun registerArduinoCommands(main: Main) {
        main.getCommand("arduino").let {
            it.description = "Show a brief description of Arduino2Minecraft"
            it.usage = """
                Available commands are: ${ChatColor.BLUE}
                ${commands.joinToString("\n${ChatColor.BLUE}")}
            """.trimIndent()
            it.executor = ArduinoCommand(main)
        }
    }
}
