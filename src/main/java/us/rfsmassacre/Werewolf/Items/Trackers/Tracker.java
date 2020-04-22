package us.rfsmassacre.Werewolf.Items.Trackers;

import org.bukkit.Material;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;

public abstract class Tracker extends WerewolfItem
{
	public Tracker(WerewolfItemType type) 
	{
		super(Material.COMPASS, type);
		
		setRecipe(createRecipe());
	}
}
