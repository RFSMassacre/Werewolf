package us.rfsmassacre.Werewolf.Items.Potions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import us.rfsmassacre.Werewolf.Managers.ItemManager;

@SuppressWarnings("deprecation")
public class CurePotion extends WerewolfPotion
{
	public CurePotion() 
	{
		super("CURE_POTION", false, Color.WHITE, PotionType.SWIFTNESS);
	}

	@Override
	protected Recipe createRecipe() 
	{
		ShapelessRecipe recipe;
		
		try
		{
			recipe = new ShapelessRecipe(getKey(), getItemStack());
		}
		catch (NoSuchMethodError exception)
		{
			recipe = new ShapelessRecipe(getItemStack());
		}
		
		recipe.addIngredient(ItemManager.getCorrectMaterial("RED_ROSE")); //Enum was changed in 1.13
		recipe.addIngredient(Material.GLASS_BOTTLE);
		recipe.addIngredient(Material.MILK_BUCKET);
		
		return recipe;
	}
}
