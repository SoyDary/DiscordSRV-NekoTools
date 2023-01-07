package me.Dariela.DSRVNekoTools.Objects;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import me.Dariela.DSRVNekoTools.NekoMain;



public class NekoEmote {
	public String url;
	public String name;
	NekoMain plugin = NekoMain.getInstance();
	
	public NekoEmote(String name, String url) {
		this.url = url;
		this.name = name;
		
	}
	
	public void saveImage() {
		URL url = null;
		byte[] response = null;
		try {
			url = new URL(this.url);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1!=(n=in.read(buf)))
			{
			   out.write(buf, 0, n);
			}
			out.close();
			in.close();
			response = out.toByteArray();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		if(response == null) {
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(plugin.getDataFolder()+"/emojis/"+name+".png");
			fos.write(response);
			fos.close();
			
		} catch (Exception e) {	
			e.printStackTrace();

		}


	}

}
