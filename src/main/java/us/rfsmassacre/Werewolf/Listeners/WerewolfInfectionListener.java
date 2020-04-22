package us.rfsmassacre.Werewolf.Listeners;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent.CureType;
import us.rfsmassacre.Werewolf.Events.WerewolfInfectionEvent;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.Items.Armor.WerewolfArmor;
import us.rfsmassacre.Werewolf.Items.Potions.CurePotion;
import us.rfsmassacre.Werewolf.Items.Potions.InfectionPotion;
import us.rfsmassacre.Werewolf.Managers.ClanManager;
import us.rfsmassacre.Werewolf.Managers.EventManager;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;

public class WerewolfInfectionListener implements Listener 
{
	private MessageManager messages;
	private WerewolfManager werewolves;
	private ClanManager clans;
	private ItemManager items;
	private EventManager events;
	
	public WerewolfInfectionListener()
	{
		messages = WerewolfPlugin.getMessageManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		clans = WerewolfPlugin.getClanManager();
		items = WerewolfPlugin.getItemManager();
		events = WerewolfPlugin.getEventManager();
	}
	
	/*
	 * WITHERFANG CLAN
	 */
	@EventHandler
	public void onInfectionPotionDrink(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		if (!event.isCancelled() && werewolves.isHuman(player))
		{
			//Only continue if they have permission to
			if (!player.hasPermission("werewolf.becomeinfected"))
				return;
			
			InfectionPotion infectPotion = new InfectionPotion();
			ItemStack consumable = event.getItem();
			if (infectPotion.equals(consumable))
			{
				ClanType type = ClanType.WITHERFANG;
				Clan clan = clans.getClan(type);
				
				WerewolfInfectionEvent infectEvent = new WerewolfInfectionEvent(player, type);
				events.callEvent(infectEvent);
				if (!infectEvent.isCancelled())
				{
					//Infect player with Witherfang Clan
					werewolves.infectWerewolf(player, type);
					clan.addMember(player);
					messages.sendWolfLocale(player, "infection.werewolf-potion");
				}
			}
		}
	}
	
	/*
	 * SILVERMANE CLAN
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWolfBite(EntityDamageByEntityEvent event)
	{
		if (!event.isCancelled() && event.getFinalDamage() > 0)
		{
			Entity defendingEntity = event.getEntity();
			Entity attackingEntity = event.getDamager();
			
			if (defendingEntity instanceof Player && attackingEntity instanceof Wolf)
			{
				Player player = (Player)defendingEntity;
				Wolf wolf = (Wolf)attackingEntity;
				
				//Continue running only if they have permission to
				if (!player.hasPermission("werewolf.becomeinfected"))
					return;
				
				if (werewolves.isHuman(player) && !wolf.isTamed())
				{
					if (werewolves.canWerewolfInfect(false))
					{
						ClanType type = ClanType.SILVERMANE;
						Clan clan = clans.getClan(type);
						
						WerewolfInfectionEvent infectEvent = new WerewolfInfectionEvent(player, type);
						events.callEvent(infectEvent);
						if (!infectEvent.isCancelled())
						{
							//Infect player with the Silvermane Clan
							werewolves.infectWerewolf(player, type);
							clan.addMember(player);
							messages.sendWolfLocale(player, "infection.wolf-bite");
						}
					}
				}
			}
		}
	}
	
	/*
	 * BLOODMOON CLAN
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWerewolfBite(EntityDamageByEntityEvent event)
	{
		if (!event.isCancelled() && event.getFinalDamage() > 0)
		{
			Entity defendingEntity = event.getEntity();
			Entity attackingEntity = event.getDamager();
			
			if (defendingEntity instanceof Player && attackingEntity instanceof Player)
			{
				Player player = (Player)defendingEntity;
				Player attackingPlayer = (Player)attackingEntity;
				
				//Only continues if both players are given permission for it.
				if (!player.hasPermission("werewolf.becomeinfected") || 
					!attackingPlayer.hasPermission("werewolf.infectothers"))
					return;
				
				if (werewolves.isHuman(player) && werewolves.isWerewolf(attackingPlayer))
				{
					Werewolf werewolf = werewolves.getWerewolf(attackingPlayer);
					if (werewolf.inWolfForm() && werewolves.canWerewolfInfect(werewolf.hasIntent()))
					{
						ClanType type = ClanType.BLOODMOON;
						Clan clan = clans.getClan(type);
						
						WerewolfInfectionEvent infectEvent = new WerewolfInfectionEvent(player, type);
						events.callEvent(infectEvent);
						if (!infectEvent.isCancelled())
						{
							//Infect player with the Bloodmoon Clan
							werewolves.infectWerewolf(player, type);
							clan.addMember(player);
							messages.sendWolfLocale(player, "infection.werewolf-bite");
						}
					}
				}
			}
		}
	}
	
	/*
	 * CURE POTION
	 */
	@EventHandler
	public void onCurePotionDrink(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		if (event.isCancelled() || !werewolves.isWerewolf(player))
			return;
		
		Werewolf werewolf = werewolves.getWerewolf(player);
		CurePotion curePotion = new CurePotion();
		ItemStack consumable = event.getItem();
		if (curePotion.equals(consumable))
		{
			if (!player.hasPermission("werewolf.drinkcurepotion"))
				return;
			
			CureType type = CureType.CURE_POTION;
			Clan clan = werewolf.getClan();
			
			WerewolfCureEvent cureEvent = new WerewolfCureEvent(player, type);
			events.callEvent(cureEvent);
			if (!cureEvent.isCancelled())
			{
				//Cure player from being a werewolf
				werewolves.cureWerewolf(werewolf);
				clan.removeMember(player);
				messages.sendWolfLocale(player, "cure.cure-potion");
			}
		}
	}
	
	/*
	 * PURITY ARMOR CHANCE
	 */
	@EventHandler
	public void onPurifiedInfect(WerewolfInfectionEvent event)
	{
		Player hunter = event.getPlayer();
		if (!event.isCancelled() && werewolves.isHuman(hunter))
		{
			double purity = 0;
			for (ItemStack armor : hunter.getInventory().getArmorContents())
			{
				WerewolfItem item = items.getWerewolfItem(armor);
				if (item != null && item instanceof WerewolfArmor)
				{
					WerewolfArmor werewolfArmor = (WerewolfArmor)item;
					purity += werewolfArmor.getPurity();
				}
			}
			
			//Random number between 1 and 100
			int random = new Random().nextInt(100) + 1;
			if (random <= purity && purity > 0)
			{
				event.setCancelled(true);
				messages.sendHunterLocale(hunter, "hunting.armor.cleansed");
			}
		}
	}
}
