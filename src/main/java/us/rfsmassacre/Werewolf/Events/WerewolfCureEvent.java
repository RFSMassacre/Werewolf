package us.rfsmassacre.Werewolf.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
	private Player player;
	private CureType type;
	
	private boolean cancel;
	
	public WerewolfCureEvent(Player player, CureType type)
	{
		this.player = player;
		this.type = type;
		
		this.cancel = false;
	}
	
	public Player getPlayer()
	{
		return player;
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
