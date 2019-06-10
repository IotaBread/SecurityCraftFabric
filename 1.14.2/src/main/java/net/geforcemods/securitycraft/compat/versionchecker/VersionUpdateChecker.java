package net.geforcemods.securitycraft.compat.versionchecker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;

public class VersionUpdateChecker {

	public static CompoundNBT getCompoundNBT() {
		CompoundNBT tag = new CompoundNBT();
		Gson gson = new GsonBuilder().create();

		try{
			URL updateURL = new URL("https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/" + Minecraft.getInstance().getVersion() + ".json");
			BufferedReader in = new BufferedReader(new InputStreamReader(updateURL.openStream()));

			Update update = gson.fromJson(in, Update.class);

			if(update == null) return null;

			if((update.getVersion().equals("0.0.0") && update.getFileName().equals("test")) || update.getVersion().equals(SecurityCraft.getVersion())){
				SecurityCraft.log("Running the latest version, no new updates avaliable.");
				return null;
			}

			tag.putString("newVersion", update.getVersion());
			tag.putString("updateUrl", update.getDownloadURL());
			tag.putString("changelog", update.getChangelog());
			tag.putString("newFileName", update.getFileName());
			tag.putBoolean("isDirectLink", true);
		}catch(JsonSyntaxException e){
			e.printStackTrace();
			return null;
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return null;
		}catch(MalformedURLException e){
			e.printStackTrace();
			return null;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}

		return tag;
	}

	public static class Update {

		private String version;
		private String downloadURL;
		private String fileName;
		private String changelog;

		public Update(String version, String downloadURL, String fileName, String changelog){
			this.version = version;
			this.downloadURL = downloadURL;
			this.fileName = fileName;
			this.changelog = changelog;
		}

		public String getVersion(){
			return version;
		}

		public String getDownloadURL(){
			return downloadURL;
		}

		public String getFileName(){
			return fileName;
		}

		public String getChangelog(){
			return changelog;
		}

	}

}
