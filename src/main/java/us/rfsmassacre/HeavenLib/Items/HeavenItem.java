package us.rfsmassacre.HeavenLib.Items;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class HeavenItem
{
    protected ItemStack item;

    protected Material material;
    @Getter
    protected String name;
    @Getter
    protected String displayName;
    @Getter
    protected final NamespacedKey key;
    @Getter
    protected int customModelData;
    @Getter
    protected Recipe recipe;

    public HeavenItem(Material material, int amount, String name,
            String displayName, List<String> lore)
    {
        this.item = new ItemStack(material, amount);

        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.key = new NamespacedKey(WerewolfPlugin.getInstance(), name);

        this.setDisplayName(displayName);
        this.setItemLore(lore);

        //NBT
        setNBT(this.key, this.name);

        this.recipe = createRecipe();
    }

    public void setCustomModelData(int customModelData)
    {
        this.customModelData = customModelData;
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
    }

    public void setNBT(NamespacedKey key, String value)
    {
        //NBT
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
        {
            return;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }
    public String getNBT(NamespacedKey key)
    {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
        {
            return null;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.get(key, PersistentDataType.STRING);
    }

    public boolean equals(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
        {
            return false;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String value = data.get(key, PersistentDataType.STRING);
        return this.name.equals(value);
    }

    public void setDisplayName(String displayName)
    {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
        {
            return;
        }

        meta.setDisplayName(ChatManager.format(displayName));
        item.setItemMeta(meta);
    }
    //Adds the ID to the first line to ensure when checking, it's O(1).
    public void setItemLore(List<String> lore)
    {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
        {
            return;
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public List<String> getItemLore()
    {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
        {
            return null;
        }

        return meta.getLore();
    }

    protected void addFlag(ItemFlag... flags)
    {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
        {
            return;
        }

        meta.addItemFlags(flags);
        item.setItemMeta(meta);
    }

    public ItemStack getItemStack()
    {
        return item;
    }

    public Material getType()
    {
        return material;
    }

    /*
     * Recipe that is needed to craft item.
     */
    protected abstract Recipe createRecipe();
}
