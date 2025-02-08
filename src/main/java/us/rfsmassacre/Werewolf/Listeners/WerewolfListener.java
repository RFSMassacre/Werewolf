package us.rfsmassacre.Werewolf.Listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.NewAlphaEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfSniffEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfTransformEvent;
import us.rfsmassacre.Werewolf.Managers.ClanManager;
import us.rfsmassacre.Werewolf.Managers.EventManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class WerewolfListener implements Listener
{
	private final ConfigManager config;
	private final EventManager events;
	private final WerewolfManager werewolves;
	private final ClanManager clans;
	private final MessageManager messages;
	
	public WerewolfListener()
	{
		config = WerewolfPlugin.getConfigManager();
		events = WerewolfPlugin.getEventManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		clans = WerewolfPlugin.getClanManager();
		messages = WerewolfPlugin.getMessageManager();
	}
	
	/*
	 * Werewolves do not receive fall damage when in wolf form.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onWerewolfFallDamage(EntityDamageEvent event)
	{
		//Cancel if the event was cancelled or not a player
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.getEntity();
		
		//Negate fall damage for Werewolves in wolf form
		if (werewolves.isWerewolf(player))
		{
			Werewolf werewolf = werewolves.getWerewolf(player);
			if (werewolf.inWolfForm())
			{
				if (event.getCause().equals(DamageCause.FALL))
				{
					event.setDamage(0);
					event.setCancelled(true);
				}
			}
		}
	}
	
	/*
	 * Werewolves naturally reduce incoming damage.
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onWerewolfDamage(EntityDamageByEntityEvent event)
	{
		//Cancel if the event was cancelled or not a player
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!werewolves.isWerewolf(player))
			return;

		Werewolf werewolf = werewolves.getWerewolf(player);
		String clan = werewolf.getType().toKey();

		if (!werewolf.inWolfForm())
			return;

		long numArmorPieces = Arrays.stream(player.getInventory().getArmorContents()).filter(item -> item != null && !item.getType().isAir()).count();
		if (numArmorPieces > 0)
			return;

		//Mitigate combat damage if in wolf form with no armor
		if (werewolves.isAlpha(player))
			event.setDamage(event.getDamage() * config.getDouble("werewolf-stats." + clan + ".alpha.defense"));
		else
			event.setDamage(event.getDamage() * config.getDouble("werewolf-stats." + clan + ".werewolf.defense"));
	}
	
	/*
	 * Werewolves hit harder when using their fist. Using a
	 * weapon would make it do almost no damage.
	 * 
	 * Takes into account the new and old methods of holding items.
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onWerewolfAttack(EntityDamageByEntityEvent event)
	{
		// Ignore if not a player
		if (!(event.getDamager() instanceof Player player))
			return;

		// Ignore if not a melee attack (mainly for compatibility with magic plugins)
		if (event.getCause() != DamageCause.ENTITY_ATTACK && event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK)
			return;

		// Ignore if damager not a werewolf
		if (!werewolves.isWerewolf(player))
			return;

		Werewolf werewolf = werewolves.getWerewolf(player);

		// Ignore if damager not in werewolf form
		if (werewolf == null || !werewolf.inWolfForm())
			return;

		//Override combat damage based on what's on your hands
		String clan = werewolf.getType().toKey();
		ItemStack[] itemsInHands = werewolf.getItemsInHands();
		double offset, factor;

		if (itemsInHands[0] == null) {
			if (werewolves.isAlpha(player)) {
				offset = config.getDouble("werewolf-stats." + clan + ".alpha.fist-damage");
				factor = config.getDouble("werewolf-stats." + clan + ".alpha.fist-factor");
			}
			else {
				offset = config.getDouble("werewolf-stats." + clan + ".werewolf.fist-damage");
				factor = config.getDouble("werewolf-stats." + clan + ".werewolf.fist-factor");
			}
		}
		else {
			if (werewolves.isAlpha(player)) {
				offset = config.getDouble("werewolf-stats." + clan + ".alpha.item-damage");
				factor = config.getDouble("werewolf-stats." + clan + ".alpha.item-factor");
			}
			else {
				offset = config.getDouble("werewolf-stats." + clan + ".werewolf.item-damage");
				factor = config.getDouble("werewolf-stats." + clan + ".werewolf.item-factor");
			}
		}
		event.setDamage(Math.max(factor * (event.getDamage() + offset), Math.min(event.getDamage(), 1D)));
	}
	
	/*
	 * If an Alpha is killed by a clan member, the Alpha status is
	 * then transferred to the killer.
	 * 
	 * If an Alpha is killed by a hunter with a silver sword, the Alpha
	 * status is then reset; defaulting to the second player with the highest level.
	 * (If hunting is enabled on this server.)
	 */
	@EventHandler(ignoreCancelled = true)
	public void onAlphaDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		if (werewolves.isAlpha(player))
		{
			if (player.getKiller() != null)
			{
				Player killer = player.getKiller();
				Werewolf alpha = werewolves.getWerewolf(player);
				Clan clan = clans.getClan(alpha);
				
				if (werewolves.isWerewolf(killer))
				{
					Werewolf werewolf = werewolves.getWerewolf(killer);
					if (alpha.getType().equals(werewolf.getType()))
					{
						NewAlphaEvent alphaEvent = new NewAlphaEvent(player, killer, clan.getType());
						events.callEvent(alphaEvent);
						if (!alphaEvent.isCancelled())
						{
							clan.makeAlpha(werewolf);
							
							messages.broadcastLocale("clan.killed-alpha",
									"{killer}", werewolf.getDisplayName(),
									"{alpha}", alpha.getDisplayName(),
									"{clan}", config.getString("menu.clan." + clan.getType().name()));
						}
					}
				}
				else if (config.getBoolean("hunting.enabled") && werewolves.isHuman(killer) && clan.getSize() > 1)
				{
					clan.getMembers((clanMembers) ->
					{
						Werewolf runnerUp = !clanMembers.get(0).equals(alpha) ? clanMembers.get(0) : clanMembers.get(1);
						NewAlphaEvent alphaEvent = new NewAlphaEvent(player, clan.getType());
						events.callEvent(alphaEvent);
						if (!alphaEvent.isCancelled())
						{
							clan.makeAlpha(runnerUp);
							//Throw alpha transfer event

							messages.broadcastLocale("clan.hunted-alpha",
									"{killer}", killer.getDisplayName(),
									"{clan}", config.getString("menu.clan." + clan.getType().name()),
									"{alpha}", alpha.getDisplayName());
						}
					});
				}
			}
		}
	}
	
	/*
	 * Werewolves must only eat meat (or items configured by
	 * owner.)
	 */
	@EventHandler(ignoreCancelled = true)
	public void onWerewolfEat(PlayerItemConsumeEvent event)
	{
		if (!werewolves.isWerewolf(event.getPlayer()))
			return;
		
		ConfigManager config = WerewolfPlugin.getConfigManager();
		ItemStack food = event.getItem();
		
		if (config.getBoolean("diet.enabled"))
		{
			List<String> foods = config.getStringList("diet.blocked-foods");
			if (foods != null)
			{
				final Player player = event.getPlayer();
				if (foods.contains(food.getType().name()))
				{
					if (!config.getBoolean("diet.prevent-consumption"))
					{
						final int foodLvl = player.getFoodLevel();
						final float saturationLvl = player.getSaturation();
						
						//Allow the event but return the hunger back to the
						//previous amount
						new BukkitRunnable()
						{
							public void run() 
							{
								player.setFoodLevel(foodLvl);
								player.setSaturation(saturationLvl);
							}	
						}.runTaskLater(WerewolfPlugin.getInstance(), 1L);
					}
					else
					{
						//Or prevent the consumption alltogether
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	/*
	 * Werewolves leveled enough can sniff on an entity to
	 * track their scent.
	 * 
	 * When in /ww track mode, Werewolf sees a scent trail to
	 * their target.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onWerewolfSniff(PlayerInteractEntityEvent event) 
	{
		if (!werewolves.isWerewolf(event.getPlayer()))
			return;
		
		Werewolf werewolf = werewolves.getWerewolf(event.getPlayer());
		int scentLvl = WerewolfPlugin.getConfigManager().getInt("maturity.scent-track");
		if (werewolf.inWolfForm())
		{
			if (event.getRightClicked() instanceof Player
			 && werewolf.getLevel() >= scentLvl) 
			{
				
				if (werewolf.canSniff())
				{
					Player target = (Player)event.getRightClicked();
					WerewolfSniffEvent sniffEvent = new WerewolfSniffEvent(event.getPlayer(), target);
					events.callEvent(sniffEvent);
					
					if (!sniffEvent.isCancelled())
					{
						werewolf.sniff();
						if (werewolves.canTrack())
						{
							//Add this player's ID to the werewolf's tracker
							werewolf.setTargetId(target.getUniqueId());
							
							messages.sendWolfLocale(event.getPlayer(), "track.scent-found",
									"{player}", target.getDisplayName());
						}
						else
						{
							messages.sendWolfLocale(event.getPlayer(), "track.scent-try");
						}
					}
				}
			}
		}
	}
	
	/*
	 * Werewolves growl when getting hurt in wolf form
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerDamaged(EntityDamageEvent event)
	{
		//If no damage was done to a player, cancel now
		if (event.getDamage() <= 0 ||
		  !(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.getEntity();
		
		//Growl when getting hurt
		if (werewolves.isWerewolf(player))
		{
			Werewolf werewolf = werewolves.getWerewolf(player);
			if (werewolf.inWolfForm())
				werewolf.growl();
		}
	}
	
	/*
	 * Prevent Werewolves from using certain commands.
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onWerewolfChat(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();

		//Commands blocked from werewolves in any form
		if (werewolves.isWerewolf(player))
		{
			for (String command : config.getStringList("blocked-commands.all"))
			{
				if (event.getMessage().startsWith(command))
				{
					event.setCancelled(true);
					messages.sendWolfLocale(event.getPlayer(), "invalid.blocked-command");
					return;
				}
			}
		}

		//Commands blocked from werewolves in werewolf form
		if (werewolves.isWerewolf(player) && werewolves.getWerewolf(player).inWolfForm())
		{
			for (String command : config.getStringList("blocked-commands.werewolf"))
			{
				if (event.getMessage().startsWith(command))
				{
					event.setCancelled(true);
					messages.sendWolfLocale(event.getPlayer(), "invalid.blocked-command-wolf");
					return;
				}
			}
		}

		//Commands always blocked for werewolf alphas
		if (werewolves.isAlpha(player))
		{
			for (String command : config.getStringList("blocked-commands.alpha"))
			{
				if (event.getMessage().startsWith(command))
				{
					event.setCancelled(true);
					messages.sendWolfLocale(event.getPlayer(), "invalid.blocked-command-alpha");
					return;
				}
			}
		}
	}
	
	/*
	 * Prevent Werewolves from transforming in no Werewolf world or when not in Survival mode
	 */
	@EventHandler(ignoreCancelled = true)
	public void onTransform(WerewolfTransformEvent event)
	{
		if (event.toWolfForm())
		{
			if (config.getStringList("no-werewolf-worlds").contains(event.getPlayer().getWorld().getName()) ||
					event.getPlayer().getGameMode().equals(GameMode.SPECTATOR) ||
					event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			{
				event.setCancelled(true);
			}
		}
	}
}
