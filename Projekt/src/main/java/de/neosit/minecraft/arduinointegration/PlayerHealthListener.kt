package de.neosit.minecraft.arduinointegration

import java.util.logging.Logger

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

@Suppress("unused")
/**
 * Diese Klasse beschäftigt sich mit der Ausgabe der Lebensbalken an den
 * Arduino.
 *
 */
class PlayerHealthListener(private val serialInterface: SerialInterface, private val log: Logger) : Listener {
    private var activePlayer: Player? = null

    /**
     * Diese Methode reagiert auf Änderungen des Lebens eines Spielers.
     * Sie wird aktiviert, sobald der Spieler geheilt wird.
     */
    @EventHandler
    fun onHeal(event: EntityRegainHealthEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        if (player === activePlayer) {
            displayPlayerHealth(player)
        }
    }

    /**
     * Diese Methode reagiert auf Änderungen des Lebens eines Spielers.
     * Sie wird aktiviert, sobald der Spieler schaden nimmt.
     */
    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        if (player === activePlayer) {
            displayPlayerHealth(player)
        }
    }

    /**
     * Diese Methode wird aufgerufen, sobald ein Spieler den Server betritt.
     *
     * @param event
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Liste der Spieler auf dem Server
        val players = Bukkit.getServer().onlinePlayers
        // Wenn der erste/einzige Spieler sich verbindet
        if (players.size == 1 || activePlayer == null) {
            // Ersten Spieler aus der Liste aller Spieler auswählen
            activePlayer = event.player
            log.info("Arduino reagiert nun auf Spieler ${activePlayer?.displayName}")
            activePlayer?.sendMessage("${ChatColor.AQUA}[Arduino2Minecraft]${ChatColor.WHITE} Der Arduino reagiert nun auf dich!")
            displayPlayerHealth(activePlayer)
        }
    }

    /**
     * Diese Methode wird aufgerufen, sobald ein Spieler den Server verlässt.
     *
     * @param event
     */
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (event.player === activePlayer) {
            // Liste aller Spieler auf dem Server
            val players = Bukkit.getServer().onlinePlayers
            // Wenn noch weitere Spieler online sind
            if (players.size > 1) {

                val iterator = players.iterator()
                var player: Player? = null

                // Lade den nächsten Spieler, der noch online ist
                while (player == null) {
                    val p = iterator.next()
                    if (p !== activePlayer) {
                        player = p
                        p.sendMessage("${ChatColor.AQUA}[Arduino2Minecraft]${ChatColor.WHITE} Der Arduino reagiert nun auf dich!")
                        log.info("Arduino reagiert nun auf Spieler " + p.displayName)
                    }
                }

                activePlayer = player
                displayPlayerHealth(player)
            } else {
                activePlayer = null
                displayPlayerHealth(null)
            }
        }
    }

    /**
     * Diese Methode wird aufgerufen, sobald der Spieler gestorben ist. Dann wird das Leben auf dem ArduinoBoard auf 0 gesetzt.
     *
     * @param event
     */
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if (player === activePlayer) {
            displayPlayerHealth(player)
        }
    }

    /**
     * Diese Methode wird aufgerufen, sobald der Spieler respawned ist. Dann wird das Leben auf dem ArduinoBoard aktualisiert.
     *
     * @param event
     */
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        if (player === activePlayer) {
            player.health = player.healthScale
            displayPlayerHealth(player)
        }
    }

    /**
     * Diese Methode sendet das Signal mit dem Leben des Spielers an den
     * Arduino. Die 10 Minecraft Herzen werden auf 5 LEDs aufgeteilt, sodass
     * eine LED gleich 2 Leben sind
     *
     * @param player
     * Der Spieler, dessen Leben angezeigt werden soll.
     */
    private fun displayPlayerHealth(player: Player?) {
        // Wenn kein Spieler vorhanden ist, sende life 0 an den Arduino
        if (null == player) {
            serialInterface.sendData("life 0")
        } else {
            // Teile das Leben des Spielers, durch die max Lebensanzahl
            // Erzeugt einen Wert zwischen 1 (10 Herzen) und 0 (0 Herzen)
            val life = player.health / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).value

            // Dieser Teil überprüft, wie viele Herzen der Spieler besitzt
            // und sendet abhängig davon einen Text an den Arduino.

            val lifeValue: Int = when {
                life >= 0.8 -> 5
                life >= 0.6 -> 4
                life >= 0.4 -> 3
                life >= 0.2 -> 2
                life > 0 -> 1
                else -> 0
            }

            serialInterface.sendData("life $lifeValue")
        }
    }
}
