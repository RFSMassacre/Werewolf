package us.rfsmassacre.Werewolf.Managers;

import java.util.UUID;

import eu.blackfire62.MySkin.Shared.SkinCache;
import eu.blackfire62.MySkin.Shared.SkinHandler;
import eu.blackfire62.MySkin.Shared.SkinProperty;
import eu.blackfire62.MySkin.Shared.Util.MojangAPI;
import eu.blackfire62.MySkin.Shared.Util.MojangAPIException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class SkinManager
{
	private ConfigManager config;
	private WerewolfManager werewolves;

	private SkinCache cache;
	private SkinHandler handler;

	private int skinTaskId;
	
	public SkinManager()
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.cache = WerewolfPlugin.getSkinCache();
		this.handler = WerewolfPlugin.getSkinHandler();

		//Save profiles
        try
        {
            SkinProperty alphaProperty = MojangAPI.getSkinProperty(getSkinUUID("Alpha"));
            SkinProperty wfProperty = MojangAPI.getSkinProperty(getSkinUUID("Witherfang"));
            SkinProperty smProperty = MojangAPI.getSkinProperty(getSkinUUID("Silvermane"));
            SkinProperty bmProperty = MojangAPI.getSkinProperty(getSkinUUID("Bloodmoon"));
            SkinProperty defaultProperty = MojangAPI.getSkinProperty(getSkinUUID("Default"));

            cache.saveSkinProperty(getSkinUUID("Alpha"), alphaProperty);
            cache.saveSkinProperty(getSkinUUID("Witherfang"), wfProperty);
            cache.saveSkinProperty(getSkinUUID("Silvermane"), smProperty);
            cache.saveSkinProperty(getSkinUUID("Bloodmoon"), bmProperty);
            cache.saveSkinProperty(getSkinUUID("Default"), defaultProperty);
        }
        catch (MojangAPIException exception)
        {
            //Do nothing
        }

		//Save UUIDs
		cache.saveUUID("WerewolfAlpha", getSkinUUID("Alpha"));
        cache.saveUUID("WF_Werewolf", getSkinUUID("Witherfang"));
        cache.saveUUID("SM_Werewolf", getSkinUUID("Silvermane"));
        cache.saveUUID("BM_Werewolf", getSkinUUID("Bloodmoon"));
        cache.saveUUID("Default", getSkinUUID("Default"));
	}
	
	public void startSkinChecker()
	{
		//Correct skins of werewolves
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		skinTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), new Runnable() 
        {
            public void run() 
            {
    			for (Werewolf werewolf : werewolves.getOnlineWerewolves())
    			{
					if (werewolf.inWolfForm())
					{
						//Updates skin to the correct one
						if (!usingSkin(werewolf))
						{
							applySkin(werewolf);
						}
					}
					else
					{
						if (usingSkin(werewolf))
						{
							removeSkin(werewolf);
						}
					}
    			}
            }
        }, 0L, config.getInt("intervals.werewolf-skins"));
	}

	public boolean applySkin(Werewolf werewolf)
	{
		SkinProperty property = cache.loadSkinProperty(getSkinUUID(werewolf));
		SkinProperty original = handler.getSkinProperty(werewolf.getPlayer());
		if (original == null)
		{
			original = cache.loadSkinProperty(getSkinUUID("Default"));
		}
		cache.saveSkinProperty(werewolf.getUUID(), original);

		handler.setSkinProperty(werewolf.getPlayer(), property);
		handler.update(werewolf.getPlayer());

		if (!usingSkin(werewolf))
		{
			return false;
		}

		return true;

	}
	public boolean removeSkin(Werewolf werewolf)
	{
		SkinProperty property = cache.loadSkinProperty(werewolf.getUUID());
		handler.setSkinProperty(werewolf.getPlayer(), property);
		handler.update(werewolf.getPlayer());
		cache.resetSkinOfPlayer(werewolf.getUUID());

		if (usingSkin(werewolf))
		{
			return false;
		}

		return true;
	}
	public boolean usingSkin(Werewolf werewolf)
	{
		UUID customId = cache.loadSkinOfPlayer(werewolf.getUUID());
		UUID skinId = getSkinUUID(werewolf);

		if (customId != null && skinId != null)
		{
			return customId.equals(skinId);
		}

		return false;
	}
	
	public void endCycles()
	{
		//In case we need to stop the buff cycle for a reload
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(skinTaskId);
	}

	public UUID getSkinUUID(Werewolf werewolf)
	{
		String skinType = werewolves.isAlpha(werewolf.getPlayer()) ? "Alpha" : werewolf.getType().toString();
		return getSkinUUID(skinType);
	}
	public UUID getSkinUUID(String type)
	{
		String skinId = config.getString("skins." + type);

		try
		{
			return UUID.fromString(skinId);
		}
		catch (IllegalArgumentException exception)
		{
			return null;
		}
	}
}
