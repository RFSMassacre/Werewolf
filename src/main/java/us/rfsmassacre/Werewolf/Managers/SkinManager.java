package us.rfsmassacre.Werewolf.Managers;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.MineSkinAPI;
import net.skinsrestorer.api.connections.MojangAPI;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.*;
import net.skinsrestorer.api.storage.PlayerStorage;
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
	private final Map<UUID, SkinProperty> oldSkins;
	
	public SkinManager()
	{
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();

		this.api = SkinsRestorerProvider.get();
		this.skins = new HashMap<>();
		this.oldSkins = new HashMap<>();
		generateSkins(true);
	}

	public void applySkin(Werewolf werewolf)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			Player player = werewolf.getPlayer();
			String type = getType(werewolf);
			PlayerStorage storage = api.getPlayerStorage();
			SkinApplier<Player> applier = api.getSkinApplier(Player.class);
			try
			{
				Optional<SkinProperty> save = storage.getSkinForPlayer(player.getUniqueId(), player.getName());
				save.ifPresent(skinProperty -> oldSkins.put(player.getUniqueId(), skinProperty));
				applier.applySkin(player, skins.get(type));
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
				SkinApplier<Player> applier = api.getSkinApplier(Player.class);
				SkinProperty oldSkin = oldSkins.get(player.getUniqueId());
				applier.applySkin(player, oldSkin);
			}
			catch (Exception exception)
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &f" +
						werewolf.getPlayer().getName() + "&c's skin cannot be cleared.");
			}
		});
	}

	public String getSkinName(String type)
	{
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
			try
			{
				String alphaUrl = getSkinURL("Alpha");
				String witherfangUrl = getSkinURL("Witherfang");
				String silvermaneUrl = getSkinURL("Silvermane");
				String bloodmoonUrl = getSkinURL("Bloodmoon");

				String alphaType = getSkinType("Alpha");
				String witherfangType = getSkinType("Witherfang");
				String silvermaneType = getSkinType("Silvermane");
				String bloodmoonType = getSkinType("Bloodmoon");

				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &7" +
						"Loading skins... &cDo not use any skins until it is done loading!");

				MineSkinAPI skinApi = api.getMineSkinAPI();
				SkinProperty alphaSkin = skinApi.genSkin(alphaUrl, SkinVariant.valueOf(alphaType)).getProperty();
				SkinProperty witherfangSkin = skinApi.genSkin(witherfangUrl,
						SkinVariant.valueOf(witherfangType)).getProperty();
				SkinProperty silvermaneSkin = skinApi.genSkin(silvermaneUrl,
						SkinVariant.valueOf(silvermaneType)).getProperty();
				SkinProperty bloodmoonSkin = skinApi.genSkin(bloodmoonUrl,
						SkinVariant.valueOf(bloodmoonType)).getProperty();

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
		else
		{
			String alphaName = getSkinName("Alpha");
			String witherfangName = getSkinName("Witherfang");
			String silvermaneName = getSkinName("Silvermane");
			String bloodmoonName = getSkinName("Bloodmoon");

			try
			{
				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &7" +
						"Loading skins... &cDo not use any skins until it is done loading!");

				MojangAPI mojangApi = api.getMojangAPI();
				Optional<MojangSkinDataResult> alphaData = mojangApi.getSkin(alphaName);
				alphaData.ifPresent(inputDataResult -> skins.put("Alpha", inputDataResult.getSkinProperty()));
				Optional<MojangSkinDataResult> witherfangData = mojangApi.getSkin(witherfangName);
				witherfangData.ifPresent(inputDataResult -> skins.put("Witherfang", inputDataResult.getSkinProperty()));
				Optional<MojangSkinDataResult> silvermaneData = mojangApi.getSkin(silvermaneName);
				silvermaneData.ifPresent(inputDataResult -> skins.put("Silvermane", inputDataResult.getSkinProperty()));
				Optional<MojangSkinDataResult> bloodmoonData = mojangApi.getSkin(bloodmoonName);
				bloodmoonData.ifPresent(inputDataResult -> skins.put("Bloodmoon", inputDataResult.getSkinProperty()));

				messages.sendMessage(Bukkit.getConsoleSender(), "&6&lWerewolf &7&l> &7" +
						"Skins have been loaded. You may now use skins.");
			}
			catch (DataRequestException exception)
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
