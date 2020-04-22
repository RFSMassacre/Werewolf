package us.rfsmassacre.HeavenLib.BaseManagers;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

public class FileManager extends Manager
{
	protected String folderName;
	protected String fileName;
	
	protected File folder;
	protected File file;
	
	public FileManager(JavaPlugin instance, String folderName, String fileName)
	{
		super(instance);
		this.fileName = fileName;
		this.folderName = folderName;
		this.folder = new File(instance.getDataFolder() + "/" + folderName);
		
		if (!folder.exists())
			folder.mkdir();
		
		reloadFiles();
	}
	
	public void reloadFiles()
	{
		file = new File(folder, fileName);
		
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
				instance.saveResource(folderName + "/" + fileName, true);
			}
		}
		catch (IOException exception)
		{
			//Print error on console neatly
			exception.printStackTrace();
		}
	}
}
