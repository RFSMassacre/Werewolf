package us.rfsmassacre.Werewolf.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class NewAlphaEvent extends Event implements Cancellable
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

	//New Alpha Event Data
	private Player alpha;
	private Player newAlpha;
	private ClanType type;
	
	private boolean cancel;
	
	public NewAlphaEvent(Player alpha, ClanType type)
	{
		this.alpha = alpha;
		this.type = type;
		
		this.cancel = false;
	}
	public NewAlphaEvent(Player alpha, Player newAlpha, ClanType type)
	{
		this.alpha = alpha;
		this.newAlpha = newAlpha;
		this.type = type;
		
		this.cancel = false;
	}
	
	public Player getAlpha()
	{
		return alpha;
	}
	public Player getNewAlpha() 
	{
		return newAlpha;
	}
	public ClanType getType() 
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
