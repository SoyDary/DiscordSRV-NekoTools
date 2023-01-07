package me.Dariela.DSRVNekoTools.Objects;

import java.util.List;

public class NekoHover {
	public String text = null;
	public List<String> hover = null;
    String action = null;
    String actionText = null;
	
	public NekoHover(String text, List<String> hover, String action, String actionText) {
		this.text = text;
		this.hover = hover;
		this.action = action;
		this.actionText = actionText;
		
	}
	
	public String getText() {
		return this.text;
	}
	
	public List<String> getHoverLines() {
		return this.hover;
	}
	
	public String getAction() {
		return this.action;	
	}
	
	public String actionText() {
		return this.actionText;
	}

}
