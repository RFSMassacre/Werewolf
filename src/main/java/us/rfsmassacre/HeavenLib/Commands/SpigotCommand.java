package us.rfsmassacre.HeavenLib.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public abstract class SpigotCommand implements CommandExecutor
{
	/*
	 * SpigotCommand is structured to avoid using long
	 * if-else chains and instead sets up a list of
	 * sub-commands to cycle through when running.
	 * 
	 * If the sub-command equals the argument it calls
	 * for, then it runs the function to execute.
	 */
	
	private String commandName;
	
	protected SubCommand mainCommand;
	protected List<SubCommand> subCommands;
	
	public SpigotCommand(String commandName)
	{
		this.commandName = commandName;
		
		this.subCommands = new ArrayList<>();
		//Remember to define the main command when extending
		//this class.
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{	
		if (mainCommand == null)
		{
			//All commands MUST have a main sub-command.
			return false;
		}
		else if (subCommands.isEmpty() || args.length == 0)
		{
			//If no arguments are given, always run the main sub-command.
			mainCommand.execute(sender, args);
			return true;
		}
		else
		{
			//If arguments are given, cycle through the right one.
			//If none found, it'll give an error defined.
			for (SubCommand subCommand : subCommands)
			{
				if (subCommand.equals(args[0]))
				{
					subCommand.execute(sender, args);
					return true;
				}
			}
		}
		
		onInvalidArgs(sender);
		return true;
	}
	
	/*
	 * Define what to run when player has invalid arguments.
	 */
	protected abstract void onInvalidArgs(CommandSender sender);
	
	/*
	 * Define what to run when player doesn't have permission.
	 */
	protected abstract void onCommandFail(CommandSender sender);
	
	/*
	 * SubCommand
	 */
	protected static abstract class SubCommand
	{
		private SpigotCommand command;
		protected String name;
		protected String permission;
		
		public SubCommand(SpigotCommand command, String name)
		{
			this.command = command;
			this.name = name;
			this.permission = command.commandName + "." + name;
		}
		
		public boolean isConsole(CommandSender sender)
		{
			return !(sender instanceof Player);
		}
		
		public boolean equals(String commandName)
		{
			return name.equalsIgnoreCase(commandName);
		}
		
		public void execute(CommandSender sender, String[] args)
		{
			if (sender.hasPermission(this.permission))
				onCommandRun(sender, args);
			else
				command.onCommandFail(sender);
		}
		
		/*
		 * Define what to run when player has permission.
		 */
		protected abstract void onCommandRun(CommandSender sender, String[] args);
	}
}
