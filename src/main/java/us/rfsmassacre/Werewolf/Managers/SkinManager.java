package us.rfsmassacre.Werewolf.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.bukkit.skinfactory.SkinFactory;
import skinsrestorer.shared.exception.SkinRequestException;
import skinsrestorer.shared.storage.Config;
import skinsrestorer.shared.storage.Locale;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.C;
import skinsrestorer.shared.utils.MineSkinAPI;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class SkinManager
{
	private ConfigManager config;
	private WerewolfManager werewolves;

	private SkinFactory factory;
	private SkinStorage storage;
	private MineSkinAPI api;

	private int skinTaskId;
	
	public SkinManager()
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.factory = SkinsRestorer.getInstance().getFactory();
		this.storage = SkinsRestorer.getInstance().getSkinStorage();
		this.api = SkinsRestorer.getInstance().getMineSkinAPI();

		try
		{
			storage.getOrCreateSkinForPlayer(config.getString("skins.Alpha"));
			storage.getOrCreateSkinForPlayer(config.getString("skins.Witherfang"));
			storage.getOrCreateSkinForPlayer(config.getString("skins.Silvermane"));
			storage.getOrCreateSkinForPlayer(config.getString("skins.Bloodmoon"));
		}
		catch (SkinRequestException exception)
		{
			exception.printStackTrace();
		}
	}

	/*
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
	 */

	public void applySkinByName(Werewolf werewolf, boolean clear)
	{
		Bukkit.getScheduler().runTaskAsynchronously(SkinsRestorer.getInstance(), () ->
		{
			Player player = werewolf.getPlayer();
			String skinName = getSkinName(werewolf);
			if (clear)
			{
				skinName = storage.getDefaultSkinNameIfEnabled(player.getName(), true);
			}

			if (C.validUsername(skinName))
			{
				try
				{
				/*
				if (save)
				{
					this.plugin.getSkinStorage().setPlayerSkin(p.getName(), skin);
				}
				 */

					factory.applySkin(player, storage.getOrCreateSkinForPlayer(skinName));
				}
				catch (SkinRequestException exception)
				{
					player.sendMessage(exception.getReason());
					/*
					if (save)
					{
						this.plugin.getSkinStorage().setPlayerSkin(p.getName(), oldSkinName != null ? oldSkinName : p.getName());
					}
					 */
				}
			}
		});
	}
	public void applySkinByURL(Werewolf werewolf, boolean clear)
	{
		Bukkit.getScheduler().runTaskAsynchronously(SkinsRestorer.getInstance(), () ->
		{
			Player player = werewolf.getPlayer();
			String skinURL = getSkinURL(werewolf);

			if (C.validUrl(skinURL))
			{
				Bukkit.broadcastMessage(skinURL);

				try
				{
					storage.setSkinData(player.getName(), api.genSkin(skinURL));
					storage.setPlayerSkin(player.getName(), player.getName());
					factory.applySkin(player, storage.getSkinData(player.getName()));
				}
				catch (SkinRequestException exception)
				{
					player.sendMessage(exception.getReason());
				/*
				if (save)
				{
					this.plugin.getSkinStorage().setPlayerSkin(p.getName(), oldSkinName != null ? oldSkinName : p.getName());
				}
				 */
				}
				catch (Exception exception)
				{
					System.out.println("[SkinsRestorer] [ERROR] could not generate skin url:");
					exception.printStackTrace();
				/*
				if (save)
				{
					this.plugin.getSkinStorage().setPlayerSkin(p.getName(), oldSkinName != null ? oldSkinName : p.getName());
				}
				 */
				}
			}
		});
	}

	public void removeSkin(Werewolf werewolf)
	{
		Bukkit.getScheduler().runTaskAsynchronously(SkinsRestorer.getInstance(), () ->
		{
			Player player = werewolf.getPlayer();
			storage.removePlayerSkin(player.getName());
			applySkinByName(werewolf, true);
		});
	}

	/*
	public void endCycles()
	{
		//In case we need to stop the buff cycle for a reload
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(skinTaskId);
	}
	 */

	public String getSkinName(Werewolf werewolf)
	{
		String skinType = werewolves.isAlpha(werewolf.getPlayer()) ? "Alpha" : werewolf.getType().toString();
		return getSkinName(skinType);
	}
	public String getSkinName(String type)
	{
		String skinName = config.getString("skins." + type);

		try
		{
			return skinName;
		}
		catch (IllegalArgumentException exception)
		{
			return null;
		}
	}

	public String getType(Werewolf werewolf)
	{
		return werewolves.isAlpha(werewolf.getPlayer()) ? "Alpha" : werewolf.getType().toString();
	}
	public String getSkinURL(Werewolf werewolf)
	{
		return getSkinURL(getType(werewolf));
	}
	public String getSkinURL(String type)
	{
		return config.getString("urls." + type);
	}
}
