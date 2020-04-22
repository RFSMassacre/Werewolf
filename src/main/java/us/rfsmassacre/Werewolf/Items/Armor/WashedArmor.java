package us.rfsmassacre.Werewolf.Items.Armor;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class WashedArmor extends WerewolfArmor
{	
	public WashedArmor(Material material) 
	{
		super(material, WerewolfItemType.valueOf("WASHED_" + material.name().replace("DIAMOND_", "")));
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
		
		recipe.shape("BQB", "QAQ", "BQB");
		recipe.setIngredient('B', Material.BLAZE_POWDER);
		recipe.setIngredient('Q', Material.QUARTZ);
		recipe.setIngredient('A', getItem().getType());
		
		return recipe;
	}

	@Override
	public int getPurity() 
	{
		return getValue("purity.washed." + getItem().getType().name().toLowerCase().replace("_", "-"));
	}
	
	@Override
	public int getDefense()
	{
		return getValue("defense.washed." + getItem().getType().name().toLowerCase().replace("_", "-"));
	}
}
