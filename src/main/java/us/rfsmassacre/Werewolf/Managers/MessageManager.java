package us.rfsmassacre.Werewolf.Managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import us.rfsmassacre.HeavenLib.Managers.ChatManager;
import us.rfsmassacre.HeavenLib.Managers.ConfigManager;
import us.rfsmassacre.HeavenLib.Managers.LocaleManager;
import us.rfsmassacre.HeavenLib.Managers.MenuManager;

import us.rfsmassacre.Werewolf.WerewolfPlugin;
import us.rfsmassacre.Werewolf.Origin.Clan;
import us.rfsmassacre.Werewolf.Origin.Werewolf;

public class MessageManager
{
	private final ConfigManager config;
	private final LocaleManager locale;
	
	private final MenuManager mainText;
	private final MenuManager adminText;
	private final MenuManager helpText1;
	private final MenuManager helpText2;
	private final MenuManager helpAdmin1;
	private final MenuManager helpAdmin2;
	
	public MessageManager()
	{
		config = WerewolfPlugin.getConfigManager();
		locale = WerewolfPlugin.getLocaleManager();
		
		mainText = new MenuManager("main.txt");
		adminText = new MenuManager("admin.txt");
		helpText1 = new MenuManager("help1.txt");
		helpText2 = new MenuManager("help2.txt");
		helpAdmin1 = new MenuManager("helpadmin1.txt");
		helpAdmin2 = new MenuManager("helpadmin2.txt");
	}
	
	public void reloadText()
	{
		mainText.reloadText();
		adminText.reloadText();
		helpText1.reloadText();
		helpText2.reloadText();
		helpAdmin1.reloadText();
		helpAdmin2.reloadText();
	}
	
	/*
	 * Replace the placer holder with its variable.
	 */
	private String replaceHolders(String locale, String[] replacers)
	{
		String message = locale;
		
		for (int slot = 0; slot < replacers.length; slot += 2)
		{
			message = message.replace(replacers[slot], replacers[slot + 1]);
		}
		
		return message;
	}
	public void sendMessage(CommandSender sender, String message, String...replacers)
	{
		sender.sendMessage(ChatManager.format(replaceHolders(message, replacers)));
	}
	
	public void sendWolfLocale(CommandSender sender, String key, String...replacers)
	{
		sendMessage(sender, locale.getMessage("prefix.werewolf") + locale.getMessage(key), replacers);
	}
	public void sendHunterLocale(CommandSender sender, String key, String...replacers)
	{
		sendMessage(sender, locale.getMessage("prefix.hunter") + locale.getMessage(key), replacers);
	}
	
	public void broadcastLocale(String key, String...replacers)
	{
		Bukkit.broadcastMessage(ChatManager.format(replaceHolders(locale.getMessage("prefix.werewolf") + locale.getMessage(key), replacers)));
	}
	
	public void sendWolfAction(Player player, String key, String...replacers)
	{
		String message = replaceHolders(locale.getMessage("prefix.werewolf") + locale.getMessage(key), replacers);
		
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
				new TextComponent(TextComponent.fromLegacyText(ChatManager.format(message))));
	}
	public void sendHunterAction(Player player, String key, String...replacers)
	{
		String message = replaceHolders(locale.getMessage("prefix.hunter") + locale.getMessage(key), replacers);
		
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
				new TextComponent(ChatManager.format(message)));
	}
	
	public String getMainText()
	{
		return mainText.getText();
	}
	public String getAdminText()
	{
		return adminText.getText();
	}
	public String getHelpText1()
	{
		return helpText1.getText();
	}
	public String getHelpText2()
	{
		return helpText2.getText();
	}
	public String getAdminHelpText1()
	{
		return helpAdmin1.getText();
	}
	public String getAdminHelpText2()
	{
		return helpAdmin2.getText();
	}
	
	//Formats the member list for this clan
	public void sendMembersList(CommandSender sender, Clan clan, int pageNumber)
	{
		Bukkit.getScheduler().runTaskAsynchronously(WerewolfPlugin.getInstance(), () ->
		{
			int number = pageNumber;
			List<String> pages = new ArrayList<>();
			final int MEMBERS_PER_PAGE = 6;

			//Top border
			int maxPages = (clan.getSize() / MEMBERS_PER_PAGE) + 1;

			//Cap the highest number and lowest number
			if (number + 1 > maxPages)
				number = maxPages - 1;
			else if (number + 1 < 0)
				number = 0;

			String clanDisplay = config.getString("menu.clan." + clan.getType().name());
			String border = "&6&l  «&e&l&m*-------------------------------------*&6&l»";
			String header = border + "\n &6&l" + clanDisplay + " &7Member List                   &7Page &8[&7" + (number + 1) + "&8/&7" + maxPages + "&8]";
			header += "\n      ";

			if (clan.getSize() > 0)
			{
				int slot = 0;
				StringBuilder page = new StringBuilder(header);
				for (Werewolf member : clan.getMembers())
				{
					//If this is last in the page
					if (slot % (MEMBERS_PER_PAGE - 1) == 0 && slot > 0)
					{
						if (!WerewolfPlugin.getWerewolfManager().isAlpha(member.getUUID()))
							page.append("\n &8[&7").append(slot + 1).append("&8] &8[&6&lLvl ").append(member.getLevel())
									.append("&8] &r").append(member.getDisplayName());
						else
							page.append("\n &8[&7").append(slot + 1).append("&8] &8[&6&lLvl ").append(member.getLevel())
									.append("&8] &2&lAlpha&r ").append(member.getDisplayName());

						page.append("\n").append(border);
						pages.add(page.toString());
						page = new StringBuilder(header);
					}

					if (!WerewolfPlugin.getWerewolfManager().isAlpha(member.getUUID()))
						page.append("\n &8[&7").append(slot + 1).append("&8] &8[&6&lLvl ").append(member.getLevel())
								.append("&8] &r").append(member.getDisplayName());
					else
						page.append("\n &8[&7").append(slot + 1).append("&8] &8[&6&lLvl ").append(member.getLevel())
								.append("&8] &2&lAlpha&r ").append(member.getDisplayName());

					//If this is the last in the list
					if (slot == clan.getSize() - 1)
					{
						for (int spacer = slot % (MEMBERS_PER_PAGE - 1); spacer < (MEMBERS_PER_PAGE - 1); spacer++)
						{
							page.append("\n      ");
						}

						page.append("\n").append(border);
						pages.add(page.toString());
					}

					slot++;
				}

				sendMessage(sender, pages.get(number));
			}
			else
			{
				String page = header;
				page += "\n &cNo members yet.";
				page += "\n \n \n \n \n \n" + border;
				pages.add(page);
				sendMessage(sender, pages.get(0));
			}
		});
	}
}
