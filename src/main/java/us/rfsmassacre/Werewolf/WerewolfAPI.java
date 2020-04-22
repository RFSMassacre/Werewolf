package us.rfsmassacre.Werewolf;

import java.util.UUID;

import org.bukkit.entity.Player;

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
}
