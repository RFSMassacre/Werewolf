/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  net.minecraft.server.v1_9_R1.Container
 *  net.minecraft.server.v1_9_R1.DataWatcher
 *  net.minecraft.server.v1_9_R1.DedicatedPlayerList
 *  net.minecraft.server.v1_9_R1.DimensionManager
 *  net.minecraft.server.v1_9_R1.Entity
 *  net.minecraft.server.v1_9_R1.EntityHuman
 *  net.minecraft.server.v1_9_R1.EntityPlayer
 *  net.minecraft.server.v1_9_R1.EnumDifficulty
 *  net.minecraft.server.v1_9_R1.EnumItemSlot
 *  net.minecraft.server.v1_9_R1.ItemStack
 *  net.minecraft.server.v1_9_R1.MathHelper
 *  net.minecraft.server.v1_9_R1.Packet
 *  net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy
 *  net.minecraft.server.v1_9_R1.PacketPlayOutEntityEquipment
 *  net.minecraft.server.v1_9_R1.PacketPlayOutEntityHeadRotation
 *  net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata
 *  net.minecraft.server.v1_9_R1.PacketPlayOutHeldItemSlot
 *  net.minecraft.server.v1_9_R1.PacketPlayOutNamedEntitySpawn
 *  net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo
 *  net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo$EnumPlayerInfoAction
 *  net.minecraft.server.v1_9_R1.PacketPlayOutPosition
 *  net.minecraft.server.v1_9_R1.PacketPlayOutRespawn
 *  net.minecraft.server.v1_9_R1.PlayerConnection
 *  net.minecraft.server.v1_9_R1.PlayerInteractManager
 *  net.minecraft.server.v1_9_R1.PlayerInventory
 *  net.minecraft.server.v1_9_R1.World
 *  net.minecraft.server.v1_9_R1.WorldData
 *  net.minecraft.server.v1_9_R1.WorldProvider
 *  net.minecraft.server.v1_9_R1.WorldServer
 *  net.minecraft.server.v1_9_R1.WorldSettings
 *  net.minecraft.server.v1_9_R1.WorldSettings$EnumGamemode
 *  net.minecraft.server.v1_9_R1.WorldType
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_9_R1.CraftServer
 *  org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package eu.blackfire62.MySkin.Bukkit.SkinHandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.blackfire62.MySkin.Shared.SkinHandler;
import eu.blackfire62.MySkin.Shared.SkinProperty;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.server.v1_9_R1.Container;
import net.minecraft.server.v1_9_R1.DataWatcher;
import net.minecraft.server.v1_9_R1.DedicatedPlayerList;
import net.minecraft.server.v1_9_R1.DimensionManager;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EnumDifficulty;
import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_9_R1.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_9_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_9_R1.PlayerConnection;
import net.minecraft.server.v1_9_R1.PlayerInteractManager;
import net.minecraft.server.v1_9_R1.PlayerInventory;
import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.WorldData;
import net.minecraft.server.v1_9_R1.WorldProvider;
import net.minecraft.server.v1_9_R1.WorldServer;
import net.minecraft.server.v1_9_R1.WorldSettings;
import net.minecraft.server.v1_9_R1.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

public class SkinHandler_v1_9_R1
implements SkinHandler {
    private WerewolfPlugin myskin;

    public SkinHandler_v1_9_R1(WerewolfPlugin myskin) {
        this.myskin = myskin;
    }

    @Override
    public SkinProperty getSkinProperty(Object player) {
        PropertyMap props = ((CraftPlayer)player).getHandle().getProfile().getProperties();
        Iterator propit = props.get("textures").iterator();
        if (!propit.hasNext()) {
            return null;
        }
        Property prop = (Property)propit.next();
        return new SkinProperty(prop.getName(), prop.getValue(), prop.getSignature());
    }

    @Override
    public void setSkinProperty(Object player, SkinProperty property) {
        PropertyMap props = ((CraftPlayer)player).getHandle().getProfile().getProperties();
        props.get(property.name).clear();
        props.put(property.name, new Property(property.name, property.value, property.signature));
    }

    @Override
    public void update(Object player) {
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(new int[]{ep.getId()});
        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{ep});
        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{ep});
        PacketPlayOutNamedEntitySpawn spawnEntity = new PacketPlayOutNamedEntitySpawn((EntityHuman)ep);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(ep.getId(), ep.getDataWatcher(), true);
        PacketPlayOutHeldItemSlot helditem = new PacketPlayOutHeldItemSlot(ep.inventory.itemInHandIndex);
        WorldServer worldserver = (WorldServer)ep.getWorld();
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(worldserver.worldProvider.getDimensionManager().getDimensionID(), worldserver.getDifficulty(), worldserver.getWorldData().getType(), ep.playerInteractManager.getGameMode());
        PacketPlayOutPosition position = new PacketPlayOutPosition(ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch, new HashSet(), 0);
        PacketPlayOutEntityHeadRotation headrotation = new PacketPlayOutEntityHeadRotation((Entity)ep, (byte)MathHelper.d((float)(ep.getHeadRotation() * 256.0f / 360.0f)));
        DedicatedPlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
        Bukkit.getScheduler().runTask((Plugin)this.myskin, () -> {
            for (int i = 0; i < playerList.players.size(); ++i) {
                EntityPlayer ep1 = (EntityPlayer)playerList.players.get(i);
                if (!ep1.getBukkitEntity().canSee((Player)ep.getBukkitEntity())) continue;
                PlayerConnection con = ep1.playerConnection;
                con.sendPacket((Packet)removePlayer);
                con.sendPacket((Packet)addPlayer);
                if (ep1.getId() != ep.getId()) {
                    con.sendPacket((Packet)destroyEntity);
                    con.sendPacket((Packet)spawnEntity);
                }
                con.sendPacket((Packet)headrotation);
                for (int j = 0; j < EnumItemSlot.values().length; ++j) {
                    EnumItemSlot slot = EnumItemSlot.values()[j];
                    ItemStack itemstack = ep.getEquipment(slot);
                    if (itemstack != null && itemstack.count > 0) continue;
                    con.sendPacket((Packet)new PacketPlayOutEntityEquipment(ep.getId(), slot, itemstack));
                }
            }
            PlayerConnection con = ep.playerConnection;
            con.sendPacket((Packet)metadata);
            con.sendPacket((Packet)respawn);
            con.sendPacket((Packet)position);
            con.sendPacket((Packet)helditem);
            ep.updateAbilities();
            ep.triggerHealthUpdate();
            ep.updateInventory(ep.activeContainer);
            ep.updateInventory(ep.defaultContainer);
        });
    }
}

