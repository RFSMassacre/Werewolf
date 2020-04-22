package us.rfsmassacre.HeavenLib.Managers;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import us.rfsmassacre.HeavenLib.BaseManagers.ResourceManager;

public class LocaleManager extends ResourceManager
{
	/*
	 * Manually saving the config files this way makes it
	 * consistent and simple to load from the default config
	 * saved in the jar in the event the user deleted the value
	 * from the new config file.
	 */
	public LocaleManager(JavaPlugin instance)
	{
		super(instance, "locale.yml");
	}
	
	//LOCALE.YML
	//Gets file or default file when needed
	public String getMessage(String key)
	{
		return ChatManager.format(file.getString(key, defaultFile.getString(key)));
	}
	public List<String> getMessageList(String key)
	{
		List<String> stringList = file.getStringList(key);
		if (stringList == null)
			stringList = defaultFile.getStringList(key);
		
		return stringList;
	}
}