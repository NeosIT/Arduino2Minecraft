package de.neosit.minecraft.arduinointegration

object PotiUtil {

    fun checkForPotiInput(input: String): Boolean {
        return input.startsWith("poti:")
    }

    fun createPotiCommand(input: String, config: Configuration): String? {
        if (checkForPotiInput(input)) {
            val value = input.split(':')[1]
            var command = config.getCommandForArduinoValue("poti")
            command = command.replace("?", value)
            return command
        }
        return null
    }
}
