package us.rfsmassacre.Werewolf.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.rfsmassacre.HeavenLib.Commands.SpigotCommand;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Managers.ClanManager;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Moon.MoonPhase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WerewolfCommand extends SpigotCommand
{
	private final ConfigManager config;
	private final WerewolfManager werewolves;
	private final ClanManager clans;
	private final MessageManager messages;
	
	public WerewolfCommand() 
	{
		super(WerewolfPlugin.getMessageManager(), "werewolf");
		
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.clans = WerewolfPlugin.getClanManager();
		this.messages = WerewolfPlugin.getMessageManager();
		
		addSubCommand(new MainCommand());
		addSubCommand(new ListCommand());
		addSubCommand(new ClanCommand());
		addSubCommand(new TransformCommand());
		addSubCommand(new TrackCommand());
		addSubCommand(new IntentCommand());
		addSubCommand(new HowlCommand());
		addSubCommand(new GrowlCommand());
		addSubCommand(new HelpCommand());
	}

	@Override
	protected void onInvalidArgs(CommandSender sender) 
	{
		messages.sendWolfLocale(sender, "invalid.main-args");
	}
	@Override
	protected void onCommandFail(CommandSender sender) 
	{
		messages.sendWolfLocale(sender, "invalid.no-permission");
	}

	/*
	 * Main Command - No args
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
			String menu = messages.getMainText();
			int werewolfAmount = werewolves.getWerewolfAmount();
			menu = menu.replace("{werewolves}", Integer.toString(werewolfAmount));
			
			//Fix grammar for the grammar nazis
			if (werewolfAmount == 1)
			{
				menu = menu.replace("Werewolves", "Werewolf");
				menu = menu.replace("roam", "roams");
			}
			
			if (!isConsole(sender))
			{	
				//Player race
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					
					int level = werewolf.getLevel();
					menu = menu.replace("{race}", config.getString("menu.race.werewolf"));
					menu = menu.replace("{level}", Integer.toString(level)).replace("{clan}", werewolf.getType().toString());
				}
				else if (werewolves.isVampire(player))
				{
					menu = menu.replace("{race}", config.getString("menu.race.vampire"));
				}
				else
				{
					menu = menu.replace("{race}", config.getString("menu.race.human"));
				}
				
				//Current world phase and days left
				MoonPhase phase = WerewolfPlugin.getMoonManager().getMoonPhase(player.getWorld());
				if (phase != null)
				{
					menu = menu.replace("{phase}", config.getString("moon-phases." + phase));
					
					int daysLeft = phase.getPosition();
					
					if (MoonPhase.FULL_MOON.inCycle(player.getWorld()))
						menu = menu.replace("{days}", config.getString("menu.days.now"));

					switch (daysLeft)
					{
						default ->
						{
							menu = menu.replace("{days}", config.getString("menu.days.later"));
							menu = menu.replace("{time}", Integer.toString(daysLeft));
						}
						case 8 -> menu = menu.replace("{days}", config.getString("menu.days.tonight"));
						case 1 -> menu = menu.replace("{days}", config.getString("menu.days.tomorrow"));
					}
				}
			}
			else
			{
				menu = menu.replace("{race}", config.getString("menu.not-applied"));
			}
			
			menu = menu.replace("{phase}", config.getString("menu.not-applied"));
			menu = menu.replace("{days}", config.getString("menu.not-applied"));
			
			sender.sendMessage(ChatManager.format(menu));
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * List Command
	 */
	private class ListCommand extends SubCommand
	{
		public ListCommand()
		{
			super("list");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			WerewolfManager werewolves = WerewolfPlugin.getWerewolfManager();
			MessageManager messages = WerewolfPlugin.getMessageManager();
			
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					Clan clan = WerewolfPlugin.getClanManager().getClan(werewolf);
					if (args.length >= 2)
					{
						try
						{
							int pageNum = Integer.parseInt(args[1]);
							messages.sendWolfLocale(sender, "processing");
							messages.sendMembersList(player, clan, pageNum - 1);
							return;
						}
						catch (NumberFormatException exception)
						{
							//Do nothing, this falls back on invalid arg error
						}
					}
					else
					{
						//Default to showing first page of list
						messages.sendWolfLocale(sender, "processing");
						messages.sendMembersList(player, clan, 0);
						return;
					}
					
					messages.sendWolfLocale(sender, "clan.invalid-arg");
					return;
				}
			}
			
			//Send error
			messages.sendWolfLocale(sender, "clan.no-clan");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			if (!(sender instanceof Player player))
			{
				return Collections.emptyList();
			}

			List<String> suggestions = new ArrayList<>();
			if (args.length == 2)
			{
				Werewolf werewolf = werewolves.getWerewolf(player);
				if (werewolf == null)
				{
					return Collections.emptyList();
				}

				double amount = clans.getClan(werewolf).getSize();
				double pages = amount / 6;
				if (pages % 6 == 0)
				{
					pages--;
				}

				for (int page = 0; page < pages; page++)
				{
					suggestions.add(Integer.toString(page + 1));
				}
			}

			return suggestions;
		}
	}
	
	/*
	 * Clan Command
	 */
	private class ClanCommand extends SubCommand
	{
		public ClanCommand()
		{
			super("clan");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					Clan clan = WerewolfPlugin.getClanManager().getClan(werewolf);
					String info = clan.getDescription();

					info = info.replace("{members}", Integer.toString(clan.getSize()));
					if (clan.getSize() == 1)
						info = info.replace("Members", "Member");
					
					//Send menu
					messages.sendMessage(player, info);
					return;
				}
			}
			
			//Send error
			messages.sendWolfLocale(sender, "clan.no-clan");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Transform Command
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
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					if (WerewolfPlugin.getMoonManager().isFullMoon(player.getWorld()))
					{
						//Send full moon cancels transform message
						messages.sendWolfLocale(player, "transform.full-moon");
					}
					else
					{
						Werewolf werewolf = werewolves.getWerewolf(player);
						int transformLvl = config.getInt("maturity.free-transform");
						
						//Let them untransform at any level
						if (werewolf.inWolfForm())
						{
							if (werewolf.untransform())
								messages.sendWolfLocale(player, "transform.from-form");
							
							//Don't do much because whatever prevented the untransformation should send a message
						}
						//But check their level when they go into the form
						else
						{
							if (werewolf.getLevel() >= transformLvl)
							{
								if (werewolf.canTransform())
								{
									if (werewolf.transform())
									{
										messages.sendWolfLocale(player, "transform.to-form");
									}
									else
									{
										messages.sendWolfLocale(player, "transform.cant-transform");
									}

								}
								else
								{
									int nextTransform = werewolf.getNextTransform();
									messages.sendWolfLocale(player, "transform.on-cooldown", 
											"{minutes}", Integer.toString(nextTransform));
								}
							}
							else
							{
								messages.sendWolfLocale(player, "transform.not-leveled",
										"{level}", Integer.toString(transformLvl));
							}
						}
					}

					return;
				}
			}
			
			//Give not Werewolf error
			messages.sendWolfLocale(sender, "transform.not-infected");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Track Command
	 */
	private class TrackCommand extends SubCommand
	{
		public TrackCommand()
		{
			super("track");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					int trackLvl = config.getInt("maturity.scent-track");
					
					if (werewolf.getLevel() >= trackLvl)
					{
						if (werewolf.inWolfForm())
						{
							if (!werewolf.isTracking())
							{
								if (werewolf.getTargetId() != null)
								{
									 Player targetPlayer = Bukkit.getPlayer(werewolf.getTargetId());
									 if (targetPlayer != null)
									 {
										 if (targetPlayer.getWorld().equals(player.getWorld()))
										 {
											 //Set the walk speed to semi-slow
											 if (werewolf.startTracking())
											 {
												 messages.sendWolfLocale(player, "track.to-track", "{player}", targetPlayer.getDisplayName());
												 return;
											 }
										 }
										 else
										 {
											 messages.sendWolfLocale(player, "track.not-in-same-world");
											 return;
										 }
									 }
									 else
									 {
										 messages.sendWolfLocale(player, "track.not-found");
										 return;
									 }
								}
								else
								{
									messages.sendWolfLocale(player, "track.no-target");
									return;
								}
							}
							else
							{
								if(werewolf.stopTracking())
								{
									messages.sendWolfLocale(player, "track.from-track");
									return;
								}
							}
						}
						else
						{
							messages.sendWolfLocale(player, "track.not-in-form");
							return;
						}
					}
					else
					{
						messages.sendWolfLocale(player, "track.not-leveled", 
								"{level}", Integer.toString(trackLvl));
						return;
					}
				}
			}
			
			//Give not Werewolf error
			messages.sendWolfLocale(sender, "track.not-infected");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Intent Command
	 */
	private class IntentCommand extends SubCommand
	{
		public IntentCommand()
		{
			super("intent");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					int transformLvl = config.getInt("maturity.intent");
					if (werewolf.getLevel() >= transformLvl)
					{
						if (!werewolf.hasIntent())
						{
							werewolf.setIntent(true);
							messages.sendWolfLocale(player, "intent.to-intent");
						}
						else
						{
							werewolf.setIntent(false);
							messages.sendWolfLocale(player, "intent.from-intent");
						}
					}
					else
					{
						messages.sendWolfLocale(player, "intent.not-leveled",
								"{level}", Integer.toString(transformLvl));
					}

					return;
				}
			}
			
			//Give not Werewolf error
			messages.sendWolfLocale(sender, "intent.not-infected");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Howl Command
	 */
	private class HowlCommand extends SubCommand
	{
		public HowlCommand()
		{
			super("howl");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					if (werewolf.inWolfForm())
					{
						if (werewolf.canHowl())
						{
							werewolf.howl();
							messages.sendWolfLocale(player, "howl.success");
						}
						else
						{
							int nextHowl = werewolf.getNextHowl();
							messages.sendWolfLocale(player, "howl.on-cooldown", 
									"{minutes}", Integer.toString(nextHowl));
						}
						return;
					}
					
					messages.sendWolfLocale(player, "howl.not-in-form");
					return;
				}
			}
			
			//Give not Werewolf error
			messages.sendWolfLocale(sender, "howl.not-infected");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Growl Command
	 */
	private class GrowlCommand extends SubCommand
	{
		public GrowlCommand()
		{
			super("growl");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			if (!isConsole(sender))
			{
				Player player = (Player)sender;
				if (werewolves.isWerewolf(player))
				{
					Werewolf werewolf = werewolves.getWerewolf(player);
					if (werewolf.inWolfForm())
					{
						if (werewolf.canGrowl())
						{
							werewolf.growl();
							messages.sendWolfLocale(player, "growl.success");
						}
						else
						{
							int nextGrowl = werewolf.getNextGrowl();
							messages.sendWolfLocale(player, "growl.on-cooldown",
									"{minutes}", Integer.toString(nextGrowl));
						}
						return;
					}
					
					messages.sendWolfLocale(player, "growl.not-in-form");
					return;
				}
			}
			
			//Give not Werewolf error
			messages.sendWolfLocale(sender, "growl.not-infected");
		}

		@Override
		protected List<String> onTabComplete(CommandSender sender, String[] args)
		{
			return Collections.emptyList();
		}
	}
	
	/*
	 * Help Command
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
			MessageManager messages = WerewolfPlugin.getMessageManager();
			String help = messages.getHelpText1();
			
			if (args.length == 2 && args[1].equalsIgnoreCase("2"))
			{
				help = messages.getHelpText2();
			}
			
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