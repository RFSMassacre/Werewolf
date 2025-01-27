package us.rfsmassacre.Werewolf.Items.Potions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import us.rfsmassacre.Werewolf.Items.WerewolfItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
public abstract class WerewolfPotion extends WerewolfItem
{	
	private boolean splash;
	private PotionType potionType;
	
	//Constructs the Werewolf Potion based on its type
	public WerewolfPotion(String name, boolean splash, Color color, PotionType potionType)
	{	
		super(Material.POTION, name);
		
		setSplash(splash);
		setPotionType(potionType);
		
		try
		{
			Material material = this.splash ? Material.SPLASH_POTION : Material.POTION;
			this.item.setType(material);
		}
		catch (NoSuchFieldError exception)
		{
			//This means it's running 1.8 and requires the use of potion objects
			//and convert it to an item stack in order to keep it all consistent
			try {
				Class<?> potionClazz = Class.forName("org.bukkit.potion.Potion");
				Constructor<?> potionConstructor = potionClazz.getConstructor(PotionType.class, int.class, boolean.class);
				Method setTypeMethod = potionClazz.getMethod("setType", PotionType.class);
				Method toItemStackMethod = potionClazz.getMethod("toItemStack", int.class);
				Object potion = potionConstructor.newInstance(this.potionType, 1, this.splash);
				setTypeMethod.invoke(potion, this.potionType);
				ItemStack itemStack = (ItemStack) toItemStackMethod.invoke(potion, 1);
				itemStack.setItemMeta(itemStack.getItemMeta());
				this.item = itemStack;

				this.setDisplayName(data.getItemName(name));
				this.setItemLore(data.getItemLore(name));
			}
			catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
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
		this.recipe = createRecipe();
	}
	
	/*
	 * Used for Spigot 1.11+
	 */
	public void setPotionColor(Color color)
	{
		PotionMeta meta = (PotionMeta) getItemStack().getItemMeta();
		meta.setColor(color);
		try {
			meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		}
		catch (NoSuchFieldError ex) {
			meta.addItemFlags(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
		}
		this.item.setItemMeta(meta);
	}
	public Color getPotionColor()
	{
		return ((PotionMeta) getItemStack().getItemMeta()).getColor();
	}
	
	/*
	 * Used for Spigot 1.9-1.10
	 */
	public void setMainEffect(PotionEffectType effect)
	{
		PotionMeta meta = (PotionMeta)getItemStack().getItemMeta();
		meta.setMainEffect(effect);
		this.item.setItemMeta(meta);
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
