package us.rfsmassacre.Werewolf.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.rfsmassacre.HeavenLib.Commands.SpigotCommand;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.Origin.Moon.MoonPhase;

public class WerewolfCommand extends SpigotCommand
{
	private final ConfigManager config;
	private final WerewolfManager werewolves;
	private final MessageManager messages;
	
	public WerewolfCommand() 
	{
		super("werewolf");
		
		this.config = WerewolfPlugin.getConfigManager();
		this.werewolves = WerewolfPlugin.getWerewolfManager();
		this.messages = WerewolfPlugin.getMessageManager();
		
		this.mainCommand = this.new MainCommand(this);
		this.subCommands.add(new ListCommand(this));
		this.subCommands.add(this.new ClanCommand(this));
		this.subCommands.add(this.new TransformCommand(this));
		this.subCommands.add(this.new TrackCommand(this));
		this.subCommands.add(this.new IntentCommand(this));
		this.subCommands.add(this.new HowlCommand(this));
		this.subCommands.add(this.new GrowlCommand(this));
		this.subCommands.add(new HelpCommand(this));
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
		public MainCommand(SpigotCommand command)
		{
			super(command, "");
			
			this.permission = "werewolf.werewolf";
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
						default:
							menu = menu.replace("{days}", config.getString("menu.days.later"));
							menu = menu.replace("{time}", Integer.toString(daysLeft));
							break;
						case 8:
							menu = menu.replace("{days}", config.getString("menu.days.tonight"));
							break;
						case 1:
							menu = menu.replace("{days}", config.getString("menu.days.tomorrow"));
							break;
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
	}
	
	/*
	 * List Command
	 */
	private static class ListCommand extends SubCommand
	{
		public ListCommand(SpigotCommand command) 
		{
			super(command, "list");
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
					String clanList = messages.getMembersList(clan, 0);
					if (args.length >= 2)
					{
						try
						{
							int pageNum = Integer.parseInt(args[1]);
							clanList = messages.getMembersList(clan, pageNum - 1);
							
							//Show specified page
							messages.sendMessage(player, clanList);
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
						messages.sendMessage(player, clanList);
						return;
					}
					
					messages.sendWolfLocale(sender, "clan.invalid-arg");
					return;
				}
			}
			
			//Send error
			messages.sendWolfLocale(sender, "clan.no-clan");
		}
	}
	
	/*
	 * Clan Command
	 */
	private class ClanCommand extends SubCommand
	{
		public ClanCommand(SpigotCommand command) 
		{
			super(command, "clan");
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
	}
	
	/*
	 * Transform Command
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
	}
	
	/*
	 * Track Command
	 */
	private class TrackCommand extends SubCommand
	{
		public TrackCommand(SpigotCommand command) 
		{
			super(command, "track");
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
	}
	
	/*
	 * Intent Command
	 */
	private class IntentCommand extends SubCommand
	{
		public IntentCommand(SpigotCommand command) 
		{
			super(command, "intent");
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
	}
	
	/*
	 * Howl Command
	 */
	private class HowlCommand extends SubCommand
	{
		public HowlCommand(SpigotCommand command) 
		{
			super(command, "howl");
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
	}
	
	/*
	 * Growl Command
	 */
	private class GrowlCommand extends SubCommand
	{
		public GrowlCommand(SpigotCommand command) 
		{
			super(command, "growl");
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
	}
	
	/*
	 * Help Command
	 */
	private static class HelpCommand extends SubCommand
	{
		public HelpCommand(SpigotCommand command) 
		{
			super(command, "help");
		}

		@Override
		protected void onCommandRun(CommandSender sender, String[] args) 
		{
			//Only two pages so no use in doing a huge check for arg types
			MessageManager messages = WerewolfPlugin.getMessageManager();
			String help = messages.getHelpText1();
			
			if (args.length == 2 && args[1].equalsIgnoreCase("2"))
				help = messages.getHelpText2();
			
			//Send help menu
			messages.sendMessage(sender, help);
		}
	}
}