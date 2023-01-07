package me.Dariela.DSRVNekoTools.Objects;

import org.bukkit.Bukkit;

import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorHandler;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.Command;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import me.Dariela.DSRVNekoTools.NekoMain;

public class NekoResponse {

	NekoMain plugin = NekoMain.getPlugin(NekoMain.class);
	public String key;
	public String label;
	public String description;
	public String role;
	public String response;
	public Long commandID = (long) 0;
	
	public NekoResponse(String key, String label, String description, String role, String response) {
		this.key = key;
		this.label = label.toLowerCase();
		this.description = description;
		this.role = response;
		this.response = response;
		this.commandID = plugin.getConfigManager().command_cache.getLong("Responses."+key);
		
	}
	public NekoEmbed getResponse() {
		return new NekoEmbed(response);
		
	}
	/*
	  STRING
	  INTEGER
	  BOOLEAN
	  USER
	  CHANNEL
	  ROLE
	  MENTIONABLE
	  NUMBER
	  */

	public void registerCommand() {
		if(commandID == 0) {
			CommandData cmd = null;
			try {
				cmd = new CommandData(label, description);
			} catch (Exception e){
				Bukkit.getConsoleSender().sendMessage("[DiscordSRV-NekoTools] Hubo un error al registrar la respuesta "+key+"  >  "+e.getLocalizedMessage());
			}
			if(cmd != null) {
				plugin.getDiscord().getJda().upsertCommand(cmd).queue((command) ->
				{
					plugin.responses.put(command.getIdLong(), this);
					plugin.getConfigManager().command_cache.set("Responses."+key, command.getIdLong()); 
					plugin.getConfigManager().saveCommandCache();	
					//Bukkit.getConsoleSender().sendMessage("[^^^^] registrado el comando "+command.getName());
				}, new ErrorHandler());
				
			}

			
		} else {
			updateCommand();
		}

		
	}
	public void updateCommand() {
		if(commandID != 0) {
			Command command = plugin.getDiscord().getJda().retrieveCommandById(commandID).complete();
			try {
		    	if(!command.getName().equals(label)) command.editCommand().setName(label).complete();
		    	if(!command.getDescription().equals(description)) command.editCommand().setDescription(description).complete();		
				
			} catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage("[DiscordSRV-NekoTools] Hubo un error al actualizar la respuesta "+key+"  >  "+e.getLocalizedMessage());
				command.delete().complete();
				plugin.getConfigManager().command_cache.set("Responses."+key, null);
				plugin.getConfigManager().saveCommandCache();
				return;
			}
			plugin.responses.put(command.getIdLong(), this);

		}
		
		
	}
		

}