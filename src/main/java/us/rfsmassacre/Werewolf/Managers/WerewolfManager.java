package us.rfsmassacre.Werewolf.Managers;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import com.clanjhoo.vampire.VampireAPI;
import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.NoChance.PvPManager.PvPlayer;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.DependencyManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Data.WerewolfDataManager;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent.CureType;
import us.rfsmassacre.Werewolf.Items.Weapons.SilverSword;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfManager 
{
	private final long MILLIS_IN_DAY = 86400000;
	
	private final WerewolfDataManager werewolfData;
	private final ConfigManager config;
	private final DependencyManager dependency;
	private final MessageManager messages;
	
	private final EventManager events;
	
	private final Map<UUID, Werewolf> werewolves;

	private final Set<BukkitTask> tasks;
	
	public WerewolfManager()
	{
		werewolfData = new WerewolfDataManager(WerewolfPlugin.getInstance());
		config = WerewolfPlugin.getConfigManager();
		dependency = WerewolfPlugin.getDependencyManager();
		messages = WerewolfPlugin.getMessageManager();
		werewolves = new HashMap<>();
		tasks = new HashSet<>();
		
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
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			for (File file : werewolfData.getFiles())
			{
				try
				{
					Werewolf werewolf = werewolfData.read(file);
					if (werewolf == null)
					{
						file.delete();
					}
				}
				catch (Exception exception)
				{
					file.delete();
				}
			}
		});
	}

	public void startFormChecker()
	{
		//Continuously give back the buffs in they were missing
		tasks.add(new BukkitRunnable()
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
        					
        					double secondsPassed = (double)(now - lastTransform) / 1000;
        					double base = config.getDouble("transformation.base");
        					double modifier = config.getDouble("transformation.modifier");
        					
        					double expiration;
        					
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
        				long clanSpeed = config.getLong("werewolf-stats." + werewolf.getType().toKey() + ".speed");
        				
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
							PotionEffect first = werewolf.getPlayer().getPotionEffect(buff.getType());
							if (first == null || first.getAmplifier() <= buff.getAmplifier())
							{
								werewolf.getPlayer().addPotionEffect(buff);
							}
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
        }.runTaskTimer(WerewolfPlugin.getInstance(), 0L, config.getInt("intervals.werewolf-buffs")));
	}
	
	public void startArmorChecker()
	{
		//Continuously drop any armor the werewolf might have on
		tasks.add(new BukkitRunnable()
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
        }.runTaskTimer(WerewolfPlugin.getInstance(), 0L, config.getInt("intervals.werewolf-drops")));
	}
	
	public void startWeaponChecker()
	{
		//Continuously cause werewolves to get hurt if touching silver
		tasks.add(new BukkitRunnable()
        {
			public void run() 
            {
        		for (Werewolf werewolf : getOnlineWerewolves())
        		{
        			Player player = werewolf.getPlayer();
        			SilverSword silverSword = new SilverSword();
        			
        			if (silverSword.isHoldingItem(player, true) && silverSword.hasItem(player))
        				player.damage(config.getInt("silver-penalty"));
        		}
            }
        }.runTaskTimer(WerewolfPlugin.getInstance(), 0L, config.getInt("intervals.werewolf-silver")));
	}
	
	public void startCureChecker()
	{
		if (!WerewolfPlugin.getConfigManager().getBoolean("auto-cure.enabled"))
		{
			return;
		}
		
		//Continuously cure werewolves who haven't transformed in a long time
		tasks.add(new BukkitRunnable()
		{
			@Override
            public void run() 
            {
        		for (Werewolf werewolf : getAllWerewolves())
        		{
					if (werewolf == null)
					{
						continue;
					}

        			boolean alphaOnly = WerewolfPlugin.getConfigManager().getBoolean("auto-cure.alpha-only");
        			if (alphaOnly && !isAlpha(werewolf.getUUID()))
					{
						continue;
					}

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
        }.runTaskTimerAsynchronously(WerewolfPlugin.getInstance(), 0L, config.getInt("intervals.cure-check")));
	}
	
	public void startScentChecker()
	{
		//Continuously drop any armor the werewolf might have on
		tasks.add(new BukkitRunnable()
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
        }.runTaskTimer(WerewolfPlugin.getInstance(), 0L, config.getInt("intervals.werewolf-scent")));
	}
	
	public void startVampirismChecker()
	{
		if (!dependency.hasPlugin("VampireRevamp"))
		{
			return;
		}

		//Only run if the Vampire plugin is running
		tasks.add(new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for (Werewolf werewolf : getOnlineWerewolves())
				{
					if (isVampire(werewolf.getPlayer()))
					{
						VampireRevamp.getVPlayerManager().getDataAsynchronous((uPlayer) -> uPlayer.setVampire(false),
								() -> {}, werewolf.getUUID());
					}
				}
			}
		}.runTaskTimer(WerewolfPlugin.getInstance(), 0L, config.getInt("intervals.hybrid-check")));
	}
	
	public void endCycles()
	{
		//In case we need to stop the buff cycle for a reload
		for (BukkitTask task : tasks)
		{
			task.cancel();
		}
	}
	
	public Werewolf getWerewolf(Player player)
	{
		return getWerewolf(player.getUniqueId());
	}
	public Werewolf getWerewolf(UUID playerId)
	{
		return werewolves.get(playerId);
	}
	public Werewolf getOfflineWerewolf(UUID playerId)
	{
		return werewolfData.read(playerId.toString());
	}
	public void getOfflineWerewolf(File file, Consumer<Werewolf> callback)
	{
		werewolfData.readAsync(file, callback);
	}
	public void getOfflineWerewolf(UUID playerId, Consumer<Werewolf> callback)
	{
		werewolfData.readAsync(playerId.toString(), callback);
	}
	public void loadWerewolf(Player player, Consumer<Werewolf> callback)
	{
		getOfflineWerewolf(player.getUniqueId(), callback);
	}
	
	public Collection<Werewolf> getOnlineWerewolves()
	{
		return werewolves.values();
	}
	public void addWerewolf(Werewolf werewolf)
	{
		werewolves.put(werewolf.getUUID(), werewolf);
	}
	public void removeWerewolf(Werewolf werewolf)
	{
		removeWerewolf(werewolf.getPlayer());
	}
	public void removeWerewolf(Player player)
	{
		werewolves.remove(player.getUniqueId());
	}
	
	public void storeWerewolves()
	{
		//Stores ever werewolf based on UUID
		for (Werewolf werewolf : werewolves.values())
		{
			storeWerewolf(werewolf);
		}
	}
	
	public void storeWerewolf(Werewolf werewolf)
	{
		werewolfData.write(werewolf.getUUID().toString(), werewolf);
	}
	
	//Returns all offline werewolves
	public List<Werewolf> getOfflineWerewolves()
	{
		List<Werewolf> offlineWerewolves = new ArrayList<>();
		
		if (getWerewolfAmount() > 0)
		{
			for (File file : werewolfData.getFiles())
			{
				Werewolf werewolf = werewolfData.read(file);
				try
				{
					if (werewolf.getPlayer() == null)
					{
						offlineWerewolves.add(werewolf);
					}
				}
				catch (Exception exception)
				{
					file.delete();
				}
			}
		}
		
		return offlineWerewolves;
	}

	public void getOfflineWerewolves(Consumer<List<Werewolf>> callback)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			callback.accept(getOfflineWerewolves());
		});
	}

	public List<Werewolf> getAllWerewolves()
	{
		List<Werewolf> allWerewolves = new ArrayList<>();
		for (File file : werewolfData.getFiles())
		{
			Werewolf werewolf = werewolfData.read(file);
			Player player = werewolf.getPlayer();
			if (player == null)
			{
				allWerewolves.add(werewolf);
			}
			else
			{
				allWerewolves.add(getWerewolf(player));
			}
		}

		return allWerewolves;
	}
	
	//Returns every single werewolf, if online gets the loaded info
	public void getAllWerewolves(Consumer<List<Werewolf>> callback)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			callback.accept(getAllWerewolves());
		});
	}
	
	//Returns number of Werewolves on the server
	public int getWerewolfAmount()
	{
		return werewolfData.getFiles().length;
	}
	
	/*
	 * Werewolf Management
	 */
	//Infects this player with this clan type and saves them in memory and in file
	public void infectWerewolf(Player player, ClanType type)
	{
		Werewolf werewolf = new Werewolf(player, type);
		addWerewolf(werewolf);
		werewolfData.writeAsync(werewolf.getUUID().toString(), werewolf);
		
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
		werewolfData.deleteAsync(werewolf.getUUID().toString());
		
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
		if (werewolves.containsKey(playerId))
		{
			return true;
		}

		for (File file : werewolfData.getFiles())
		{
			if (file.getName().equals(playerId.toString()))
			{
				return true;
			}
		}

		return false;
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
			for (ClanType type : ClanType.values())
			{
				Clan clan = WerewolfPlugin.getClanManager().getClan(type);
				if (clan.isAlpha(playerId))
				{
					return true;
				}
			}
		}

		return false;
	}
	public boolean isAlpha(Player player)
	{
		if (player == null)
		{
			return false;
		}

		return isAlpha(player.getUniqueId());
	}
	//Checks if player is a vampire
	public boolean isVampire(Player player)
	{
		if (WerewolfPlugin.getDependencyManager().hasPlugin("VampireRevamp"))
			return VampireAPI.isVampire(player);
		else
			return false;
	}
	//Checks if player is human
	public boolean isHuman(Player player)
	{
		return !isVampire(player) && !isWerewolf(player);
	}
	
	//Runs the numbers to see if the infection was successful
	public boolean canWolfInfect()
	{
		int chance = config.getInt("infection.wolf.chance");
		
		//Return false if the chance is 0 or less
		if (chance <= 0)
			return false;
		
		//Random number between 1 and 100
		int random = new Random(System.currentTimeMillis()).nextInt(100) + 1;
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
		int random = new Random(System.currentTimeMillis()).nextInt(100) + 1;
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
