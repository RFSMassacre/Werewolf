package us.rfsmassacre.HeavenLib.Items;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class HeavenItem
{
    protected ItemStack item;
    protected Material material;
    protected String name;
    protected String displayName;
    protected NamespacedKey key;
    protected Recipe recipe;

    public HeavenItem(Material material, int amount, String name, String displayName, List<String> lore)
    {
        this.item = new ItemStack(material, amount);

        this.name = name;
        this.displayName = displayName;
        this.material = material;

        try
        {
            this.key = new NamespacedKey(WerewolfPlugin.getInstance(), name);
        }
        catch (NoClassDefFoundError exception)
        {
            //Do nothing. This means it's below 1.9.
        }

        this.setDisplayName(displayName);
        this.setItemLore(lore);

        //NBT
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.addCompound("WerewolfPlugin");
        nbtItem.getCompound("WerewolfPlugin").setString("IID", this.name);
        nbtItem.applyNBT(item);

        this.recipe = createRecipe();
    }

    public boolean equals(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.getType().equals(Material.AIR))
        {
            return false;
        }

        NBTItem otherItem = new NBTItem(itemStack);
        NBTCompound compound = otherItem.getCompound("WerewolfPlugin");
        if (compound == null)
        {
            //Bukkit.broadcastMessage("Is Null!");
            return false;
        }

        String value = compound.getString("IID");
        //Bukkit.broadcastMessage(value);
        return this.name.equals(value);
    }

    public void setDisplayName(String displayName)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatManager.format(displayName));
        item.setItemMeta(meta);
    }
    //Adds the ID to the first line to ensure when checking it's O(1).
    public void setItemLore(List<String> newLore)
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        List<String> lines = new ArrayList<>();
        for (String line : newLore)
        {
            lines.add(ChatManager.format(line));
        }

        lore.addAll(lines);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    public List<String> getItemLore()
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        return lore;
    }

    protected void addFlag(ItemFlag... flags)
    {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
    }

    /*
     * Getters
     */
    public Material getType()
    {
        return material;
    }
    public String getName()
    {
        return name;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    public Recipe getRecipe()
    {
        return recipe;
    }
    public ItemStack getItemStack()
    {
        return item;
    }
    public NamespacedKey getKey()
    {
        return key;
    }

    /*
     * Recipe that is needed to craft item.
     */
    protected abstract Recipe createRecipe();
}
