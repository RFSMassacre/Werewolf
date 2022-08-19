package us.rfsmassacre.Werewolf.Managers;

import org.bukkit.event.Event;

import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class EventManager 
{
	private final WerewolfPlugin instance;
	
	public EventManager()
	{
		instance = WerewolfPlugin.getInstance();
	}
	
	//Throws new event via plugin instance
	public void callEvent(Event event)
	{
		instance.getServer().getPluginManager().callEvent(event);
	}
}
