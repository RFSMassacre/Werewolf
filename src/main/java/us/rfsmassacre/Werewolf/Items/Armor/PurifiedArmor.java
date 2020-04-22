package us.rfsmassacre.Werewolf.Items.Armor;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;

public class PurifiedArmor extends WerewolfArmor
{
	public PurifiedArmor(Material material) 
	{	
		super(material, WerewolfItemType.valueOf("PURIFIED_" + material.name().replace("DIAMOND_", "")));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Recipe createRecipe() 
	{
		FurnaceRecipe recipe = new FurnaceRecipe(getItem(), getItem().getType());
		
		try
		{
			recipe.setExperience(10.0F);
		}
		catch (NoSuchMethodError exception)
		{
			//Do nothing since this just means it's 1.8
		}
		
		return recipe;
	}

	@Override
	public int getPurity() 
	{
		return getValue("purity.purified." + getItem().getType().name().toLowerCase().replace("_", "-"));
	}
	
	@Override
	public int getDefense()
	{
		return getValue("defense.purified." + getItem().getType().name().toLowerCase().replace("_", "-"));
	}
}
