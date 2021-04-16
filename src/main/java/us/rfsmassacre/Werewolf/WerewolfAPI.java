package us.rfsmassacre.Werewolf;

import java.util.UUID;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

		NBTItem otherItem = new NBTItem(item);
		NBTCompound compound = otherItem.getCompound("WerewolfPlugin");
		if (compound == null)
		{
			return false;
		}

		String value = compound.getString("IID");
		return value != null;
	}
	public static boolean isWerewolfItem(ItemStack item, String name)
	{
		if (item == null || item.getType().equals(Material.AIR))
		{
			return false;
		}

		NBTItem otherItem = new NBTItem(item);
		NBTCompound compound = otherItem.getCompound("WerewolfPlugin");
		if (compound == null)
		{
			return false;
		}

		String value = compound.getString("IID");
		return value.equalsIgnoreCase(name);
	}
	public static String getWerewolfItemID(ItemStack item)
	{
		if (item == null || item.getType().equals(Material.AIR))
		{
			return null;
		}

		NBTItem otherItem = new NBTItem(item);
		NBTCompound compound = otherItem.getCompound("WerewolfPlugin");
		if (compound == null)
		{
			return null;
		}

		return compound.getString("IID");
	}
}
