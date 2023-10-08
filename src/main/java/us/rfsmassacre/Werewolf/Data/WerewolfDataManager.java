package us.rfsmassacre.Werewolf.Data;

import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import us.rfsmassacre.HeavenLib.BaseManagers.YamlStorage;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfDataManager extends YamlStorage<Werewolf>
{
	public WerewolfDataManager(WerewolfPlugin instance) 
	{
		super(instance, "players");
	}

	@Override
	public Werewolf load(YamlConfiguration data)
	{
		String id = data.getString("uuid");
		if (id == null)
		{
			return null;
		}

		UUID playerId = UUID.fromString(id);
		Werewolf werewolf = new Werewolf(playerId);
		werewolf.setDisplayName(data.getString("display-name"));
		werewolf.setType(ClanType.fromString(data.getString("clan")));
		werewolf.setLevel(data.getInt("level"));
		werewolf.setIntent(data.getBoolean("intent"));
		werewolf.setWolfForm(data.getBoolean("wolf-form"));
		werewolf.setLastTransform(data.getLong("last-transform"));
		
		return werewolf;
	}
	
	@Override
	public YamlConfiguration save(Werewolf werewolf)
	{
		YamlConfiguration data = new YamlConfiguration();
		data.set("uuid", werewolf.getUUID().toString());
		data.set("display-name", werewolf.getDisplayName());
		data.set("clan", werewolf.getType().toString());
		data.set("level", werewolf.getLevel());
		data.set("intent", werewolf.hasIntent());
		data.set("wolf-form", werewolf.inWolfForm());
		data.set("last-transform", werewolf.getLastTransform());
		return data;
	}
}
