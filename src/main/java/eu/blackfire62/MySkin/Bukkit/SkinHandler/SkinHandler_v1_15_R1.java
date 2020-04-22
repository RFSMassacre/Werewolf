package eu.blackfire62.MySkin.Bukkit.SkinHandler;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.blackfire62.MySkin.Shared.SkinHandler;
import eu.blackfire62.MySkin.Shared.SkinProperty;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

import java.util.HashSet;
import java.util.Iterator;

public class SkinHandler_v1_15_R1 implements SkinHandler
{
    private WerewolfPlugin myskin;

    public SkinHandler_v1_15_R1(WerewolfPlugin myskin)
    {
        this.myskin = myskin;
    }

    @Override
    public SkinProperty getSkinProperty(Object player)
    {
        PropertyMap props = ((CraftPlayer)player).getHandle().getProfile().getProperties();
        Iterator propit = props.get("textures").iterator();
        if (!propit.hasNext())
        {
            return null;
        }
        Property prop = (Property)propit.next();
        return new SkinProperty(prop.getName(), prop.getValue(), prop.getSignature());
    }

    @Override
    public void setSkinProperty(Object player, SkinProperty property)
    {
        PropertyMap props = ((CraftPlayer)player).getHandle().getProfile().getProperties();
        props.get(property.name).clear();
        props.put(property.name, new Property(property.name, property.value, property.signature));
    }

    @Override
    public void update(Object player)
    {
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(new int[]{ep.getId()});
        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{ep});
        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{ep});
        PacketPlayOutNamedEntitySpawn spawnEntity = new PacketPlayOutNamedEntitySpawn((EntityHuman)ep);
        PacketPlayOutHeldItemSlot helditem = new PacketPlayOutHeldItemSlot(ep.inventory.itemInHandIndex);
        WorldServer worldserver = (WorldServer)ep.getWorld();
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(ep.getId(), ep.getDataWatcher(), true);
        PacketPlayOutEntityStatus status = new PacketPlayOutEntityStatus(ep, (byte)28);
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(worldserver.worldProvider.getDimensionManager(), WorldData.c((long)worldserver.getWorldData().getSeed()), worldserver.getWorldData().getType(), ep.playerInteractManager.getGameMode());
        PacketPlayOutPosition position = new PacketPlayOutPosition(ep.locX(), ep.locY(), ep.locZ(), ep.yaw, ep.pitch, new HashSet(), 0);
        PacketPlayOutEntityHeadRotation headrotation = new PacketPlayOutEntityHeadRotation(ep, (byte)MathHelper.d((ep.getHeadRotation() * 256.0f / 360.0f)));
        DedicatedPlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
        Bukkit.getScheduler().runTask(this.myskin, () -> {
            for (int i = 0; i < playerList.players.size(); ++i)
            {
                EntityPlayer ep1 = playerList.players.get(i);
                if (!ep1.getBukkitEntity().canSee(ep.getBukkitEntity()))
                {
                    continue;
                }
                PlayerConnection con = ep1.playerConnection;
                con.sendPacket(removePlayer);
                con.sendPacket(addPlayer);
                if (ep1.getId() != ep.getId())
                {
                    con.sendPacket(destroyEntity);
                    con.sendPacket(spawnEntity);
                    con.sendPacket(headrotation);
                }
                for (int j = 0; j < EnumItemSlot.values().length; ++j)
                {
                    EnumItemSlot slot = EnumItemSlot.values()[j];
                    ItemStack itemstack = ep.getEquipment(slot);
                    if (itemstack.isEmpty())
                    {
                        continue;
                    }
                    con.sendPacket(new PacketPlayOutEntityEquipment(ep.getId(), slot, itemstack));
                }
            }
            PlayerConnection con = ep.playerConnection;
            con.sendPacket(respawn);
            con.sendPacket(position);
            con.sendPacket(helditem);
            con.sendPacket(metadata);
            con.sendPacket(status);
            ep.updateAbilities();
            ep.triggerHealthUpdate();
            ep.updateInventory(ep.defaultContainer);
            ep.getBukkitEntity().recalculatePermissions();
        });
    }
}
