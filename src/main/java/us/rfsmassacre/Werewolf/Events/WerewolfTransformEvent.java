package us.rfsmassacre.Werewolf.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfTransformEvent extends Event implements Cancellable
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
	
	//Werewolf Transformation Data
	private Player player;
	private ClanType type;
	private boolean wolfForm;
	
	private boolean cancel;
	
	public WerewolfTransformEvent(Player player, ClanType type, boolean wolfForm)
	{
		this.player = player;
		this.type = type;
		this.wolfForm = wolfForm;
		
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
	
	public boolean toWolfForm()
	{
		return wolfForm;
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
