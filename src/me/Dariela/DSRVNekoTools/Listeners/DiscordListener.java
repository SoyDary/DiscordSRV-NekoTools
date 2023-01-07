package me.Dariela.DSRVNekoTools.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorHandler;
import me.Dariela.DSRVNekoTools.NekoMain;
import me.Dariela.DSRVNekoTools.Objects.NekoEmbed;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("deprecation")
public class DiscordListener {
	private NekoMain plugin = NekoMain.getPlugin(NekoMain.class);
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
	HashMap<User, Integer> intentos = new HashMap<User, Integer>();
	
	@Subscribe
	public void onuwu(DiscordGuildMessagePreProcessEvent e) {	
		User user = e.getAuthor();
		if(user.isBot()) return;
		Message message = e.getMessage();
		if(e.getChannel() == discord.getMainTextChannel()) {
			e.setCancelled(true);
			UUID uuid = discord.getAccountLinkManager().getLinkedAccounts().get(user.getId());
			if(uuid != null) {
				TextComponent msg = plugin.getConfigManager().getChatFormat(user, e.getMessage().getContentDisplay());
				Message rf = message.getReferencedMessage();
				String finalmsg = null;
				if(rf != null) {
					User rfuser = rf.getAuthor();
					if(!rfuser.isBot()) {					
						finalmsg = rfuser.getAsTag();
					} else {
						if(rfuser.getDiscriminator().equals("0000")) {
							finalmsg = rfuser.getName();
						}
					}		
				}
				if(finalmsg != null) { 
					finalmsg = PlaceholderAPI.setPlaceholders(null, "%profile_{"+finalmsg+"}_discordprefix-tag%");
					TextComponent reply = plugin.getUtils().getComponent("  &b[Respuesta]");
					TextComponent txthover = plugin.getUtils().getText(plugin.getUtils().color(finalmsg+ "&f "+rf.getContentDisplay()));
					ComponentBuilder cb = new ComponentBuilder();
					cb.append(txthover);							
					HoverEvent ev = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create());
					reply.setHoverEvent(ev);
					msg.addExtra(reply);
					
		}
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(plugin.getUtils().isignored(p.getUniqueId(), uuid)) continue;
					p.spigot().sendMessage(msg);				
				}		
			} else {
				user.openPrivateChannel().queue((channel) ->
				{
					channel.sendMessage(new NekoEmbed(plugin.getConfigManager().config.getString("Chat.LinkedAccountRequiredMessage")).getMessage(null, user, null)).queue(null, new ErrorHandler());				
				});
				e.getMessage().addReaction("❌").queue();
			}		
		}
	}
	public UUID UUIDFromUser(User user) {
		String discordID = user.getId();
		Map<String, UUID> accounts = discord.getAccountLinkManager().getLinkedAccounts();
		if(accounts.containsKey(discordID)) {
			return accounts.get(discordID);
			
		} else {
			return null;
		}
		
		
	}

	
	//channel.sendMessage(new NekoEmbed(plugin.getConfigManager().config.getString("Chat.LinkedAccountRequiredMessage")).getMessage(UUIDFromUser(user).toString(), user, null)).queue(null, new ErrorHandler());		




}
