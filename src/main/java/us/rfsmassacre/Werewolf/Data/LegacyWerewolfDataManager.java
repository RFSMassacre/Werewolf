package us.rfsmassacre.Werewolf.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import us.rfsmassacre.HeavenLib.BaseManagers.DataManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class LegacyWerewolfDataManager extends DataManager<List<Werewolf>>
{
	public LegacyWerewolfDataManager() 
	{
		super(WerewolfPlugin.getInstance(), "import");
	}

	@Override
	protected List<Werewolf> loadData(YamlConfiguration data)
	{
		List<Werewolf> oldWerewolves = new ArrayList<>();
		/*
		 * Do not import werewolf if anything in data is null
		 * This is because the original data is suspected to never
		 * delete data upon a werewolf being cured.
		 * Adding them would mean adding people who stopped being
		 * a werewolf and this would force it back.
		 *
		 */
		for (String key : data.getKeys(false))
		{
			Werewolf werewolf = new Werewolf(UUID.fromString(key));
			if (data.getString(key + ".PlayerName") == null)
				continue;
			werewolf.setDisplayName(data.getString(key + ".PlayerName"));
			werewolf.setIntent(false);
			if (data.getInt(key + ".Transformations") == 0)
				continue;
			werewolf.setLevel(data.getInt(key + ".Transformations"));
			werewolf.setWolfForm(false);
			werewolf.setLastTransform(System.currentTimeMillis());

			String clan = data.getString(key + ".Clan");
			if (clan == null)
			{
				continue;
			}

			switch (clan)
			{
				case "Potion":
					werewolf.setType(ClanType.WITHERFANG);
					break;
				case "WildBite":
					werewolf.setType(ClanType.BLOODMOON);
					break;
				case "WerewolfBite":
					werewolf.setType(ClanType.SILVERMANE);
					break;
				default:
					continue;
			}

			oldWerewolves.add(werewolf);
		}
		
		return oldWerewolves;
	}

	@Override
	protected void storeData(List<Werewolf> object, YamlConfiguration data)
	{
		//We are not storing anything in the legacy files
	}
}
