package us.rfsmassacre.Werewolf.Origin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.WerewolfTransformEvent;
import us.rfsmassacre.Werewolf.Managers.EventManager;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class Werewolf implements Comparable<Werewolf>
{
	private ConfigManager config;
	private EventManager events;
	
	private static final int MINUTE = 60000; //Number of milliseconds in a min
	private static final int SECOND = 1000; //Number of milliseconds in a sec

	private final UUID playerId; //ID of player for offline use
	private String displayName; //Prefix and displayname for offline use
	private ClanType type; //Clan werewolf belongs to
	private int level; //Werewolf level
	
	private boolean intent; //Intent to infect others
	private boolean tracking; //Wether Werewolf is tracking a scent
	private UUID targetId; //Target being tracked by this werewolf
	private boolean wolfForm; //Wether transformed or not
	private List<Wolf> wolfPack; //Current wolves following this werewolf
	
	private long lastTransform; //Last time transformed
	private long lastHowl; //Last time howled
	private long lastGrowl; //Last time growled
	private long lastSniff; //Last time sniffed
	
	public Werewolf(UUID playerId)
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.events = WerewolfPlugin.getEventManager();
		this.playerId = playerId;

		setDisplayName(null);
		setType(null);
		setLevel(0);
		setIntent(false);
		setTargetId(null);
		setTracking(false);
		setWolfForm(false);
		setWolfPack(new ArrayList<>());
		setLastTransform(System.currentTimeMillis());
		setLastHowl(0);
		setLastGrowl(0);
	}
	public Werewolf(Player player, ClanType type)
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.events = WerewolfPlugin.getEventManager();
		this.playerId = player.getUniqueId();

		setDisplayName(player.getDisplayName());
		setType(type);
		setLevel(0);
		setIntent(false);
		setTargetId(null);
		setTracking(false);
		setWolfForm(false);
		setWolfPack(new ArrayList<>());
		setLastTransform(System.currentTimeMillis());
		setLastHowl(0);
		setLastGrowl(0);
	}


	public UUID getUUID()
	{
		return this.playerId;
	}

	public Player getPlayer()
	{
		return Bukkit.getPlayer(playerId);
	}

	public String getDisplayName()
	{
		Player player = getPlayer();
		return (player != null ? player.getDisplayName() : this.displayName);
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public ClanType getType()
	{
		return this.type;
	}
	public Clan getClan()
	{
		return WerewolfPlugin.getClanManager().getClan(this.type);
	}
	public void setType(ClanType type)
	{
		this.type = type;
	}

	public int getLevel()
	{
		return this.level;
	}
	public void setLevel(int level)
	{
		this.level = level;
	}
	public void addLevel()
	{
		this.level++;
	}

	public boolean hasIntent()
	{
		return this.intent;
	}
	public void setIntent(boolean intent)
	{
		this.intent = intent;
	}

	public UUID getTargetId()
	{
		return this.targetId;
	}
	public void setTargetId(UUID playerId)
	{
		this.targetId = playerId;
	}

	public boolean isTracking()
	{
		return this.tracking;
	}
	public void setTracking(boolean tracking)
	{
		this.tracking = tracking;
	}

	public boolean inWolfForm()
	{
		return this.wolfForm;
	}
	public void setWolfForm(boolean wolfForm)
	{
		this.wolfForm = wolfForm;
	}

	public long getLastTransform()
	{
		return lastTransform;
	}
	public void setLastTransform(long lastTransform)
	{
		this.lastTransform = lastTransform + 1;
	}

	public long getLastHowl()
	{
		return lastHowl;
	}
	public void setLastHowl(long lastHowl)
	{
		this.lastHowl = lastHowl + 1;
	}

	public long getLastGrowl()
	{
		return lastGrowl;
	}
	public void setLastGrowl(long lastGrowl)
	{
		this.lastGrowl = lastGrowl + 1;
	}

	public long getLastSniff()
	{
		return lastSniff;
	}
	public void setLastSniff(long lastSniff)
	{
		this.lastSniff = lastSniff;
	}

	public List<Wolf> getWolfPack()
	{
		return this.wolfPack;
	}
	public void setWolfPack(List<Wolf> wolfPack)
	{
		this.wolfPack = wolfPack;
	}
	public void addPackWolf(Wolf wolf)
	{
		this.wolfPack.add(wolf);
	}
	public void removePackWolf(Wolf wolf)
	{
		this.wolfPack.remove(wolf);
	}

	//Transformation function
	public boolean transform()
	{
		Player player = getPlayer();
		if (!inWolfForm() && player != null)
		{
			WerewolfTransformEvent event = new WerewolfTransformEvent(player, this.type, true);
			events.callEvent(event);
			if (!event.isCancelled())
			{
				//Update skin whenever a werewolf transform-to event is called!!!!

				setWolfForm(true);

				/*
				 * Mimicking same effects from the original.
				 * Thanks DogOnFire, but you could've made
				 * the transformation sequence easy.
				 */
				howl();
				dropArmor();
				dropItems();

				for (PotionEffect effect : player.getActivePotionEffects())
				{
					player.removePotionEffect(effect.getType());
				}

				try {
					player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 1));
				}
				catch (NoSuchFieldError ex) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.getByName("CONFUSION"), 100, 1));
				}
				player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 100);
				player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
				player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
				player.setWalkSpeed((float)config.getDouble("werewolf-stats." + type.toKey() + ".speed"));
				setLastTransform(System.currentTimeMillis());

				return true;
			}
		}

		return false;
	}
	public boolean untransform()
	{
		Player player = getPlayer();
		if (inWolfForm() && player != null)
		{
			WerewolfTransformEvent event = new WerewolfTransformEvent(player, this.type, false);
			events.callEvent(event);
			if (!event.isCancelled())
			{
				//Remember to remove skin when a transform-from event is called!!!

				setWolfForm(false);
				stopTracking();
				/*
				 * Mimicking same effects from original.
				 * Thanks DogOnFire, but you could've made
				 * the transformation sequence easy.
				 */
				growl();

				for (PotionEffect effect : player.getActivePotionEffects())
				{
					player.removePotionEffect(effect.getType());
				}

				/*
				player.removePotionEffect(PotionEffectType.BLINDNESS);
				player.removePotionEffect(PotionEffectType.CONFUSION);
				player.removePotionEffect(PotionEffectType.HUNGER);
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				player.removePotionEffect(PotionEffectType.JUMP);
				player.removePotionEffect(PotionEffectType.SPEED);
				player.removePotionEffect(PotionEffectType.REGENERATION);
				player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.BLINDNESS);
				 */

				player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 100);
				player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
				player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
				player.setWalkSpeed(0.2F);
				setLastTransform(System.currentTimeMillis());

				return true;
			}
		}

		return false;
	}

	//Sound effects for Werewolf
	public void howl()
	{
		try
		{
			Player player = getPlayer();
			player.getWorld().playSound(player.getLocation(), Sound.valueOf(config.getString("sound.howl")),
					(float)config.getDouble("sound.volume"),	1.0F);
		}
		catch (Exception exception)
		{
			//Do nothing as the sound input was not placed properly
			//The error type changes per version, so use generic exception instead
		}

		setLastHowl(System.currentTimeMillis());
	}
	public void growl()
	{
		try
		{
			Player player = getPlayer();
			player.getWorld().playSound(player.getLocation(), Sound.valueOf(config.getString("sound.growl")),
					(float)config.getDouble("sound.volume"), 1.0F);
		}
		catch (Exception exception)
		{
			//Do nothing as the sound input was not placed properly
			//The error type changes per version, so use generic exception instead
		}

		setLastGrowl(System.currentTimeMillis());
	}
	public void sniff()
	{
		try
		{
			Player player = getPlayer();
			player.getWorld().playSound(player.getLocation(), Sound.valueOf(config.getString("sound.pant")), 5.0F, 0.6F);
		}
		catch (Exception exception)
		{
			//Do nothing as the sound input was not placed properly
			//The error type changes per version, so use generic exception instead
		}

		setLastSniff(System.currentTimeMillis());
	}

	//Checks if under moonlight
	public boolean isOutside()
	{
		Player player = getPlayer();
		Location location = player.getLocation();
		int playerY = location.getBlockY();
		int highestY = location.getWorld().getHighestBlockYAt(location);

		return playerY >= highestY;
	}
	//Checks if off cooldown for an action
	public int getNextHowl()
	{
		int cooldown = config.getInt("cooldowns.howl") * MINUTE;
		return (int)((cooldown - (System.currentTimeMillis() - lastHowl)) / MINUTE);
	}
	public int getNextGrowl()
	{
		int cooldown = config.getInt("cooldowns.growl") * MINUTE;
		return (int)((cooldown - (System.currentTimeMillis() - lastGrowl)) / MINUTE);
	}
	public int getNextTransform()
	{
		int cooldown = config.getInt("cooldowns.transform") * MINUTE;
		return (int)((cooldown - (System.currentTimeMillis() - lastTransform)) / MINUTE);
	}
	public int getNextUntransform()
	{
		return 0;
	}
	public int getNextSniff()
	{
		int cooldown = config.getInt("cooldowns.sniff") * SECOND;
		return (int)((cooldown - (System.currentTimeMillis() - lastSniff)) / SECOND);
	}

	public boolean canHowl()
	{
		return getNextHowl() <= 0;
	}
	public boolean canGrowl()
	{
		return getNextGrowl() <= 0;
	}
	public boolean canTransform()
	{
		return getNextTransform() <= 0;
	}
	public boolean canSniff()
	{
		return getNextSniff() <= 0;
	}

	//Compareable
	@Override
	public int compareTo(Werewolf werewolf)
	{
		boolean selfAlpha = WerewolfPlugin.getWerewolfManager().isAlpha(this.playerId);
		boolean otherAlpha = WerewolfPlugin.getWerewolfManager().isAlpha(werewolf.playerId);

		//Alpha is higher, otherwise the level is higher
		if (selfAlpha && !otherAlpha)
			return 1;
		else if (!selfAlpha && otherAlpha)
			return -1;
		else
			return this.level - werewolf.getLevel();
	}

	//Equipment Management
	public void dropArmor()
	{
		Player player = getPlayer();
		ItemStack[] equipment = player.getInventory().getArmorContents();

		//Drop each item
		for (ItemStack armor : equipment)
		{
			if (armor != null && !armor.getType().equals(Material.AIR))
			{
				//High level werewolves just have it placed back in their inventory
				if (level >= config.getInt("maturity.no-drop") && player.getInventory().firstEmpty() > -1)
				{
					player.getInventory().addItem(armor);
					player.updateInventory();
				}
				else
				{
					player.getWorld().dropItemNaturally(player.getLocation(), armor);
				}
			}
		}


		//Then remove each item equipped
		for (int slot = 0; slot < equipment.length; slot++)
		{
			equipment[slot] = new ItemStack(Material.AIR);
		}
		player.getInventory().setArmorContents(equipment);
	}

	@SuppressWarnings("deprecation")
	public ItemStack[] getItemsInHands()
	{
		Player player = getPlayer();
		ItemStack[] items = new ItemStack[2];
		try
		{
			//Both hands much be empty in order to be considered fist
			ItemStack rightItem = player.getInventory().getItemInMainHand();
			ItemStack leftItem = player.getInventory().getItemInOffHand();

			items[0] = rightItem;
			items[1] = leftItem;
		}
		catch (NoSuchMethodError exception)
		{
			//Run the pre 1.9 item getter
			ItemStack item = player.getItemInHand();

			items[0] = item;
			items[1] = null;
		}

		//Had to use old style methods to work across all versions
		if (items[0].getType().equals(Material.AIR))
			items[0] = null;
		if (items[1] == null || items[1].getType().equals(Material.AIR))
			items[1] = null;

		return items;
	}

	@SuppressWarnings("deprecation")
	public void dropItems()
	{
		Player player = getPlayer();
		ItemStack[] items = getItemsInHands();

		if (items[0] != null)
		{
			player.getWorld().dropItemNaturally(player.getLocation(), items[0]);

			try
			{
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			}
			catch (NoSuchMethodError exception)
			{
				player.getInventory().setItemInHand(new ItemStack(Material.AIR));
			}
		}
		else if (items[1] != null)
		{
			player.getWorld().dropItemNaturally(player.getLocation(), items[1]);

			player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
		}

		player.updateInventory();
	}

	public boolean showTrail()
	{
		Player player = getPlayer();
		Player target = Bukkit.getPlayer(targetId);
		if (target != null && player.getWorld().equals(target.getWorld()))
		{
			World world = player.getWorld();
			Vector start = player.getLocation().toVector();
			Vector direction = target.getLocation().toVector().subtract(start);
			double yOffset = config.getDouble("track.y-offset");
			int range = config.getInt("track.range");
			double targetDistance = player.getLocation().distance(Bukkit.getPlayer(targetId).getLocation());

			String particleName = config.getString("track.particle");
			int amount = config.getInt("track.particle-amount");

			BlockIterator iterator = new BlockIterator(world, start, direction, yOffset, range);

			int far = config.getInt("track.distances.far");
			int close = config.getInt("track.distances.close");
			int veryClose = config.getInt("track.distances.very-close");

			while(iterator.hasNext())
			{
				Color color;

				if (targetDistance >= far)
					color = Color.fromRGB(128, 128, 128);
				else if (targetDistance < far && targetDistance >= close)
					color = Color.fromRGB(0, 128, 0);
				else if (targetDistance < close && targetDistance >= veryClose)
					color = Color.fromRGB(128, 128, 0);
				else
					color = Color.fromRGB(128, 0, 0);

				Location nextLoc = iterator.next().getLocation();
				for (int times = 0; times < amount; times++)
				{
				    player.spawnParticle(Particle.valueOf(particleName), nextLoc, 0, color.getRed(), color.getGreen(), color.getBlue());
				}
			}
			return true;
		}

		return false;
	}

	public boolean isGoldImmune()
	{
		return level >= config.getInt("maturity.gold-immunity");
	}

	//Tracking function
	public boolean startTracking()
	{
		Player player = getPlayer();
		if (inWolfForm() && !isTracking() && player != null && this.targetId != null)
		{
			setTracking(true);
			return true;
		}
		
		return false;
	}
	public boolean stopTracking()
	{
		Player player = getPlayer();
		if (isTracking())
		{
			setTracking(false);

            player.removePotionEffect(PotionEffectType.BLINDNESS);
			try {
				player.removePotionEffect(PotionEffectType.SLOWNESS);
			}
			catch (NoSuchFieldError ex) {
				player.removePotionEffect(PotionEffectType.getByName("SLOW"));
			}

			return true;
		}
		
		return false;
	}
}
