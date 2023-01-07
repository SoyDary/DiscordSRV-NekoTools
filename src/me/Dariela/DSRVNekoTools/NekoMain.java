package me.Dariela.DSRVNekoTools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import me.Dariela.DSRVNekoTools.Listeners.DiscordHook;
import me.Dariela.DSRVNekoTools.Listeners.Eventos;
import me.Dariela.DSRVNekoTools.Managers.ConfigManager;
import me.Dariela.DSRVNekoTools.Managers.DataManager;
import me.Dariela.DSRVNekoTools.Objects.NekoHover;
import me.Dariela.DSRVNekoTools.Objects.NekoResponse;
import me.Dariela.DSRVNekoTools.Utils.Utils;
import net.luckperms.api.LuckPerms;


public class NekoMain extends JavaPlugin {
	Utils utils;
	ConfigManager cm;
	DiscordHook discordhook;
	LuckPerms luckperms;
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");	
	public Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");	
	DataManager datamanager;
	List<String> dcmds = new ArrayList<String>();
	public String version = getDescription().getVersion();
	public HashMap<String, NekoHover> hovers = new HashMap<String, NekoHover>();
	public HashMap<Long, NekoResponse> responses = new HashMap<Long, NekoResponse>();
	public HashMap<String, Button> buttons = new HashMap<String, Button>();
	public String cmd_verification = null;
	public List<String> new_responses = new ArrayList<String>();
	int times = 1;
	int t = 0;

	
	public void onEnable() {
		if(DiscordSRV.isReady) {
			enablePlugin();	
		} else {
			tryEnable();
		}
	}
	public void enablePlugin() {
		this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");	
		this.datamanager = new DataManager(this);
		this.cm = new ConfigManager(this); cm.onStart();
		this.discordhook = new DiscordHook(this);
		this.utils = new Utils(this);
		this.luckperms = (LuckPerms)getServer().getServicesManager().load(LuckPerms.class);
		Bukkit.getPluginManager().registerEvents(new Eventos(), this);
		this.registerCommands();
		this.discordhook.loadDiscordHook();
		this
		.discordhook.removezZzListeners();
		Bukkit.getConsoleSender().sendMessage("[DiscordSRV-NekoTools] Enabled uwu");
	

		
	}
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("[DiscordSRV-NekoTools] Disabled unu");
		discordhook.unloadDiscordHook();
		
	}
	

	
	public void registerCommands() {	
		getCommand("vincular").setExecutor(new Commands());
		getCommand("desvincular").setExecutor(new Commands());
		getCommand("discordtools").setExecutor(new Commands());
		getCommand("discordtools").setTabCompleter(new Commands());
	}
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	public LuckPerms getLuckPermsAPI() {
		return this.luckperms;
	}
	public DiscordSRV getDiscord() {
		return this.discord;
	}
	public ConfigManager getConfigManager() {
		return this.cm;
	}
	public Utils getUtils() {
		return this.utils;
	}
	public DataManager getDataManager() {
		return this.datamanager;
	}

	public void tryEnable() {
	    t = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		public void run() {	
			if(!DiscordSRV.isReady) {
				times++;
				if(times <= 6) {
				} else {
					cancelEnable();
					Bukkit.getPluginManager().disablePlugin((Plugin) this);
				}			
			} else {
				enablePlugin();	
				cancelEnable();			
			}		
		}		
		}, 60L, 60L);
	}
	public static NekoMain getInstance() {
		return (NekoMain)JavaPlugin.getPlugin(NekoMain.class);
	}

	public void cancelEnable() {
		Bukkit.getScheduler().cancelTask(t);
	}
	
	public String getServerVersion()
	{
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "unknown";
		}
		return version;	
	}
}