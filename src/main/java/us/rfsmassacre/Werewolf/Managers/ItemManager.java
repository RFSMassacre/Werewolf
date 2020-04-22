package us.rfsmassacre.Werewolf.Managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.Items.WerewolfItem.WerewolfItemType;
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
	private WerewolfPlugin instance;
	private ConfigManager config;
	
	private HashMap<WerewolfItemType, WerewolfItem> items;
	private int itemTaskId;
	private int armorTaskId;
	
	public ItemManager()
	{
		instance = WerewolfPlugin.getInstance();
		config = WerewolfPlugin.getConfigManager();
		
		items = new HashMap<WerewolfItemType, WerewolfItem>();
		
		reloadItems();
		loadRecipes();
		
		startItemUpdater();
		startArmorChecker();
	}
	
	private void reloadItems()
	{
		items.clear();
		
		items.put(WerewolfItemType.INFECTION_POTION, new InfectionPotion());
		items.put(WerewolfItemType.CURE_POTION, new CurePotion());
		items.put(WerewolfItemType.WOLFSBANE_POTION, new WolfsbanePotion());
		
		items.put(WerewolfItemType.ASH, new Ash());
		
		items.put(WerewolfItemType.SILVER_SWORD, new SilverSword());
		
		items.put(WerewolfItemType.VAMPIRE_TRACKER, new VampireTracker());
		items.put(WerewolfItemType.WEREWOLF_TRACKER, new WerewolfTracker());
		
		items.put(WerewolfItemType.WASHED_HELMET, new WashedArmor(Material.DIAMOND_HELMET));
		items.put(WerewolfItemType.WASHED_CHESTPLATE, new WashedArmor(Material.DIAMOND_CHESTPLATE));
		items.put(WerewolfItemType.WASHED_LEGGINGS, new WashedArmor(Material.DIAMOND_LEGGINGS));
		items.put(WerewolfItemType.WASHED_BOOTS, new WashedArmor(Material.DIAMOND_BOOTS));
		
		items.put(WerewolfItemType.PURIFIED_HELMET, new PurifiedArmor(Material.DIAMOND_HELMET));
		items.put(WerewolfItemType.PURIFIED_CHESTPLATE, new PurifiedArmor(Material.DIAMOND_CHESTPLATE));
		items.put(WerewolfItemType.PURIFIED_LEGGINGS, new PurifiedArmor(Material.DIAMOND_LEGGINGS));
		items.put(WerewolfItemType.PURIFIED_BOOTS, new PurifiedArmor(Material.DIAMOND_BOOTS));
	}
	
	//Reload item data
	public void reloadRecipes()
	{
		unloadRecipes();
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
			String typeName = item.getType().name().toLowerCase().replace("_", "-");
			Recipe recipe = item.getRecipe();
			
			if (config.getBoolean("recipes." + typeName) && recipe != null)
				instance.getServer().addRecipe(recipe);
		}
	}
	public void unloadRecipes()
	{
		HashSet<Recipe> allRecipes = new HashSet<Recipe>();
		Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
		while (iterator.hasNext())
		{
			Recipe recipe = iterator.next();
			allRecipes.add(recipe);
			if (WerewolfItem.hasId(recipe.getResult()))
			{
				allRecipes.remove(recipe);
			}
		}
		Bukkit.getServer().clearRecipes();
		for (Recipe vanillaRecipe : allRecipes)
		{
			Bukkit.getServer().addRecipe(vanillaRecipe);
		}
	}
	
	public WerewolfItem getWerewolfItem(WerewolfItemType type)
	{
		return items.get(type);
	}
	public WerewolfItem getWerewolfItem(ItemStack item)
	{
		for (WerewolfItem werewolfItem : items.values())
		{
			if (werewolfItem.equals(item))
				return werewolfItem;
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
		itemTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
        		for (Player player : Bukkit.getOnlinePlayers())
        		{
        			boolean update = false; //In case inventory needs to be manually updated
        			for (ItemStack item : player.getInventory().getContents())
        			{
        				if (item != null && item.hasItemMeta())
        				{
	        				WerewolfItem werewolfItem = getWerewolfItem(item);
	        				ItemMeta meta = item.getItemMeta();
	        				
	        				if (werewolfItem != null && !meta.getLore().equals(werewolfItem.getItemLore())
	        			    && !(werewolfItem instanceof SilverSword) && !(werewolfItem instanceof WerewolfArmor))
	        				{
	        					meta.setLore(werewolfItem.getItemLore());
	        					item.setItemMeta(meta);
	        				}
        				}
        			}
        			
        			if (update)
        				player.updateInventory();
        		}
            }
        }, 0L, config.getInt("intervals.item-update"));
	}
	public void startArmorChecker()
	{
		//Continuously drop any armor the werewolf might have on
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		itemTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
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
		Material material = Material.getMaterial(name);
		
		if (version.startsWith("1.13") || version.startsWith("1.14")
		|| version.startsWith("1.15"))
		{
			if (material == null)
				material = Material.getMaterial(name, true);
		}
		
		return material;
	}
}
