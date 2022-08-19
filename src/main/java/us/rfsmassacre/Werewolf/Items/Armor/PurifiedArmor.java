package us.rfsmassacre.Werewolf.Items.Armor;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.List;

public class PurifiedArmor extends WerewolfArmor
{
	public PurifiedArmor(Material material)
	{	
		super(material, "PURIFIED_" + material.toString().replace("DIAMOND_", ""));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Recipe createRecipe() 
	{
		FurnaceRecipe recipe = new FurnaceRecipe(getItemStack(), getItemStack().getType());
		
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
		return getValue("purity.purified." + getItemStack().getType().name().toLowerCase().replace("_", "-"));
	}
	
	@Override
	public int getDefense()
	{
		return getValue("bonus.purified." + getItemStack().getType().name().toLowerCase().replace("_", "-"));
	}
}
