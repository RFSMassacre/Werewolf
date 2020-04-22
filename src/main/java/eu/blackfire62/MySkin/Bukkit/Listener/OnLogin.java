/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package eu.blackfire62.MySkin.Bukkit.Listener;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import eu.blackfire62.MySkin.Shared.SkinProperty;
import eu.blackfire62.MySkin.Shared.Util.MojangAPI;
import eu.blackfire62.MySkin.Shared.Util.MojangAPIException;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class OnLogin
implements Listener {
    private WerewolfPlugin myskin;

    public OnLogin(WerewolfPlugin myskin) {
        this.myskin = myskin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onLogin(final PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        this.myskin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.myskin, new Runnable(){

            @Override
            public void run() {
                try {
                    Player p = e.getPlayer();
                    UUID skin = OnLogin.this.myskin.getSkinCache().loadSkinOfPlayer(p.getUniqueId());
                    if (skin == null) {
                        UUID premiumuuid = OnLogin.this.myskin.getSkinCache().loadUUID(p.getName());
                        if (premiumuuid == null) {
                            premiumuuid = MojangAPI.getUUID(p.getName());
                            OnLogin.this.myskin.getSkinCache().saveUUID(p.getName(), premiumuuid);
                        }
                        skin = premiumuuid;
                    }
                    try {
                        SkinProperty property = MojangAPI.getSkinProperty(skin);
                        if (property != null) {
                            OnLogin.this.myskin.getSkinHandler().setSkinProperty(p, property);
                            OnLogin.this.myskin.getSkinHandler().update(p);
                            OnLogin.this.myskin.getSkinCache().saveSkinProperty(skin, property);
                        }
                    }
                    catch (Exception e2) {
                        SkinProperty property = OnLogin.this.myskin.getSkinCache().loadSkinProperty(skin);
                        if (property != null) {
                            OnLogin.this.myskin.getSkinHandler().setSkinProperty(p, property);
                            OnLogin.this.myskin.getSkinHandler().update(p);
                        }
                    }
                }
                catch (MojangAPIException ex) {
                    // empty catch block
                }
                catch (Exception ex) {
                    // empty catch block
                }
            }
        });
    }

}

