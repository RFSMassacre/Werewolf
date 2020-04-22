package us.rfsmassacre.Werewolf.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import us.rfsmassacre.HeavenLib.BaseManagers.DataManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class ClanDataManager extends DataManager
{
	public ClanDataManager(WerewolfPlugin instance) 
	{
		super(instance, "clans");
	}

	@Override
	protected Clan loadData(YamlConfiguration data) throws IOException
	{
		Clan clan = new Clan();
		clan.setType(ClanType.fromString(data.getString("clan-type")));
		
		if (data.getString("alpha-id") != null)
		{
			try
			{
				clan.setAlphaId(UUID.fromString(data.getString("alpha-id")));
			}
			catch (IllegalArgumentException exception)
			{
				//Do nothing
			}
		}
		
		if (data.getStringList("member-ids") != null)
		{
			for (String stringId : data.getStringList("member-ids"))
			{
				try
				{
					clan.addMemberId(UUID.fromString(stringId));
				}
				catch (IllegalArgumentException exception)
				{
					//Skip to the next thing on the list
					continue;
				}
			}
		}
		
		return clan;
	}

	@Override
	protected void storeData(Object object, YamlConfiguration data) throws IOException 
	{
		//Cancel if it's not a clan object
		if (!(object instanceof Clan))
			throw new IOException();
		
		Clan clan = (Clan)object;
		data.set("clan-type", clan.getType().toString());
		
		if (clan.getAlphaId() != null)
			data.set("alpha-id", clan.getAlphaId().toString());
		
		if (clan.getMemberIds() != null)
		{
			ArrayList<String> stringIds = new ArrayList<String>();
			for (UUID memberId : clan.getMemberIds())
			{
				stringIds.add(memberId.toString());
			}
			data.set("member-ids", stringIds);
		}
	}

}
