package us.rfsmassacre.Werewolf.Items.Armor;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class WashedArmor extends WerewolfArmor
{	
	public WashedArmor(Material material)
	{
		super(material, "WASHED_" + material.toString().replace("DIAMOND_", ""));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Recipe createRecipe() 
	{
		ShapedRecipe recipe;
		
		try
		{
			recipe = new ShapedRecipe(getKey(), getItemStack());
		}
		catch (NoSuchMethodError exception)
		{
			recipe = new ShapedRecipe(getItemStack());
		}
		
		recipe.shape("BQB", "QAQ", "BQB");
		recipe.setIngredient('B', Material.BLAZE_POWDER);
		recipe.setIngredient('Q', Material.QUARTZ);
		recipe.setIngredient('A', getItemStack().getType());
		
		return recipe;
	}

	@Override
	public int getPurity() 
	{
		return getValue("purity.washed." + getItemStack().getType().name().toLowerCase().replace("_", "-"));
	}
	
	@Override
	public int getDefense()
	{
		return getValue("bonus.washed." + getItemStack().getType().name().toLowerCase().replace("_", "-"));
	}
}
