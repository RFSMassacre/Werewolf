package us.rfsmassacre.Werewolf;

import eu.blackfire62.MySkin.Bukkit.Listener.OnLogin;
import eu.blackfire62.MySkin.Bukkit.SkinHandler.*;
import eu.blackfire62.MySkin.Shared.FileSkinCache;
import eu.blackfire62.MySkin.Shared.SkinCache;
import eu.blackfire62.MySkin.Shared.SkinHandler;
import eu.blackfire62.MySkin.Shared.Util.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
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
import us.rfsmassacre.Werewolf.Origin.Clan;

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

	private static SkinHandler skinHandler;
	private static SkinCache skinCache;
	
	private static LegacyWerewolfDataManager legacyWerewolfData;
	private static LegacyAlphaDataManager legacyAlphaData;
	
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
		
		//Initialize listeners
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new WerewolfInfectionListener(), this);
		getServer().getPluginManager().registerEvents(new WerewolfListener(), this);
		getServer().getPluginManager().registerEvents(new CraftingListener(), this);
		getServer().getPluginManager().registerEvents(new WerewolfHuntingListener(), this);
		getServer().getPluginManager().registerEvents(new PvPListener(), this);

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
		else
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-vampire");
		}
		
		//Sets up the needed modules for skins
		try
		{
			String version = dependency.getServerVersion();
			if (version.startsWith("1.15"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_15_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.14"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_14_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (dependency.betweenVersions("1.13.1", "1.13.2"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_13_R2.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.13"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_13_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.12"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_12_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.11"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_11_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.10"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_10_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.9.4"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_9_R2.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (dependency.betweenVersions("1.9.0", "1.9.3"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_9_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (dependency.betweenVersions("1.8.4", "1.8.8"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_8_R3.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (version.startsWith("1.8.3"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_8_R2.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else if (dependency.betweenVersions("1.8.0", "1.8.2"))
			{
				skinHandler = (SkinHandler)Class.forName(SkinHandler_v1_8_R1.class.getPackage().getName() + ".SkinHandler_" + Reflect.serverVersion).getConstructor(((this)).getClass()).newInstance(new Object[]{this});
				skinCache = new FileSkinCache(this.getDataFolder());
				getServer().getPluginManager().registerEvents(new SkinListener(), this);
			}
			else
			{
				messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-skins");
			}
		}
		catch (Exception e)
		{
			messages.sendWolfLocale(Bukkit.getConsoleSender(), "invalid.no-skins");
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
	public static SkinHandler getSkinHandler()
	{
		return skinHandler;
	}
	public static SkinCache getSkinCache()
	{
		return skinCache;
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
