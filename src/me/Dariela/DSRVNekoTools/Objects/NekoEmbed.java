package me.Dariela.DSRVNekoTools.Objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionMapping;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import me.Dariela.DSRVNekoTools.NekoMain;
import me.clip.placeholderapi.PlaceholderAPI;

public class NekoEmbed {
	private String txt;
	private NekoMain plugin = NekoMain.getPlugin(NekoMain.class);
	private FileConfiguration embeds = plugin.getConfigManager().embeds;
	public Boolean isEphemeral = false;
	public Boolean needAccount = false;
	String tag = null;
	public Collection<Button> buttons = new ArrayList<Button>();
	
	
	public NekoEmbed(String txt) {
		this.txt = txt;		
		String embedTag = StringUtils.substringBetween(txt, "<embed:", ">");
		this.needAccount = plugin.getConfigManager().responses.getBoolean(embedTag+".NeedAccount");
		if(embedTag !=  null) {
			isEphemeral = false;
			this.isEphemeral = embeds.getBoolean(embedTag+".Ephemeral");
			this.tag = embedTag;
			loadButtons();		
		}
	}
	private void loadButtons() {
		if(embeds.getStringList(tag+".Buttons") == null) return;
		for(String str : embeds.getStringList(tag+".Buttons")) {
			if(plugin.buttons.containsKey(str.toLowerCase())) this.buttons.add(plugin.buttons.get(str.toLowerCase()));	
		}
	}
	
	public Message getMessage(UUID uuid, User user, List<OptionMapping> options) {
		if(uuid == null) uuid = plugin.getDiscord().getAccountLinkManager().getUuid(user.getId());
		if(this.needAccount && uuid == null) {
			return new NekoEmbed("<embed:need_account>").getMessage(null, user, options);
		}

		MessageBuilder mb = new MessageBuilder();
		String embedTag = StringUtils.substringBetween(txt, "<embed:", ">");
		if(embedTag == null) return mb.setContent(replace(txt, uuid, user)).build();
		if(embeds.getConfigurationSection(embedTag) == null) return mb.setContent(replace(txt, uuid, user)).build(); 
		
		String content = embeds.getString(embedTag+".Content");
		if(content != null && content != "") mb.setContent(content);
		if(embeds.getBoolean(embedTag+".Embed.Enabled")) {
			EmbedBuilder eb = new EmbedBuilder();
			String color = embeds.getString(embedTag+".Embed.Color");
			String author_name = embeds.getString(embedTag+".Embed.Author.Name");
			String author_url = embeds.getString(embedTag+".Embed.Author.Url");
			String author_image = embeds.getString(embedTag+".Embed.Author.ImageUrl");	
			String thumbnailurl = embeds.getString(embedTag+".Embed.ThumbnailUrl");	
			String title_text = embeds.getString(embedTag+".Embed.Title.Text");
			String title_url = embeds.getString(embedTag+".Embed.Title.Url");
			String description = embeds.getString(embedTag+".Embed.Description");				
			List<String> fields = embeds.getStringList(embedTag+".Embed.Fields");
			String imageurl = embeds.getString(embedTag+".Embed.ImageUrl");
			String footer_text = embeds.getString(embedTag+".Embed.Footer.Text");
			String footer_icon_url = embeds.getString(embedTag+".Embed.Footer.IconUrl");
			/////////////////Build embed///////////////////////////
			String validColor = color != null ? replace(color, uuid, user) : "null";
			if(validColor.matches("^&?#([aA-fF0-9]{6})$")) eb.setColor(Color.decode(validColor));		
			eb.setAuthor(replace(author_name, uuid, user), replace(author_url, uuid, user), replace(author_image, uuid, user)); 
			eb.setThumbnail(replace(thumbnailurl, uuid, user));
			eb.setTitle(replace(title_text, uuid, user), replace(title_url, uuid, user));  
			eb.setDescription(replace(description, uuid, user));
			for(String str : fields) {
				String[] aa = str.split(";");
				if(aa.length > 0) {		
					if(aa[0].equals("blank")) {		
						eb.addBlankField(Boolean.valueOf(aa[1])); 
					} else {
						String title = str.split(";")[0];
						String desc = str.split(";")[1];
						Boolean inline = Boolean.valueOf(str.split(";")[2]);
						desc = desc.replaceAll(Pattern.quote("\\n"), "\n");					
						eb.addField(replace(title, uuid, user), replace(desc, uuid, user), inline);	
					}						
				}			
			}
			eb.setImage(replace(imageurl, uuid, user)); 
			eb.setFooter(replace(footer_text, uuid, user), replace(footer_icon_url, uuid, user));
			eb.setTimestamp(null);
			////////////////////////////////////////////////////////
			mb.setEmbeds(eb.build());
			
		}
		return mb.build();	
		
		
	}
	public String replace(String str, UUID uuid, User user) {
		OfflinePlayer of = uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
		Player p = of != null ? of.getPlayer() : null;
		
		if(str == null) return null;
		str = str
				.replaceAll("%user-discriminator%", user.getDiscriminator())
				.replaceAll("%user-id%", user.getId())
				.replaceAll("%user-avatar%", user.getAvatarUrl())
				.replaceAll("%user-tag%", user.getAsTag())
		        .replaceAll("%user-name%", user.getName());
	   if(of != null) {
		   str = str
				.replaceAll("%player-name%", of.getName())
		        .replaceAll("%player-uuid%", of.getUniqueId().toString())
		        .replaceAll("%player-textureid%", plugin.getUtils().getSkinID(of.getPlayer()));
	   } else {
		   str = str
				.replaceAll("%player-name%", "null")
		        .replaceAll("%player-uuid%", "null")
		        .replaceAll("%player-textureid%", "null");
	   }
	   
	   
		str = PlaceholderAPI.setPlaceholders(p, str);	
		return ChatColor.stripColor(str);
		
	}
	

}
