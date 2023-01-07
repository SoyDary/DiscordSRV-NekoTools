package me.Dariela.DSRVNekoTools.Utils;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.User;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import github.scarsz.discordsrv.dependencies.alexh.Fluent.HashMap;
import me.Dariela.DSRVNekoTools.NekoMain;
import me.Dariela.DSRVNekoTools.Objects.NekoHover;
import me.Dariela.DSRVNekoTools.Objects.NekoHoverElement;


public class Utils {
	NekoMain plugin;
	public Boolean hasPlaceholderApi = Boolean.valueOf(false);
	private final Pattern pattern;
	public HashMap<String, NekoHover> hovers = new HashMap<String, NekoHover>();
  
 
  public Utils(NekoMain plugin) {
    this.pattern = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");
    this.plugin = plugin;
    Plugin pha = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
    if (pha != null)
      this.hasPlaceholderApi = Boolean.valueOf(pha.isEnabled()); 
  }
  public TextComponent getComponent(String msg) {
		TextComponent end = new TextComponent();
		for(NekoHoverElement element : ComponentUtil.getTextComponents("&#ffffff"+msg)) {
			TextComponent part = element.getTextComponent();
			end.addExtra(part);
			
		}
		return end;
	  
  }
  public Boolean isignored(UUID player1, UUID player2) {
	  User user = plugin.essentials.getUser(player1);
	  User user2 = plugin.essentials.getUser(player2);
	  return user.isIgnoredPlayer(user2);
	  
  }
  public TextComponent getComponent(Player p, String msg) {
		TextComponent end = new TextComponent();
		for(NekoHoverElement element : ComponentUtil.getTextComponents("&#ffffff"+msg, p, null)) {
			TextComponent part = element.getTextComponent();
			String texto = element.getText();
			
			if(hovers.containsKey(texto)) {
				NekoHover hover = hovers.get(texto);
				part = ComponentUtil.addNekoHover(hover, p, null);
			}
			end.addExtra(part);
			
		}
		return end;
	  
  }
  public String color(String text) {
	    String end = "";
	    if (text == null || text == "")
	      return ""; 
	    String text2 = parseColor(text);
	    String[] words = text2.split(Pattern.quote("&#"));
	    if (words.length != 0) {
	      int count = 0;
	      byte b;
	      int i;
	      String[] arrayOfString;
	      for (i = (arrayOfString = words).length, b = 0; b < i; ) {
	        String t = arrayOfString[b];
	        String more = "";
	        if (count != 0)
	          more = "#"; 
	        String t2 = t;
	        t2 = normalColor(t2);
	        t2 = HexColor(String.valueOf(more) + t2);
	        end = String.valueOf(end) + t2;
	        count++;
	        b++;
	      } 
	      return end;
	    } 
	    return text;
	  }

private String parseColor(String text) {
	    if ((((text.length() == 0) ? 1 : 0) | ((text == null) ? 1 : 0)) != 0 || text.length() < 7)
	      return text; 
	    String tedit = text;
	    String text2 = text;
	    for (int i = text2.length() - 1; i > 0; i--) {
	      String c = (new StringBuilder(String.valueOf(text2.charAt(i)))).toString();
	      if (c.contains("#") && i - 1 < 0 && text2.length() >= 7) {
	        String color = String.valueOf(c) + text2.charAt(i + 1) + text2.charAt(i + 2) + text2.charAt(i + 3) + text2.charAt(i + 4) + text2.charAt(i + 5) + text2.charAt(i + 6);
	        if (isColor(color))
	          tedit = addChar(text2, "&", i); 
	      } else if (c.contains("#") && i - 1 > 0 && i + 6 <= text2.length() - 1) {
	        String color = String.valueOf(c) + text2.charAt(i + 1) + text2.charAt(i + 2) + text2.charAt(i + 3) + text2.charAt(i + 4) + text2.charAt(i + 5) + text2.charAt(i + 6);
	        if (isColor(color) && !(new StringBuilder(String.valueOf(text2.charAt(i - 1)))).toString().contains("&"))
	          tedit = addChar(text2, "&", i); 
	      } 
	    } 
	    return tedit;
	  }
private String addChar(String str, String ch, int position) {
	    return String.valueOf(str.substring(0, position)) + ch + str.substring(position);
	  }
private boolean isColor(String text) {
	    String text2 = text;
	    if (text.startsWith("&"))
	      text2 = text.replaceFirst("&", ""); 
	    try {
	      ChatColor.of(text2);
	      return true;
	    } catch (Exception ex) {
	      return false;
	    }     
	  }

private String HexColor(String message) {
	    Matcher matcher = this.pattern.matcher(message);
	    while (matcher.find()) {
	      String color = message.substring(matcher.start(), matcher.end());
	      Boolean isColor = Boolean.valueOf(false);
	      try {
	        ChatColor.of(color);
	        isColor = Boolean.valueOf(true);
	      } catch (Exception exception) {}
	      if (isColor.booleanValue())
	        message = message.replace(color, ""+ChatColor.of(color)); 
	    } 
	    return message;
	  }
	  
private String normalColor(String message) {
	    message = ChatColor.translateAlternateColorCodes('&', message);
	    return message;
	  }


  public TextComponent advColor(String text) {
		    String ttt = text;
		    if (ttt.contains("�x"))
		      ttt = removeRgb(ttt); 
		    String[] rgb = ChatColor.translateAlternateColorCodes('&', ttt).split(Pattern.quote("#"));
		    ArrayList<TextComponent> list = new ArrayList<>();
		    TextComponent end = new TextComponent("");
		    int count = 0;
		    byte b;
		    int i;
		    String[] arrayOfString1;
		    for (i = (arrayOfString1 = rgb).length, b = 0; b < i; ) {
		      String v = arrayOfString1[b];
		      if (count == 0) {
		        TextComponent tc0 = new TextComponent(v.replaceAll("&", "�"));
		        list.add(tc0);
		      } else {
		        Boolean isColor = Boolean.valueOf(true);
		        try {
		          String acolor = "#" + v.substring(0, 6);
		          ChatColor.of(acolor);
		        } catch (Exception e) {
		          isColor = Boolean.valueOf(false);
		        } 
		        String color = "";
		        String v2 = "";
		        if (!isColor.booleanValue()) {
		          v2 = "#" + v;
		        } else {
		          color = "#" + v.substring(0, 6);
		          v2 = v.substring(6);
		        } 
		        TextComponent tc0 = new TextComponent(v2.replaceAll("&", "�"));
		        if (isColor.booleanValue())
		          tc0.setColor(ChatColor.of(color)); 
		        list.add(tc0);
		      } 
		      count++;
		      b++;
		    } 
		    for (TextComponent tc1 : list)
		      end.addExtra((BaseComponent)tc1); 
		    return end;
		}

	  

  public TextComponent getText(String text) {
	    String ttt = text;
	    ttt = removeRgb(ttt);
	    String[] rgb = ChatColor.translateAlternateColorCodes('&', ttt).split(Pattern.quote("&#"));
	    ArrayList<TextComponent> list = new ArrayList<>();
	    TextComponent end = new TextComponent("");
	    int count = 0;
	    byte b;
	    int i;
	    String[] arrayOfString1;
	    for (i = (arrayOfString1 = rgb).length, b = 0; b < i; ) {
	      String v = arrayOfString1[b];
	      if (count == 0) {
	        String v2 = v;
	        TextComponent tc0 = new TextComponent(v2);
	        list.add(tc0);
	      } else {
	        String color = "#" + v.substring(0, 6);
	        String v2 = v.substring(6);
	        //v2 = PlaceholderAPI.setPlaceholders(p, v2.replaceAll("%dcu%", "%discordsrv_user_tag%").replaceAll("%dcu2%", "%javascript_no_discord%"));
	        TextComponent tc0 = new TextComponent(v2);
	        tc0.setColor(ChatColor.of(color));
	        list.add(tc0);
	      } 
	      count++;
	      b++;
	    } 
	    for (TextComponent tc1 : list)
	      end.addExtra((BaseComponent)tc1); 
	    return end;
	  }

private String removeRgb(String textold) {
	    if (textold.length() < 12)
	      return textold; 
	    String text = textold.replaceAll("�", "&");
	    String endText = text;
	    String[] rgb = text.split("&x");
	    byte b;
	    int i;
	    String[] arrayOfString1;
	    for (i = (arrayOfString1 = rgb).length, b = 0; b < i; ) {
	      String value = arrayOfString1[b];
	      if (value.length() >= 12) {
	        String color = value.substring(0, 12);
	        int amount = 0;
	        for (int j = 0; j < color.length(); j++) {
	          if (color.charAt(j) == '&')
	            amount++; 
	        } 
	        if (amount == 6) {
	          String endColor = "&x" + color;
	          String newColor = "&#" + color.replaceAll("&", "");
	          endText = endText.replaceAll(endColor, newColor);
	        } 
	      } 
	      b++;
	    } 
	    return endText;
	  }
public GameProfile getGameProfile(Player p) {
		try {
			Class<?> strClass = (Class<?>) Class.forName("org.bukkit.craftbukkit."+plugin.getServerVersion()+".entity.CraftPlayer");
			GameProfile profile = (GameProfile) strClass.cast(p).getClass().getMethod("getProfile").invoke(strClass.cast(p));
			return profile;			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			return null;
		}
}
public String getSkinID(Player p) {
		GameProfile profile = getGameProfile(p);
		if(profile == null) return null;
		Property property = profile.getProperties().get("textures").iterator().next();
		String b64texture = property.getValue();
		byte[] valueDecoded = Base64.getDecoder().decode(b64texture);				
		String id = new String(valueDecoded).split("url")[1].split("\"")[2].split("texture/")[1];
		return id;	
	}


}
