package me.Dariela.DSRVNekoTools.Listeners;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.Command;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonStyle;
import me.Dariela.DSRVNekoTools.NekoMain;
import me.Dariela.DSRVNekoTools.Managers.DiscordManager;
import me.Dariela.DSRVNekoTools.Objects.NekoResponse;

public class DiscordHook {
	
	HashMap<String, String> discord_commands = new HashMap<String, String>();
	NekoMain plugin;  
	DiscordListener discord;
	
    public DiscordHook(NekoMain plugin) {
    	this.plugin = plugin;
    	this.discord = new DiscordListener();
    	
    }
    public void loadDiscordHook() {
	    
	    DiscordSRV.api.subscribe(this.discord);
	    DiscordManager.reload();
	    removeUnknownCacheCommands();
	    registerDiscord();
	    loadButtons();
	    lodadVerificationCommand();
	    loadCustomCommands();
	    //updateCommands();
	    plugin.getDiscord().getMainGuild().updateCommands().queue();
	    	
	    
    }
    public void unloadDiscordHook() {  	
    	DiscordSRV.api.unsubscribe(this.discord);
    	unloadVerificationCommand();
    	removeUnknownCacheCommands();
    	//updateCommands();
    }
    
    public void removezZzListeners() {	
		if(plugin.getDiscord().getJda().getRegisteredListeners() == null) return;
		for(Object obj :plugin.getDiscord().getJda().getRegisteredListeners()) {
	        if(obj.getClass().getName().equals("github.scarsz.discordsrv.listeners.DiscordAccountLinkListener")){
	    		plugin.getDiscord().getJda().removeEventListener(obj);
	        }
		}
    }
    

    private void registerDiscord() {
		List<Object> listeners = plugin.getDiscord().getJda().getRegisteredListeners();
		String status = "[DiscordSRV - NekoTools -> JDA] Registrado un nuevo listener..";
	    if (listeners != null) {
		      for (Object ob : listeners) {
		    	  if(ob.getClass().getName().contains("me.Dariela.DSRVNekoTools.Listeners.JDAListener")) {	
		    		  
		    		  status = "[DiscordSRV - NekoTools -> JDA] Actualizado el listener de discord";
		    		  plugin.getDiscord().getJda().removeEventListener(ob);
		    	  }   
		      }
		    }
    
	    Bukkit.getConsoleSender().sendMessage(status);
        plugin.getDiscord().getJda().addEventListener(new JDAListener(plugin));	    

    }
    public void unloadVerificationCommand() {
    	if(!plugin.getConfigManager().verification_enabled) {
        	String command_ID = plugin.getConfigManager().command_cache.getString("ConfigCommands.verification");
    		Command command = plugin.getDiscord().getJda().retrieveCommandById(command_ID).complete();
    		command.delete().complete();
    		plugin.getConfigManager().command_cache.set("ConfigCommands.verification", null); 
    		plugin.getConfigManager().saveCommandCache();
    	}

    	
    }
    public void unloadCustomCommand(String str) {
    	Long command_ID = plugin.getConfigManager().command_cache.getLong("Responses."+str);
    	if(command_ID != 0) {
    		Command command = plugin.getDiscord().getJda().retrieveCommandById(command_ID).complete();
    		command.delete().complete();
    		plugin.getConfigManager().command_cache.set("ConfigCommands.verification", null); 
    		plugin.getConfigManager().saveCommandCache();
    	}	
    }
    
    public void lodadVerificationCommand() {
    	Long command_ID = plugin.getConfigManager().command_cache.getLong("ConfigCommands.verification");
		if(plugin.getConfigManager().verification_enabled) {
			if(command_ID == 0) {
				if(!discord_commands.containsKey(plugin.getConfigManager().verification_label)) {
					CommandData cmdata = new CommandData(plugin.getConfigManager().verification_label, plugin.getConfigManager().verification_desc);
					cmdata.addOption(OptionType.INTEGER, "code", plugin.getConfigManager().verification_desc, true);
					plugin.getDiscord().getJda().upsertCommand(cmdata).queue((command) ->
					{
						plugin.cmd_verification = command.getName();
						plugin.getConfigManager().command_cache.set("ConfigCommands.verification", command.getIdLong()); 
						plugin.getConfigManager().saveCommandCache();
					}, null);			
				} 				
			} else {
				Command command = plugin.getDiscord().getJda().retrieveCommandById(command_ID).complete();
		    	String label = plugin.getConfigManager().verification_label;
		    	String desc = plugin.getConfigManager().verification_desc;
		    	if(!command.getName().equals(label)) command.editCommand().setName(label).complete();
		    	if(!command.getDescription().equals(desc)) command.editCommand().setName(desc).complete();				
			}
		} 
    	
    }

    
    public void removeUnknownCacheCommands() {
    	for(String str : plugin.getConfigManager().command_cache.getConfigurationSection("Responses.").getKeys(false)) {	
    		String cache = plugin.getConfigManager().responses.getString(str+".Command.label");
    		if(cache == null) {
    			Long command_ID = plugin.getConfigManager().command_cache.getLong("Responses."+str);
    			Command command = plugin.getDiscord().getJda().retrieveCommandById(command_ID).complete();
    			//Bukkit.getConsoleSender().sendMessage("[-----] Elminado el "+command.getName());
    			command.delete().complete();
    			plugin.getConfigManager().command_cache.set("Responses."+str, null);
    			plugin.getConfigManager().saveCommandCache();
    			
    		}
    	}
    	/*
    	reloadCacheResponses();
    	List<Long> jda_commands = new ArrayList<Long>();
    	HashMap<String, Long> cache_commands = new HashMap<String, Long>();
    	for(Command cmd : plugin.getDiscord().getJda().retrieveCommands().complete()) {
    		jda_commands.add(cmd.getIdLong());		
    	}
    	for(String str : cache_responses) {
    		Long id = plugin.getConfigManager().command_cache.getLong("Responses."+str);
    		cache_commands.put(str, id);		
    	}
    	for(String cmd : cache_commands.keySet()) {
    		if(!jda_commands.contains(cache_commands.get(cmd))) {
    			plugin.getConfigManager().command_cache.set("Responses."+cmd, null);
    			plugin.getConfigManager().saveCommandCache();
    			cache_responses.remove(cmd);
    		}  		
    	}

    	*/	
    }
    /*
    public void reloadCacheResponses() {
    	if(!plugin.getConfigManager().command_cache.contains("Responses.")) return;
    	for(String str : plugin.getConfigManager().command_cache.getConfigurationSection("Responses.").getKeys(false)) {
    		this.cache_responses.add(str);
    		
    	}
    }
    private void deleteCache(String str) {
    	plugin.getConfigManager().command_cache.set("Responses."+str, null);
    	plugin.getConfigManager().saveCommandCache();
    	
    }
     */
   
    public void updateCommands() {
    
    	for(Guild guild : plugin.getDiscord().getJda().getGuilds()) {
    		guild.updateCommands().complete();
    		
    	}
    }
	public void loadButtons() {
		for(String key : plugin.getConfigManager().buttons.getConfigurationSection("").getKeys(false)){
			plugin.buttons.put(key.toLowerCase(), button(key));
			
		}		
	}
	

    private void loadCustomCommands() {
		for(String str : plugin.getConfigManager().responses.getConfigurationSection("").getKeys(false)) {
			String id = str;
			String cmd_label = plugin.getConfigManager().responses.getString(str+".Command.label");
			String description = plugin.getConfigManager().responses.getString(str+".Command.description");
			String role_needed  = plugin.getConfigManager().responses.getString(str+".Command.role_needed");
			String response  = plugin.getConfigManager().responses.getString(str+".response");
			NekoResponse reponse = new NekoResponse(id, cmd_label, description, role_needed, response); 			
		    if(!plugin.new_responses.contains(id)) plugin.new_responses.add(id);
		    
			reponse.registerCommand();	
		}
    	
    }
	public Button button(String key) {
		Button button = null;
		String type = plugin.getConfigManager().buttons.getString(key+".type").toUpperCase();
		String label = plugin.getConfigManager().buttons.getString(key+".label");
		String label_emoji_key = StringUtils.substringBetween(label, "EMOJI{", "}");
		String keyEmoji = plugin.getConfigManager().buttons.getString(key+".emoji");
		String link = plugin.getConfigManager().buttons.getString(key+".link");

		
		Emoji emoji = null;
		if(keyEmoji != null) {
			emoji = Emoji.fromMarkdown(keyEmoji);
			if(keyEmoji.length() == 1) emoji = Emoji.fromUnicode(keyEmoji);		
		}
		
		
		
		if(type.equals("LINK")) {
			if(label_emoji_key != null) {
				Emoji label_emoji = null;
				label_emoji = Emoji.fromMarkdown(label_emoji_key);
				if(label_emoji_key.length() == 1) label_emoji = Emoji.fromUnicode(label_emoji_key);			
				button = Button.of(ButtonStyle.LINK, link, label_emoji);	
				
			} else {
				button = Button.of(ButtonStyle.LINK, link, label, emoji);
			}

			
		} else {
			if(label_emoji_key != null) {
				Emoji label_emoji = null;
				label_emoji = Emoji.fromMarkdown(label_emoji_key);
				if(label_emoji_key.length() == 1) label_emoji = Emoji.fromUnicode(label_emoji_key);			
				button = Button.of(ButtonStyle.valueOf(type), "nekotools:"+key, label_emoji);			
			} else {
				button = Button.of(ButtonStyle.valueOf(type), "nekotools:"+key, label, emoji);
			}
		}
		
		
		
		return button;
		
	}

 
	  
}
	 