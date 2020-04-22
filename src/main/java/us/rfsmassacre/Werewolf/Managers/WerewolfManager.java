package us.rfsmassacre.Werewolf.Managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.massivecraft.vampire.entity.UPlayer;

import me.NoChance.PvPManager.PvPlayer;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.DependencyManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Data.WerewolfDataManager;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent.CureType;
import us.rfsmassacre.Werewolf.Items.Weapons.SilverSword;
import us.rfsmassacre.Werewolf.Items.WerewolfItem.WerewolfItemType;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfManager 
{
	private final long MILLIS_IN_DAY = 86400000;
	
	private WerewolfDataManager werewolfData;
	private ConfigManager config;
	private DependencyManager dependency;
	private MessageManager messages;
	
	private EventManager events;
	
	private HashMap<Player, Werewolf> werewolves;
	private int formTaskId;
	private int armorTaskId;
	private int weaponTaskId;
	private int scentTaskId;
	private int cureTaskId;
	private int vampireTaskId;
	
	public WerewolfManager()
	{
		werewolfData = new WerewolfDataManager(WerewolfPlugin.getInstance());
		config = WerewolfPlugin.getConfigManager();
		dependency = WerewolfPlugin.getDependencyManager();
		messages = WerewolfPlugin.getMessageManager();
		werewolves = new HashMap<Player, Werewolf>();
		
		events = WerewolfPlugin.getEventManager();
		
		//Start all checks
		startFormChecker();
		startArmorChecker();
		startWeaponChecker();
		startScentChecker();
		startCureChecker();
		startVampirismChecker();
		
		//Loads all werewolves to their clans
		syncClans();
	}
	
	public void purgeBrokenFiles()
	{
		for (File file : werewolfData.listFiles())
		{
			try
			{
				getOfflineWerewolf(file);
			}
			catch (Exception exception)
			{
				file.delete();
			}
		}
	}
	
	public void startFormChecker()
	{
		//Continuously give back the buffs in they were missing
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		formTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
        		for (Werewolf werewolf : getOnlineWerewolves())
        		{	
        			if (werewolf.inWolfForm())
        			{
        				/*
        				 * Terminate Transformation
        				 */
        				//Remove their form if they're in a no werewolf world
        				if (config.getStringList("no-werewolf-worlds").contains(werewolf.getPlayer().getWorld().getName()))
        				{
        					werewolf.untransform();
        					continue;
        				}
        				
        				//Untransform when their time expires.
        				if (config.getBoolean("transformation.limit"))
        				{
        					int level = werewolf.getLevel();
        					long lastTransform = werewolf.getLastTransform();
        					long now = System.currentTimeMillis();
        					
        					double secondsPassed = (now - lastTransform) / 1000;
        					double base = config.getDouble("transformation.base");
        					double modifier = config.getDouble("transformation.modifier");
        					
        					double expiration = 0.0;
        					
        					if (config.getString("transformation.equation").toUpperCase().equals("LINEAR"))
        					{
        						expiration = (modifier * level) + base;
        					}
        					else if (config.getString("transformation.equation").toUpperCase().equals("EXPONENTIAL"))
        					{
        						expiration = (Math.pow(level, modifier)) + base;
        					}
        					else //FLAT EQUATION
        					{
        						expiration = base;
        					}
        					
        					//Untransform when time is up
        					if (secondsPassed >= expiration && !WerewolfPlugin.getMoonManager().isFullMoon(werewolf.getPlayer().getWorld()))
        					{
        						if (werewolf.untransform())
        							messages.sendWolfLocale(werewolf.getPlayer(), "transform.from-form");
        						
        						continue;
        					}
        				}
        				
        				/*
        				 * Keep Transformation Up
        				 */
        				Clan clan = WerewolfPlugin.getClanManager().getClan(werewolf);
        				Long clanSpeed = config.getLong("werewolf-stats." + werewolf.getType().toKey() + ".speed");
        				
    					//Return their speed to their normal forms
    					if (werewolf.getPlayer().getWalkSpeed() < clanSpeed)
    						werewolf.getPlayer().setWalkSpeed(clanSpeed);
    					
    					for (String effectName : config.getStringList("blocked-potions"))
    					{
    						PotionEffectType effect = PotionEffectType.getByName(effectName);
    						if (effect != null)
    							werewolf.getPlayer().removePotionEffect(effect);
    					}
    					
        				for (PotionEffect buff : clan.getBuffs())
        				{
        					//Ensures the buffs remain permanent for the duration of the transformation
        					if (!werewolf.getPlayer().hasPotionEffect(buff.getType()))
        						werewolf.getPlayer().addPotionEffect(buff);
        				}
        			}
        			
        			//Ensure that alpha werewolves cannot toggle their PVP off
        			if (isAlpha(werewolf.getUUID()) && dependency.hasPlugin("PvPManager") 
           				 && config.getBoolean("support.PvPManager"))
       				{
       					PvPlayer player = PvPlayer.get(werewolf.getPlayer());
       					if (!player.hasPvPEnabled())
       					{
       						player.setPvP(true);
       						messages.sendWolfLocale(werewolf.getPlayer(), "clan.alpha-pvp");
       					}
       				}
        		}
            }
        }, 0L, config.getInt("intervals.werewolf-buffs"));
	}
	
	public void startArmorChecker()
	{
		//Continuously drop any armor the werewolf might have on
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		armorTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
        		for (Werewolf werewolf : getOnlineWerewolves())
        		{
        			if (werewolf.inWolfForm())
        			{
        				werewolf.dropArmor();
        			}
        		}
            }
        }, 0L, config.getInt("intervals.werewolf-drops"));
	}
	
	public void startWeaponChecker()
	{
		//Continuously cause werewolves to get hurt if touching silver
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		weaponTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
			public void run() 
            {
        		for (Werewolf werewolf : getOnlineWerewolves())
        		{
        			Player player = werewolf.getPlayer();
        			SilverSword silverSword = (SilverSword)WerewolfPlugin.getItemManager().getWerewolfItem(WerewolfItemType.SILVER_SWORD);
        			
        			if (silverSword.isHoldingItem(player, true) && silverSword.hasItem(player))
        				player.damage(config.getInt("silver-penalty"));
        		}
            }
        }, 0L, config.getInt("intervals.werewolf-silver"));
	}
	
	public void startCureChecker()
	{
		if (!WerewolfPlugin.getConfigManager().getBoolean("auto-cure.enabled"))
			return; 
		
		//Continuously cure werewolves who haven't transformed in a long time
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		cureTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
        		for (Werewolf werewolf : getAllWerewolves())
        		{
        			long noCureTime = System.currentTimeMillis() - werewolf.getLastTransform();
        			long cureDelay = config.getLong("auto-cure.days") * MILLIS_IN_DAY;
        			if (noCureTime >= cureDelay)
        			{
        				WerewolfCureEvent cureEvent = new WerewolfCureEvent(werewolf.getPlayer(), CureType.AUTO_CURE);
        				if (events != null)
        					events.callEvent(cureEvent);
        				if (!cureEvent.isCancelled())
        				{	
            				messages.broadcastLocale("cure.auto-cure",
            						"{werewolf}", werewolf.getDisplayName());
            				
            				cureWerewolf(werewolf);
        				}
        			}
            	}
            }
        }, 0L, config.getInt("intervals.cure-check"));
	}
	
	public void startScentChecker()
	{
		//Continuously drop any armor the werewolf might have on
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scentTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
        		for (Werewolf werewolf : getOnlineWerewolves())
        		{
        			if (werewolf.inWolfForm() && werewolf.isTracking())
        			{
        				//Blind them and slow them down
        				werewolf.showTrail();
						if (!werewolf.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS))
							werewolf.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 720000, 0));
						if (!werewolf.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
							werewolf.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 720000, 5));

        				if (werewolf.canSniff())
						{
							werewolf.sniff();
						}
        			}
        		}
            }
        }, 0L, config.getInt("intervals.werewolf-scent"));
	}
	
	public void startVampirismChecker()
	{
		if (dependency.hasPlugin("Vampire"))
		{
			//Only run if the Vampire plugin is running
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			vampireTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
	        {
	            public void run() 
	            {
	    			for (Werewolf werewolf : getOnlineWerewolves())
	    			{
	    				if (isVampire(werewolf.getPlayer()))
	    				{
	    					UPlayer uPlayer = UPlayer.get(werewolf.getPlayer());
	    					uPlayer.setVampire(false);
	    				}
	    			}
	            }
	        }, 0L, config.getInt("intervals.hybrid-check"));
		}
	}
	
	public void endCycles()
	{
		//In case we need to stop the buff cycle for a reload
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(formTaskId);
		scheduler.cancelTask(armorTaskId);
		scheduler.cancelTask(cureTaskId);
		scheduler.cancelTask(scentTaskId);
		scheduler.cancelTask(weaponTaskId);
		scheduler.cancelTask(vampireTaskId);
	}
	
	public Werewolf getWerewolf(Player player)
	{
		 return werewolves.get(player);
	}
	public Werewolf getWerewolf(UUID playerId)
	{
		Player player = Bukkit.getPlayer(playerId);
		if (player != null)
			return getWerewolf(player);
		else
			return null;
	}
	public Werewolf getOfflineWerewolf(File file)
	{
		return (Werewolf)werewolfData.loadFromFile(file);
	}
	public Werewolf getOfflineWerewolf(UUID playerId)
	{
		return (Werewolf)werewolfData.loadFromFile(playerId.toString());
	}
	public Werewolf loadWerewolf(Player player)
	{
		Werewolf werewolf = getOfflineWerewolf(player.getUniqueId());
		werewolf.setPlayer(player);
		addWerewolf(werewolf);
		return werewolf;
	}
	
	public Collection<Werewolf> getOnlineWerewolves()
	{
		return werewolves.values();
	}
	public void addWerewolf(Werewolf werewolf)
	{
		werewolves.put(werewolf.getPlayer(), werewolf);
	}
	public void removeWerewolf(Werewolf werewolf)
	{
		removeWerewolf(werewolf.getPlayer());
	}
	public void removeWerewolf(Player player)
	{
		werewolves.remove(player);
	}
	
	public void storeWerewolves()
	{
		if (!werewolves.isEmpty())
		{
			//Stores ever werewolf based on UUID
			for (Werewolf werewolf : werewolves.values())
			{
				werewolfData.saveToFile(werewolf, werewolf.getUUID().toString());
			}
		}
	}
	
	public void storeWerewolf(Werewolf werewolf)
	{
		werewolfData.saveToFile(werewolf, werewolf.getUUID().toString());
	}
	
	//Returns all offline werewolves
	public ArrayList<Werewolf> getOfflineWerewolves()
	{
		ArrayList<Werewolf> offlineWerewolves = new ArrayList<Werewolf>();
		
		if (getWerewolfAmount() > 0)
		{
			for (File file : werewolfData.listFiles())
			{
				Werewolf werewolf = (Werewolf)werewolfData.loadFromFile(file);
				try
				{
					if (Bukkit.getPlayer(werewolf.getUUID()) == null)
						offlineWerewolves.add(werewolf);
				}
				catch (Exception exception)
				{
					file.delete();
				}
			}
		}
		
		return offlineWerewolves;
	}
	
	//Returns every single werewolf, if online gets the loaded info
	public ArrayList<Werewolf> getAllWerewolves()
	{
		ArrayList<Werewolf> allWerewolves = new ArrayList<Werewolf>();
		for (File file : werewolfData.listFiles())
		{
			Werewolf werewolf = (Werewolf)werewolfData.loadFromFile(file);
			try
			{
				Player player = Bukkit.getPlayer(werewolf.getUUID());
				if (player == null)
					allWerewolves.add(werewolf);
				else
					allWerewolves.add(getWerewolf(player));
			}
			catch (Exception exception)
			{
				file.delete();
			}
		}
		
		return allWerewolves;
	}
	
	//Returns number of Werewolves on the server
	public int getWerewolfAmount()
	{
		return werewolfData.listFiles().length;
	}
	
	/*
	 * Werewolf Management
	 */
	//Infects this player with this clan type and saves them in memory and in file
	public void infectWerewolf(Player player, ClanType type)
	{
		Werewolf werewolf = new Werewolf(player, type);
		addWerewolf(werewolf);
		werewolfData.saveToFile(werewolf, werewolf.getUUID().toString());
		
		player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 100);
		player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
		player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
	}
	//Deletes their data and makes them human
	public void cureWerewolf(Werewolf werewolf)
	{
		werewolf.untransform();
		
		if (isAlpha(werewolf.getUUID()))
			werewolf.getClan().setAlphaId(null);
		
		removeWerewolf(werewolf);
		werewolfData.deleteFile(werewolf.getUUID().toString());
		
		if (werewolf.getPlayer() != null)
		{
			werewolf.getPlayer().getLocation().getWorld().playEffect(werewolf.getPlayer().getLocation(), Effect.SMOKE, 100);
			werewolf.getPlayer().getLocation().getWorld().playEffect(werewolf.getPlayer().getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
			werewolf.getPlayer().getLocation().getWorld().playEffect(werewolf.getPlayer().getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
		}
	}
	//Checks if they're a werewolf
	public boolean isWerewolf(UUID playerId)
	{
		return werewolfData.loadFromFile(playerId.toString()) != null;
	}
	public boolean isWerewolf(Player player)
	{
		return isWerewolf(player.getUniqueId());
	}
	//Check if they're an alpha
	public boolean isAlpha(UUID playerId)
	{
		if (config.getBoolean("alphas"))
		{
			if (isWerewolf(playerId))
			{
				Werewolf werewolf = getWerewolf(Bukkit.getPlayer(playerId));
				if (werewolf == null)
					werewolf = getOfflineWerewolf(playerId);

				Clan clan = WerewolfPlugin.getClanManager().getClan(werewolf.getType());
				if (clan.isAlpha(werewolf))
					return true;
			}
		}

		return false;
	}
	public boolean isAlpha(Player player)
	{
		return isAlpha(player.getUniqueId());
	}
	//Checks if player is a vampire
	public boolean isVampire(Player player)
	{
		if (WerewolfPlugin.getDependencyManager().hasPlugin("Vampire"))
			return UPlayer.get(player).isVampire();
		else
			return false;
	}
	//Checks if player is human
	public boolean isHuman(Player player)
	{
		return !isVampire(player) && !isWerewolf(player);
	}
	
	//Runs the numbers to see if the infection was successful
	public boolean canWolfInfect(boolean hasIntent)
	{
		int chance = config.getInt("infection.wolf.chance");
		
		//Return false if the chance is 0 or less
		if (chance <= 0)
			return false;
		
		//Random number between 1 and 100
		int random = new Random().nextInt(100) + 1; 
		return random <= chance;
	}
	public boolean canWerewolfInfect(boolean hasIntent)
	{
		int chance = config.getInt("infection.werewolf.chance");
		int intent = config.getInt("infection.werewolf.intent");
		
		//Return false if the chance is 0 or less
		if (chance <= 0)
			return false;
		
		//Random number between 1 and 100
		int random = new Random().nextInt(100) + 1; 
		return hasIntent ? random <= intent : random <= chance;
	}
	public boolean canTrack()
	{
		int chance = config.getInt("track.chance");
		
		//Return false if the chance is 0 or less
		if (chance <= 0)
			return false;
		
		//Random number between 1 and 100
		int random = new Random().nextInt(100) + 1;
		return random <= chance;
	}
	
	public void syncClans()
	{
		//Go through all werewolves that have data and add them into the clans data
		//This is so we only need to traverse through all of them only at startup instead of
		//every time calling a function
		ClanManager clans = WerewolfPlugin.getClanManager();
		
		Clan witherfang = clans.getClan(ClanType.WITHERFANG);
		Clan silvermane = clans.getClan(ClanType.SILVERMANE);
		Clan bloodmoon = clans.getClan(ClanType.BLOODMOON);
		
		witherfang.clearMemberIds();
		silvermane.clearMemberIds();
		bloodmoon.clearMemberIds();
		
		for (Werewolf werewolf : getAllWerewolves())
		{
			switch (werewolf.getType())
			{
				case WITHERFANG:
					witherfang.addMemberId(werewolf.getUUID());
					break;
				case SILVERMANE:
					silvermane.addMemberId(werewolf.getUUID());
					break;
				case BLOODMOON:
					bloodmoon.addMemberId(werewolf.getUUID());
					break;
				default:
					break;
			}
		}
	}
}
