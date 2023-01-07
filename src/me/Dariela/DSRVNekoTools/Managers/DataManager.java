package me.Dariela.DSRVNekoTools.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.Dariela.DSRVNekoTools.NekoMain;
import me.Dariela.DSRVNekoTools.Objects.NekoEmote;

public class DataManager {
	NekoMain plugin;
	public List<String> used_codes = new ArrayList<String>();
	public HashMap<UUID, NekoEmote> buttons = new HashMap<UUID, NekoEmote>();
	
	public DataManager(NekoMain plugin) {
		this.plugin = plugin;
		
	}
	

}
