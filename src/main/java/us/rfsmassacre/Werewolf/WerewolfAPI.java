package us.rfsmassacre.Werewolf;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.Managers.ItemManager;

public class WerewolfAPI 
{
	/*
	 * Online Check for Race
	 */
	public static boolean isHuman(Player player)
	{
		return WerewolfPlugin.getWerewolfManager().isHuman(player);
	}
	public static boolean isVampire(Player player)
	{
		return WerewolfPlugin.getWerewolfManager().isVampire(player);
	}
	public static boolean isWerewolf(Player player)
	{
		return WerewolfPlugin.getWerewolfManager().isWerewolf(player);
	}
	public static boolean isAlpha(Player player)
	{
		return WerewolfPlugin.getWerewolfManager().isAlpha(player);
	}
	
	/*
	 * Offline Check for Werewolf
	 */
	public static boolean isWerewolf(UUID playerId)
	{
		return WerewolfPlugin.getWerewolfManager().isWerewolf(playerId);
	}
	public static boolean isAlpha(UUID playerId)
	{
		return WerewolfPlugin.getWerewolfManager().isAlpha(playerId);
	}

	/*
	 * Item Checks
	 */
	public static boolean isWerewolfItem(ItemStack item)
	{
		if (item == null || item.getType().equals(Material.AIR))
		{
			return false;
		}

		ItemManager items = WerewolfPlugin.getItemManager();
		WerewolfItem werewolfItem = items.getWerewolfItem(item);
		return werewolfItem != null;
	}
	public static boolean isWerewolfItem(ItemStack item, String name)
	{
		if (item == null || item.getType().equals(Material.AIR))
		{
			return false;
		}

		ItemManager items = WerewolfPlugin.getItemManager();
		return items.getWerewolfItem(item).getName().equals(name);
	}
	public static String getWerewolfItemID(ItemStack item)
	{
		if (item == null || item.getType().equals(Material.AIR))
		{
			return null;
		}

		ItemManager items = WerewolfPlugin.getItemManager();
		WerewolfItem werewolfItem = items.getWerewolfItem(item);
		if (werewolfItem == null)
		{
			return null;
		}

		return werewolfItem.getName();
	}
}
