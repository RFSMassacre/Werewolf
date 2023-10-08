package us.rfsmassacre.Werewolf.Origin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
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
	public enum ClanType
	{
		WITHERFANG("Witherfang"),
		SILVERMANE("Silvermane"),
		BLOODMOON("Bloodmoon");
		
		private final String title;
		
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
				{
					return type;
				}

				if (type.name().equalsIgnoreCase(title))
				{
					return type;
				}
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
	private List<UUID> memberIds;
	
	private List<PotionEffect> buffs;
	
	/*
	 * Constructor
	 */
	public Clan()
	{
		setMemberIds(new ArrayList<>());
		setBuffs(new ArrayList<>());
	}
	public Clan(ClanType type, String description)
	{
		setType(type);
		setDescription(description);
		setMemberIds(new ArrayList<>());
		setBuffs(new ArrayList<>());
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

	public List<UUID> getMemberIds()
	{
		return memberIds;
	}
	public void setMemberIds(List<UUID> memberIds)
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

	public List<PotionEffect> getBuffs()
	{
		return buffs;
	}
	public void setBuffs(List<PotionEffect> buffs)
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
		return isAlpha(werewolf.getUUID());
	}
	public boolean isAlpha(UUID playerId)
	{
		return playerId.equals(alphaId);
	}
	public int getSize()
	{
		return memberIds.size();
	}
	public boolean isEmpty()
	{
		return memberIds.isEmpty();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Clan)
		{
			Clan clan = (Clan)object;
			return this.clanType.equals(clan.getType());
		}
		
		return false;
	}

	public List<Werewolf> getMembers()
	{
		WerewolfManager werewolves = WerewolfPlugin.getWerewolfManager();
		List<Werewolf> members = new ArrayList<>();
		for (UUID memberId : memberIds)
		{
			//Load online data and if not found load offline data
			Werewolf member = werewolves.getWerewolf(memberId);
			if (member == null)
			{
				member = werewolves.getOfflineWerewolf(memberId);
			}

			//Add only if data was found
			if (member != null)
			{
				members.add(member);
			}
		}

		//Sort from highest level to lowest level
		members.sort(Collections.reverseOrder());
		return members;
	}
		
	public void getMembers(Consumer<List<Werewolf>> callback)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () -> callback.accept(getMembers()));
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
