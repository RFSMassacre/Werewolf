package us.rfsmassacre.Werewolf.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import us.rfsmassacre.HeavenLib.BaseManagers.DataManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class LegacyAlphaDataManager extends DataManager<List<UUID>>
{
	public LegacyAlphaDataManager() 
	{
		super(WerewolfPlugin.getInstance(), "import");
	}

	@Override
	protected List<UUID> loadData(YamlConfiguration data)
	{
		List<UUID> alphaWerewolves = new ArrayList<>();
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
	protected void storeData(List<UUID> object, YamlConfiguration data)
	{
		//We are not storing anything in the legacy files
	}
}
