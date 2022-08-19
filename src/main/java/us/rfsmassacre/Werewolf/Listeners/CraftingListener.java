package us.rfsmassacre.Werewolf.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.Items.Potions.CurePotion;
import us.rfsmassacre.Werewolf.Items.Potions.InfectionPotion;
import us.rfsmassacre.Werewolf.Items.Potions.WolfsbanePotion;
import us.rfsmassacre.Werewolf.Items.Weapons.SilverSword;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Items.Armor.Ash;
import us.rfsmassacre.Werewolf.Items.Armor.PurifiedArmor;
import us.rfsmassacre.Werewolf.Items.Armor.WashedArmor;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class CraftingListener implements Listener
{
	private final ConfigManager config;
	private final MessageManager messages;
	private final WerewolfManager werewolves;
	private final ItemManager items;
	
	private final List<String> exemptItems;
	private final List<Material> armorTypes;
	
	public CraftingListener()
	{
		config = WerewolfPlugin.getConfigManager();
		messages = WerewolfPlugin.getMessageManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		items = WerewolfPlugin.getItemManager();
		
		exemptItems = new ArrayList<>();
		exemptItems.add(new InfectionPotion().getName());
		exemptItems.add(new CurePotion().getName());
		exemptItems.add(new WolfsbanePotion().getName());
		exemptItems.add(new SilverSword().getName());
		
		armorTypes = new ArrayList<>();
		armorTypes.add(Material.DIAMOND_HELMET);
		armorTypes.add(Material.DIAMOND_CHESTPLATE);
		armorTypes.add(Material.DIAMOND_LEGGINGS);
		armorTypes.add(Material.DIAMOND_BOOTS);
	}
	
	
	/*
	 * Hunting Item Create
	 * 
	 * Force all but the potions and silver sword from being human only.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onHuntingItemCreate(CraftItemEvent event)
	{
		Player hunter = (Player)event.getWhoClicked();
		InventoryType invType = event.getInventory().getType();
		
		if (invType.equals(InventoryType.CRAFTING) || invType.equals(InventoryType.WORKBENCH))
		{
			for (WerewolfItem werewolfItem : items.getWerewolfItems())
			{
				if (werewolfItem != null && werewolfItem.equals(event.getRecipe().getResult())
				&& !exemptItems.contains(werewolfItem.getDisplayName()))
				{
					if (!config.getBoolean("hunting.enabled"))
					{
						event.setCancelled(true);
						messages.sendHunterLocale(hunter, "hunting.disabled");
						return;
					}

					if (!werewolves.isHuman(hunter))
					{
						event.setCancelled(true);
						messages.sendHunterLocale(hunter, "hunting.racial.craft",
								"{item}", werewolfItem.getDisplayName());
					}
				}
			}
		}
	}
	
	/*
	 * Purification Process
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPurificationCheck(InventoryClickEvent event)
	{
		Player hunter = (Player)event.getWhoClicked();
		InventoryType invType = event.getInventory().getType();
		
		if (invType.equals(InventoryType.FURNACE))
		{
			ItemStack cursor = event.getCursor();
			ItemStack slot = event.getCurrentItem();
			
			//When item is shift clicked
			if (event.isShiftClick() && slot != null && armorTypes.contains(slot.getType()))
			{
				WerewolfItem armor = items.getWerewolfItem(slot);
				//Cancel if it's armor but not washed armor
				if (armor == null || !(armor instanceof WashedArmor))
				{
					event.setCancelled(true);
					hunter.closeInventory();
					hunter.updateInventory();
					messages.sendHunterLocale(hunter, "hunting.armor.wrong-item");
					return;
				}

				//Cancel if they are not human
				if (armor != null && armor instanceof WashedArmor && !werewolves.isHuman(hunter))
				{
					event.setCancelled(true);
					hunter.closeInventory();
					hunter.updateInventory();
					messages.sendHunterLocale(hunter, "hunting.racial.use", 
							  "{item}", armor.getDisplayName());
					return;
				}
			}
			
			if (event.getSlotType().equals(SlotType.CRAFTING))
			{
				//When item is on cursor
				if (cursor != null && armorTypes.contains(cursor.getType()))
				{
					WerewolfItem armor = items.getWerewolfItem(cursor);
					//Cancel if it's armor but not washed armor
					if (armor == null || !(armor instanceof WashedArmor))
					{
						event.setCancelled(true);
						hunter.closeInventory();
						hunter.updateInventory();
						messages.sendHunterLocale(hunter, "hunting.armor.wrong-item");
						return;
					}
					
					//Cancel if they are not human
					if (armor != null && armor instanceof WashedArmor && !werewolves.isHuman(hunter))
					{
						event.setCancelled(true);
						hunter.closeInventory();
						hunter.updateInventory();
						messages.sendHunterLocale(hunter, "hunting.racial.smelt", 
								  "{item}", armor.getDisplayName());
						return;
					}
				}
			}
			
			//When purified armor is being picked up from a furnace
			if (event.getSlotType().equals(SlotType.RESULT))
			{
				//When there is armor in the result slot
				if (slot != null && armorTypes.contains(slot.getType()))
				{
					WerewolfItem armor = items.getWerewolfItem(slot);
					if (armor != null && armor instanceof PurifiedArmor && !werewolves.isHuman(hunter))
					{
						event.setCancelled(true);
						hunter.closeInventory();
						hunter.updateInventory();
						messages.sendHunterLocale(hunter, "hunting.racial.smelt", 
												  "{item}", armor.getDisplayName());
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPurificationAttempt(FurnaceSmeltEvent event)
	{
		WerewolfItem item = items.getWerewolfItem(event.getSource());
		if (armorTypes.contains(event.getSource().getType()))
		{
			//Burn the armor if it wasn't washed
			if (item == null || !(item instanceof WashedArmor))
			{
				event.setResult(new Ash().getItemStack());
			}
			else
			{
				int chance = config.getInt("hunting.purification.chance");
				int random = new Random().nextInt(100) + 1;

				if (random <= chance)
				{
					event.setResult(new Ash().getItemStack());
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPurificationFail(FurnaceExtractEvent event)
	{
		Player hunter = event.getPlayer();
		Ash ash = new Ash();
		
		if (ash.getItemStack().getType().equals(event.getItemType()))
		{
			messages.sendHunterLocale(hunter, "hunting.armor.burned");
		}
	}
}
