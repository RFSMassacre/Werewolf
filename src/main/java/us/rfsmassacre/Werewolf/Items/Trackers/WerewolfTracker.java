package us.rfsmassacre.Werewolf.Items.Trackers;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class WerewolfTracker extends Tracker
{
	public WerewolfTracker() 
	{
		super(WerewolfItemType.WEREWOLF_TRACKER);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Recipe createRecipe()
	{
		ShapedRecipe recipe;
		
		try
		{
			recipe = new ShapedRecipe(getKey(), getItem());
		}
		catch (NoSuchMethodError exception)
		{
			recipe = new ShapedRecipe(getItem());
		}
		
		try
		{
			recipe.shape("RRR", "RCR", "RRR");
			recipe.setIngredient('R', Material.RABBIT_FOOT);
			recipe.setIngredient('C', Material.COMPASS);
			
			return recipe;
		}
		catch (NoSuchFieldError exception)
		{
			//If anything messes up just return no recipe
			return null;
		}
	}
}
