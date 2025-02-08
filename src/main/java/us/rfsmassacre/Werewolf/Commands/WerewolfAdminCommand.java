package us.rfsmassacre.Werewolf.Commands;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import us.rfsmassacre.HeavenLib.Commands.SpigotCommand;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfCureEvent.CureType;
import us.rfsmassacre.Werewolf.Events.WerewolfInfectionEvent;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;
import us.rfsmassacre.Werewolf.Managers.*;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Data.LegacyAlphaDataManager;
import us.rfsmassacre.Werewolf.Data.LegacyWerewolfDataManager;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;
import us.rfsmassacre.Werewolf.Origin.Moon.MoonPhase;

public class WerewolfAdminCommand extends SpigotCommand
{
	private final ConfigManager config;
	private final ClanManager clans;
	private final WerewolfManager werewolves;
	private final MessageManager messages;
	private final EventManager events;
	private final ItemManager items;
	
	public WerewolfAdminCommand() 
	{
		super(WerewolfPlugin.getMessageManager(), "werewolfadmin");
		
		this.config = WerewolfPlugin.getConfigManager();
		this.clans = WerewolfPlugin.getClanManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();
		this.events = WerewolfPlugin.getEventManager();
		this.items = WerewolfPlugin.getItemManager();

		addSubCommand(new MainCommand());
		addSubCommand(new SpawnCommand());
		addSubCommand(new ShowCommand());
		addSubCommand(new TransformCommand());
		addSubCommand(new InfectCommand());
		addSubCommand(new CureCommand());
		addSubCommand(new SetAlphaCommand());
		addSubCommand(new SetLevelCommand());
		addSubCommand(new AddLevelCommand());
		addSubCommand(new SetPhaseCommand());
		addSubCommand(new PurgeCommand());
		addSubCommand(new ReloadCommand());
		addSubCommand(new ImportCommand());
		addSubCommand(new HelpCommand());
	}

	@Override
	protected void onInvalidArgs(CommandSender sender) 
	{
		messages.sendWolfLocale(sender, "invalid.admin-args");
	}
	@Override
	protected void onCommandFail(CommandSender sender) 
	{
		messages.sendWolfLocale(sender, "invalid.no-permission");
	}
	
	/*
	 * Main Admin Command - No args
	 */
	private class MainCommand extends SubCommand
	{
		public MainCommand()
		{
			super("werewolf");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			String menu = messages.getAdminText();
			int werewolfAmount = werewolves.getWerewolfAmount();
			menu = menu.replace("{werewolves}", Integer.toString(werewolfAmount));
			if (werewolfAmount == 1)
			{
				menu = menu.replace("Werewolves", "Werewolf");
			}
			
			int witherfangs = clans.getClan(ClanType.WITHERFANG).getSize();
			int silvermanes = clans.getClan(ClanType.SILVERMANE).getSize();
			int bloodmoons = clans.getClan(ClanType.BLOODMOON).getSize();
			menu = menu.replace("{wf-members}", Integer.toString(witherfangs))
					   .replace("{sm-members}", Integer.toString(silvermanes))
					   .replace("{bm-members}", Integer.toString(bloodmoons));
			sender.sendMessage(ChatManager.format(menu));
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Spawn Item Command
	 */
	private class SpawnCommand extends SubCommand
	{
		public SpawnCommand()
		{
			super("spawn");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (isConsole(sender)) {
				// Give console error
				messages.sendWolfLocale(sender, "admin.spawn.console");
				return;
			}
			Player player = (Player)sender;
			if (args.length != 2) {
				// No args
				messages.sendWolfLocale(sender, "admin.spawn.no-args");
				return;
			}
			String name = args[1];
			if (name == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.spawn.no-args");
				return;
			}
			WerewolfItem werewolfItem = items.getWerewolfItem(args[1].toUpperCase());
			if (werewolfItem != null)
			{
				ItemStack item = werewolfItem.getItemStack();
				player.getInventory().addItem(item);

				messages.sendWolfLocale(player, "admin.spawn.success",
						"{item}", werewolfItem.getDisplayName());
			}
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			if (args.length != 2)
				return List.of();
			List<String> suggestions = new ArrayList<>();
			Collection<WerewolfItem> werewolfItems = items.getWerewolfItems();
			for (WerewolfItem item : werewolfItems)
				suggestions.add(item.getName());

			return suggestions;
		}
	}

	/*
	 * Show Other Command
	 */
	private class ShowCommand extends SubCommand
	{
		public ShowCommand() {
			super("show");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args)
		{
			if (args.length != 2) {
				// No args
				messages.sendWolfLocale(sender, "admin.show.no-args");
				return;
			}
			if (args[1] == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.show.no-args");
				return;
			}
			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.show.wrong-name");
				return;
			}

			Werewolf werewolf = werewolves.getWerewolf(player);
			if (werewolf == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.show.not-ww");
				return;
			}

			messages.sendWolfLocale(sender, "admin.show.success", "{player}", args[1]);
			messages.sendWolfLocale(sender, "admin.show.level", "{level}:" + werewolf.getLevel());
			messages.sendWolfLocale(sender, "admin.show.clan", "{clan}:" + werewolf.getType());
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			if (args.length != 2)
				return List.of();
			List<String> suggestions = new ArrayList<>();
			for (Player player : Bukkit.getOnlinePlayers())
				suggestions.add(player.getName());

			return suggestions;
		}
	}
	
	/*
	 * Transform Others Command
	 */
	private class TransformCommand extends SubCommand
	{
		public TransformCommand()
		{
			super("transform");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length != 2) {
				//Give not Werewolf error
				messages.sendWolfLocale(sender, "admin.transform.no-args");
				return;
			}
			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.spawn.wrong-name");
				return;
			}
			if (!werewolves.isWerewolf(player))
			{
				messages.sendWolfLocale(sender, "admin.transform.not-infected",
						"{player}", player.getDisplayName());
				return;
			}
			if (WerewolfPlugin.getMoonManager().isFullMoon(player.getWorld()))
			{
				//Send full moon cancels transform message
				messages.sendWolfLocale(sender, "admin.transform.full-moon");
				return;
			}
			Werewolf werewolf = werewolves.getWerewolf(player);
			if (!werewolf.inWolfForm()) {
				werewolf.transform();
				messages.sendWolfLocale(player, "transform.to-form");
			}
			else {
				werewolf.untransform();
				messages.sendWolfLocale(player, "transform.from-form");
			}

			messages.sendWolfLocale(sender, "admin.transform.success",
					"{werewolf}", werewolf.getDisplayName());
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			if (args.length != 2)
				return List.of();
			List<String> suggestions = new ArrayList<>();
			for (Player player : Bukkit.getOnlinePlayers())
				suggestions.add(player.getName());

			return suggestions;
		}
	}
	
	/*
	 * Infect Command
	 */
	private class InfectCommand extends SubCommand
	{
		public InfectCommand()
		{
			super("infect");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length < 3) {
				messages.sendWolfLocale(sender, "admin.infect.no-args");
				return;
			}
			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.spawn.wrong-name");
				return;
			}
			ClanType type = ClanType.fromString(args[2]);
			if (type == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.spawn.bad-clan");
				return;
			}
			Clan clan = clans.getClan(type);
			if (!werewolves.isHuman(player)) {
				messages.sendWolfLocale(sender, "admin.infect.not-human",
						"{player}", player.getDisplayName());
				return;
			}
			WerewolfInfectionEvent event = new WerewolfInfectionEvent(player, type);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled())
			{
				werewolves.infectWerewolf(player, type);
				clan.addMember(player);
				messages.sendWolfLocale(sender, "admin.infect.success",
						"{player}", player.getDisplayName(),
						"{clan}", type.toString());
			}
			else
			{
				messages.sendWolfLocale(sender, "admin.infect.failed",
						"{player}", player.getDisplayName());
			}
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			if (args.length != 2 && args.length != 3)
				return List.of();
			List<String> suggestions = new ArrayList<>();
			if (args.length == 2) {
				for (Player player : Bukkit.getOnlinePlayers())
					suggestions.add(player.getName());
			}
			else {
				for (ClanType type : ClanType.values())
					suggestions.add(type.toString());
			}

			return suggestions;
		}
	}
	
	/*
	 * Cure Command
	 */
	private class CureCommand extends SubCommand
	{
		public CureCommand()
		{
			super("cure");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length < 2) {
				messages.sendWolfLocale(sender, "admin.cure.no-args");
				return;
			}
			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				// Invalid arg error
				messages.sendWolfLocale(sender, "admin.spawn.wrong-name");
				return;
			}
			if (!werewolves.isWerewolf(player)) {
				messages.sendWolfLocale(sender, "admin.cure.not-infected",
						"{player}", player.getDisplayName());
				return;
			}
			Werewolf werewolf = werewolves.getWerewolf(player);
			Clan clan = werewolf.getClan();

			WerewolfCureEvent event = new WerewolfCureEvent(player.getUniqueId(), CureType.COMMAND);
			if (events != null)
				events.callEvent(event);

			if (!event.isCancelled()) {
				werewolves.cureWerewolf(werewolf);
				clan.removeMember(player);

				messages.sendWolfLocale(sender, "admin.cure.success",
						"{player}", player.getDisplayName());
			}
			else {
				messages.sendWolfLocale(sender, "admin.cure.failed",
						"{player}", player.getDisplayName());
			}
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			if (args.length != 2)
				return List.of();
			List<String> suggestions = new ArrayList<>();
			for (Player player : Bukkit.getOnlinePlayers())
				suggestions.add(player.getName());

			return suggestions;
		}
	}
	
	/*
	 * Set Alpha Command
	 */
	private class SetAlphaCommand extends SubCommand
	{
		public SetAlphaCommand()
		{
			super("setalpha");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length >= 2)
			{
				try
				{
					Player player = Bukkit.getPlayer(args[1]);
					if (player != null)
					{
						if (werewolves.isWerewolf(player))
						{
							Werewolf werewolf = werewolves.getWerewolf(player);
							Clan clan = WerewolfPlugin.getClanManager().getClan(werewolf);
							clan.makeAlpha(werewolf);

							messages.sendWolfLocale(sender, "admin.setalpha.success",
									"{werewolf}", player.getDisplayName(),
									"{clan}", clan.getType().toString());
						}
						else
						{
							messages.sendWolfLocale(sender, "admin.setalpha.not-infected",
									"{player}", player.getDisplayName());
						}
						
						return;
					}
				}
				catch (NumberFormatException error)
				{
					//Do nothing here since it will fall back to invalid args
				}
			}
			
			//Send invalid arg error
			messages.sendWolfLocale(sender, "admin.setalpha.no-args");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			List<String> suggestions = new ArrayList<>();
			if (args.length == 2)
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					suggestions.add(player.getName());
				}
			}

			return suggestions;
		}
	}
	
	/*
	 * Set Level Command
	 */
	private class SetLevelCommand extends SubCommand
	{
		public SetLevelCommand()
		{
			super("setlevel");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length >= 3)
			{
				try
				{
					Player player = Bukkit.getPlayer(args[1]);
					int level = Integer.parseInt(args[2]);
					
					if (player != null && level > -1)
					{
						if (werewolves.isWerewolf(player))
						{
							Werewolf werewolf = werewolves.getWerewolf(player);
							werewolf.setLevel(level);

							messages.sendWolfLocale(sender, "admin.level.success",
									"{player}", player.getDisplayName(),
									"{level}", Integer.toString(werewolf.getLevel()));
						}
						else
						{
							messages.sendWolfLocale(sender, "admin.level.not-infected",
									"{player}", player.getDisplayName());
						}
						
						return;
					}
				}
				catch (NumberFormatException error)
				{
					//Do nothing here since it will fall back to invalid args
				}
			}
			
			//Send invalid arg error
			messages.sendWolfLocale(sender, "admin.level.no-args");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			List<String> suggestions = new ArrayList<>();
			if (args.length == 2)
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					suggestions.add(player.getName());
				}
			}
			if (args.length == 3)
			{
				for (int number = 0; number <= 100; number++)
				{
					suggestions.add(Integer.toString(number));
				}
			}

			return suggestions;
		}
	}
	
	/*
	 * Add Level Command
	 */
	private class AddLevelCommand extends SubCommand
	{
		public AddLevelCommand()
		{
			super("addlevel");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length >= 3)
			{
				try
				{
					Player player = Bukkit.getPlayer(args[1]);
					int level = Integer.parseInt(args[2]);
					
					if (player != null && level > -1)
					{
						if (werewolves.isWerewolf(player))
						{
							Werewolf werewolf = werewolves.getWerewolf(player);
							werewolf.setLevel(werewolf.getLevel() + level);

							messages.sendWolfLocale(sender, "admin.level.success",
									"{player}", player.getDisplayName(),
									"{level}", Integer.toString(werewolf.getLevel()));
						}
						else
						{
							messages.sendWolfLocale(sender, "admin.level.not-infected",
									"{player}", player.getDisplayName());
						}
						
						return;
					}
				}
				catch (NumberFormatException error)
				{
					//Do nothing here since it will fall back to invalid args
				}
			}
			
			//Send invalid arg error
			messages.sendWolfLocale(sender, "admin.level.no-args");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			List<String> suggestions = new ArrayList<>();
			if (args.length == 2)
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					suggestions.add(player.getName());
				}
			}
			if (args.length == 3)
			{
				for (int number = 0; number <= 100; number++)
				{
					suggestions.add(Integer.toString(number));
				}
			}

			return suggestions;
		}
	}
	
	/*
	 * Set Phase Command
	 */
	private class SetPhaseCommand extends SubCommand
	{
		public SetPhaseCommand()
		{
			super("setphase");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (!isConsole(sender))
			{
				if (args.length >= 2)
				{
					Player player = (Player)sender;
					World world = player.getWorld();
					
					if (!config.getStringList("blocked-worlds").contains(world.getName()))
					{
						MoonPhase phase = MoonPhase.fromString(args[1]);
						if (phase != null)
						{
							WerewolfPlugin.getMoonManager().setMoonPhase(phase, world);
							
							messages.sendWolfLocale(player, "admin.setphase.success",
									"{phase}", phase.toString());
						}
						else
						{
							//No moon in this world error
							messages.sendWolfLocale(sender, "admin.setphase.no-moon");
						}
					}
					else
					{
						//World blacklisted
						messages.sendWolfLocale(sender, "admin.setphase.blocked-world");
					}
					return;
				}
				
				//Invalid args
				messages.sendWolfLocale(sender, "admin.setphase.no-args");
			}
			else
			{
				//Console error
				messages.sendWolfLocale(sender, "admin.setphase.console");
			}
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			List<String> suggestions = new ArrayList<>();
			if (args.length == 2)
			{

				for (MoonPhase phase : MoonPhase.values())
				{
					suggestions.add(phase.toString());
				}
			}

			return suggestions;
		}
	}
	
	/*
	 * Purge Command
	 */
	private class PurgeCommand extends SubCommand
	{
		public PurgeCommand()
		{
			super("purge");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			werewolves.purgeBrokenFiles();
			
			messages.sendWolfLocale(sender, "admin.purge");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Reload Command
	 */
	private class ReloadCommand extends SubCommand
	{
		public ReloadCommand()
		{
			super("reload");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			//Reload configs
			WerewolfPlugin.getConfigManager().reloadFiles();
			WerewolfPlugin.getLocaleManager().reloadFiles();
			WerewolfPlugin.getItemManager().reloadRecipes();
			WerewolfPlugin.getMoonManager().reloadMoons();
			
			//Reload tasks
			WerewolfPlugin.getWerewolfManager().endCycles();
			WerewolfPlugin.getWerewolfManager().startArmorChecker();
			WerewolfPlugin.getWerewolfManager().startFormChecker();
			WerewolfPlugin.getWerewolfManager().startCureChecker();
			WerewolfPlugin.getWerewolfManager().startScentChecker();
			WerewolfPlugin.getWerewolfManager().startWeaponChecker();
			WerewolfPlugin.getWerewolfManager().startVampirismChecker();
			
			WerewolfPlugin.getClanManager().endCycle();
			WerewolfPlugin.getClanManager().startAlphaChecker();
			WerewolfPlugin.getClanManager().reload();
			WerewolfPlugin.getMessageManager().reloadText();

			WerewolfPlugin.getItemManager().reloadRecipes();
			WerewolfPlugin.getItemManager().endCycles();
			WerewolfPlugin.getItemManager().startItemUpdater();
			WerewolfPlugin.getItemManager().startArmorChecker();

			messages.sendWolfLocale(sender, "admin.reload");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Import Command
	 */
	private class ImportCommand extends SubCommand
	{
		private final LegacyWerewolfDataManager legacyWerewolfData;
		private final LegacyAlphaDataManager legacyAlphaData;
		
		public ImportCommand()
		{
			super("import");
			
			legacyWerewolfData = WerewolfPlugin.getLegacyDataManager();
			legacyAlphaData = WerewolfPlugin.getLegacyAlphaDataManager();
		}

		//We already know this will always spit back these kind of lists
		//ONLY RUN WHEN NO PLAYERS ARE ONLINE!!
		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			messages.sendWolfLocale(sender, "processing");
			Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
			{
				List<Werewolf> oldWerewolves = legacyWerewolfData.loadFromFile("werewolves");
				List<UUID> alphaIds = legacyAlphaData.loadFromFile("clans");

				//Cycle through all the old data and import them.
				//Give back the Alpha status to the old Alpha
				//This overrides current Alphas
				if (oldWerewolves.size() > 0)
				{
					for (Werewolf werewolf : oldWerewolves)
					{
						werewolves.storeWerewolf(werewolf);
						Clan clan = clans.getClan(werewolf);

						if (alphaIds.contains(werewolf.getUUID()))
							clan.makeAlpha(werewolf);

						//Load up Werewolf data if the werewolf is online
						Player player = Bukkit.getPlayer(werewolf.getUUID());
						if (player != null)
						{
							werewolves.getOfflineWerewolf(player.getUniqueId());
						}
					}

					messages.sendWolfLocale(sender, "admin.import.complete");
					return;
				}

				messages.sendWolfLocale(sender, "admin.import.no-files");
			});
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Help Admin Command
	 */
	private class HelpCommand extends SubCommand
	{
		public HelpCommand()
		{
			super("help");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			//Only two pages so no use in doing a huge check for arg types
			String help = messages.getAdminHelpText1();
			
			if (args.length == 2 && args[1].equalsIgnoreCase("2"))
				help = messages.getAdminHelpText2();
			
			//Send help menu
			messages.sendMessage(sender, help);
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			List<String> suggestions = new ArrayList<>();
			if (args.length == 2)
			{
				suggestions.add("1");
				suggestions.add("2");
			}

			return suggestions;
		}
	}
}