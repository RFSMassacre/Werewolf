package us.rfsmassacre.Werewolf.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.Werewolf.Events.NewAlphaEvent;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.WerewolfTransformEvent;
import us.rfsmassacre.Werewolf.Managers.SkinManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class SkinListener implements Listener
{
	private ConfigManager config;
	private WerewolfManager werewolves;
	private SkinManager skins;
	
	public SkinListener()
	{
		config = WerewolfPlugin.getConfigManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		skins = new SkinManager();
	}

	@EventHandler
	public void onWerewolfTransform(WerewolfTransformEvent event)
	{
		if (!event.isCancelled() && skins != null)
		{
			if (event.toWolfForm())
			{
				skins.applySkinByName(werewolves.getWerewolf(event.getPlayer()), false);
			}
			else
			{
				skins.removeSkin(werewolves.getWerewolf(event.getPlayer()));
			}
		}
	}


	@EventHandler
	public void onNewAlpha(final NewAlphaEvent event)
	{
		if (!event.isCancelled() && skins != null)
		{
			final Werewolf oldAlpha = werewolves.getWerewolf(event.getAlpha());
			final Werewolf newAlpha = werewolves.getWerewolf(event.getNewAlpha());

			new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (oldAlpha != null && oldAlpha.inWolfForm())
                    {
						skins.applySkinByName(werewolves.getWerewolf(oldAlpha.getPlayer()), false);
                    }
                    if (newAlpha != null && newAlpha.inWolfForm())
                    {
						skins.applySkinByName(werewolves.getWerewolf(newAlpha.getPlayer()), false);
                    }
                }
            }.runTaskLater(WerewolfPlugin.getInstance(), 1L);
		}
	}
}
