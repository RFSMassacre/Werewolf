package us.rfsmassacre.Werewolf.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.rfsmassacre.Werewolf.Events.NewAlphaEvent;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.WerewolfTransformEvent;
import us.rfsmassacre.Werewolf.Managers.SkinManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class SkinListener implements Listener
{	
	private WerewolfManager werewolves;
	private SkinManager skins;
	
	public SkinListener()
	{	
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
				skins.applySkin(werewolves.getWerewolf(event.getPlayer()));
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
                        skins.applySkin(werewolves.getWerewolf(oldAlpha.getPlayer()));
                    }
                    if (newAlpha != null && newAlpha.inWolfForm())
                    {
                        skins.applySkin(werewolves.getWerewolf(newAlpha.getPlayer()));
                    }
                }
            }.runTaskLater(WerewolfPlugin.getInstance(), 1L);
		}
	}
}
