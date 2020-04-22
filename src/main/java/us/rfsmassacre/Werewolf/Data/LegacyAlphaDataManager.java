package us.rfsmassacre.Werewolf.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import us.rfsmassacre.HeavenLib.BaseManagers.DataManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class LegacyAlphaDataManager extends DataManager
{
	public LegacyAlphaDataManager() 
	{
		super(WerewolfPlugin.getInstance(), "import");
	}

	@Override
	protected ArrayList<UUID> loadData(YamlConfiguration data) throws IOException 
	{
		ArrayList<UUID> alphaWerewolves = new ArrayList<UUID>();
		if (data.getKeys(false).size() > 0)
		{
			for (String key : data.getKeys(false))
			{
				String alphaId = data.getString(key + ".Alpha");
				if (alphaId != null)
				{
					alphaWerewolves.add(UUID.fromString(alphaId));
				}
			}
		}
		
		return alphaWerewolves;
	}

	@Override
	protected void storeData(Object object, YamlConfiguration data) throws IOException 
	{
		//We are not storing anything in the legacy files
	}
}
