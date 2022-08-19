package us.rfsmassacre.Werewolf.Managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Moon;
import us.rfsmassacre.Werewolf.Origin.Moon.MoonPhase;

public class MoonManager 
{	
	private static ConfigManager config;
	private static Map<World, Moon> moons;
	
	public MoonManager()
	{
		config = WerewolfPlugin.getConfigManager();
		setMoons(new HashMap<>());
		
		reloadMoons();
	}
	
	public void reloadMoons()
	{
		if (!moons.isEmpty())
		{
			for (Moon moon : moons.values())
			{
				moon.endCycle();
			}
			
			moons.clear();
		}
		
		List<String> blockedWorlds = config.getStringList("blocked-worlds");
		for (World world : Bukkit.getWorlds())
		{
			if (!blockedWorlds.contains(world.getName())
			& world.getEnvironment().equals(Environment.NORMAL))
			{
				addMoon(new Moon(world));
			}
		}
	}
	
	private void setMoons(HashMap<World, Moon> newMoons)
	{
		moons = newMoons;
	}
	
	public void addMoon(Moon moon)
	{
		moons.put(moon.getWorld(), moon);
	}
	private Moon getMoon(World world)
	{
		return moons.get(world);
	}
	//Cancel cycles before deleting to ensure it's no longer running
	public void removeMoon(Moon moon)
	{
		moon.endCycle();
		moons.remove(moon.getWorld());
	}
	public void removeMoon(World world)
	{
		Moon moon = getMoon(world);
		if (moon == null)
			return;
			
		moon.endCycle();
		moons.remove(world);
	}
	
	//Get moon phase of this world
	public MoonPhase getMoonPhase(World world)
	{
		Moon moon = getMoon(world);
		if (moon == null)
			return null;
		
		return (moon.getCurrentPhase() != null ? moon.getCurrentPhase() : moon.getNextPhase());
	}
	
	//Check for phases
	//Full moons never occur without a moon
	public boolean isFullMoon(World world)
	{
		Moon moon = getMoon(world);
		if (moon == null)
			return false;
		
		return moon.isFullMoon();
	}
	//No moon = daytime
	public boolean isDayTime(World world)
	{
		Moon moon = getMoon(world);
		if (moon == null)
			return true;
		
		return moon.isDayTime();
	}
	//No moon != night time
	public boolean isNightTime(World world)
	{
		Moon moon = getMoon(world);
		if (moon == null)
			return false;
		
		return moon.isNightTime();
	}
	
	public void setMoonPhase(MoonPhase phase, World world)
	{
		Moon moon = getMoon(world);
		if (moon != null)
			moon.setMoonPhase(phase);
	}
}
