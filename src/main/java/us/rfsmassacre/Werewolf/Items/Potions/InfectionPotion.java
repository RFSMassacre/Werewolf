package us.rfsmassacre.Werewolf.Items.Potions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionType;

@SuppressWarnings("deprecation")
public class InfectionPotion extends WerewolfPotion
{
	public InfectionPotion() 
	{
		super(WerewolfItemType.INFECTION_POTION, false, Color.ORANGE, PotionType.FIRE_RESISTANCE);
	}

	@Override
	protected Recipe createRecipe() 
	{
		ShapelessRecipe recipe;
		
		try
		{
			recipe = new ShapelessRecipe(getKey(), getItem());
		}
		catch (NoSuchMethodError exception)
		{
			recipe = new ShapelessRecipe(getItem());
		}
		
		recipe.addIngredient(Material.SLIME_BALL);
		recipe.addIngredient(Material.LEATHER);
		recipe.addIngredient(Material.BLAZE_POWDER);
		recipe.addIngredient(Material.GHAST_TEAR);
		recipe.addIngredient(Material.GLASS_BOTTLE);
		
		return recipe;
	}
}
