package us.rfsmassacre.Werewolf.Items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.rfsmassacre.HeavenLib.Items.HeavenItem;
import us.rfsmassacre.Werewolf.Data.ItemDataManager;
import us.rfsmassacre.Werewolf.Managers.ItemManager;
import us.rfsmassacre.Werewolf.WerewolfPlugin;

import java.util.ArrayList;

public abstract class WerewolfItem extends HeavenItem
{
    protected static ItemDataManager data = new ItemDataManager(WerewolfPlugin.getInstance());

    public WerewolfItem(Material material, String name)
    {
        super(material, 1, name, name, new ArrayList<>());

        this.displayName = data.getItemName(name);
        this.setDisplayName(data.getItemName(name));
        this.setItemLore(data.getItemLore(name));

        this.recipe = createRecipe();
    }

    public static void reloadData()
    {
        data.reloadFiles();
    }

    public static boolean isWerewolfItem(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.getType().equals(Material.AIR))
        {
            return false;
        }

        ItemManager items = WerewolfPlugin.getItemManager();
        WerewolfItem werewolfItem = items.getWerewolfItem(itemStack);
        return werewolfItem != null;
    }

    @SuppressWarnings("deprecation")
    public boolean isHoldingItem(Player hunter, boolean bothHands)
    {
        try
        {
            ItemStack main = hunter.getInventory().getItemInMainHand();
            if (this.equals(main))
            {
                return true;
            }

            if (bothHands)
            {
                ItemStack off = hunter.getInventory().getItemInOffHand();
                if (this.equals(off))
                {
                    return true;
                }
            }
        }
        catch (NoSuchMethodError exception)
        {
            ItemStack item = hunter.getInventory().getItemInHand();
            if (this.equals(item))
            {
                return true;
            }
        }

        return false;
    }
    public boolean hasItem(Player hunter)
    {
        for (ItemStack item : hunter.getInventory().getContents())
        {
            if (this.equals(item))
            {
                return true;
            }
        }

        return false;
    }
}
