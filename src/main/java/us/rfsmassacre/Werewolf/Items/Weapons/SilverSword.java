package us.rfsmassacre.Werewolf.Items.Weapons;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;

public class SilverSword extends WerewolfItem
{
	public SilverSword() 
	{
		super(Material.IRON_SWORD, WerewolfItemType.SILVER_SWORD);
		
		setRecipe(createRecipe());
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
		
		recipe.shape("ABQ", "AQB", "IAA");
		recipe.setIngredient('B', Material.BLAZE_POWDER);
		recipe.setIngredient('Q', Material.QUARTZ);
		recipe.setIngredient('I', Material.IRON_SWORD);
		
		return recipe;
	}
}
