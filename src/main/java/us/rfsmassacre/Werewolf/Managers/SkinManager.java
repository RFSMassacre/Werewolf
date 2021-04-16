package us.rfsmassacre.Werewolf.Managers;

import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.shared.exception.SkinRequestException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class SkinManager
{
	private ConfigManager config;
	private WerewolfManager werewolves;
	private MessageManager messages;

	private SkinsRestorerAPI api;
	
	public SkinManager()
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();

		this.api = SkinsRestorerAPI.getApi();
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

	public void applySkin(@Nonnull Werewolf werewolf)
	{
		Player player = werewolf.getPlayer();
		String type = getType(werewolf);
		String skinName = getSkinName(type);

		try
		{
			api.setSkin(player.getName(), skinName);
			api.applySkin(new PlayerWrapper(player));
		}
		catch (SkinRequestException exception)
		{
			messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> " + type + " skin cannot be set.");
		}
	}

	/*
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

					factory.applySkin(player, storage.getOrCreateSkinForPlayer(skinName));
				}
				catch (SkinRequestException exception)
				{
					player.sendMessage(exception.getReason());
				}
			}
		});
	}
	*/

	/*
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
				}
				if (save)
				{
					this.plugin.getSkinStorage().setPlayerSkin(p.getName(), oldSkinName != null ? oldSkinName : p.getName());
				}
				}
				catch (Exception exception)
				{
					System.out.println("[SkinsRestorer] [ERROR] could not generate skin url:");
					exception.printStackTrace();
				}
				if (save)
				{
					this.plugin.getSkinStorage().setPlayerSkin(p.getName(), oldSkinName != null ? oldSkinName : p.getName());
				}
				}
			}
		});
	}
	 */

	public void removeSkin(Werewolf werewolf)
	{
		Player player = werewolf.getPlayer();
		api.removeSkin(player.getName());
		api.applySkin(new PlayerWrapper(player));
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
