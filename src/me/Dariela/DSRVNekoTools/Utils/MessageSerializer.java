package me.Dariela.DSRVNekoTools.Utils;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;

import java.awt.Color;

public class MessageSerializer {
	public MessageEmbed embedFromConfig() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.decode("#004d99"));
		eb.setAuthor(null, null, null);
		eb.setThumbnail(null);
		eb.setTitle(null, null);
		eb.setDescription("uwu");
		eb.addField(null, null, false);
		eb.setImage(null);
		eb.setFooter(null, null);
		eb.setTimestamp(null);
		return eb.build();
	}

}