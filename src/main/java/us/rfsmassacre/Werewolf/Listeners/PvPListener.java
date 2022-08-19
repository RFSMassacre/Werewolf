package us.rfsmassacre.Werewolf.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.NoChance.PvPManager.PvPlayer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.DependencyManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.TrackerTargetEvent;
import us.rfsmassacre.Werewolf.Managers.MessageManager;

public class PvPListener implements Listener
{
	private ConfigManager config;
	private DependencyManager dependency;
	private MessageManager messages;
	
	public PvPListener()
	{
		config = WerewolfPlugin.getConfigManager();
		dependency = WerewolfPlugin.getDependencyManager();
		messages = WerewolfPlugin.getMessageManager();
	}
	
	/*
	 * Prevent hunters from targeting people with PVP off.
	 *
	 * PVPManager
	 */
	@EventHandler(ignoreCancelled = true)
	public void onTargetPeaceful(TrackerTargetEvent event)
	{
		if (dependency.hasPlugin("PvPManager") && config.getBoolean("support.PvPManager"))
		{
			PvPlayer hunter = PvPlayer.get(event.getHunter());
			PvPlayer target = PvPlayer.get(event.getTarget());
			
			if (!hunter.hasPvPEnabled() || !target.hasPvPEnabled())
				event.setCancelled(true);
		}
	}
	
	/*
	 * Prevent trespassers from being PVP free.
	 *
	 * GriefPrevention
	 */
	@EventHandler(ignoreCancelled = true)
	public void onTresspass(PlayerMoveEvent event)
	{
		if (dependency.hasPlugin("PvPManager") && dependency.hasPlugin("GriefPrevention") 
		 && config.getBoolean("support.PvPManager") && config.getBoolean("hunting.enabled"))
		{
			//Don't check if within the same X, Z coords.
			if (event.getFrom().getBlockX() == event.getTo().getBlockX()
			 && event.getFrom().getBlockZ() == event.getTo().getBlockY())
			{
				return;
			}
			
			Player player = event.getPlayer();
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getTo(), true, null);
			
			if (player.hasPermission("werewolf.avoidpvp") || claim == null)
			{
				return;
			}
			
			if (claim.isAdminClaim() && !config.getBoolean("hunting.force-pvp.admin-claims"))
			{
				return;
			}
			
			if (!claim.isAdminClaim() && !config.getBoolean("hunting.force-pvp.claims"))
			{
				return;
			}
				
			//Force PVP on if the player walking in has no trust levels to the claims
			if (claim.allowAccess(player) != null && claim.allowContainers(player) != null && claim.allowEdit(player) != null)
			{
				PvPlayer trespasser = PvPlayer.get(player);
				if (!trespasser.hasPvPEnabled())
				{
					trespasser.setPvP(true);
					
					messages.sendHunterLocale(player, "hunting.claims.trespassing", 
							"{owner}", claim.getOwnerName());
					
					return;
				}
			}
		}
	}
}
