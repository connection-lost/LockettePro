package me.crafter.mc.lockettepro;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

	private static Plugin plugin;
	private static FileConfiguration config;
	private static FileConfiguration lang;
	private static Set<Material> lockables = new HashSet<Material>();
	private static Set<String> privatestrings = new HashSet<String>();
	private static Set<String> additionalstrings = new HashSet<String>();
	private static Set<String> everyonestrings = new HashSet<String>();
	private static Set<String> timerstrings = new HashSet<String>();
	private static String defaultprivatestring = "[Private]";
	private static String defaultadditionalstring = "[More Users]";
	private static byte enablequickprotect = (byte)1;
	private static boolean blockinterfereplacement = true;
	private static boolean blockitemtransferin = false;
	private static boolean blockitemtransferout = false;
	private static int cachetime = 0;
	private static boolean cacheenabled = false;
	private static byte blockhopperminecart = 0;
	
	public Config(Plugin _plugin){
		plugin = _plugin;
		reload();
	}
	
	@SuppressWarnings("deprecation")
	public static void reload(){
		plugin.saveDefaultConfig();
		initAdditionalFiles();
		config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
		lang = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml"));
		String enablequickprotectstring = config.getString("enable-quick-protect", "true");
		switch (enablequickprotectstring.toLowerCase()){
		case "true":
			enablequickprotect = 1;
			break;
		case "false":
			enablequickprotect = 0;
			break;
		case "sneak":
			enablequickprotect = 2;
			break;
		default:
			enablequickprotect = 1;
			break;
		}
		blockinterfereplacement = config.getBoolean("block-interfere-placement", true);
		blockitemtransferin = config.getBoolean("block-item-transfer-in", false);
		blockitemtransferout = config.getBoolean("block-item-transfer-out", true);
		
		List<String> privatestringlist = config.getStringList("private-signs");
		List<String> additionalstringlist = config.getStringList("additional-signs");
		List<String> everyonestringlist = config.getStringList("everyone-signs");
		privatestrings = new HashSet<String>(privatestringlist);
		additionalstrings = new HashSet<String>(additionalstringlist);
		everyonestrings = new HashSet<String>(everyonestringlist);
		defaultprivatestring = privatestringlist.get(0);
		defaultadditionalstring = additionalstringlist.get(0);
		
		List<String> timerstringlist = config.getStringList("timer-signs");
		List<String> timerstringlist2 = new ArrayList<String>();
		for (String timerstring : timerstringlist){
			if (timerstring.contains("@")) timerstringlist2.add(timerstring);
		}
		timerstrings = new HashSet<String>(timerstringlist2);

		cachetime = config.getInt("cache-time-seconds", 0) * 1000;
		cacheenabled = (config.getInt("cache-time-seconds", 0) > 0);
		if (cacheenabled){
			plugin.getLogger().info("You have cache enabled!");
			plugin.getLogger().info("This is currently for experimental purpose only!");
		}
		
		String blockhopperminecartstring = config.getString("block-hopper-minecart", "remove");
		switch (blockhopperminecartstring.toLowerCase()){
		case "true":
			blockhopperminecart = 1;
			break;
		case "false":
			blockhopperminecart = 0;
			break;
		case "remove":
			blockhopperminecart = 2;
			break;
		default:
			blockhopperminecart = 2;
			break;
		}
		List<String> unprocesseditems = config.getStringList("lockables");
		lockables = new HashSet<Material>();
		for (String unprocesseditem : unprocesseditems){
			if (unprocesseditem.equals("*")){
				for (Material material : Material.values()){
					lockables.add(material);
				}
				plugin.getLogger().info("All blocks are default to be lockable!");
				plugin.getLogger().info("Add '-<Material>' to exempt a block, such as '-STONE'!");
				continue;
			}
			boolean add = true;
			if (unprocesseditem.startsWith("-")){
				add = false;
				unprocesseditem = unprocesseditem.substring(1);
			}
			try { // Is it a number?
				int materialid = Integer.parseInt(unprocesseditem);
				// Hit here without error means yes it is
				if (add){
					lockables.add(Material.getMaterial(materialid));
				} else {
					lockables.remove(Material.getMaterial(materialid));
				}
			} catch (Exception ex){
				// It is not really a number...
				Material material = Material.getMaterial(unprocesseditem);
				if (material == null){
					plugin.getLogger().info(unprocesseditem + " is not an item!");
				} else {
					if (add){
						lockables.add(material);
					} else {
						lockables.remove(material);
					}
				}
			}
		}
		lockables.remove(Material.WALL_SIGN);
	}
	
	public static void initAdditionalFiles(){
		File langfile = new File(plugin.getDataFolder(), "lang.yml");
		if (!langfile.exists()){
			plugin.saveResource("lang.yml", false);
		}
	}

	public static byte getQuickProtectAction() {return enablequickprotect;}
	public static boolean isInterferePlacementBlocked() {return blockinterfereplacement;}
	public static boolean isItemTransferInBlocked() {return blockitemtransferin;}
	public static boolean isItemTransferOutBlocked() {return blockitemtransferout;}
	public static byte getHopperMinecartAction() {return blockhopperminecart;}
	
	public static String getLang(String path){
		return ChatColor.translateAlternateColorCodes('&', lang.getString(path, ""));
	}
	
	public static boolean isLockable(Material material){
		return lockables.contains(material);
	}
	
	public static boolean isPrivateSignString(String message){
		return privatestrings.contains(message);
	}
	
	public static boolean isAdditionalSignString(String message){
		return additionalstrings.contains(message);
	}
	
	public static boolean isEveryoneSignString(String message){
		return everyonestrings.contains(message);
	}
	
	public static boolean isTimerSignString(String message){
		for (String timerstring : timerstrings){
			String[] splitted = timerstring.split("@", 2);
			if (message.startsWith(splitted[0]) && message.endsWith(splitted[1])){
				return true;
			}
		}
		return false;
	}
	
	public static int getTimer(String message){
		for (String timerstring : timerstrings){
			String[] splitted = timerstring.split("@", 2);
			if (message.startsWith(splitted[0]) && message.endsWith(splitted[1])){
				String newmessage = message.replace(splitted[0], "").replace(splitted[1], "");
				try {
					int seconds = Integer.parseInt(newmessage);
					return Math.min(seconds, 20);
				} catch (Exception ex){}
			}
		}
		return 0;
	}
	
	public static String getDefaultPrivateString(){
		return defaultprivatestring;
	}
	
	public static String getDefaultAdditionalString(){
		return defaultadditionalstring;
	}

	public static int getCacheTimeMillis(){
		return cachetime;
	}
	
	public static boolean isCacheEnabled(){
		return cacheenabled;
	}
	
}
