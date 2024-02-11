package us.rfsmassacre.Werewolf.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class WerewolfCureEvent extends Event implements Cancellable
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
	
	//EventType
	public static enum CureType
	{
		CURE_POTION, AUTO_CURE, COMMAND, PURIFIER_SWORD;
	}
	
	//Werewolf Cure Event Data
	private UUID uuid;
	private CureType type;
	
	private boolean cancel;
	
	public WerewolfCureEvent(UUID uuid, CureType type)
	{
		this.uuid = uuid;
		this.type = type;
		
		this.cancel = false;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	public CureType getType()
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
