package me.Dariela.DSRVNekoTools.Managers;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import me.Dariela.DSRVNekoTools.NekoMain;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DiscordManager {
  public static File DiscordFile = new File("plugins/NekoTeams/discord.yml");
  
  public static FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(DiscordFile);
  
  public static HashMap<String, String> keys = new HashMap<>();
  
  public static void reload() {
	  /*
    config = (FileConfiguration)YamlConfiguration.loadConfiguration(DiscordFile);
    for (String key : config.getConfigurationSection("").getKeys(false)) {
      if (config.getString(String.valueOf(key) + ".Alias") != null)
        keys.put(String.valueOf(config.getString(String.valueOf(key) + ".Alias")) + key, key); 
    } 
    */
  }
  
  static NekoMain plugin = NekoMain.getPlugin(NekoMain.class);
  

 
  
  public static void sendDiscord(TextChannel channel, final Message message, String l) {
    String path = l;
    String content = simpleReplace(config.getString(String.valueOf(path) + ".Content"), path);
    MessageEmbed me = null;
    Boolean enabled = Boolean.valueOf(config.getBoolean(String.valueOf(path) + ".Enabled"));
    Boolean embed = Boolean.valueOf(config.getBoolean(String.valueOf(path) + ".Embed.Enabled"));
    Boolean webhook = Boolean.valueOf(config.getBoolean(String.valueOf(path) + ".Webhook.Enable"));
    if (!enabled.booleanValue())
      return; 
    if (embed.booleanValue()) {
      String authorImage = embed.booleanValue() ? simpleReplace(config.getString(String.valueOf(path) + ".Embed.Author.ImageUrl"), path) : null;
      String authorName = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Author.Name"), path);
      String authorUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Author.Url"), path);
      String thumbnail = simpleReplace(config.getString(String.valueOf(path) + ".Embed.ThumbnailUrl"), path);
      String titleText = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Title.Text"), path);
      String titleUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Title.Url"), path);
      String description = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Description"), path);
      String imageUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.ImageUrl"), path);
      String footerText = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Footer.Text"), path);
      String color = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Color"), path);
      String footerUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Footer.IconUrl"), path);
      String fields = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Fields"), path);
      EmbedBuilder builder = new EmbedBuilder();
      builder.setColor(Color.decode(color));
      builder.setAuthor(authorName, (authorUrl == "") ? null : authorUrl, (authorImage == "") ? null : authorImage);
      builder.setThumbnail((thumbnail == "") ? null : thumbnail);
      builder.setTitle(titleText, titleUrl);
      builder.setDescription(description);
      builder.setImage((imageUrl == "") ? null : imageUrl);
      builder.setFooter(footerText, (footerUrl == "") ? null : footerUrl);
      if (fields != null && !fields.equalsIgnoreCase(""))
        if ((fields.split(",")).length == 1) {
          if (fields.equalsIgnoreCase("<blank>")) {
            builder.addBlankField(true);
          } else {
            String v1 = fields.split(";")[0];
            String v2 = fields.split(";")[1];
            Boolean v3 = Boolean.valueOf(fields.split(";")[2]);
            builder.addField(v1, v2, v3.booleanValue());
          } 
        } else {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = fields.split(",")).length, b = 0; b < i; ) {
            String text = arrayOfString[b];
            if (text.equalsIgnoreCase("<blank>")) {
              builder.addBlankField(true);
            } else {
              String v1 = text.split(";")[0];
              String v2 = text.split(";")[1];
              Boolean v3 = Boolean.valueOf(text.split(";")[2]);
              builder.addField(v1, v2, v3.booleanValue());
            } 
            b++;
          } 
        }  
      me = builder.build();
    } 
    if (webhook.booleanValue()) {
      String hookAvatar = simpleReplace(config.getString(String.valueOf(path) + ".Webhook.AvatarUrl"), path);
      String hookName = simpleReplace(config.getString(String.valueOf(path) + ".Webhook.Name"), path);
      WebhookUtil.deliverMessage(channel, hookName, hookAvatar, content, me);
    } else if (embed.booleanValue()) {
      MessageBuilder mb = new MessageBuilder();
      mb.setContent(content);
      mb.setEmbeds(me);
      Message m = mb.build();
      channel.sendMessage(m).queue(msg -> {
    	  
          });
    } else {
      DiscordUtil.sendMessage(channel, content);

    } 
  }
  
  public static Object getUpdatedMessage(String l) {
    String path = l;
    String content = simpleReplace(config.getString(String.valueOf(path) + ".Content"), path);
    MessageEmbed me = null;
    Boolean enabled = Boolean.valueOf(config.getBoolean(String.valueOf(path) + ".Enabled"));
    Boolean embed = Boolean.valueOf(config.getBoolean(String.valueOf(path) + ".Embed.Enabled"));
    Boolean webhook = Boolean.valueOf(config.getBoolean(String.valueOf(path) + ".Webhook.Enable"));
    if (!enabled.booleanValue())
      return null; 
    if (embed.booleanValue()) {
      String authorImage = embed.booleanValue() ? simpleReplace(config.getString(String.valueOf(path) + ".Embed.Author.ImageUrl"), path) : null;
      String authorName = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Author.Name"), path);
      String authorUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Author.Url"), path);
      String thumbnail = simpleReplace(config.getString(String.valueOf(path) + ".Embed.ThumbnailUrl"), path);
      String titleText = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Title.Text"), path);
      String titleUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Title.Url"), path);
      String description = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Description"), path);
      String imageUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.ImageUrl"), path);
      String footerText = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Footer.Text"), path);
      String color = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Color"), path);
      String footerUrl = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Footer.IconUrl"), path);
      String fields = simpleReplace(config.getString(String.valueOf(path) + ".Embed.Fields"), path);
      EmbedBuilder builder = new EmbedBuilder();
      builder.setColor(Color.decode(color));
      builder.setAuthor(authorName, (authorUrl == "") ? null : authorUrl, (authorImage == "") ? null : authorImage);
      builder.setThumbnail((thumbnail == "") ? null : thumbnail);
      builder.setTitle(titleText, titleUrl);
      builder.setDescription(description);
      builder.setImage((imageUrl == "") ? null : imageUrl);
      builder.setFooter(footerText, (footerUrl == "") ? null : footerUrl);
      if (fields != null && !fields.equalsIgnoreCase(""))
        if ((fields.split(",")).length == 1) {
          if (fields.equalsIgnoreCase("<blank>")) {
            builder.addBlankField(true);
          } else {
            String v1 = fields.split(";")[0];
            String v2 = fields.split(";")[1];
            Boolean v3 = Boolean.valueOf(fields.split(";")[2]);
            builder.addField(v1, v2, v3.booleanValue());
          } 
        } else {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = fields.split(",")).length, b = 0; b < i; ) {
            String text = arrayOfString[b];
            if (text.equalsIgnoreCase("<blank>")) {
              builder.addBlankField(true);
            } else {
              String v1 = text.split(";")[0];
              String v2 = text.split(";")[1];
              Boolean v3 = Boolean.valueOf(text.split(";")[2]);
              builder.addField(v1, v2, v3.booleanValue());
            } 
            b++;
          } 
        }  
      me = builder.build();
    } 
    if (!webhook.booleanValue()) {
      if (embed.booleanValue()) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setContent(content);
        messageBuilder.setEmbeds(me);
        Message message = messageBuilder.build();
        return message;
      } 
      MessageBuilder mb = new MessageBuilder();
      mb.setContent(content);
      Message m = mb.build();
      return m;
    } 
    return null;
  }
  
  public static String simpleReplace(String text, String path) {
    String endText = text;
    endText = PlaceholderUtil.replacePlaceholders(endText);
    return endText;
  }
}