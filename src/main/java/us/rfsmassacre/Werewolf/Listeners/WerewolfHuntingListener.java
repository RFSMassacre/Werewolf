package us.rfsmassacre.Werewolf.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.TrackerTargetEvent;
import us.rfsmassacre.Werewolf.Events.TrackerTargetEvent.TargetType;
import us.rfsmassacre.Werewolf.Items.WerewolfItemOld;
import us.rfsmassacre.Werewolf.Items.Armor.WerewolfArmor;
import us.rfsmassacre.Werewolf.Items.Potions.WolfsbanePotion;
import us.rfsmassacre.Werewolf.Items.Trackers.WerewolfTracker;
import us.rfsmassacre.Werewolf.Items.Weapons.SilverSword;
import us.rfsmassacre.Werewolf.Items.WerewolfItemOld.WerewolfItemType;
import us.rfsmassacre.Werewolf.Managers.EventManager;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.MoonManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class WerewolfHuntingListener implements Listener
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
		private Player werewolf;
		
		public Target(Player hunter, Player werewolf)
		{
			this.hunter = hunter;
			this.werewolf = werewolf;
		}
		
		private double getDistance()
		{
			return this.hunter.getLocation().distance(werewolf.getLocation());
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
	
	public WerewolfHuntingListener()
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
            	WerewolfTracker tracker = new WerewolfTracker();
            	
        		for (Player hunter : hunters.keySet())
        		{
        			Target target = hunters.get(hunter);
        			Werewolf werewolf = werewolves.getWerewolf(target.werewolf);
        			
        			/*
        			 * Stops tracking if...
        			 * 
        			 * The hunter or target log off,
        			 * The target moves to another world,
        			 * The hunter no longer holds the item in their hand,
        			 * or if the hunter no longer has the item in their inventory.
        			 */
        			if (hunter != null && target != null && werewolf != null && werewolf.getPlayer() != null
        			 && werewolf.getPlayer().getWorld().equals(hunter.getWorld())
        			 && tracker.isHoldingItem(hunter, config.getBoolean("hunting.trackers.use-either-hand"))
        			 && tracker.hasItem(hunter) && werewolves.isHuman(hunter) && config.getBoolean("hunting.enabled")
        			 && isValid(hunter, target.werewolf))
        			{
        				TrackerTargetEvent event = new TrackerTargetEvent(hunter, target.werewolf, TargetType.WEREWOLF_TARGET);
        				events.callEvent(event);
        				if (!event.isCancelled())
        				{
		        			hunter.setCompassTarget(hunters.get(hunter).werewolf.getLocation());
		        			
		        			messages.sendHunterAction(hunter, "hunting.tracker.actionbar",
		        					"{item}", tracker.getDisplayName(),
									"{target}", target.werewolf.getDisplayName(),
									"{distance}", Integer.toString((int)Math.round(target.getDistance())));
		        			
		        			continue;
        				}
        			}
        			
    				hunters.remove(hunter);
    				hunter.setCompassTarget(new Location(hunter.getWorld(), 0, 0, 0));
    				
    				messages.sendHunterLocale(hunter, "hunting.tracker.lost",
    						"{item}", tracker.getDisplayName(),
							"{target}", target.werewolf.getDisplayName());
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
	 * Werewolf Tracker
	 */
	@EventHandler(ignoreCancelled = true)
	public void onWerewolfTracking(PlayerInteractEvent event)
	{
		Player hunter = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		
		WerewolfTracker tracker = new WerewolfTracker();

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
							if (werewolves.isWerewolf(player))
							{
								if (isValid(hunter, player))
									targets.add(new Target(hunter, player));
							}
						}
						
						if (!targets.isEmpty())
						{
							Collections.sort(targets, Collections.reverseOrder());
							
							for (Target target : targets)
							{
								TrackerTargetEvent targetEvent = new TrackerTargetEvent(hunter, target.werewolf, TargetType.WEREWOLF_TARGET);
								events.callEvent(targetEvent);
								if (!targetEvent.isCancelled())
								{
									hunters.put(hunter, target);
									messages.sendHunterLocale(hunter, "hunting.tracker.found",
											"{item}", tracker.getDisplayName(),
											"{target}", target.werewolf.getDisplayName(),
											"{distance}", Integer.toString((int)Math.round(target.getDistance())));
									
									return;
								}
							}
						}
						
						messages.sendHunterLocale(hunter, "hunting.tracker.failed");
						return;
					}
					
					messages.sendHunterLocale(hunter, "hunting.tracker.already-found", "{target}", hunters.get(hunter).werewolf.getDisplayName());
					return;
				}
				
				//Clear target if locked onto a werewolf when left clicking
				if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
				{
					if (hunters.containsKey(hunter))
					{
						Target target = hunters.get(hunter);
						
						messages.sendHunterLocale(hunter, "hunting.tracker.lost",
								"{item}", tracker.getDisplayName(),
								"{target}", target.werewolf.getDisplayName());
						
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
	
	/*
	 * Silver Sword
	 */
	@EventHandler(ignoreCancelled = true)
	public void onSilverSwordHit(EntityDamageByEntityEvent event)
	{
		if (!event.isCancelled() && event.getEntity() instanceof Player 
		  && event.getDamager() instanceof Player)
		{
			Player victim = (Player)event.getEntity();
			Player attacker = (Player)event.getDamager();
			SilverSword sword = new SilverSword();
			
			//If werewolf is hit by silver sword in wolf form, undo the defense buff
			if (werewolves.isWerewolf(victim))
			{
				Werewolf werewolf = werewolves.getWerewolf(victim);
				String clan = werewolf.getType().toKey();
				
				if (werewolf.inWolfForm() && sword.isHoldingItem(attacker, false) && sword.hasItem(attacker))
				{
					event.setDamage(event.getDamage() / config.getDouble("werewolf-stats." + clan + ".werewolf.defense"));
				}
			}
		}
	}
	/*
	 * Gold Sword
	 */
	@EventHandler(ignoreCancelled = true)
	public void onGoldSwordHit(EntityDamageByEntityEvent event)
	{
		if (!event.isCancelled() && event.getEntity() instanceof Player 
				  && event.getDamager() instanceof Player)
		{
			Player victim = (Player)event.getEntity();
			Player attacker = (Player)event.getDamager();
			Material goldSword = ItemManager.getCorrectMaterial("GOLD_SWORD");
			
			//If werewolf is hit by gold sword in wolf form, undo the defense buff
			if (werewolves.isWerewolf(victim))
			{
				Werewolf werewolf = werewolves.getWerewolf(victim);
				String clan = werewolf.getType().toKey();
				
				if (werewolf.inWolfForm() && WerewolfItemOld.isHoldingItem(goldSword, attacker, false) && WerewolfItemOld.hasItem(goldSword, attacker))
				{
					event.setDamage(event.getDamage() / config.getDouble("werewolf-stats." + clan + ".werewolf.defense"));
				}
			}
		}
	}
	
	/*
	 * WOLFSBANE POTION
	 */
	@EventHandler(ignoreCancelled = true)
	public void onWolfsBanePotionBreak(PotionSplashEvent event)
	{
		//Cancel if the event is cancelled or not wolfsBane
		WolfsbanePotion wolfsBane = new WolfsbanePotion();
		if (!event.isCancelled() && wolfsBane.equals(event.getPotion().getItem()))
		{
			for (LivingEntity entity : event.getAffectedEntities())
			{
				if (entity instanceof Player)
				{
					Player player = (Player)entity;
					if (werewolves.isWerewolf(player))
					{
						if (player.hasPermission("werewolf.immunewolfsbane"))
							return;
						
						//If they were in wolf form, simply transform them back
						//Thrown when in human form triggers transform cooldown
						Werewolf werewolf = werewolves.getWerewolf(player);
						if (werewolf.inWolfForm())
							werewolf.untransform();
						else
							werewolf.setLastTransform(System.currentTimeMillis());
						
						messages.sendWolfLocale(player, "cure.wolfsbane-potion");
					}
				}
			}
		}
	}
	
	/*
	 * PURITY ARMOR BONUS
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onSuperNaturalDamage(EntityDamageByEntityEvent event)
	{
		if (event.isCancelled())
			return;
		
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if ((damager instanceof Player) && (entity instanceof Player))
		{
			Player attacker = (Player)damager;
			Player defender = (Player)entity;
			
			if (werewolves.isHuman(defender) && !werewolves.isHuman(attacker))
			{
				int rawDefense = 0;
				for (ItemStack armor : defender.getInventory().getArmorContents())
				{
					WerewolfItem item = items.getWerewolfItem(armor);
					if (item != null && item instanceof WerewolfArmor)
					{
						WerewolfArmor werewolfArmor = (WerewolfArmor)item;
						rawDefense += werewolfArmor.getDefense();
					}
				}
				
				double defense = 1.0 - (rawDefense / 100);
				event.setDamage(event.getDamage() * defense);
			}
		}
	}
	
	//Check if target is still valid
	private boolean isValid(Player hunter, Player target)
	{
		if (werewolves.isWerewolf(target))
		{
			if ((config.getBoolean("hunting.target.werewolf.wolf-form") && werewolves.getWerewolf(target).inWolfForm())
			 | (config.getBoolean("hunting.target.werewolf.human-form") && !werewolves.getWerewolf(target).inWolfForm())
			&& (config.getBoolean("hunting.target.werewolf.daytime") && moons.isDayTime(hunter.getWorld()))
			 | (config.getBoolean("hunting.target.werewolf.nighttime") && moons.isNightTime(hunter.getWorld())))
			{
				return true;
			}
		}
		
		return false;
	}
}
