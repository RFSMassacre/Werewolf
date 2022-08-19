package us.rfsmassacre.Werewolf.Managers;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.MenuManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Data.ClanDataManager;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Clan.ClanType;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class ClanManager 
{
	private ClanDataManager clanData;
	private Map<ClanType, Clan> clans;
	
	private MenuManager witherfangMenu;
	private MenuManager silvermaneMenu;
	private MenuManager bloodmoonMenu;
	private ConfigManager config;
	
	private int alphaTaskId;
	
	public ClanManager()
	{
		clanData = new ClanDataManager(WerewolfPlugin.getInstance());
		clans = new HashMap<>();
		
		//Load clan descriptions
		witherfangMenu = new MenuManager("witherfang.txt");
		silvermaneMenu = new MenuManager("silvermane.txt");
		bloodmoonMenu = new MenuManager("bloodmoon.txt");
		config = WerewolfPlugin.getConfigManager();
		
		//Load default settings
		Clan newWitherfangClan = new Clan(ClanType.WITHERFANG, witherfangMenu.getText());
		Clan newSilvermaneClan = new Clan(ClanType.SILVERMANE, silvermaneMenu.getText());
		Clan newBloodmoonClan = new Clan(ClanType.BLOODMOON, bloodmoonMenu.getText());

		setPotionEffects(newWitherfangClan);
		setPotionEffects(newSilvermaneClan);
		setPotionEffects(newBloodmoonClan);

		/*
		newWitherfangClan.addBuff(new PotionEffect(PotionEffectType.HUNGER, 72000, 0));
		newWitherfangClan.addBuff(new PotionEffect(PotionEffectType.NIGHT_VISION, 72000, 1));
		newWitherfangClan.addBuff(new PotionEffect(PotionEffectType.JUMP, 72000, 3));
		newWitherfangClan.addBuff(new PotionEffect(PotionEffectType.SPEED, 72000, 3));

		newSilvermaneClan.addBuff(new PotionEffect(PotionEffectType.HUNGER, 72000, 2));
		newSilvermaneClan.addBuff(new PotionEffect(PotionEffectType.NIGHT_VISION, 72000, 1));
		newSilvermaneClan.addBuff(new PotionEffect(PotionEffectType.JUMP, 72000, 2));
		newSilvermaneClan.addBuff(new PotionEffect(PotionEffectType.SPEED, 72000, 1));
		newSilvermaneClan.addBuff(new PotionEffect(PotionEffectType.REGENERATION, 72000, 2));
		

		newBloodmoonClan.addBuff(new PotionEffect(PotionEffectType.HUNGER, 72000, 1));
		newBloodmoonClan.addBuff(new PotionEffect(PotionEffectType.NIGHT_VISION, 72000, 1));
		newBloodmoonClan.addBuff(new PotionEffect(PotionEffectType.JUMP, 72000, 1));
		newBloodmoonClan.addBuff(new PotionEffect(PotionEffectType.SPEED, 72000, 0));
		newBloodmoonClan.addBuff(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 72000, 2));
		 */
		
		//If data is found per clan, loads those instead
		//Transfer data instead of overwriting the actual object
		//This is to keep the descriptions and buffs the same
		if (clanData.loadFromFile(ClanType.WITHERFANG.toString()) != null)
		{
			Clan witherfangClan = (Clan)clanData.loadFromFile(ClanType.WITHERFANG.toString());
			newWitherfangClan.setAlphaId(witherfangClan.getAlphaId());
			newWitherfangClan.setMemberIds(witherfangClan.getMemberIds());
		}
		if (clanData.loadFromFile(ClanType.SILVERMANE.toString()) != null)
		{
			Clan silvermaneClan = (Clan)clanData.loadFromFile(ClanType.SILVERMANE.toString());
			newSilvermaneClan.setAlphaId(silvermaneClan.getAlphaId());
			newSilvermaneClan.setMemberIds(silvermaneClan.getMemberIds());
		}
		if (clanData.loadFromFile(ClanType.BLOODMOON.toString()) != null)
		{
			Clan bloodmoonClan = (Clan)clanData.loadFromFile(ClanType.BLOODMOON.toString());
			newBloodmoonClan.setAlphaId(bloodmoonClan.getAlphaId());
			newBloodmoonClan.setMemberIds(bloodmoonClan.getMemberIds());
		}
		
		//Save their name as their enum (in upper case)
		clans.put(ClanType.WITHERFANG, newWitherfangClan);
		clans.put(ClanType.SILVERMANE, newSilvermaneClan);
		clans.put(ClanType.BLOODMOON, newBloodmoonClan);
		
		//Store clans to ensure files exist
		storeClans();
		
		//Ensure each clan has an alpha
		startAlphaChecker();
	}
	
	public void storeClans()
	{
		//Store each clan to their files
		clanData.saveToFile(getClan(ClanType.WITHERFANG), ClanType.WITHERFANG.toString());
		clanData.saveToFile(getClan(ClanType.SILVERMANE), ClanType.SILVERMANE.toString());
		clanData.saveToFile(getClan(ClanType.BLOODMOON), ClanType.BLOODMOON.toString());
	}
	
	public void reload()
	{
		witherfangMenu.reloadText();
		silvermaneMenu.reloadText();
		bloodmoonMenu.reloadText();
		
		Clan witherfangClan = clans.get(ClanType.WITHERFANG);
		Clan silvermaneClan = clans.get(ClanType.SILVERMANE);
		Clan bloodmoonClan = clans.get(ClanType.BLOODMOON);
		
		witherfangClan.setDescription(witherfangMenu.getText());
		silvermaneClan.setDescription(silvermaneMenu.getText());
		bloodmoonClan.setDescription(bloodmoonMenu.getText());

		setPotionEffects(witherfangClan);
		setPotionEffects(silvermaneClan);
		setPotionEffects(bloodmoonClan);
		
		clans.put(ClanType.WITHERFANG, witherfangClan);
		clans.put(ClanType.SILVERMANE, silvermaneClan);
		clans.put(ClanType.BLOODMOON, bloodmoonClan);
	}
	
	public Clan getClan(ClanType type)
	{
		return clans.get(type);
	}
	public Clan getClan(Werewolf werewolf)
	{
		return clans.get(werewolf.getType());
	}
	
	/*
	 * Alpha Werewolves are chosen from 3 criteria in order:
	 * 
	 * 1: If they are the only Werewolf in the clan, they are Alpha.
	 * 2: If they are the highest level in the clan, they are Alpha.
	 * 3: If they die by another clan member, the Alpha is transfered to them.
	 */
	public void startAlphaChecker()
	{
		//Continuously ensure each clan has an alpha through the criteria
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		alphaTaskId = scheduler.scheduleSyncRepeatingTask(WerewolfPlugin.getInstance(), () -> {
			if (!config.getBoolean("alphas"))
			{
				return;
			}

			for (Clan clan : clans.values())
			{
				if (clan.getAlphaId() == null & !clan.isEmpty())
				{
					//Check for solo Werewolf
					for (Werewolf werewolf : clan.getMembers())
					{
						if (werewolf != null)
						{
							clan.makeAlpha(werewolf);
							break;
						}
					}
				}
			}
		}, 0L, WerewolfPlugin.getConfigManager().getInt("intervals.alpha-update"));
	}
	
	public void endCycle()
	{
		//In case we need to stop the buff cycle for a reload
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.cancelTask(alphaTaskId);
	}

	private void setPotionEffects(Clan clan)
	{
		ClanType clanType = clan.getType();
		List<String> formats = config.getPotionList("werewolf-effects." + clanType.name().toLowerCase());
		List<PotionEffect> effects = new ArrayList<>();
		for (String format : formats)
		{
			try
			{
				String[] line = format.split(":");
				String potionName = line[0];
				int power = Integer.parseInt(line[1]);
				int duration = Integer.parseInt(line[2]);
				PotionEffectType potionType = PotionEffectType.getByName(potionName);
				if (potionType == null)
				{
					continue;
				}

				PotionEffect effect = new PotionEffect(potionType, duration, power);
				effects.add(effect);
			}
			catch (NumberFormatException exception)
			{
				exception.printStackTrace();
			}
		}
		clan.setBuffs(effects);
	}
}
