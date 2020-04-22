package us.rfsmassacre.Werewolf.Items.Potions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import us.rfsmassacre.Werewolf.Items.WerewolfItem;

@SuppressWarnings("deprecation")
public abstract class WerewolfPotion extends WerewolfItem
{	
	private boolean splash;
	private PotionType potionType;
	
	//Constructs the Werewolf Potion based on its type
	public WerewolfPotion(WerewolfItemType itemType, boolean splash, Color color, PotionType potionType) 
	{	
		super(Material.POTION, itemType);
		
		setSplash(splash);
		setPotionType(potionType);
		
		try
		{
			setMaterial(this.splash ? Material.SPLASH_POTION : Material.POTION);
		}
		catch (NoSuchFieldError exception)
		{
			//This means it's running 1.8 and requires the use of potion objects
			//and convert it to an item stack in order to keep it all consistent
			Potion potion = new Potion(this.potionType, 1, this.splash);
			ItemStack itemStack = potion.toItemStack(1);
			itemStack.setItemMeta(itemStack.getItemMeta());
			setItem(itemStack);
			
			setItemName(getItemData().getItemName(getType()));
			setItemLore(getItemData().getItemLore(getType()));
		}
		
		try
		{
			setPotionColor(color);
		}
		catch (NoSuchMethodError exception)
		{
			//This means it's running 1.10 or lower and requires the old methods to change colors.
			setMainEffect(this.potionType.getEffectType());
		}
		
		//Running it on this level should make the color kick in before the recipe is created.
		setRecipe(createRecipe());
	}
	
	/*
	 * Used for Spigot 1.11+
	 */
	public void setPotionColor(Color color)
	{
		PotionMeta meta = (PotionMeta)getItem().getItemMeta();
		meta.setColor(color);
		setItemMeta(meta);
	}
	public Color getPotionColor()
	{
		return ((PotionMeta)getItem().getItemMeta()).getColor();
	}
	
	/*
	 * Used for Spigot 1.10-1.9
	 */
	public void setMainEffect(PotionEffectType effect)
	{
		PotionMeta meta = (PotionMeta)getItem().getItemMeta();
		meta.setMainEffect(effect);
		setItemMeta(meta);
	}
	
	public boolean isSplash() 
	{
		return splash;
	}
	private void setSplash(boolean splash) 
	{
		this.splash = splash;
	}

	public PotionType getPotionType() 
	{
		return potionType;
	}
	private void setPotionType(PotionType potionType) 
	{
		this.potionType = potionType;
	}
}
