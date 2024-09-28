package us.rfsmassacre.Werewolf.Listeners;

import java.util.Random;

import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.event.InfectionChangeEvent;
import com.clanjhoo.vampire.event.VampireTypeChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Items.Armor.WerewolfArmor;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class VampireInfectionListener implements Listener
{
	private final MessageManager messages;
	private final WerewolfManager werewolves;
	private final ItemManager items;
	
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
	@EventHandler(ignoreCancelled = true)
	public void onVampireInfect(InfectionChangeEvent event)
	{
		if (event.isCancelled())
			return;
		
		VPlayer vPlayer = event.getVPlayer();
		if (werewolves.isWerewolf(vPlayer.getPlayer().getUniqueId()))
		{
			event.setInfection(0);
			if (vPlayer.isVampire())
				vPlayer.setVampire(false);
			event.setCancelled(true);
		}
	}
	
	/*
	 * VAMPIRE INFECTION COMPLETE
	 * 
	 * Cancels and cures vampire infection if already a Werewolf.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onVampireComplete(VampireTypeChangeEvent event)
	{
		if (event.isCancelled())
			return;
		
		VPlayer vPlayer = event.getVPlayer();
		if (werewolves.isWerewolf(vPlayer.getPlayer().getUniqueId()) && event.isVampire())
		{
			event.setCancelled(true);
		}
	}
	
	/*
	 * PURITY ARMOR CHANCE
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPurifiedInfect(InfectionChangeEvent event)
	{
		Player hunter = event.getVPlayer().getPlayer();
		if (!event.isCancelled() && werewolves.isHuman(hunter))
		{
			double purity = 0;
			for (ItemStack armor : hunter.getInventory().getArmorContents())
			{
				WerewolfItem item = items.getWerewolfItem(armor);
				if (item instanceof WerewolfArmor)
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
