package us.rfsmassacre.Werewolf.Items.Armor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

public abstract class WerewolfArmor extends WerewolfItem
{
	private final ConfigManager config;
	
	public WerewolfArmor(Material material, String name)
	{
		super(material, name);
		this.config = WerewolfPlugin.getConfigManager();

		List<String> newLore = new ArrayList<>();
		for (String line : getItemLore())
		{
			String finalLine = line.replace("+?%", "+" + getPurity() + "%");
			finalLine = finalLine.replace("+!%", "+" + getDefense() + "%");
			newLore.add(finalLine);
		}
		this.setItemLore(newLore);

		this.recipe = createRecipe();
	}
	
	protected int getValue(String key)
	{
		return config.getInt(key);
	}
	
	/*
	 * Use the getValue function in order to get the purity number.
	 */
	public abstract int getPurity();
	
	public abstract int getDefense();
}
