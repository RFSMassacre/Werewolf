package us.rfsmassacre.HeavenLib.Managers;

import org.bukkit.plugin.java.JavaPlugin;

import us.rfsmassacre.HeavenLib.BaseManagers.Manager;

public class DependencyManager extends Manager
{
	public DependencyManager(JavaPlugin instance)
	{
		super(instance);
	}
	
	public boolean hasPlugin(String pluginName)
	{
		//Easy way to check if needed plugin is enabled
		return instance.getServer().getPluginManager().isPluginEnabled(pluginName) &&
			   instance.getServer().getPluginManager().getPlugin(pluginName) != null;
	}
	
	public String getServerVersion()
	{
		//Screw DogOnFire, server checking can be done in a few lines.
		String rawVersion = instance.getServer().getVersion();
		String mcVersion = rawVersion.substring(rawVersion.indexOf("("));
		String version = mcVersion.replace("(MC: ", "").replace(")", "");
		
		return version;
	}
	public boolean betweenVersions(String version1, String version2)
	{
		String first = version1.replaceFirst("^1.", "");
		double lower = Double.parseDouble(first);

		String second = version2.replaceFirst("^1.", "");
		double higher = Double.parseDouble(second);

		String third = getServerVersion().replaceFirst("^1.", "");
		double middle = Double.parseDouble(third);

		return middle >= lower & middle <= higher;
	}
}
