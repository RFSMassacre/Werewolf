package us.rfsmassacre.Werewolf.Commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import us.rfsmassacre.HeavenLib.Commands.SpigotCommand;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Data.LegacyAlphaDataManager;
import us.rfsmassacre.Werewolf.Data.LegacyWerewolfDataManager;
import us.rfsmassacre.Werewolf.Items.WerewolfItem.WerewolfItemType;
import us.rfsmassacre.Werewolf.Managers.ClanManager;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;
import us.rfsmassacre.Werewolf.Origin.Moon.MoonPhase;

public class WerewolfAdminCommand extends SpigotCommand
{
	private ConfigManager config;
	private ClanManager clans;
	private WerewolfManager werewolves;
	private MessageManager messages;
	
	public WerewolfAdminCommand() 
	{
		super("werewolfadmin");
		
		this.config = WerewolfPlugin.getConfigManager();
		this.clans = WerewolfPlugin.getClanManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();
		
		this.mainCommand = this.new MainCommand(this);
		this.subCommands.add(this.new SpawnCommand(this));
		this.subCommands.add(this.new TransformCommand(this));
		this.subCommands.add(this.new InfectCommand(this));
		this.subCommands.add(this.new CureCommand(this));
		this.subCommands.add(this.new SetAlphaCommand(this));
		this.subCommands.add(this.new SetLevelCommand(this));
		this.subCommands.add(this.new AddLevelCommand(this));
		this.subCommands.add(this.new SetPhaseCommand(this));
		this.subCommands.add(this.new PurgeCommand(this));
		this.subCommands.add(this.new ReloadCommand(this));
		this.subCommands.add(this.new ImportCommand(this));
		this.subCommands.add(this.new HelpCommand(this));
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
		public MainCommand(SpigotCommand command) 
		{
			super(command, "");
			
			this.permission = "werewolf.admin";
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			String menu = messages.getAdminText();
			int werewolfAmount = werewolves.getWerewolfAmount();
			menu = menu.replace("{werewolves}", Integer.toString(werewolfAmount));
			if (werewolfAmount == 1)
				menu = menu.replace("Werewolves", "Werewolf");
			
			int witherfangs = clans.getClan(ClanType.WITHERFANG).getSize();
			int silvermanes = clans.getClan(ClanType.SILVERMANE).getSize();
			int bloodmoons = clans.getClan(ClanType.BLOODMOON).getSize();
			
			menu = menu.replace("{wf-members}", Integer.toString(witherfangs))
					   .replace("{sm-members}", Integer.toString(silvermanes))
					   .replace("{bm-members}", Integer.toString(bloodmoons));
			
			sender.sendMessage(ChatManager.format(menu));
		}
	}
	
	/*
	 * Spawn Item Command
	 */
	private class SpawnCommand extends SubCommand
	{
		public SpawnCommand(SpigotCommand command) 
		{
			super(command, "spawn");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			ItemManager items = WerewolfPlugin.getItemManager();
			
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (args.length >= 2)
				{
					WerewolfItemType type = WerewolfItemType.fromString(args[1].toUpperCase());
					
					if (type != null)
					{
						ItemStack item = items.getWerewolfItem(type).getItem();
						player.getInventory().addItem(item);
			
						messages.sendWolfLocale(player, "admin.spawn.success",
								"{item}", item.getItemMeta().getDisplayName());
						return;
					}
					
					//Invalid arg error
					messages.sendWolfLocale(sender, "admin.spawn.no-args");
					return;
				}
				else
				{
					//Give console error
					messages.sendWolfLocale(sender, "admin.spawn.console");
					return;
				}
			}
		}
	}
	
	/*
	 * Transform Others Command
	 */
	private class TransformCommand extends SubCommand
	{
		public TransformCommand(SpigotCommand command) 
		{
			super(command, "transform");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length == 2)
			{
				Player player = Bukkit.getPlayer(args[1]);
				if (werewolves.isWerewolf(player))
				{
					if (WerewolfPlugin.getMoonManager().isFullMoon(player.getWorld()))
					{
						//Send full moon cancels transform message
						messages.sendWolfLocale(sender, "admin.transform.full-moon");
						return;
					}
					else
					{
						Werewolf werewolf = werewolves.getWerewolf(player);
						if (!werewolf.inWolfForm())
						{
							werewolf.transform();
							messages.sendWolfLocale(player, "transform.to-form");
						}
						else
						{
							werewolf.untransform();
							messages.sendWolfLocale(player, "transform.from-form");
						}
						
						messages.sendWolfLocale(sender, "admin.transform.success",
								"{werewolf}", werewolf.getDisplayName());
						
						return;
					}
				}
				else
				{
					messages.sendWolfLocale(sender, "admin.transform.not-infected",
							"{player}", player.getDisplayName());
					return;
				}
			}
			
			//Give not Werewolf error
			messages.sendWolfLocale(sender, "admin.transform.no-args");
		}
	}
	
	/*
	 * Infect Command
	 */
	private class InfectCommand extends SubCommand
	{
		public InfectCommand(SpigotCommand command) 
		{
			super(command, "infect");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length >= 3)
			{
				Player player = Bukkit.getPlayer(args[1]);
				ClanType type = ClanType.fromString(args[2]);
				Clan clan = clans.getClan(type);
				
				if (player != null && type != null)
				{
					if (werewolves.isHuman(player))
					{
						werewolves.infectWerewolf(player, type);
						clan.addMember(player);
						messages.sendWolfLocale(sender, "admin.infect.success",
								"{player}", player.getDisplayName(),
								"{clan}", type.toString());
					}
					else
					{
						messages.sendWolfLocale(sender, "admin.infect.not-human",
								"{player}", player.getDisplayName());
					}
					
					return;
				}
			}
			
			messages.sendWolfLocale(sender, "admin.infect.no-args");
		}
	}
	
	/*
	 * Cure Command
	 */
	private class CureCommand extends SubCommand
	{
		public CureCommand(SpigotCommand command) 
		{
			super(command, "cure");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (args.length >= 2)
			{
				Player player = Bukkit.getPlayer(args[1]);
				
				if (player != null)
				{
					if (werewolves.isWerewolf(player))
					{
						Werewolf werewolf = werewolves.getWerewolf(player);
						Clan clan = werewolf.getClan();
						
						werewolves.cureWerewolf(werewolf);
						clan.removeMember(player);
						
						messages.sendWolfLocale(sender, "admin.cure.success",
								"{player}", player.getDisplayName());
					}
					else
					{
						messages.sendWolfLocale(sender, "admin.cure.not-infected",
								"{player}", player.getDisplayName());
					}
					
					return;
				}
			}
			
			messages.sendWolfLocale(sender, "admin.cure.no-args");
		}
	}
	
	/*
	 * Set Alpha Command
	 */
	private class SetAlphaCommand extends SubCommand
	{
		public SetAlphaCommand(SpigotCommand command) 
		{
			super(command, "setalpha");
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
	}
	
	/*
	 * Set Level Command
	 */
	private class SetLevelCommand extends SubCommand
	{
		public SetLevelCommand(SpigotCommand command) 
		{
			super(command, "setlevel");
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
	}
	
	/*
	 * Add Level Command
	 */
	private class AddLevelCommand extends SubCommand
	{
		public AddLevelCommand(SpigotCommand command) 
		{
			super(command, "addlevel");
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
	}
	
	/*
	 * Set Phase Command
	 */
	private class SetPhaseCommand extends SubCommand
	{
		public SetPhaseCommand(SpigotCommand command) 
		{
			super(command, "setphase");
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
							return;
						}
						else
						{
							//No moon in this world error
							messages.sendWolfLocale(sender, "admin.setphase.no-moon");
							return;
						}
					}
					else
					{
						//World blacklisted
						messages.sendWolfLocale(sender, "admin.setphase.blocked-world");
						return;
					}
				}
				
				//Invalid args
				messages.sendWolfLocale(sender, "admin.setphase.no-args");
				return;
			}
			else
			{
				//Console error
				messages.sendWolfLocale(sender, "admin.setphase.console");
				return;
			}
		}
	}
	
	/*
	 * Purge Command
	 */
	private class PurgeCommand extends SubCommand
	{
		public PurgeCommand(SpigotCommand command) 
		{
			super(command, "purge");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			werewolves.purgeBrokenFiles();
			
			messages.sendWolfLocale(sender, "admin.purge");
		}
	}
	
	/*
	 * Reload Command
	 */
	private class ReloadCommand extends SubCommand
	{
		public ReloadCommand(SpigotCommand command) 
		{
			super(command, "reload");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			//Reload configs
			WerewolfPlugin.getConfigManager().reloadFiles();
			WerewolfPlugin.getLocaleManager().reloadFiles();
			//WerewolfPlugin.getItemManager().reloadRecipes();
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
			WerewolfPlugin.getClanManager().reloadMenus();
			WerewolfPlugin.getMessageManager().reloadText();

			WerewolfPlugin.getItemManager().endCycles();
			WerewolfPlugin.getItemManager().startItemUpdater();
			WerewolfPlugin.getItemManager().startArmorChecker();

			messages.sendWolfLocale(sender, "admin.reload");
		}	
	}
	
	/*
	 * Import Command
	 */
	private class ImportCommand extends SubCommand
	{
		private LegacyWerewolfDataManager legacyWerewolfData;
		private LegacyAlphaDataManager legacyAlphaData;
		
		public ImportCommand(SpigotCommand command) 
		{
			super(command, "import");
			
			legacyWerewolfData = WerewolfPlugin.getLegacyDataManager();
			legacyAlphaData = WerewolfPlugin.getLegacyAlphaDataManager();
		}

		//We already know this will always spit back these kind of arraylists
		@SuppressWarnings("unchecked")
		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			ArrayList<Werewolf> oldWerewolves = (ArrayList<Werewolf>)legacyWerewolfData.loadFromFile("werewolves");
			ArrayList<UUID> alphaIds = (ArrayList<UUID>)legacyAlphaData.loadFromFile("clans");
			
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
						werewolves.loadWerewolf(player);
				}
				
				messages.sendWolfLocale(sender, "admin.import.complete");
				return;
			}
			
			messages.sendWolfLocale(sender, "admin.import.no-files");
		}	
	}
	
	/*
	 * Help Admin Command
	 */
	private class HelpCommand extends SubCommand
	{
		public HelpCommand(SpigotCommand command) 
		{
			super(command, "help");
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
	}
}