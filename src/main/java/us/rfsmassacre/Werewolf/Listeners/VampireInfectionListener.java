package us.rfsmassacre.Werewolf.Listeners;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.vampire.entity.UPlayer;
import com.massivecraft.vampire.event.EventVampirePlayerInfectionChange;
import com.massivecraft.vampire.event.EventVampirePlayerVampireChange;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.Items.Armor.WerewolfArmor;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class VampireInfectionListener implements Listener
{
	private MessageManager messages;
	private WerewolfManager werewolves;
	private ItemManager items;
	
	public VampireInfectionListener()
	{
		messages = WerewolfPlugin.getMessageManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		items = WerewolfPlugin.getItemManager();
	}
	
	/*
	 * VAMPIRE INFECTION START
	 * 
	 * Cancels and cures vampire infection if already a Werewolf.
	 */
	@EventHandler
	public void onVampireInfect(EventVampirePlayerInfectionChange event)
	{
		if (event.isCancelled())
			return;
		
		UPlayer uPlayer = event.getUplayer();
		if (werewolves.isWerewolf(uPlayer.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
			
			if (uPlayer.isVampire())
				uPlayer.setVampire(false);
		}
	}
	
	/*
	 * VAMPIRE INFECTION COMPLETE
	 * 
	 * Cancels and cures vampire infection if already a Werewolf.
	 */
	@EventHandler
	public void onVampireComplete(EventVampirePlayerVampireChange event)
	{
		if (event.isCancelled())
			return;
		
		UPlayer uPlayer = event.getUplayer();
		if (werewolves.isWerewolf(uPlayer.getPlayer().getUniqueId()) && event.isVampire())
		{
			event.setCancelled(true);
		}
	}
	
	/*
	 * PURITY ARMOR CHANCE
	 */
	@EventHandler
	public void onPurifiedInfect(EventVampirePlayerInfectionChange event)
	{
		Player hunter = event.getUplayer().getPlayer();
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
