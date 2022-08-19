package us.rfsmassacre.Werewolf;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.DependencyManager;
import us.rfsmassacre.HeavenLib.Managers.LocaleManager;

import us.rfsmassacre.Werewolf.Commands.WerewolfAdminCommand;
import us.rfsmassacre.Werewolf.Commands.WerewolfCommand;
import us.rfsmassacre.Werewolf.Data.LegacyAlphaDataManager;
import us.rfsmassacre.Werewolf.Data.LegacyWerewolfDataManager;
import us.rfsmassacre.Werewolf.Listeners.*;
import us.rfsmassacre.Werewolf.Managers.*;

public class WerewolfPlugin extends JavaPlugin
{
	private static WerewolfPlugin instance;
	
	private static ChatManager chat;
	private static ConfigManager config;
	private static LocaleManager locale;
	private static DependencyManager dependency;
	
	private static ClanManager clans;
	private static WerewolfManager werewolves;
	private static ItemManager items;
	private static MessageManager messages;
	private static MoonManager moons;
	private static EventManager events;
	
	private static LegacyWerewolfDataManager legacyWerewolfData;
	private static LegacyAlphaDataManager legacyAlphaData;

	private static Permission permissions;
	
	@Override
	public void onEnable()
	{
		instance = this;

		//Set up data folder
		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		//Set up library managers
		chat = new ChatManager(this);
		config = new ConfigManager(this);
		locale = new LocaleManager(this);
		dependency = new DependencyManager(this);

		//Initialize Werewolf managers
		messages = new MessageManager();
		clans = new ClanManager();
		werewolves = new WerewolfManager();
		items = new ItemManager();
		moons = new MoonManager();
		events = new EventManager();

		//Initialize legacy support
		legacyWerewolfData = new LegacyWerewolfDataManager();
		legacyAlphaData = new LegacyAlphaDataManager();

		//Prepare vault permission stuff
		RegisteredServiceProvider<Permission> provider =
				getServer().getServicesManager().getRegistration(Permission.class);
		if (provider != null)
		{
			if (config.getBoolean("group-permissions.enabled"))
			{
				permissions = provider.getProvider();
				if (!permissions.hasGroupSupport())
				{
					messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-groups");
					permissions = null;
				}
			}
		}
		else
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-vault");
		}

		//Initialize listeners
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new WerewolfInfectionListener(), this);
		getServer().getPluginManager().registerEvents(new WerewolfListener(), this);
		getServer().getPluginManager().registerEvents(new CraftingListener(), this);
		getServer().getPluginManager().registerEvents(new WerewolfHuntingListener(), this);
		getServer().getPluginManager().registerEvents(new PvPListener(), this);
		//Sets up the needed modules for skins
		if (dependency.hasPlugin("SkinsRestorer"))
		{
			getServer().getPluginManager().registerEvents(new SkinListener(), this);
		}
		else
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-skins");
		}

		//Fixes odd error with WorldGuard by making checking at this level before initializing.
		if (dependency.hasPlugin("WorldEdit") && dependency.hasPlugin("WorldGuard"))
		{
			getServer().getPluginManager().registerEvents(new RegionListener(), this);
		}
		else
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-regions");
		}

		//Fixes error at the start up of servers without Vampires.
		if (dependency.hasPlugin("Vampire"))
		{
			getServer().getPluginManager().registerEvents(new VampireInfectionListener(), this);
			getServer().getPluginManager().registerEvents(new VampireHuntingListener(), this);
		}
		else if (dependency.hasPlugin("VampireRevamp"))
		{
			getServer().getPluginManager().registerEvents(new VampireHuntingListener(), this);
		}
		else
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-vampire");
		}
		//getServer().getPluginManager().registerEvents(new OnLogin(this), this);

		//Register commands
		this.getCommand("werewolf").setExecutor(new WerewolfCommand());
		this.getCommand("werewolfadmin").setExecutor(new WerewolfAdminCommand());

		if (dependency.getServerVersion().startsWith("1.7"))
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.outdated-server");
		}
	}
	
	@Override
	public void onDisable()
	{
		//Save all data
		clans.storeClans();
		werewolves.storeWerewolves();
	}
	
	//Get the instance needed across this plugin
	public static WerewolfPlugin getInstance()
	{
		return instance;
	}

	/*
	 * User Group Toggle
	 */
	public static boolean updateGroup(Player player)
	{
		if (werewolves.isWerewolf(player.getUniqueId()))
		{
			return setGroup(player, true);
		}
		else
		{
			return setGroup(player, false);
		}
	}
	public static boolean setGroup(Player player, boolean add)
	{
		if (permissions != null)
		{
			String groupName = config.getString("group-permissions.group");
			if (add)
			{
				return permissions.playerAddGroup(null, player, groupName);
			}
			else
			{
				return permissions.playerRemoveGroup(null, player, groupName);
			}
		}

		return false;
	}
	
	/*
	 * HeavenLib Managers
	 */
	public static ChatManager getChatManager()
	{
		return chat;
	}
	public static ConfigManager getConfigManager()
	{
		return config;
	}
	public static LocaleManager getLocaleManager()
	{
		return locale;
	}
	public static DependencyManager getDependencyManager()
	{
		return dependency;
	}
	
	/*
	 * Werewolf Managers
	 */
	public static ClanManager getClanManager()
	{
		return clans;
	}
	public static WerewolfManager getWerewolfManager()
	{
		return werewolves;
	}
	public static ItemManager getItemManager()
	{
		return items;
	}
	public static MessageManager getMessageManager()
	{
		return messages;
	}
	public static MoonManager getMoonManager()
	{
		return moons;
	}
	public static EventManager getEventManager()
	{
		return events;
	}
	public static LegacyWerewolfDataManager getLegacyDataManager()
	{
		return legacyWerewolfData;
	}
	public static LegacyAlphaDataManager getLegacyAlphaDataManager()
	{
		return legacyAlphaData;
	}
}
