package us.rfsmassacre.Werewolf.Origin;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class Moon 
{
	//Static functions moons need
	private static final long WEEKEND = 192000;
	
	public static long getTime(World world)
	{
		//Loop back to 0 at the final hour.
		return world.getFullTime() % WEEKEND;
	}
	
	//Moon Phases
	public enum MoonPhase
	{
		FULL_MOON(13000, 24000, 8),
		WANING_GIBBOUS(37000, 48000, 7),
		LAST_QUARTER(61000, 72000, 6),
		WANING_CRESCENT(85000, 94000, 5),
		NEW_MOON(109000, 120000, 4),
		WAXING_CRESCENT(133000, 144000, 3),
		FIRST_QUARTER(157000, 168000, 2),
		WAXING_GIBBOUS(181000, 192000, 1);
		
		private long start;
		private long end;
		private int position;
		
		MoonPhase(long start, long end, int position)
		{
			this.start = start;
			this.end = end;
			this.position = position;
		}
		
		public long getStart()
		{
			return this.start;
		}
		public long getEnd()
		{
			return this.end;
		}
		public int getPosition()
		{
			return this.position;
		}
		
		public boolean inCycle(World world)
		{
			long ticks = getTime(world);
			return (ticks >= start && ticks <= end);
		}
		
		public static MoonPhase fromString(String name)
		{
			for (MoonPhase phase : MoonPhase.values())
			{
				if (name.equalsIgnoreCase(phase.name()))
					return phase;
			}
			
			return null;
		}
	}
	
	private World world;
	private List<UUID> transformedIds; //Ensures WWs can't relog for more levels
	private int taskId;
	
	//This is so we don't need to constantly keep looping through every tick.
	//Before each loop, check if we're still in the same phase!
	private WerewolfManager werewolves;
	private MessageManager messages;
	
	public Moon(final World world)
	{
		setWorld(world);
		
		transformedIds = new ArrayList<>();
		werewolves = WerewolfPlugin.getWerewolfManager();
		messages = WerewolfPlugin.getMessageManager();
		
		startCycle();
	}
	
	public void startCycle()
	{
		//Continuously force Werewolves outside to transform
		//Done this long to only use one single thread. Too many threads cause lag
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), () -> {
			if (!werewolves.getOnlineWerewolves().isEmpty())
			{
				//Trigger all outdoor Werewolves to transform without control during full moons
				if (isFullMoon())
				{
					for (Player player : world.getPlayers())
					{
						//Untransform if you're in wolf form to ensure you get a level when transforming again
						Werewolf werewolf = werewolves.getWerewolf(player);
						if (werewolf != null && werewolf.isOutside() && !werewolf.inWolfForm())
						{
							//Attempt to transform werewolf
							if (werewolf.transform())
							{
								messages.sendWolfLocale(werewolf.getPlayer(), "full-moon.transformed");
								//Add level if they haven't leveled this night
								if (!transformedIds.contains(werewolf.getUUID()))
								{
									werewolf.addLevel();
									transformedIds.add(werewolf.getUUID());
								}
							}
						}
					}
				}
				else //Else if it's no longer the full moon
				{
					//If successfully untransformed, remove from list
					//This is so we can keep trying to untransform anyone who failed to untransform
					ListIterator<UUID> iterator = transformedIds.listIterator();
					while (iterator.hasNext())
					{
						Werewolf werewolf = werewolves.getWerewolf(iterator.next());
						if (werewolf != null && werewolf.inWolfForm())
						{
							if (werewolf.untransform())
							{
								messages.sendWolfLocale(werewolf.getPlayer(), "full-moon.morning");
								iterator.remove();
							}
						}
						else //This means they logged off or untransformed and no longer need to be tracked
							iterator.remove();
					}
				}
			}
		}, 0L, WerewolfPlugin.getConfigManager().getInt("intervals.moon-cycle"));
	}
	public void endCycle()
	{
		//In case we need to stop the moon cycle for a relaod
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(taskId);
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	public World getWorld()
	{
		return this.world;
	}
	
	/*
	 * TIME MANAGEMENT FUNCTIONS
	 */
	public boolean isDayTime()
	{
		return getCurrentPhase() == null;
	}
	public boolean isNightTime()
	{
		return getCurrentPhase() != null;
	}
	public boolean isFullMoon()
	{
		return MoonPhase.FULL_MOON.inCycle(world);
	}
	public MoonPhase getCurrentPhase()
	{
		for (MoonPhase phase : MoonPhase.values())
		{
			if (phase.inCycle(this.world))
				return phase;
		}
		
		return null;
	}
	public MoonPhase getNextPhase()
	{
		//Cycle through the shortest start order so logically if
		//it's true earlier, it's gotta be the next phase.
		for (MoonPhase phase : MoonPhase.values())
		{
			if (getTime(this.world) <= phase.start)
				return phase;
		}
		
		return null;
	}
	//Might be buggy in use
	public void setMoonPhase(MoonPhase phase)
	{
		this.world.setFullTime(phase.getStart());
	}
}
