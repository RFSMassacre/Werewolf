package us.rfsmassacre.Werewolf.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TrackerTargetEvent extends Event implements Cancellable
{
	//Handler List
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() 
	{
		return HANDLERS;
	}
	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}
	
	//Event Type
	public enum TargetType
	{
		WEREWOLF_TARGET,
		VAMPIRE_TARGET
	}
	
	//Tracker Target Event Data
	private Player hunter;
	private Player target;
	private TargetType type;
	
	private boolean cancel;
	
	public TrackerTargetEvent(Player hunter, Player target, TargetType type)
	{
		this.hunter = hunter;
		this.target = target;
		this.type = type;
		
		this.cancel = false;
	}

	public Player getHunter() 
	{
		return hunter;
	}
	
	public Player getTarget()
	{
		return target;
	}

	public TargetType getType() 
	{
		return type;
	}

	@Override
	public boolean isCancelled() 
	{
		return cancel;
	}
	@Override
	public void setCancelled(boolean cancel) 
	{
		this.cancel = cancel;
	}
}
