package us.rfsmassacre.Werewolf.Data;

import java.util.ArrayList;
import java.util.List;

import us.rfsmassacre.HeavenLib.BaseManagers.ResourceManager;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Items.WerewolfItem.WerewolfItemType;

public class ItemDataManager extends ResourceManager
{	
	public ItemDataManager(WerewolfPlugin instance) 
	{
		super(instance, "items.yml");
	}
	
	/*
	 * Get item configuration from file of werewolf items.
	 */
	public String getItemName(WerewolfItemType type)
	{
		return ChatManager.format(file.getString(type.toString() + ".name", defaultFile.getString(type.toString() + ".name")));
	}
	public ArrayList<String> getItemLore(WerewolfItemType type)
	{
		ArrayList<String> lore = new ArrayList<String>();
		
		List<String> fileLore = file.getStringList(type.toString() + ".lore");
		List<String> defaultLore = defaultFile.getStringList(type.toString() + ".lore");
		
		if (fileLore != null && !fileLore.isEmpty())
		{
			for (String line : fileLore)
			{
				lore.add(ChatManager.format(line));
			}
		}
		else
		{
			for (String line : defaultLore)
			{
				lore.add(ChatManager.format(line));
			}
		}
		
		return !lore.isEmpty() ? lore : null;
	}
}
