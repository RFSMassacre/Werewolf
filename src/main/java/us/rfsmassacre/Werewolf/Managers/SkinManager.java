package us.rfsmassacre.Werewolf.Managers;

import net.skinsrestorer.api.connections.model.MineSkinResponse;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.SkinIdentifier;
import net.skinsrestorer.api.property.SkinVariant;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SkinManager
{
	private final SkinsRestorer api;

	private final ConfigManager config;
	private final MessageManager messages;
	private final WerewolfManager werewolves;

	private final Map<String, SkinProperty> skins;
	private final Map<UUID, SkinProperty> oldSkinProperty;
	
	public SkinManager()
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();

		this.api = SkinsRestorerProvider.get();
		this.skins = new HashMap<>();
		this.oldSkinProperty = new HashMap<>();
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
				Optional<SkinProperty> skinData = api.getPlayerStorage().getSkinOfPlayer(player.getUniqueId());
				if (skinData.isPresent())
				{
					oldSkinProperty.put(player.getUniqueId(), skinData.get());
					api.getPlayerStorage().removeSkinIdOfPlayer(player.getUniqueId());
				}

				if (config.getBoolean("use-urls"))
				{
					api.getSkinApplier(Player.class).applySkin(player, skins.get(type));
				}
				else
				{
					api.getSkinApplier(Player.class).applySkin(player, SkinIdentifier.ofCustom(getSkinName(werewolf)));
				}
			}
			catch (Exception exception)
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &c" + type +
						" skin cannot be set on &f" + player.getName() + "&c.");
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
				SkinProperty skinProp = oldSkinProperty.get(player.getUniqueId());
				if (skinProp == null)
				{
					api.getPlayerStorage().removeSkinIdOfPlayer(player.getUniqueId());
				}
				else
				{
					api.getSkinApplier(Player.class).applySkin(player, skinProp);
				}
			}
			catch (Exception exception)
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &f" +
						werewolf.getPlayer().getName() + "&c's skin cannot be cleared.");
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

				MineSkinResponse alphaSkin = api.getMineSkinAPI().genSkin(alphaUrl, SkinVariant.valueOf(alphaType));
				MineSkinResponse witherfangSkin = api.getMineSkinAPI().genSkin(witherfangUrl, SkinVariant.valueOf(witherfangType));
				MineSkinResponse silvermaneSkin = api.getMineSkinAPI().genSkin(silvermaneUrl, SkinVariant.valueOf(silvermaneType));
				MineSkinResponse bloodmoonSkin = api.getMineSkinAPI().genSkin(bloodmoonUrl, SkinVariant.valueOf(bloodmoonType));

				skins.clear();
				skins.put("Alpha", alphaSkin.getProperty());
				skins.put("Witherfang", witherfangSkin.getProperty());
				skins.put("Silvermane", silvermaneSkin.getProperty());
				skins.put("Bloodmoon", bloodmoonSkin.getProperty());

				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &7" +
						"Skins have been loaded. You may now use skins.");
			}
			catch (MineSkinException | DataRequestException exception)
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
