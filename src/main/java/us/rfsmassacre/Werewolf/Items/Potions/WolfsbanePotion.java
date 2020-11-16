package us.rfsmassacre.Werewolf.Items.Potions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionType;

import us.rfsmassacre.Werewolf.Managers.ItemManager;

@SuppressWarnings("deprecation")
public class WolfsbanePotion extends WerewolfPotion
{
	public WolfsbanePotion() 
	{
		super("WOLFSBANE_POTION", true, Color.RED, PotionType.INSTANT_HEAL);
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
		
		recipe.addIngredient(Material.GLASS_BOTTLE);
		recipe.addIngredient(Material.MILK_BUCKET);
		recipe.addIngredient(Material.CARROT);
		recipe.addIngredient(ItemManager.getCorrectMaterial("SULPHUR")); //Enum was changed in 1.13
		recipe.addIngredient(Material.CACTUS);
		
		return recipe;
	}
}
