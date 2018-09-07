package de.neosit.minecraft.arduinointegration.commands

import de.neosit.minecraft.arduinointegration.Main
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ArduinoCommand(private val main: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when {
            args.size == 1 && helpCmds.contains(args[0]) -> {
                sender.sendMessage(command.usage)
                return true
            }
            args.isEmpty() -> {
                sender.sendMessage("""
                    Unknown command "${args.joinToString(" ")}"
                    Type ${ChatColor.BLUE}/arduino --help${ChatColor.RESET} for all available commands.
                """.trimIndent())
                return true
            }
            args[0] == "send" && args.size == 1 -> {
                sender.sendMessage(main.getCommand("arduino send").description)
                return true
            }
            args[0] == "send" -> {
                val message = args.slice(1 until args.count()).joinToString(" ")
                main.serial.sendData(message)
                return true
            }
            args[0] == "reload" && args.size == 1 -> {
                sender.sendMessage(main.getCommand("arduino reload").description)
                return true
            }
            args[0] == "reload" && args.size == 2 && args[1] == "config" -> {
                main.reloadConfig()
                main.configuration.reload(main.config)
                return true
            }
            else -> {
                return false
            }
        }
    }
}

val helpCmds = listOf("-h", "--help", "?")
