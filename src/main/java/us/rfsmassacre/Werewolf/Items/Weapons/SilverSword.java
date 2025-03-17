package us.rfsmassacre.Werewolf.Items.Weapons;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import us.rfsmassacre.HeavenLib.Items.HeavenItem;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;

import java.util.ArrayList;

public class SilverSword extends WerewolfItem
{
	public SilverSword() 
	{
		super(Material.IRON_SWORD, "SILVER_SWORD");
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
		
		recipe.shape("ABQ", "AQB", "IAA");
		recipe.setIngredient('B', Material.BLAZE_POWDER);
		recipe.setIngredient('Q', Material.QUARTZ);
		recipe.setIngredient('I', Material.IRON_SWORD);
		
		return recipe;
	}
}
