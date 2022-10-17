package us.rfsmassacre.Werewolf.Managers;

import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinVariant;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.exception.SkinRequestException;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

import java.util.HashMap;
import java.util.Map;

public class SkinManager
{
	private final SkinsRestorerAPI api;

	private final ConfigManager config;
	private final MessageManager messages;
	private final WerewolfManager werewolves;

	private final Map<String, IProperty> skins;
	private final Map<String, String> oldSkinNames;
	private final Map<String, IProperty> oldSkinData;
	
	public SkinManager()
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();

		this.api = SkinsRestorerAPI.getApi();
		this.skins = new HashMap<>();
		this.oldSkinNames = new HashMap<>();
		this.oldSkinData = new HashMap<>();
		generateSkins(true);
	}

	public void applySkin(Werewolf werewolf)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			Player player = werewolf.getPlayer();
			String type = getType(werewolf);

			try
			{
				String oldSkin = api.getSkinName(player.getName());
				IProperty skinData = api.getSkinData(oldSkin);
				if (config.getBoolean("use-urls"))
				{
					api.applySkin(new PlayerWrapper(player), skins.get(type));
				}
				else
				{
					api.applySkin(new PlayerWrapper(player), getSkinName(werewolf));
				}
				if (oldSkin != null)
				{
					oldSkinNames.put(player.getName(), oldSkin);
					if (skinData != null)
					{
						oldSkinData.put(oldSkin, skinData);
					}
				}
			}
			catch (Exception exception)
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &c" + type +
						" skin cannot be set.");
			}
		});
	}

	public void removeSkin(Werewolf werewolf)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			try
			{
				Player player = werewolf.getPlayer();
				api.removeSkin(player.getName());
				String oldSkin = oldSkinNames.get(player.getName());
				if (oldSkin == null)
				{
					IProperty emptySkin = api.createPlatformProperty("textures", "", "");
					api.applySkin(new PlayerWrapper(player), emptySkin);
				}
				else
				{
					if (config.getBoolean("use-urls"))
					{
						IProperty skinData = oldSkinData.get(oldSkin);
						api.applySkin(new PlayerWrapper(player), skinData);
					}
					else
					{
						api.applySkin(new PlayerWrapper(player), oldSkin);
					}
				}
			}
			catch (Exception exception)
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &c" +
						werewolf.getPlayer().getName() + "'s skin cannot be cleared.");
			}
		});
	}

	public String getSkinName(Werewolf werewolf)
	{
		return getSkinName(werewolf.getType().toString(), werewolves.isAlpha(werewolf.getPlayer()));
	}
	public String getSkinName(String type)
	{
		return config.getString("skins." + type);
	}
	public String getSkinName(String type, boolean alpha)
	{
		if (alpha)
		{
			return config.getString("skins.Alpha");
		}

		return config.getString("skins." + type);
	}

	public String getType(Werewolf werewolf)
	{
		return werewolves.isAlpha(werewolf.getPlayer()) ? "Alpha" : werewolf.getType().toString();
	}
	public String getSkinType(String type)
	{
		String skinType = config.getString("skin-urls." + type + ".skin-type");
		return skinType == null ? "SLIM" : skinType;
	}
	public String getSkinURL(String type)
	{
		return config.getString("skin-urls." + type + ".url");
	}


	private void generateSkins()
	{
		if (config.getBoolean("use-urls"))
		{
			String alphaUrl = getSkinURL("Alpha");
			String witherfangUrl = getSkinURL("Witherfang");
			String silvermaneUrl = getSkinURL("Silvermane");
			String bloodmoonUrl = getSkinURL("Bloodmoon");

			String alphaType = getSkinType("Alpha");
			String witherfangType = getSkinType("Witherfang");
			String silvermaneType = getSkinType("Silvermane");
			String bloodmoonType = getSkinType("Bloodmoon");

			try
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &7" +
						"Loading skins... &cDo not use any skins until it is done loading!");

				IProperty alphaSkin = api.genSkinUrl(alphaUrl, SkinVariant.valueOf(alphaType));
				IProperty witherfangSkin = api.genSkinUrl(witherfangUrl, SkinVariant.valueOf(witherfangType));
				IProperty silvermaneSkin = api.genSkinUrl(silvermaneUrl, SkinVariant.valueOf(silvermaneType));
				IProperty bloodmoonSkin = api.genSkinUrl(bloodmoonUrl, SkinVariant.valueOf(bloodmoonType));

				skins.clear();
				skins.put("Alpha", alphaSkin);
				skins.put("Witherfang", witherfangSkin);
				skins.put("Silvermane", silvermaneSkin);
				skins.put("Bloodmoon", bloodmoonSkin);

				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &7" +
						"Skins have been loaded. You may now use skins.");
			}
			catch (SkinRequestException exception)
			{
				exception.printStackTrace();
			}
		}
	}
	public void generateSkins(boolean async)
	{
		if (async)
		{
			Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), (Runnable)this::generateSkins);
		}
		else
		{
			generateSkins();
		}
	}
}
