package us.rfsmassacre.Werewolf.Items.Trackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;

public abstract class Tracker extends WerewolfItem
{
	public Tracker(String name)
	{
		super(Material.COMPASS, name);
		
		this.recipe = createRecipe();
	}
}
