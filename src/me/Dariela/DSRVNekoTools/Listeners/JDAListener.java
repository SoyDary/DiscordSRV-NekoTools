package me.Dariela.DSRVNekoTools.Listeners;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SelectionMenuEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonStyle;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import me.Dariela.DSRVNekoTools.NekoMain;
import me.Dariela.DSRVNekoTools.Objects.NekoEmbed;
import me.Dariela.DSRVNekoTools.Objects.NekoEmote;
import me.Dariela.DSRVNekoTools.Objects.NekoResponse;

public class JDAListener extends ListenerAdapter {

	NekoMain plugin;
	
    public JDAListener(NekoMain plugin) {
        this.plugin = plugin;
    }
    
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		String[] a = msg.split(" ");
		if(a[0].equals("!clear")) {
			if(a.length == 2) {
				int cantidad = Integer.parseInt(a[1]);
				List<Message> mensajes = e.getChannel().getHistory().retrievePast(cantidad).complete();
				for(Message m : mensajes) {
					if(m.getAuthor().isBot()) m.delete().complete();
					
				}
			}
		}
    }
    
    
    public void onSelectionMenu(SelectionMenuEvent e) {
    
    	if(e.getSelectionMenu().getId().startsWith("menu:request-")) {
    		String id = e.getSelectionMenu().getId().split("request-")[1];
    		e.reply("Solicitud enviada!").setEphemeral(true).queue();
        	e.getChannel().sendMessage(rankRequest(e)).setActionRow(
        			Button.primary("accept-request-uwu:"+id, "Aceptar").withEmoji(Emoji.fromUnicode("✅")),
        			Button.danger("deny-request-uwu:"+id, "Denegar").withEmoji(Emoji.fromUnicode("❌"))
        			).queue();
     
    		
 		
    	}

    	
    }
    
    public void onSlashCommand(SlashCommandEvent e) {
    	Long id = e.getCommandIdLong();
    	if (e.getCommandId().equals(plugin.getConfigManager().command_cache.getString("ConfigCommands.verification"))) {
    		String code = e.getOption("code").getAsString();
			AccountLinkManager alm = plugin.getDiscord().getAccountLinkManager();		
			if(alm.getLinkingCodes().containsKey(code) && !plugin.getDataManager().used_codes.contains(code)) {
				try {			
					NekoEmbed response = new NekoEmbed(plugin.getConfigManager().getConfirmation_sucess());
					plugin.getDataManager().used_codes.add(code);			
					e.reply(response.getMessage(alm.getLinkingCodes().get(code), e.getUser(), null)).setEphemeral(response.isEphemeral).queue();
					alm.process(code, e.getUser().getId());
					return;
						
				} catch (IllegalStateException ex) {}
			} else {
				
				NekoEmbed response = new NekoEmbed(plugin.getConfigManager().getInvalid_code());
				e.reply(response.getMessage(null, e.getUser(), e.getOptions())).setEphemeral(true).queue();
				return;				
			}   		
    	}
    	if(plugin.responses.containsKey(id)) {
    		NekoResponse response = plugin.responses.get(id);
    		ReplyAction reply = e.reply(response.getResponse().getMessage(null, e.getUser(), e.getOptions()));
    		if(!response.getResponse().buttons.isEmpty()) {
        		reply.addActionRow(response.getResponse().buttons).queue(miau -> 
        		{
        			miau.setEphemeral(response.getResponse().isEphemeral);
        			
        		});		
    		} else {
    			reply.setEphemeral(response.getResponse().isEphemeral).queue();
    		
    		}

    		
    		
    		//e.reply(response.getResponse().getMessage(null, e.getUser(), e.getOptions())).setEphemeral(response.getResponse().isEphemeral).queue((mm) ->{
    		
    		//});
    	}

    }




    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getComponentId().startsWith("discordtools-verify-code:")) {
        	String code = e.getComponentId().split("discordtools-verify-code:")[1];
			AccountLinkManager alm = plugin.getDiscord().getAccountLinkManager();		
			if(alm.getLinkingCodes().containsKey(code) && !plugin.getDataManager().used_codes.contains(code)) {
				try {
	
					NekoEmbed response = new NekoEmbed(plugin.getConfigManager().getConfirmation_sucess());
					//e.editButton(e.getButton().asDisabled()).queue();
					//OfflinePlayer of = Bukkit.getOfflinePlayer(alm.getLinkingCodes().get(code));
					plugin.getDataManager().used_codes.add(code);			
					e.reply(response.getMessage(alm.getLinkingCodes().get(code), e.getUser(), null)).setEphemeral(response.isEphemeral).queue();
					alm.process(code, e.getUser().getId());
					
					e.editButton(e.getButton().asDisabled()).queue();
					
					
				} catch (IllegalStateException ex) {}
			} else {
				NekoEmbed response = new NekoEmbed(plugin.getConfigManager().getInvalid_code());
				e.reply(response.getMessage(null, e.getUser(), null)).setEphemeral(true).queue();;
				e.editButton(e.getButton().asDisabled()).queue();
				
			}
			return;
        }
       
        if (e.getComponentId().equals("nekotools:reset_password")) {
        	e.reply("> <:yellow_prisma:862570818768338994> Contraseña reestablecida!").setEphemeral(true).queue();
        	e.editButton(e.getButton().asDisabled().withStyle(ButtonStyle.SECONDARY)).queue();
            new BukkitRunnable() {
                @Override
                public void run() {
                	OfflinePlayer of = Bukkit.getOfflinePlayer(plugin.getDiscord().getAccountLinkManager().getUuid(e.getUser().getId()));
                	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "authme unregister "+of.getName());
                }
            }.runTask(plugin);
      
        }
        if (e.getComponentId().equals("discordtools-delete")) {
        	e.getMessage().delete().complete();   
    
        }
    }

	
	public MessageEmbed embed(String msg, String color) {
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode(color));
    	builder.setDescription(msg);
    	return builder.build();
    	}
	
	public Message newEmbed(String msg, String color) {
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode(color));
    	builder.setDescription(msg);
    	mb.setEmbeds(builder.build());
    	return mb.build();
    	}
	
	public Message emojiEmbed(String msg,  String url) {
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode("#ffff4d"));
    	builder.setDescription("> Emoji: :"+msg+": \n> Name: `"+msg+"` \n> Link: ["+url.split("/")[6]+"]("+url+")");
    	builder.setThumbnail(url);
    	mb.setEmbeds(builder.build());
    	return mb.build();
    	}
	public Message rankRequest(SelectionMenuEvent e) {
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode("#ffff99"));
    	builder.setThumbnail(e.getUser().getAvatarUrl());
    	builder.setAuthor("Solicitud de rango entrante", null, "https://cdn.discordapp.com/emojis/928560104079507486.webp?size=80&quality=lossless");
    	builder.setDescription("<:gold_prisma:862570818559148052> "+e.getUser().getAsMention()+" solicita el rango `"+e.getSelectedOptions().get(0).getLabel()+"`");
    	builder.setImage(e.getSelectionMenu().getId().split("request-")[1]);
    	mb.setEmbeds(builder.build());
    	return mb.build();
    	}
}

/*

    */
    /*
    @Override
    public void onSlashCommand(SlashCommandEvent e) {
    	if(e.getName().equals("owo")) {
    		List<OptionMapping> options = e.getOptions();
    		if(options.contains(OptionType.MENTIONABLE)) {
    			
    		} 
    			
    		e.reply("mensaje").queue();;
    	}
    }
    
}
*/
/*
public void onSlashCommand(SlashCommandEvent e) {
    if (!e.getName().equals("ping")) return; // make sure we handle the right command
    long time = System.currentTimeMillis();
    e.reply("Pong!").setEphemeral(true) // reply or acknowledge
            .flatMap(v ->
                    e.getHook().editOriginalFormat("UwU: %d ms", System.currentTimeMillis() - time) // then edit original
            ).queue(); // Queue both reply and edit
}
*/
