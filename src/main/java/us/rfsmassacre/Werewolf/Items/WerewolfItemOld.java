package us.rfsmassacre.Werewolf.Items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Data.ItemDataManager;

public abstract class WerewolfItemOld implements Listener
{
	public enum WerewolfItemType
	{
		INFECTION_POTION("InfectionPotion", "§0§0§0§5§o"), 
		CURE_POTION("CurePotion", "§0§0§1§5§o"), 
		WOLFSBANE_POTION("WolfsBanePotion", "§0§0§2§5§o"),
		
		ASH("Ash", "§4§4§4§5§o"),
		
		SILVER_SWORD("SilverSword", "§1§0§0§5§o"),
		
		VAMPIRE_TRACKER("VampireTracker", "§2§1§0§5§o"),
		WEREWOLF_TRACKER("WerewolfTracker", "§2§1§1§5§o"),
		
		WASHED_HELMET("WashedHelmet", "§3§0§0§5§o"),
		WASHED_CHESTPLATE("WashedChestplate", "§4§0§0§5§o"),
		WASHED_LEGGINGS("WashedLeggings", "§5§0§0§5§o"),
		WASHED_BOOTS("WashedBoots", "§6§0§0§5§o"),
		
		PURIFIED_HELMET("PurifiedHelmet", "§3§1§0§5§o"),
		PURIFIED_CHESTPLATE("PurifiedChestplate", "§4§1§0§5§o"),
		PURIFIED_LEGGINGS("PurifiedLeggings", "§5§1§0§5§o"),
		PURIFIED_BOOTS("PurifiedBoots", "§6§1§0§5§o"),
		
		PURIFIER_SWORD("PurifierSword", "§4§0§0§5§o"),
		
		SALT("Salt", "§5§0§0§5§o"),
		FLARE("Flare", "§5§5§0§5§o"),
		GARLIC("Garlic", "§5§5§5§5§o"),
		
		STEEL_TRAP("SteelTrap", "§6§0§0§5§o");
		
		private String alias;
		private String id;
		
		WerewolfItemType(String alias, String id)
		{
			this.alias = alias;
			this.id = id;
		}
		
		public static WerewolfItemType fromString(String name)
		{
			for (WerewolfItemType type : WerewolfItemType.values())
			{
				if (name.equalsIgnoreCase(type.toString()) || name.equalsIgnoreCase(type.alias))
					return type;
			}
			
			return null;
		}
		public static WerewolfItemType fromID(String id)
		{
			for (WerewolfItemType type : WerewolfItemType.values())
			{
				if (type.id.equals(id))
					return type;
			}
			
			return null;
		}
	}
	
	private ItemDataManager itemData;
	
	private ItemStack item;
	private WerewolfItemType type;
	private Recipe recipe;
	
	private NamespacedKey key;
	
	public WerewolfItemOld(Material material, WerewolfItemType type)
	{
		setItemData(new ItemDataManager(WerewolfPlugin.getInstance()));
		
		setItem(new ItemStack(material));
		setType(type);
		
		setItemName(getItemData().getItemName(getType().toString()));
		setItemLore(getItemData().getItemLore(getType().toString()));
		
		try
		{
			setKey(new NamespacedKey(WerewolfPlugin.getInstance(), type.alias.toUpperCase()));
		}
		catch (NoClassDefFoundError exception)
		{
			//Do nothing, this means server is outdated.
		}
	}
	
	/*
	 * All items have a recipe, but have different kinds.
	 */
	protected abstract Recipe createRecipe();
	
	private void setItemData(ItemDataManager itemData)
	{
		this.itemData = itemData;
	}
	protected ItemDataManager getItemData()
	{
		return itemData;
	}
	
	protected void setItem(ItemStack item)
	{
		this.item = item;
	}
	public ItemStack getItem()
	{
		return item;
	}
	
	protected void setMaterial(Material material)
	{
		setItem(new ItemStack(material));
		
		setItemName(getItemData().getItemName(getType().toString()));
		setItemLore(getItemData().getItemLore(getType().toString()));
	}
	public Material getMaterial()
	{
		return item.getType();
	}
	
	private void setType(WerewolfItemType type)
	{
		this.type = type;
	}
	public WerewolfItemType getType()
	{
		return type;
	}
	
	public void setItemMeta(ItemMeta meta)
	{
		item.setItemMeta(meta);
	}
	public ItemMeta getItemMeta()
	{
		return item.getItemMeta();
	}
	
	public void setItemName(String displayName)
	{
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(displayName);
		setItemMeta(meta);
	}
	public String getItemName()
	{
		return item.hasItemMeta() ? item.getItemMeta().getDisplayName() : item.getType().toString();
	}
	
	public void setItemLore(List<String> displayLore)
	{
		//Write the ID of this item on the lore.
		String firstLine = type.id + displayLore.get(0);
		displayLore.set(0, firstLine);
		
		ItemMeta meta = getItemMeta();
		meta.setLore(displayLore);
		setItemMeta(meta);
	}
	public List<String> getItemLore()
	{
		return item.hasItemMeta() ? getItemMeta().getLore() : null;
	}
	
	public boolean equals(ItemStack item)
	{
		try
		{
			//Compares by writing the ID of the item at the first line
			//hidden by color codes.
			if (item != null && item.hasItemMeta())
			{
				for (String line : item.getItemMeta().getLore())
				{
					if (line.contains(type.id))
						return true;
				}
			}
			
			return false;
		}
		catch (NullPointerException exception)
		{
			return false;
		}
	}
	
	public static boolean hasId(ItemStack item)
	{
		try
		{
			//Compares by writing the ID of the item at the first line
			//hidden by color codes.
			if (item != null && item.hasItemMeta())
			{
				for (String line : item.getItemMeta().getLore())
				{
					for (WerewolfItemType itemType : WerewolfItemType.values())
					{
						if (line.contains(itemType.id))
							return true;
					}
				}
			}
			
			return false;
		}
		catch (NullPointerException exception)
		{
			return false;
		}
	}
	
	/*
	 * Uses preferred function for older versions of MC.
	 */
	@SuppressWarnings("deprecation")
	public boolean isHoldingItem(Player player, boolean bothHands)
	{
		ItemStack rightItem = null;
		ItemStack leftItem = null;
		
		try
		{
			rightItem = player.getInventory().getItemInMainHand();
			leftItem = player.getInventory().getItemInOffHand();
		}
		catch (NoSuchMethodError exception)
		{
			rightItem = player.getItemInHand();
		}
		
		if (bothHands)
			return equals(rightItem) || equals(leftItem);
		else
			return equals(rightItem);
	}
	public boolean hasItem(Player player)
	{
		for (ItemStack item : player.getInventory().getContents())
		{
			if (equals(item))
				return true;
		}
		
		return false;
	}
	
	/*
	 * Used to find whether player has or is holding general item 
	 */
	@SuppressWarnings("deprecation")
	public static boolean isHoldingItem(Material material, Player player, boolean bothHands)
	{
		ItemStack rightItem = null;
		ItemStack leftItem = null;
		
		try
		{
			rightItem = player.getInventory().getItemInMainHand();
			leftItem = player.getInventory().getItemInOffHand();
		}
		catch (NoSuchMethodError exception)
		{
			rightItem = player.getItemInHand();
		}
		
		if (bothHands)
			return material.equals(rightItem.getType()) || material.equals(leftItem.getType());
		else
			return material.equals(rightItem.getType());
	}
	public static boolean hasItem(Material material, Player player)
	{
		for (ItemStack item : player.getInventory().getContents())
		{
			if (material.equals(item.getType()))
				return true;
		}
		
		return false;
	}
	
	protected void setRecipe(Recipe recipe)
	{
		this.recipe = recipe;
	}
	public Recipe getRecipe()
	{
		return recipe;
	}

	protected NamespacedKey getKey() 
	{
		return key;
	}
	private void setKey(NamespacedKey key) 
	{
		this.key = key;
	}
}
