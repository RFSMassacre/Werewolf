package us.rfsmassacre.Werewolf.Items.Trackers;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class VampireTracker extends Tracker
{
	public VampireTracker() 
	{
		super(WerewolfItemType.VAMPIRE_TRACKER);
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
			recipe.shape("GGG", "GCG", "GGG");
			recipe.setIngredient('G', Material.GHAST_TEAR);
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
