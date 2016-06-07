package de.neosit.minecraft.arduinointegration;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Diese Klasse beschäftigt sich mit der Ausgabe der Lebensbalken an den
 * Arduino.
 * 
 */
public class PlayerHealthListener implements Listener {
	private Logger log;

	// Serielle Schnittstelle für den Arduino
	private SerialInterface serialInterface;
	private Player activePlayer = null;

	/**
	 * Laden der seriellen Schnittstelle und der Konfiguration.
	 * 
	 * @param serialInterface
	 * @param config
	 */
	public PlayerHealthListener(SerialInterface serialInterface, Logger log) {
		this.serialInterface = serialInterface;
		this.log = log;
	}
	
	/**
	 * Diese Methode reagiert auf Änderungen des Lebens eines Spielers.
	 * Sie wird aktiviert, sobald der Spieler geheilt wird.
	 */
	@EventHandler
	public void onHeal(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (player == activePlayer) {
			displayPlayerHealth(player);
		}
	}

	/**
	 * Diese Methode reagiert auf Änderungen des Lebens eines Spielers.
	 * Sie wird aktiviert, sobald der Spieler schaden nimmt.
	 */
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (player == activePlayer) {
			displayPlayerHealth(player);
		}
	}

	/**
	 * Diese Methode wird aufgerufen, sobald ein Spieler den Server betritt.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Liste der Spieler auf dem Server
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		// Wenn der erste/einzige Spieler sich verbindet
		if (players.size() == 1 || activePlayer == null) {
			// Ersten Spieler aus der Liste aller Spieler auswählen
			activePlayer = players.iterator().next();
			log.info("Arduino reagiert nun auf Spieler " + activePlayer.getDisplayName());
			activePlayer.sendMessage(ChatColor.AQUA +"[Arduino2Minecraft]" + ChatColor.WHITE +" Der Arduino reagiert nun auf dich!");
			displayPlayerHealth(activePlayer);
		}
	}

	/**
	 * Diese Methode wird aufgerufen, sobald ein Spieler den Server verlässt.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer() == activePlayer) {
			// Liste aller Spieler auf dem Server
			Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
			// Wenn noch weitere Spieler online sind
			if (players.size() > 1) {

				Iterator<? extends Player> iterator = players.iterator();
				Player player = null;

				// Lade den nächsten Spieler, der noch online ist
				while (player == null) {
					Player p = iterator.next();
					if (p != activePlayer) {
						player = p;
						p.sendMessage(ChatColor.AQUA +"[Arduino2Minecraft]" + ChatColor.WHITE +" Der Arduino reagiert nun auf dich!");
						log.info("Arduino reagiert nun auf Spieler " + p.getDisplayName());
					}
				}

				activePlayer = player;
				displayPlayerHealth(player);
			} else {
				activePlayer = null;
				displayPlayerHealth(null);
			}
		}
	}

	/**
	 * Diese Methode wird aufgerufen, sobald der Spieler gestorben ist. Dann wird das Leben auf dem ArduinoBoard auf 0 gesetzt.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player == activePlayer) {
			displayPlayerHealth(player);
		}
	}
	
	/**
	 * Diese Methode wird aufgerufen, sobald der Spieler respawned ist. Dann wird das Leben auf dem ArduinoBoard aktualisiert.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (player == activePlayer) {
			player.setHealth(player.getHealthScale());
			displayPlayerHealth(player);
		}
	}
	
	/**
	 * Diese Methode sendet das Signal mit dem Leben des Spielers an den
	 * Arduino. Die 10 Minecraft Herzen werden auf 5 LEDs aufgeteilt, sodass
	 * eine LED gleich 2 Leben sind
	 * 
	 * @param player
	 *            Der Spieler, dessen Leben angezeigt werden soll.
	 */
	public void displayPlayerHealth(Player player) {
		// Wenn kein Spieler vorhanden ist, sende life 0 an den Arduino
		if (null == player) {
			serialInterface.sendData("life 0");
		} else {
			// Teile das Leben des Spielers, durch die max Lebensanzahl
			// Erzeugt einen Wert zwischen 1 (10 Herzen) und 0 (0 Herzen)
			double life = player.getHealth() / player.getMaxHealth();

			// Dieser Teil überprüft, wie viele Herzen der Spieler besitzt
			// und sendet abhängig davon einen Text an den Arduino.
			if (life >= 0.8) {
				serialInterface.sendData("life 5");
			} else if (life >= 0.6) {
				serialInterface.sendData("life 4");
			} else if (life >= 0.4) {
				serialInterface.sendData("life 3");
			} else if (life >= 0.2) {
				serialInterface.sendData("life 2");
			} else if (life > 0) {
				serialInterface.sendData("life 1");
			} else {
				serialInterface.sendData("life 0");
			}

		}
	}
}
