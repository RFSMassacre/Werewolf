package us.rfsmassacre.HeavenLib.BaseManagers;

import org.bukkit.plugin.java.JavaPlugin;

/*
 * This is to be extended to another managers for easy
 * re-use. All managers save an instance of the plugin
 * it's being used for.
 */
public abstract class Manager 
{
	protected JavaPlugin instance;
	
	public Manager(JavaPlugin instance)
	{
		this.instance = instance;
	}
}
