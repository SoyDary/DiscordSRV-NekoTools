package me.Dariela.DSRVNekoTools.Objects;

import net.md_5.bungee.api.chat.TextComponent;

public class NekoHoverElement {
    String oldText;
    TextComponent component;
    
    public NekoHoverElement(TextComponent component, String oldText) {   	
        this.component = component;
        this.oldText = oldText;
    	
    }
    
    public TextComponent getTextComponent() {
        return this.component;
    }

    public String getText() {
        return this.oldText;
    }
    
    
}
