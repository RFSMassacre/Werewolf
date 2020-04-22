package us.rfsmassacre.Werewolf.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WerewolfSniffEvent extends Event implements Cancellable
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
	
	//Werewolf Sniff Data
	private Player werewolf;
	private Player target;
	
	private boolean cancel;
	
	public WerewolfSniffEvent(Player werewolf, Player target)
	{
		this.werewolf = werewolf;
		this.target = target;
		
		this.cancel = false;
	}
	
	public Player getWerewolf()
	{
		return werewolf;
	}
	
	public Player getTarget()
	{
		return target;
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
