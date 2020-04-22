package us.rfsmassacre.Werewolf.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfInfectionEvent extends Event implements Cancellable
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

	//Werewolf Infection Event Data
	private Player player;
	private ClanType type;
	
	private boolean cancel;
	
	public WerewolfInfectionEvent(Player player, ClanType type)
	{
		this.player = player;
		this.type = type;
		
		this.cancel = false;
	}
	
	public Player getPlayer()
	{
		return player;
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
