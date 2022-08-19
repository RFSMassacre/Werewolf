package us.rfsmassacre.Werewolf.Listeners;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.DependencyManager;
import us.rfsmassacre.Werewolf.Events.TrackerTargetEvent;
import us.rfsmassacre.Werewolf.Events.WerewolfSniffEvent;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class RegionListener implements Listener
{
    private final ConfigManager config;
    private final DependencyManager dependency;
    private final MessageManager messages;

    public RegionListener()
    {
        config = WerewolfPlugin.getConfigManager();
        dependency = WerewolfPlugin.getDependencyManager();
        messages = WerewolfPlugin.getMessageManager();
    }

    /*
     * Prevent prey from ruining trackers by staying in safe zones.
     *
     * WorldGuard
     *
     * I only know about the set.allow method. If any better methods are shown, I will update later.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTargetSafeZone(TrackerTargetEvent event)
    {
        if (dependency.hasPlugin("WorldGuard") && config.getBoolean("support.WorldGuard"))
        {
            WorldGuardPlugin plugin = WorldGuardPlugin.inst();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            Player target = event.getTarget();
            LocalPlayer localTarget = plugin.wrapPlayer(target);
            Location location = localTarget.getLocation();

            if (!query.testState(location, localTarget, Flags.PVP)
                    || !query.testState(location, localTarget, Flags.INVINCIBILITY)) {
                if (config.getBoolean("hunting.target.safe-zones")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /*
     * Prevent Werewolves from tracking players in safe zones.
     *
     * WorldGuard
     */
    @EventHandler(ignoreCancelled = true)
    public void onSniffSafeZone(WerewolfSniffEvent event)
    {
        if (dependency.hasPlugin("WorldGuard") && config.getBoolean("support.WorldGuard"))
        {
            WorldGuardPlugin plugin = WorldGuardPlugin.inst();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            Player target = event.getTarget();
            LocalPlayer localTarget = plugin.wrapPlayer(target);
            Location location = localTarget.getLocation();

            if (!query.testState(location, localTarget, Flags.PVP)
                    || !query.testState(location, localTarget, Flags.INVINCIBILITY)) {
                if (config.getBoolean("track.safe-zones")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
