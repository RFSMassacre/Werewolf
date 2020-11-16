package us.rfsmassacre.Werewolf.Items.Armor;

import org.bukkit.inventory.Recipe;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.Managers.ItemManager;

public class Ash extends WerewolfItem
{
	public Ash() 
	{
		super(ItemManager.getCorrectMaterial("SULPHUR"), "ASH"); //Enum was changed in 1.13
	}

	@Override
	protected Recipe createRecipe() 
	{
		//This item has no recipe. Intended to be a useless item.
		return null;
	}
}
