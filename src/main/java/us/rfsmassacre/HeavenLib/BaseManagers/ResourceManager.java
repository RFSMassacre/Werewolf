package us.rfsmassacre.HeavenLib.BaseManagers;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * RESOURCE MANAGERS HANDLE FILES SUCH AS CONFIGS AND LOCALES.
 * To be extended so you can specify what data types you need.
 */

public abstract class ResourceManager extends Manager
{
	protected String fileName;
	protected YamlConfiguration file;
	protected YamlConfiguration defaultFile;
	
	public ResourceManager(JavaPlugin instance, String fileName)
	{
		super(instance);
		this.fileName = fileName;
		
		reloadFiles();
	}
	
	public void reloadFiles()
	{
		File newFile = new File(instance.getDataFolder(), fileName);
		
		try
		{
			defaultFile = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(fileName)));
			
			if (!newFile.exists())
			{
				newFile.createNewFile();
				instance.saveResource(fileName, true);
			}
			
			file = YamlConfiguration.loadConfiguration(newFile);
		}
		catch (IOException exception)
		{
			//Print error on console neatly
			exception.printStackTrace();
		}
	}
}
