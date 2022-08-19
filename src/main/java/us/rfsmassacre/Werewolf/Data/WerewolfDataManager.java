package us.rfsmassacre.Werewolf.Data;

import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import us.rfsmassacre.HeavenLib.BaseManagers.DataManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfDataManager extends DataManager<Werewolf>
{
	public WerewolfDataManager(WerewolfPlugin instance) 
	{
		super(instance, "players");
	}

	@Override
	protected Werewolf loadData(YamlConfiguration data)
	{
		Werewolf werewolf = new Werewolf();
		werewolf.setPlayer(null);
		werewolf.setUUID(UUID.fromString(data.getString("uuid")));
		werewolf.setDisplayName(data.getString("display-name"));
		werewolf.setType(ClanType.fromString(data.getString("clan")));
		werewolf.setLevel(data.getInt("level"));
		werewolf.setIntent(data.getBoolean("intent"));
		werewolf.setWolfForm(data.getBoolean("wolf-form"));
		werewolf.setLastTransform(data.getLong("last-transform"));
		
		return werewolf;
	}
	
	@Override
	protected void storeData(Werewolf werewolf, YamlConfiguration data)
	{
		data.set("uuid", werewolf.getUUID().toString());
		data.set("display-name", werewolf.getDisplayName());
		data.set("clan", werewolf.getType().toString());
		data.set("level", werewolf.getLevel());
		data.set("intent", werewolf.hasIntent());
		data.set("wolf-form", werewolf.inWolfForm());
		data.set("last-transform", werewolf.getLastTransform());
	}
}
