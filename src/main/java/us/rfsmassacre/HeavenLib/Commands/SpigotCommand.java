package us.rfsmassacre.HeavenLib.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.rfsmassacre.Werewolf.Managers.MessageManager;

/**
 * Easier way to set up commands for Spigot Plugins.
 */
public abstract class SpigotCommand implements TabExecutor
{
	protected final MessageManager locale;
	protected final String pluginName;
	protected final String commandName;
	private final LinkedHashMap<String, SubCommand> subCommands;

	public SpigotCommand(MessageManager locale, String pluginName, String commandName)
	{
		this.locale = locale;
		this.pluginName = pluginName;
		this.commandName = commandName;
		this.subCommands = new LinkedHashMap<>();
	}

	/**
	 * Setup locale and command name when instantiating.
	 *
	 * @param locale Locale.
	 * @param commandName Command name.
	 */
	public SpigotCommand(MessageManager locale, String commandName)
	{
		this(locale, "werewolf", commandName);
	}

	/**
	 * When the sender runs a command.
	 *
	 * @param sender CommandSender.
	 * @param command Command given by plugin.yml file.
	 * @param label Label given by plugin.
	 * @param args Arguments given by sender.
	 * @return Boolean: Whether it succeeded or failed.
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			String[] args)
	{
		if (subCommands.isEmpty())
		{
			//All commands MUST have a main sub-command.
			//If you want a command with no arguments, set the name to a blank string.
			return false;
		}
		else if (args.length == 0)
		{
			//If no arguments are given, always run the first sub-command.
			subCommands.values().iterator().next().execute(sender, args);
			return true;
		}
		else
		{
			//If arguments are given, cycle through the right one.
			//If none found, it'll give an error defined.
			String argument = args[0].toLowerCase();
			if (subCommands.containsKey(argument))
			{
				subCommands.get(argument).execute(sender, args);
				return true;
			}
		}

		onInvalidArgs(sender);
		return true;
	}

	/**
	 * When the sender enters arguments.
	 * @param sender CommandSender.
	 * @param command Command given by plugin.yml file.
	 * @param alias Alias given by plugin.yml file.
	 * @param args Arguments given by sender.
	 * @return List of suggestions.
	 */
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
			String[] args)
	{
		if (args.length == 0)
		{
			return Collections.emptyList();
		}
		else if (args.length == 1)
		{
			//All subcommand names should be showed when typing the command in.
			List<String> suggestions = new ArrayList<>();
			for (String subName : subCommands.keySet())
			{
				SubCommand subCommand = subCommands.get(subName);
				if (subName.toLowerCase().startsWith(args[args.length - 1].toLowerCase()) &&
						sender.hasPermission(subCommand.permission))
				{
					suggestions.add(subName);
				}
			}
			return suggestions;
		}
		else
		{
			//This section should show the argument of each subcommand
			List<String> suggestions = new ArrayList<>();
			for (SubCommand subCommand : subCommands.values())
			{
				if (subCommand.name.equalsIgnoreCase(args[0]))
				{
					List<String> tab = subCommand.onTabComplete(sender, args);
					if (tab == null || tab.isEmpty())
					{
						return Collections.emptyList();
					}

					for (String suggestion : tab)
					{
						if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						{
							suggestions.add(suggestion);
						}
					}
				}
			}
			return suggestions;
		}
	}

	/**
	 * Adds subCommand to the map.
	 * @param subCommand SubCommand.
	 */
	protected void addSubCommand(SubCommand subCommand)
	{
		subCommands.put(subCommand.name, subCommand);
	}

	/**
	 * Removes subCommand from the map.
	 * @param subCommand SubCommand.
	 */
	protected void removeSubCommand(SubCommand subCommand)
	{
		subCommands.remove(subCommand.name);
	}

	/**
	 * Broken down commands within a larger command in order to make running commands easier.
	 * Simply implement each subCommand, add them to the map, and it will run for you.
	 */
	protected abstract class SubCommand
	{
		protected final String name;
		protected final String permission;

		public SubCommand(String name, String permission)
		{
			this.name = name;
			this.permission = permission;
		}

		public SubCommand(String name)
		{
			this.name = name;
			this.permission = (pluginName == null || pluginName.isEmpty() ? "" : pluginName + ".") + commandName +
					(name == null || name.isEmpty() ? "" : "." + name);
		}

		/**
		 * Check if sender is a player. (This assumes the opposite is console.)
		 * @param sender CommandSender.
		 * @return Boolean: Whether it is the opposite of a player.
		 */
		public boolean isConsole(CommandSender sender)
		{
			return !(sender instanceof Player);
		}

		/**
		 * Execute onRun() or onFail() on permission.
		 * @param sender CommandSender.
		 * @param args Array of command arguments.
		 */
		public void execute(CommandSender sender, String[] args)
		{
			if (sender.hasPermission(this.permission))
			{
				onCommandRun(sender, args);
			}
			else
			{
				onCommandFail(sender);
			}
		}

		/**
		 * When the command is run.
		 * @param sender CommandSender.
		 * @param args Array of arguments.
		 */
		protected abstract void onCommandRun(CommandSender sender, String[] args);

		/**
		 * List of suggestions
		 * @param sender CommandSender.
		 * @param args Array of arguments.
		 * @return List of suggestions.
		 */
		protected abstract List<String> onTabComplete(CommandSender sender, String[] args);
	}

	/**
	 * When the command fails.
	 * @param sender CommandSender.
	 */
	protected abstract void onCommandFail(CommandSender sender);

	/**
	 * When the command does not meet the argument requirements.
	 * @param sender CommandSender.
	 */
	protected abstract void onInvalidArgs(CommandSender sender);

	/**
	 * Play sound during a command input.
	 * @param sender CommandSender.
	 * @param sound Sound.
	 * @param volume Volume.
	 * @param pitch Pitch.
	 */
	protected void playSound(CommandSender sender, Sound sound, float volume, float pitch)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			player.playSound(player.getLocation(), sound, volume, pitch);
		}
	}
}
