package us.rfsmassacre.Werewolf.Managers;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Items.Armor.Ash;
import us.rfsmassacre.Werewolf.Items.Armor.PurifiedArmor;
import us.rfsmassacre.Werewolf.Items.Armor.WashedArmor;
import us.rfsmassacre.Werewolf.Items.Armor.WerewolfArmor;
import us.rfsmassacre.Werewolf.Items.Potions.CurePotion;
import us.rfsmassacre.Werewolf.Items.Potions.InfectionPotion;
import us.rfsmassacre.Werewolf.Items.Potions.WolfsbanePotion;
import us.rfsmassacre.Werewolf.Items.Trackers.VampireTracker;
import us.rfsmassacre.Werewolf.Items.Trackers.WerewolfTracker;
import us.rfsmassacre.Werewolf.Items.Weapons.SilverSword;

public class ItemManager 
{
	private final WerewolfPlugin instance;
	private final ConfigManager config;
	
	private final Map<String, WerewolfItem> items;
	private int itemTaskId;
	private int armorTaskId;
	
	public ItemManager()
	{
		instance = WerewolfPlugin.getInstance();
		config = WerewolfPlugin.getConfigManager();
		
		items = new HashMap<>();
		
		reloadItems();
		loadRecipes();
		
		startItemUpdater();
		startArmorChecker();
	}
	
	private void reloadItems()
	{
		items.clear();
		
		items.put(new InfectionPotion().getName(), new InfectionPotion());
		items.put(new CurePotion().getName(), new CurePotion());
		items.put(new WolfsbanePotion().getName(), new WolfsbanePotion());
		
		items.put(new Ash().getName(), new Ash());
		
		items.put(new SilverSword().getName(), new SilverSword());
		
		items.put(new VampireTracker().getName(), new VampireTracker());
		items.put(new WerewolfTracker().getName(), new WerewolfTracker());
		
		items.put(new WashedArmor(Material.DIAMOND_HELMET).getName(), new WashedArmor(Material.DIAMOND_HELMET));
		items.put(new WashedArmor(Material.DIAMOND_CHESTPLATE).getName(), new WashedArmor(Material.DIAMOND_CHESTPLATE));
		items.put(new WashedArmor(Material.DIAMOND_LEGGINGS).getName(), new WashedArmor(Material.DIAMOND_LEGGINGS));
		items.put(new WashedArmor(Material.DIAMOND_BOOTS).getName(), new WashedArmor(Material.DIAMOND_BOOTS));
		
		items.put(new PurifiedArmor(Material.DIAMOND_HELMET).getName(), new PurifiedArmor(Material.DIAMOND_HELMET));
		items.put(new PurifiedArmor(Material.DIAMOND_CHESTPLATE).getName(), new PurifiedArmor(Material.DIAMOND_CHESTPLATE));
		items.put(new PurifiedArmor(Material.DIAMOND_LEGGINGS).getName(), new PurifiedArmor(Material.DIAMOND_LEGGINGS));
		items.put(new PurifiedArmor(Material.DIAMOND_BOOTS).getName(), new PurifiedArmor(Material.DIAMOND_BOOTS));
	}
	
	//Reload item data
	public void reloadRecipes()
	{
		unloadRecipes();
		WerewolfItem.reloadData();
		reloadItems();
		loadRecipes();
	}
	
	//Load Recipes
	public void loadRecipes()
	{
		ConfigManager config = WerewolfPlugin.getConfigManager();
		
		//Add recipes
		for (WerewolfItem item : getWerewolfItems())
		{
			String name = item.getName().toLowerCase().replace("_", "-");
			Recipe recipe = item.getRecipe();
			if (config.getBoolean("recipes." + name) && recipe != null)
			{
				if (instance.getServer().getRecipe(item.getKey()) == null)
				{
					instance.getServer().addRecipe(recipe);
				}
			}
		}
	}
	public void unloadRecipes()
	{
		Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
		while (iterator.hasNext())
		{
			Recipe recipe = iterator.next();
			if (WerewolfItem.isWerewolfItem(recipe.getResult()))
			{
				iterator.remove();
			}
		}
	}
	
	public WerewolfItem getWerewolfItem(String name)
	{
		return items.get(name);
	}
	public WerewolfItem getWerewolfItem(ItemStack item)
	{
		for (WerewolfItem werewolfItem : items.values())
		{
			if (werewolfItem.equals(item))
			{
				return werewolfItem;
			}
		}
		
		return null;
	}
	public Collection<WerewolfItem> getWerewolfItems()
	{
		return items.values();
	}
	
	
	public void startItemUpdater()
	{
		//Continuously drop any armor the werewolf might have on
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		itemTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), () ->
        {
			for (Player player : Bukkit.getOnlinePlayers())
			{
				boolean update = false;
				for (ItemStack item : player.getInventory().getContents())
				{
					if (item != null && item.hasItemMeta())
					{
						WerewolfItem werewolfItemOld = getWerewolfItem(item);
						ItemMeta meta = item.getItemMeta();
						if (meta == null)
						{
							continue;
						}

						if (werewolfItemOld != null)
						{
							if (!werewolfItemOld.getItemStack().getItemMeta().getLore().equals(meta))
							{
								update = true;
							}
							else
							{
								continue;
							}

							if (!werewolfItemOld.getItemLore().equals(meta.getLore()) &&
									!(werewolfItemOld instanceof SilverSword) &&
									!(werewolfItemOld instanceof WerewolfArmor))
							{
								meta.setLore(werewolfItemOld.getItemLore());
								item.setItemMeta(meta);
							}
						}
					}
				}

				if (update)
				{
					player.updateInventory();
				}
			}
        }, 0L, config.getInt("intervals.item-update"));
	}
	public void startArmorChecker()
	{
		//Continuously drop any armor the werewolf might have on
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		armorTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), () ->
        {
			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (!WerewolfPlugin.getWerewolfManager().isHuman(player))
				{
					boolean update = false; //In case this player needs to update his inventory
					ItemStack[] equipment = player.getInventory().getArmorContents();
					for (int slot = 0; slot < equipment.length; slot++)
					{
						if (getWerewolfItem(equipment[slot]) instanceof WerewolfArmor)
						{
							player.getWorld().dropItemNaturally(player.getLocation(), equipment[slot]);
							equipment[slot] = new ItemStack(Material.AIR);
							update = true;
						}
					}

					if (update)
					{
						player.getInventory().setArmorContents(equipment);
						player.updateInventory();
					}
				}
			}
        }, 0L, config.getInt("intervals.hunting-armor-checker"));
	}
	
	public void endCycles()
	{
		//In case we need to stop the buff cycle for a reload
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(itemTaskId);
		scheduler.cancelTask(armorTaskId);
	}
	
	/*
	 * Convert Material enums to their proper versions.
	 * 
	 * This will check if it's 1.13. If it's not, then it'll attempt to find the old name.
	 * Returns null if nothing is found.
	 */
	public static Material getCorrectMaterial(String name)
	{
		String version = WerewolfPlugin.getDependencyManager().getServerVersion();
		Material material = Material.getMaterial(name.toUpperCase());
		
		if (version.startsWith("1.13") || version.startsWith("1.14")
		|| version.startsWith("1.15") || version.startsWith("1.16")
		|| version.startsWith("1.17") || version.startsWith("1.18")
		|| version.startsWith("1.19") || version.startsWith("1.20")
		|| version.startsWith("1.21"))
		{
			if (material == null)
			{
				material = Material.getMaterial(name.toUpperCase(), true);
			}
		}
		
		return material;
	}

	public static Attribute getAttribute(String attributeName)
	{
		try
		{
			if (WerewolfPlugin.getDependencyManager().getServerVersion().startsWith("1.21"))
			{
				return Attribute.valueOf(attributeName.toUpperCase());
			}
			else
			{
				return Attribute.valueOf("GENERIC_" + attributeName.toUpperCase());
			}
		}
		catch (IllegalArgumentException exception)
		{
			return null;
		}
	}

	public static PotionEffectType getPotionEffectType(String potionType)
	{
		try
		{
			if (WerewolfPlugin.getDependencyManager().getServerVersion().startsWith("1.21"))
			{
				return PotionEffectType.getByName(potionType.toUpperCase());
			}

			switch (potionType.toUpperCase())
			{
				case "NAUSEA" ->
				{
					return PotionEffectType.getByName("CONFUSION");
				}
				case "INSTANT_HEALTH" ->
				{
					return PotionEffectType.getByName("HEAL");
				}
				case "SLOWNESS" ->
				{
					return PotionEffectType.getByName("SLOW");
				}
				default ->
				{
					return PotionEffectType.getByName(potionType.toUpperCase());
				}
			}
		}
		catch (IllegalArgumentException exception)
		{
			return null;
		}
	}
}
