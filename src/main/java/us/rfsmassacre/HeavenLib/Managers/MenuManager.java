package us.rfsmassacre.HeavenLib.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import us.rfsmassacre.HeavenLib.BaseManagers.FileManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class MenuManager extends FileManager
{
	private List<String> lines;
	
	public MenuManager(String fileName) 
	{
		super(WerewolfPlugin.getInstance(), "menus", fileName);
		
		reloadText();
	}
	
	public void reloadText()
	{
		reloadFiles();
		
		try 
		{
			lines = Files.readAllLines(file.toPath());
		} 
		catch (IOException exception) 
		{
			exception.printStackTrace();
		}
	}
	
	public String getText()
	{
		return String.join("\n", lines);
	}
}
