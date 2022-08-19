package us.rfsmassacre.Werewolf.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.TrackerTargetEvent;
import us.rfsmassacre.Werewolf.Events.TrackerTargetEvent.TargetType;
import us.rfsmassacre.Werewolf.Items.Trackers.VampireTracker;
import us.rfsmassacre.Werewolf.Items.WerewolfItemOld.WerewolfItemType;
import us.rfsmassacre.Werewolf.Managers.EventManager;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.MoonManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class VampireHuntingListener implements Listener
{
	private MessageManager messages;
	private ConfigManager config;
	private WerewolfManager werewolves;
	private ItemManager items;
	private MoonManager moons;
	private EventManager events;
	
	private HashMap<Player, Target> hunters;
	private int trackerTaskId;
	
	private class Target implements Comparable<Target>
	{
		private Player hunter;
		private Player vampire;
		
		public Target(Player hunter, Player vampire)
		{
			this.hunter = hunter;
			this.vampire = vampire;
		}
		
		private double getDistance()
		{
			return this.hunter.getLocation().distance(vampire.getLocation());
		}

		public int compareTo(Target other) 
		{
			if (this.getDistance() > other.getDistance())
				return -1;
			else if (this.getDistance() < other.getDistance())
				return 1;
			else
				return 0;
		}
	}
	
	public VampireHuntingListener()
	{
		messages = WerewolfPlugin.getMessageManager();
		config = WerewolfPlugin.getConfigManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		items = WerewolfPlugin.getItemManager();
		moons = WerewolfPlugin.getMoonManager();
		events = WerewolfPlugin.getEventManager();
		
		hunters = new HashMap<Player, Target>();
		startTrackerChecker();
		
	}
	
	/*
	 * Werewolf Trackers will auto-update the position of the
	 * target whilst they are in WW form.
	 */
	public void startTrackerChecker()
	{
		//Continuously drop any armor the werewolf might have on
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		trackerTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
            	VampireTracker tracker = new VampireTracker();
            	
        		for (Player hunter : hunters.keySet())
        		{
        			Target target = hunters.get(hunter);
        			
        			/*
        			 * Stops tracking if...
        			 * 
        			 * The hunter or target log off,
        			 * The target moves to another world,
        			 * The hunter no longer holds the item in their hand,
        			 * The hunter no longer has the item in their inventory
        			 */
        			if (hunter != null && target != null && target.vampire != null
        			 && target.vampire.getWorld().equals(hunter.getWorld())
        			 && tracker.isHoldingItem(hunter, config.getBoolean("hunting.trackers.use-either-hand"))
        			 && tracker.hasItem(hunter) && werewolves.isHuman(hunter) && config.getBoolean("hunting.enabled")
        			 && isValid(hunter, target.vampire))
        			{
        				TrackerTargetEvent event = new TrackerTargetEvent(hunter, target.vampire, TargetType.VAMPIRE_TARGET);
        				events.callEvent(event);
        				if (!event.isCancelled())
        				{
		        			hunter.setCompassTarget(hunters.get(hunter).vampire.getLocation());
		        			
		        			messages.sendHunterAction(hunter, "hunting.tracker.actionbar",
		        					"{item}", tracker.getDisplayName(),
									"{target}", target.vampire.getDisplayName(),
									"{distance}", Integer.toString((int)Math.round(target.getDistance())));
		        			
		        			continue;
        				}
        			}
        			
    				hunters.remove(hunter);
    				hunter.setCompassTarget(new Location(hunter.getWorld(), 0, 0, 0));
    				
    				messages.sendHunterLocale(hunter, "hunting.tracker.lost",
    						"{item}", tracker.getDisplayName(),
							"{target}", target.vampire.getDisplayName());
        		}
            }
        }, 0L, config.getInt("intervals.trackers"));
	}
	public void endCycles()
	{
		//In case we need to stop the cycle for a reload
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(trackerTaskId);
	}
	
	/*
	 * Vampire Tracker
	 */
	@EventHandler(ignoreCancelled = true)
	public void onVampireTracking(PlayerInteractEvent event)
	{
		Player hunter = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		
		VampireTracker tracker = new VampireTracker();

		if (tracker.equals(item))
		{
			if (!config.getBoolean("hunting.enabled"))
			{
				messages.sendHunterLocale(hunter, "hunting.disabled");
				return;
			}
			
			//Auto lock onto the nearest werewolf when right clicking
			//Skips to the next target in list if the event is cancelled
			if (werewolves.isHuman(hunter))
			{
				if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
				{
					if (!hunters.containsKey(hunter))
					{
						ArrayList<Target> targets = new ArrayList<Target>();
						for (Player player : hunter.getWorld().getPlayers())
						{
							if (isValid(hunter, player))
								targets.add(new Target(hunter, player));
						}
						
						if (!targets.isEmpty())
						{
							Collections.sort(targets, Collections.reverseOrder());
							
							for (Target target : targets)
							{
								TrackerTargetEvent targetEvent = new TrackerTargetEvent(hunter, target.vampire, TargetType.VAMPIRE_TARGET);
								events.callEvent(targetEvent);
								if (!targetEvent.isCancelled())
								{
									hunters.put(hunter, target);
									messages.sendHunterLocale(hunter, "hunting.tracker.found",
											"{item}", tracker.getDisplayName(),
											"{target}", target.vampire.getDisplayName(),
											"{distance}", Integer.toString((int)Math.round(target.getDistance())));
									
									return;
								}
							}
						}
						
						messages.sendHunterLocale(hunter, "hunting.tracker.failed");
						return;
					}
					
					messages.sendHunterLocale(hunter, "hunting.tracker.already-found", "{target}", hunters.get(hunter).vampire.getDisplayName());
					return;
				}
				
				//Clear target if locked onto a werewolf when left clicking
				if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
				{
					if (hunters.containsKey(hunter))
					{
						Target target = hunters.get(hunter);
						
						messages.sendHunterLocale(hunter, "hunting.tracker.lost",
								"{item}", tracker.getName(),
								"{target}", target.vampire.getDisplayName());
						
						hunters.remove(hunter);
						hunter.setCompassTarget(new Location(hunter.getWorld(), 0, 0, 0));
					}
					else
					{
						messages.sendHunterLocale(hunter, "hunting.tracker.no-target",
								"{item}", tracker.getDisplayName());
					}
					
					return;
				}
			}
			
			messages.sendHunterLocale(hunter, "hunting.racial.use",
					"{item}", tracker.getDisplayName());
			
			return;
		}
	}
	
	//Check if target is still valid
	private boolean isValid(Player hunter, Player target)
	{
		if (werewolves.isVampire(target))
		{
			if ((config.getBoolean("hunting.target.vampire.daytime") && moons.isDayTime(hunter.getWorld()))
			|| (config.getBoolean("hunting.target.vampire.nighttime") && moons.isNightTime(hunter.getWorld())))
			{
				return true;
			}
		}
		
		return false;
	}
}
