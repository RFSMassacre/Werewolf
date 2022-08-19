package us.rfsmassacre.Werewolf.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.Werewolf.Events.NewAlphaEvent;
import us.rfsmassacre.Werewolf.Origin.Werewolf;
import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Events.WerewolfTransformEvent;
import us.rfsmassacre.Werewolf.Managers.SkinManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class SkinListener implements Listener
{
	private final ConfigManager config;
	private final WerewolfManager werewolves;
	private final SkinManager skins;
	
	public SkinListener()
	{
		config = WerewolfPlugin.getConfigManager();
		werewolves = WerewolfPlugin.getWerewolfManager();
		skins = WerewolfPlugin.getSkinManager();
	}

	@EventHandler(ignoreCancelled = true)
	public void onWerewolfTransform(WerewolfTransformEvent event)
	{
		if (skins != null)
		{
			Werewolf werewolf = werewolves.getWerewolf(event.getPlayer());
			if (event.toWolfForm())
			{
				skins.applySkin(werewolf);
			}
			else
			{
				skins.removeSkin(werewolf);
			}
		}
	}


	@EventHandler(ignoreCancelled = true)
	public void onNewAlpha(final NewAlphaEvent event)
	{
		if (skins != null)
		{
			final Werewolf oldAlpha = werewolves.getWerewolf(event.getAlpha());
			final Werewolf newAlpha = werewolves.getWerewolf(event.getNewAlpha());

			if (oldAlpha != null && oldAlpha.inWolfForm())
			{
				skins.applySkin(werewolves.getWerewolf(oldAlpha.getPlayer()));
			}
			if (newAlpha != null && newAlpha.inWolfForm())
			{
				skins.applySkin(werewolves.getWerewolf(newAlpha.getPlayer()));
			}
		}
	}
}
