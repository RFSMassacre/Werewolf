package us.rfsmassacre.Werewolf.Origin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;


import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Managers.MessageManager;
import us.rfsmassacre.Werewolf.Managers.WerewolfManager;

public class Clan
{
	/*
	 * Clan Type
	 */
	public static enum ClanType
	{
		WITHERFANG("Witherfang"),
		SILVERMANE("Silvermane"),
		BLOODMOON("Bloodmoon");
		
		private String title;
		
		ClanType(String title)
		{
			this.title = title;
		}
		
		@Override
		public String toString()
		{
			return this.title;
		}
		
		public String toKey()
		{
			return this.name().toLowerCase();
		}
		
		public static ClanType fromString(String title)
		{
			for (ClanType type : ClanType.values())
			{
				if (type.toString().equalsIgnoreCase(title))
					return type;
			}
			
			return null;
		}
	}
	
	/*
	 * Clan Data
	 */
	private ClanType clanType;
	private String description;
	
	private UUID alphaId;
	private ArrayList<UUID> memberIds;
	
	private ArrayList<PotionEffect> buffs;
	
	/*
	 * Constructor
	 */
	public Clan()
	{
		setMemberIds(new ArrayList<UUID>());
		setBuffs(new ArrayList<PotionEffect>());
	}
	public Clan(ClanType type, String description)
	{
		setType(type);
		setDescription(description);
		setMemberIds(new ArrayList<UUID>());
		setBuffs(new ArrayList<PotionEffect>());
	}

	/*
	 * Setters and Getters
	 */
	public ClanType getType() 
	{
		return clanType;
	}
	public void setType(ClanType clanType) 
	{
		this.clanType = clanType;
	}
	
	public String getDescription() 
	{
		return description;
	}
	public void setDescription(String description) 
	{
		this.description = description;
	}

	public UUID getAlphaId() 
	{
		return alphaId;
	}
	public void setAlphaId(UUID alphaId) 
	{
		this.alphaId = alphaId;
	}

	public ArrayList<UUID> getMemberIds() 
	{
		return memberIds;
	}
	public void setMemberIds(ArrayList<UUID> memberIds) 
	{
		this.memberIds = memberIds;
	}
	public void addMemberId(UUID memberId)
	{
		if (!this.memberIds.contains(memberId))
			this.memberIds.add(memberId);
	}
	public void removeMemberId(UUID memberId)
	{
		memberIds.remove(memberId);
	}
	public void clearMemberIds()
	{
		memberIds.clear();
	}
	
	public void addMember(Player player)
	{
		addMemberId(player.getUniqueId());
	}
	public void removeMember(Player player)
	{
		removeMemberId(player.getUniqueId());
	}

	public ArrayList<PotionEffect> getBuffs() 
	{
		return buffs;
	}
	public void setBuffs(ArrayList<PotionEffect> buffs) 
	{
		this.buffs = buffs;
	}
	public void addBuff(PotionEffect buff)
	{
		this.buffs.add(buff);
	}
	public void removeBuff(PotionEffect buff)
	{
		this.buffs.remove(buff);
	}
	
	/*
	 * Management Functions
	 */
	//Checks if player pertains to this clan
	public boolean isMember(Werewolf werewolf)
	{
		return werewolf.getType().equals(this.clanType);
	}
	public boolean isAlpha(Werewolf werewolf)
	{
		return werewolf.getUUID().equals(this.alphaId);
	}
	public int getSize()
	{
		return memberIds.size();
	}
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Clan)
		{
			Clan clan = (Clan)object;
			if (this.clanType.equals(clan.getType()))
				return true;
		}
		
		return false;
	}
		
	public ArrayList<Werewolf> getMembers()
	{
		WerewolfManager werewolves = WerewolfPlugin.getWerewolfManager();
		ArrayList<Werewolf> members = new ArrayList<Werewolf>();
		for (UUID memberId : memberIds)
		{
			//Load online data and if not found load offline data
			Werewolf member = werewolves.getWerewolf(memberId);
			if (member == null)
				member = werewolves.getOfflineWerewolf(memberId);
			
			//Add only if data was found
			if (member != null)
				members.add(member);
		}
		
		//Sort from highest level to lowest level
		Collections.sort(members, Collections.reverseOrder());
		
		return members;
	}
	
	//Makes this werewolf the new alpha
	public void makeAlpha(Werewolf werewolf)
	{
		MessageManager messages = WerewolfPlugin.getMessageManager();
		setAlphaId(werewolf.getUUID());
		
		messages.broadcastLocale("clan.new-alpha",
				"{alpha}", werewolf.getDisplayName(),
				"{clan}", WerewolfPlugin.getConfigManager().getString("menu.clan." + clanType.name()));
		
		//Update their skin to Alpha
	}
}
