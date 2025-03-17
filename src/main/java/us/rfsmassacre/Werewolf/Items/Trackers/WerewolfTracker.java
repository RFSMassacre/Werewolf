package us.rfsmassacre.Werewolf.Items.Trackers;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class WerewolfTracker extends Tracker
{
	public WerewolfTracker() 
	{
		super("WEREWOLF_TRACKER");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Recipe createRecipe()
	{
		ShapedRecipe recipe;
		
		try
		{
			recipe = new ShapedRecipe(key, getItemStack());
		}
		catch (NoSuchMethodError exception)
		{
			recipe = new ShapedRecipe(getItemStack());
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
