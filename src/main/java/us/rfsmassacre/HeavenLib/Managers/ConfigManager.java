package us.rfsmassacre.HeavenLib.Managers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import us.rfsmassacre.HeavenLib.BaseManagers.ResourceManager;

public class ConfigManager extends ResourceManager
{
	/*
	 * Manually saving the config files this way makes it
	 * consistent and simple to load from the default config
	 * saved in the jar in the event the user deleted the value
	 * from the new config file.
	 */
	public ConfigManager(JavaPlugin instance)
	{
		super(instance, "config.yml");
	}
	
	//CONFIG.YML
	//Gets config data or default config data when needed
	public String getString(String key)
	{
		return file.getString(key, defaultFile.getString(key));
	}
	public int getInt(String key)
	{
		return file.getInt(key, defaultFile.getInt(key));
	}
	public boolean getBoolean(String key)
	{
		return file.getBoolean(key, defaultFile.getBoolean(key));
	}
	public double getDouble(String key)
	{
		return file.getDouble(key, defaultFile.getDouble(key));
	}
	public long getLong(String key)
	{
		return file.getLong(key, defaultFile.getLong(key));
	}
	
	/*
	 * For some reason the getList functions do not allow a
	 * default parameter, so I just checked for null as a
	 * backup check. It will return null if absolutely nothing
	 * is found.
	 */
	public List<String> getStringList(String key)
	{
		List<String> option = file.getStringList(key);
		if (option == null)
			option = defaultFile.getStringList(key);
		
		return option;
	}
	public List<Integer> getIntegerList(String key)
	{
		List<Integer> option = file.getIntegerList(key);
		if (option == null)
			option = defaultFile.getIntegerList(key);
		
		return option;
	}
	public List<Double> getDoubleList(String key)
	{
		List<Double> option = file.getDoubleList(key);
		if (option == null)
			option = defaultFile.getDoubleList(key);
		
		return option;
	}
	public List<Long> getLongList(String key)
	{
		List<Long> option = file.getLongList(key);
		if (option == null)
			option = defaultFile.getLongList(key);
		
		return option;
	}

	public List<String> getPotionList(String key)
	{
		List<String> option = file.getStringList(key);
		if (option.isEmpty())
		{
			option = defaultFile.getStringList(key);
		}
		return option;
	}
	public ConfigurationSection getConfigurationSection(String key)
	{
		ConfigurationSection section = file.getConfigurationSection(key);
		if (section == null)
		{
			section = defaultFile.getConfigurationSection(key);
		}
		return section;
	}
}
