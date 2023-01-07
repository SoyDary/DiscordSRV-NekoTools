package me.Dariela.DSRVNekoTools;

import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.bukkit.entity.Player;

import github.scarsz.discordsrv.dependencies.alexh.Fluent.Map;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emote;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorHandler;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.Command;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonStyle;
import github.scarsz.discordsrv.dependencies.jda.api.requests.ErrorResponse;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import me.Dariela.DSRVNekoTools.Objects.NekoEmbed;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Commands implements CommandExecutor, TabCompleter{
	private NekoMain plugin = NekoMain.getPlugin(NekoMain.class);
	private String color(String str) {
		return plugin.getUtils().color(str);
		}
	String jda = null;
	Integer times = 0;
	int t = 0;
	
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


	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, org.bukkit.command.Command cmd, String label, String[] a)  {
		String l = cmd.getLabel();
		Player p = (Player) s;
		if(l.equalsIgnoreCase("desvincular")) {
			Bukkit.dispatchCommand(p, "discordsrv unlink");
			
		}
		if(l.equalsIgnoreCase("vincular")) {
			StringBuilder sb = new StringBuilder();			
		    for(int i = 0; i < a.length; i++) {
		    	if (i > 0) sb.append(" "+a[i]); else sb.append(a[i]);
		      }	   
		    String str = sb.toString().replaceAll(" ", "");	    
			try {
				plugin.getDiscord().getMainGuild().getMemberByTag(str);	
			} catch (IllegalArgumentException ex) {
				p.spigot().sendMessage(plugin.getUtils().getText(plugin.getConfigManager().getInvalidTag(p)));
				return true;
			}
			Member member = null;
			try {
				member = plugin.getDiscord().getMainGuild().getMemberByTag(sb.toString());
				if(member == null) {
					
					String name = str.split("#")[0]; String tag = str.split("#")[1];
					member = plugin.getDiscord().getMainGuild().findMembers(mem -> {
						String m_TAG = mem.getUser().getAsTag().replaceAll(" ", "");
					    if (m_TAG.startsWith(name+"#") && m_TAG.endsWith("#"+tag)){
					        return true;
					    }
					    return false;
					}).get().get(0);
				}
			} catch (Exception e) {}
			if(member == null) {
				p.spigot().sendMessage(plugin.getUtils().getText(plugin.getConfigManager().getUnknown_user(p)));
				return true;
			}
			String tag = member.getUser().getAsTag();
			github.scarsz.discordsrv.dependencies.jda.api.entities.User user = member.getUser();
			AccountLinkManager alm = plugin.getDiscord().getAccountLinkManager();
			if(alm.getLinkedAccounts().containsKey(member.getId()) && alm.getLinkedAccounts().get(member.getId()).equals(p.getUniqueId())) {
				p.sendMessage(color(plugin.getConfigManager().getdiscordSRV_Messages().getString("MinecraftAccountAlreadyLinked")));
				return true;
			}
			String code = alm.generateCode(p.getUniqueId());
			Button verify_button = Button.primary("discordtools-verify-code:"+code, "Verificar").withEmoji(Emoji.fromUnicode("✅"));
			Button delete_button = Button.danger("discordtools-delete", Emoji.fromUnicode("🗑️"));
			member.getUser().openPrivateChannel().queue((channel) ->
			{
				
			    channel.sendMessage(new NekoEmbed(plugin.getConfigManager().getConfirmation_request()).getMessage(p.getUniqueId(), user, null))
			    .setActionRow(verify_button, delete_button)
			    
			    .queue(rm -> p.spigot().sendMessage(plugin.getUtils().getText(plugin.getConfigManager().getSucess_message(p, tag, code))), new ErrorHandler()
	                    .ignore(ErrorResponse.UNKNOWN_MESSAGE) 
	                    .handle(ErrorResponse.CANNOT_SEND_TO_USER, (e) -> p.spigot().sendMessage(plugin.getUtils().getText(plugin.getConfigManager().getError_message(p, tag, code)))));
			});
				
		}
		if(l.equalsIgnoreCase("discordtools")) {
			if(a.length == 0) {

				
			} else {
				if(a[0].equalsIgnoreCase("commands")) {
					if(a.length == 1) {
						TextComponent end = plugin.getUtils().getText("&7&m&L                                              \n");
						end.addExtra(plugin.getUtils().getText("&#ffff80Comandos registrados en &#ffff00"+plugin.getDiscord().getJda().getSelfUser().getAsTag()+"\n"));
						for(Command dc_command : plugin.getDiscord().getJda().retrieveCommands().complete()) {
							TextComponent dcmd = plugin.getUtils().getText("&#40ff00☛ &3/&b"+dc_command.getName());
						    ComponentBuilder cb = new ComponentBuilder();
						    cb.append(plugin.getUtils().getText("&eID: &7"+dc_command.getId()));
						    cb.append(plugin.getUtils().getText("\n\n&c> &4&l&nClic para eliminar comando &c<"));
						    cb.append(plugin.getUtils().getText("\n\n&7&oMUCHO CUIDADITO JIJIJIJIJI"));
						    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create());
						    dcmd.setHoverEvent(event);
						    dcmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discordtools commands delete "+dc_command.getId()));    
						    end.addExtra(dcmd);		
						    end.addExtra("\n");
						}
						end.addExtra(plugin.getUtils().getText("&8&m&l                                              "));
						p.spigot().sendMessage(end);
					} else {
						if(a[1].equalsIgnoreCase("delete")) {
							long cmd_id = Long.valueOf(a[2]);
							plugin.getDiscord().getJda().retrieveCommandById(cmd_id).complete().delete().queue(aaa -> {
								p.sendMessage("§4Comando eliminado!");
								
							});
						}
					}
					
				}
				if(a[0].equalsIgnoreCase("nya")) {
					int count = 0;
					Guild kokori = plugin.discord.getMainGuild();
					Set<String> accounts = plugin.discord.getAccountLinkManager().getLinkedAccounts().keySet();
					int added = 0;
					int removed = 0;
					Role verificado = kokori.getRoleById("820722116559241256");
					for(Member mem : kokori.getMembersWithRoles(verificado)){
						if(!accounts.contains(mem.getId())) {
							kokori.removeRoleFromMember(mem, verificado).queue();
							removed++;
						}
					}
					for(String id : accounts) {
						Member member = kokori.getMemberById(id);
						if(member == null) continue;
						if(!member.getRoles().contains(verificado)) {
							kokori.addRoleToMember(member, verificado).queue();
							added++;
						}
						Bukkit.broadcast(""+member.getUser().getAsTag(), "*");
						count++;
					}
					Bukkit.broadcast("§8-----------------------------", "*");
					Bukkit.broadcast("§e"+accounts.size()+" §bcuentas encontradas! §f"+"(§3"+count+" §fdentro)", "");
					Bukkit.broadcast("§a"+added+" §7roles añadidos", "");
					Bukkit.broadcast("§c"+removed+" §7roles eliminados!", "");				
					Bukkit.broadcast("§8-----------------------------", "*");

					
				}
				if(a[0].equalsIgnoreCase("check")) {
					jda = "uwu";
					p.sendMessage("§3cambiado JDA a §7"+jda);
				}
				if(a[0].equalsIgnoreCase("test")) {
					
					for(Emote emo :plugin.getDiscord().getJda().getEmotes()) {
						
						Bukkit.broadcast("§e> §7"+emo.getName()+" §8- §f"+emo.getAsMention(), "*");
					}
				}
				if(a[0].equalsIgnoreCase("test2")) {


				}
				if(a[0].equalsIgnoreCase("send-dm")) {
					StringBuilder sb = new StringBuilder();			
				    for(int i = 2; i < a.length; i++) {
				    	if (i > 2) sb.append(" "+a[i]); else sb.append(a[i]);
				      }	
;
				    String usuario = a[1];
				    String mensaje = sb.toString();
					try {
						plugin.getDiscord().getMainGuild().getMemberByTag(usuario);	
					} catch (IllegalArgumentException ex) {
						p.sendMessage("§cFormato de id invalido!");
						return true;
					}
					String name = usuario.split("#")[0]; String tag = usuario.split("#")[1];
				    try {
						Member user = null;
						
						user = plugin.getDiscord().getMainGuild().findMembers(member -> {
							String m_TAG = member.getUser().getAsTag().replaceAll(" ", "");
						    if (m_TAG.startsWith(name+"#") && m_TAG.endsWith("#"+tag)){
						        return true;
						    }
						    return false;
						}).get().get(0);
						user.getUser().openPrivateChannel().queue((pm) -> 
						{
							
							pm.sendMessage(message(p, mensaje)).queue();
				
						});
						p.sendMessage(color("&8[&#ff99ff"+user.getUser().getAsTag()+"&8] &7--> &f&o"+mensaje));	    	
				    } catch (Exception ex){
				    	p.sendMessage("§cusuario invalido!");
				    	
				    }
					
				}
			}
	
		    /*
			for(Group group :plugin.getLuckPermsAPI().getGroupManager().getLoadedGroups()){
				
				InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
				if(user.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()) {		
				}
			}
			*/
			
		}
		return true;
	}

	public Message message(Player p, String msg) {
		
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode("#ff99ff"));
    	builder.setAuthor("Mensaje recibido de "+p.getName(),null,"https://i.imgur.com/c9iMyAI.png"); 
    	builder.setDescription(msg);
    	builder.setThumbnail("https://mc-heads.net/head/"+p.getName()+"/50/left.png"); 
    	mb.setEmbeds(builder.build()); 	
    	return mb.build();
    	}
	public Message vefiry(Player p) {
		
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode("#004d99"));
    	builder.setTitle("__**Verificación de cuenta**__");
    	builder.setDescription("<:aqua_prisma:862570818223079424> Da clic al botón para confirmar que `"+p.getName()+"` es tu cuenta.. ");
    	builder.setThumbnail("https://mc-heads.net/head/"+p.getName()+"/55/left.png"); 
    	mb.setEmbeds(builder.build()); 	
    	return mb.build();
    	}
	public Message newEmbed(String msg, String color) {
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode(color));
    	builder.setDescription(msg);
    	mb.setEmbeds(builder.build());
    	return mb.build();
    	}
	 public void sendMessage(TextChannel context, User user, String content) {
	     ((github.scarsz.discordsrv.dependencies.jda.api.entities.User) user).openPrivateChannel()
	         .flatMap(channel -> channel.sendMessage(content))
	         .delay(Duration.ofSeconds(30))
	         .flatMap(Message::delete) // delete after 30 seconds
	         .queue(null, new ErrorHandler()
	             .ignore(ErrorResponse.UNKNOWN_MESSAGE) // if delete fails that's fine
	             .handle(
	                 ErrorResponse.CANNOT_SEND_TO_USER,  // Fallback handling for blocked messages
	                 (e) -> context.sendMessage("Failed to send message, you block private messages!").queue()));
	 }
	@Override
	public List<String> onTabComplete(CommandSender s, org.bukkit.command.Command c, String label, String[] a) {
		String l = c.getLabel();
	    if (l.equalsIgnoreCase("discordtools")) {
		      if (a.length == 1) {
		        List<String> commandsList = new ArrayList<>();
		        List<String> preCommands = new ArrayList<>();
		        commandsList.add("test");
		        commandsList.add("commands");
		        commandsList.add("test2");
		        commandsList.add("nya");
		        commandsList.add("check");
		        commandsList.add("send-dm");
		        for (String text : commandsList) {
		          if (text.toLowerCase().startsWith(a[0].toLowerCase()))
		            preCommands.add(text); 
		        } 
		        return preCommands;
		      } 
		    } 
	    return null;
		
	}


}