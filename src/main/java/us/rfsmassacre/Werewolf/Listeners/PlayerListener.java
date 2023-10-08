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
	private final WerewolfManager werewolves;
	
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
		werewolves.getOfflineWerewolf(player.getUniqueId(), (werewolf) ->
		{
			if (werewolf != null)
			{
				werewolves.addWerewolf(werewolf);
				new BukkitRunnable()
				{
					public void run()
					{
						if (werewolf.inWolfForm())
							werewolf.untransform();
					}
				}.runTaskLater(WerewolfPlugin.getInstance(), 20L);

				WerewolfPlugin.updateGroup(player);
			}
		});
	}
	
	/*
	 * Unload Werewolf data on Leave
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		//Unload data if werewolf
		Werewolf werewolf = werewolves.getWerewolf(player);
		if (werewolf == null)
		{
			return;
		}

		werewolves.storeWerewolf(werewolf);
		werewolves.removeWerewolf(werewolf);
	}
	
	/*
	 * Untransform on death
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		
		//Untransform if werewolf
		Werewolf werewolf = werewolves.getWerewolf(player);
		if (werewolf == null)
		{
			return;
		}

		if (werewolf.inWolfForm())
		{
			werewolf.untransform();
		}
	}
}
