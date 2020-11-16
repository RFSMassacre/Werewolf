package us.rfsmassacre.Werewolf.Items.Armor;

import java.util.ArrayList;

import org.bukkit.Material;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

public abstract class WerewolfArmor extends WerewolfItem
{
	private ConfigManager config;
	
	public WerewolfArmor(Material material, String name)
	{
		super(material, name);
		config = WerewolfPlugin.getConfigManager();
		
		ArrayList<String> lore = new ArrayList<String>();
		for (String line : getItemLore())
		{
			lore.add(line.replace("+?%", "+" + Integer.toString(getPurity()) + "%").replace("+!%", "+" + Integer.toString(getDefense()) + "%"));
		}
		this.setItemLore(lore);
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
