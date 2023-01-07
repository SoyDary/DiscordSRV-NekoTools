package me.Dariela.DSRVNekoTools.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import me.Dariela.DSRVNekoTools.NekoMain;
import me.Dariela.DSRVNekoTools.Objects.NekoHover;
import me.Dariela.DSRVNekoTools.Objects.NekoHoverElement;
import me.Dariela.DSRVNekoTools.Utils.ComponentUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;

public class ConfigManager {
	String chat_format = "Chat.Format";
	String Invalid_tag = "Verification.Messages.MinecraftMessages.Invalid_tag";
    String Unknown_user = "Verification.Messages.MinecraftMessages.Unknown_user";
    String Sucess_message = "Verification.Messages.MinecraftMessages.Sucess_message";
	String Error_message = "Verification.Messages.MinecraftMessages.Error_message";
    String Confirmation_request = "Verification.Messages.DiscordMessages.Confirmation_request";
    String Confirmation_sucess  = "Verification.Messages.DiscordMessages.Confirmation_sucess";
	String Invalid_code = "Verification.Messages.DiscordMessages.Invalid_code";
	
	public static NekoMain plugin;
	public HashMap<UUID, Player> codes = new HashMap<UUID, Player>();
	public HashMap<Player, UUID> cache = new HashMap<Player, UUID>();
	public List<String> embedsList = new ArrayList<String>();
	
	File configFile;
	File embedsFile;
	File command_cacheFile;	
	File responsesFile;
	File buttonsFile;

	public FileConfiguration config;
	public FileConfiguration embeds;
	public FileConfiguration command_cache;
	public FileConfiguration responses;
	public FileConfiguration buttons;
	
	
	public Boolean verification_enabled;
	public String verification_label;
	public String verification_desc;
	
	@SuppressWarnings("static-access")
	public ConfigManager(NekoMain plugin) {
		this.configFile = new File(plugin.getDataFolder() + File.separator+ "config.yml");
		this.embedsFile = new File(plugin.getDataFolder() + File.separator+ "embeds.yml");
		this.command_cacheFile = new File(plugin.getDataFolder() +File.separator+ "command_cache.yml");
		this.responsesFile = new File(plugin.getDataFolder() +File.separator+ "responses.yml");		
		this.buttonsFile = new File(plugin.getDataFolder() +File.separator+ "buttons.yml");		
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
		this.embeds = (FileConfiguration)YamlConfiguration.loadConfiguration(this.embedsFile);
		this.command_cache = (FileConfiguration)YamlConfiguration.loadConfiguration(this.command_cacheFile);
		this.responses = (FileConfiguration)YamlConfiguration.loadConfiguration(this.responsesFile);
		this.buttons = (FileConfiguration)YamlConfiguration.loadConfiguration(this.buttonsFile);
		
		this.plugin = plugin;
		this.verification_enabled = config.getBoolean("Verification.DiscordCommand.enabled");
	    this.verification_label = config.getString("Verification.DiscordCommand.command").toLowerCase();
		this.verification_desc = config.getString("Verification.DiscordCommand.description");
		
	}

	public FileConfiguration getdiscordSRV_Messages() {
		return  (FileConfiguration)YamlConfiguration.loadConfiguration(plugin.getDiscord().getMessagesFile());
		
		
	}
	public void onStart() {		
		if(!configFile.exists()) {	
            plugin.saveResource("config.yml", false);
            plugin.reloadConfig();
		}
		if(!command_cacheFile.exists()) {	
			saveCommandCache();
		}
		for(String str : embeds.getConfigurationSection("").getKeys(false)) {
			embedsList.add(str);
		}
		loadHovers();
	}
	public void saveConfig() {
		try {
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveCommandCache() {
		try {
			this.command_cache.save(this.command_cacheFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        embeds = YamlConfiguration.loadConfiguration(embedsFile);
        command_cache  = YamlConfiguration.loadConfiguration(command_cacheFile);
    }
	
	public void loadResponses() {

	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	public String getConfirmation_sucess() {
		return config.getString(Confirmation_sucess);
	}
	public String getConfirmation_request() {
		return config.getString(Confirmation_request);
	}
	public String getInvalid_code() {
		return config.getString(Invalid_code);
	}

	public String getInvalidTag(Player p) {
		String result = config.getString(Invalid_tag)	
				.replaceAll("%player-name%", p.getName())
				.replaceAll("%player-uuid%", p.getUniqueId().toString());	
		result = PlaceholderAPI.setPlaceholders(p, result);
		return plugin.getUtils().color(setPapis(result,p));
	}
	public String getUnknown_user(Player p) {
		String result = config.getString(Unknown_user)
				.replaceAll("%player-name%", p.getName())
				.replaceAll("%uuid%", p.getUniqueId().toString());
		result = PlaceholderAPI.setPlaceholders(p, result);
		return plugin.getUtils().color(setPapis(result,p));
	}
	public String getSucess_message(Player p, String user, String code) {
		String result = config.getString(Sucess_message)
				.replaceAll("%user-tag%", user)
				.replaceAll("%code%", code)
				.replaceAll("%player-namer", p.getName())
				.replaceAll("%player-uuid%", p.getUniqueId().toString());
		result = PlaceholderAPI.setPlaceholders(p, result);
		return plugin.getUtils().color(setPapis(result,p));
	}
	public String getError_message(Player p, String user, String code) {
		String result = config.getString(Error_message)	
				.replaceAll("%user%", user)
				.replaceAll("%code%", code)
				.replaceAll("%player-name%", p.getName())
				.replaceAll("%player-uuid%", p.getUniqueId().toString());
		result = PlaceholderAPI.setPlaceholders(p, result);
		return plugin.getUtils().color(setPapis(result,p));
	}
	public static String setPapis(String txt, Player p) {
		if(plugin.getUtils().hasPlaceholderApi) {
			return PlaceholderAPI.setPlaceholders(p, txt);
		} else {
			return txt;
		}	
	}
	public TextComponent getChatFormat(User user, String mensaje) {
		String format = colorToHex(config.getString(chat_format));
		format = format.replaceAll("%message%", mensaje);
		TextComponent end = new TextComponent();
		for(NekoHoverElement element : ComponentUtil.getTextComponents(format, null, user)) {
			TextComponent part = element.getTextComponent();
			String texto = element.getText();
			if(plugin.hovers.containsKey(texto)) {
				NekoHover hover = plugin.hovers.get(texto);
				part = ComponentUtil.addNekoHover(hover, null, user);
			}
			end.addExtra(part);			
		}
		return end;
		
	}
	public void loadHovers() {
		for(String key : config.getConfigurationSection("Chat.Hovers").getKeys(false)) {
			String text = config.getString("Chat.Hovers."+key+".text");
			List<String> lines = config.getStringList("Chat.Hovers."+key+".lines");
			String action = config.getString("Chat.Hovers."+key+".action");
			String actionText = config.getString("Chat.Hovers."+key+".action_text");
			NekoHover hover = new NekoHover(text, lines, action, actionText);
			plugin.hovers.put("%hover:"+key+"%", hover);
			
		}
	}
	public String colorToHex(String str) {
		str = str
				.replaceAll("&0", "&#000000")
				.replaceAll("&1", "&#0000aa")
				.replaceAll("&2", "&#00aa00")
				.replaceAll("&3", "&#00aaaa")
				.replaceAll("&4", "&#aa0000")
				.replaceAll("&5", "&#aa00aa")
				.replaceAll("&6", "&#ffaa00")
				.replaceAll("&7", "&#aaaaaa")
				.replaceAll("&8", "&#555555")
				.replaceAll("&9", "&#5555ff")
				.replaceAll("&a", "&#55ff55")
				.replaceAll("&b", "&#55ffff")
				.replaceAll("&c", "&#ff5555")
				.replaceAll("&d", "&#ff55ff")
		        .replaceAll("&e", "&#ffff55")
		        .replaceAll("&f", "&#ffffff");
		return str;

	}


	
	


	
	
}