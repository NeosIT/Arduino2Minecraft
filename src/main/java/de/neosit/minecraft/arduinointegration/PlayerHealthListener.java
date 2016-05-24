package de.neosit.minecraft.arduinointegration;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Set health for the first player
 * 
 * @author Tobias
 */
public class PlayerHealthListener implements Listener {

	private SerialInterface serialInterface;
	private Configuration config;
	private Player firstPlayer = null;

	public PlayerHealthListener(SerialInterface serialInterface, Configuration config) {
		this.serialInterface = serialInterface;
		this.config = config;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
		if (players.size() == 1 || firstPlayer == null) {
			firstPlayer = players.iterator().next();
			displayPlayerHealth(firstPlayer);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer() == firstPlayer) {
			Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
			if (players.size() > 1) {
				
				Iterator<? extends Player> iterator = players.iterator();
				Player player = null;
				
				// find next non leaving player
				while(player == null) {
					Player p = iterator.next();
					if (p != firstPlayer) {
						player = p;
					}
				}
				
				firstPlayer = player;
				displayPlayerHealth(player);
				serialInterface.sendData("player " + player.getName());
			} else {
				firstPlayer = null;
				displayPlayerHealth(null);
				serialInterface.sendData("no players");
			}
		}
	}
	
	public void registerSchedule(JavaPlugin plugin) {
		Runnable r = new Runnable() {
			
			public void run() {
				if (null == firstPlayer) {
					return;
				}
				
				displayPlayerHealth(firstPlayer);
			}
		};
		
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, r, 0, config.getHealthbarPeriod());
	}
	
	public void displayPlayerHealth(Player player) {
		if (null == player) {
			serialInterface.sendData("life 0");
		} else {
			double life = player.getHealth() / player.getMaxHealth();
			
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
