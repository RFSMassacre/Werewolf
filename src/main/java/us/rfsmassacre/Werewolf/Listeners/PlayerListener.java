package us.rfsmassacre.Werewolf.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class PlayerListener implements Listener
{
	private WerewolfManager werewolves;
	
	public PlayerListener()
	{
		werewolves = WerewolfPlugin.getWerewolfManager();
	}
	
	/*
	 * Load Werewolf data on Login
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		
		//Load data if werewolf
		if (werewolves.isWerewolf(player))
		{
			//And untransform them to avoid bugs
			//Done here in case they may still have a skin on
			final Werewolf werewolf = werewolves.loadWerewolf(player);
			
			//Update skin and untransform a minute after to ensure
			//no errors occur
			new BukkitRunnable()
			{
				public void run()
				{
					if (werewolf.inWolfForm())
						werewolf.untransform();
				}
			}.runTaskLater(WerewolfPlugin.getInstance(), 20L);
		}

		WerewolfPlugin.updateGroup(player);
	}
	
	/*
	 * Unload Werewolf data on Leave
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		
		//Unload data if werewolf
		if (werewolves.isWerewolf(player))
		{
			Werewolf werewolf = werewolves.getWerewolf(player);
			werewolves.storeWerewolf(werewolf);
			werewolves.removeWerewolf(werewolf);
		}
	}
	
	/*
	 * Untransform on death
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		
		//Untransform if werewolf
		if (werewolves.isWerewolf(player))
		{
			Werewolf werewolf = werewolves.getWerewolf(player);
			if (werewolf.inWolfForm())
				werewolf.untransform();
		}
	}
}
