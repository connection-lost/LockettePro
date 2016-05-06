package me.crafter.mc.lockettepro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class LockettePro extends JavaPlugin {

	private static Plugin plugin;
	private boolean debug = false;
	private static boolean is19 = false;

	public void onEnable(){
    	plugin = this;
		// Read config
		new Config(this);
		// Register Listeners
		// If debug mode is not on, debug listener won't register
    	if (debug) getServer().getPluginManager().registerEvents(new BlockDebugListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockPlayerListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockEnvironmentListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockInventoryMoveListener(), this);
    	// If UUID is not enabled, UUID listener won't register
    	if (Config.isUuidEnabled()){
			if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null){
	    		DependencyProtocolLib.setUpProtocolLib(this);
			} else {
				plugin.getLogger().info("ProtocolLib is not found!");
				plugin.getLogger().info("UUID support requires ProtocolLib, or else signs will be ugly!");
			}
    	}
    	// Dependency
    	new Dependency(this);
    	// Metrics
    	try {
    		Metrics metrics = new Metrics(this);
	        metrics.start();
    	} catch (Exception ex){}
    	if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")){
    		is19 = false;
    	} else {
    		is19 = true;
    	}
    }
	
    public void onDisable(){
		if (Config.isUuidEnabled() && Bukkit.getPluginManager().getPlugin("ProtocolLib") != null){
			DependencyProtocolLib.cleanUpProtocolLib(this);
		}
    }
    
    public static Plugin getPlugin(){
    	return plugin;
    }
    
    public static boolean is19(){
    	return is19;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, final String[] args){
    	if (cmd.getName().equals("lockettepro")){
    		if (args.length == 0){
    	    	Utils.sendMessages(sender, Config.getLang("command-usage"));
    		} else {
    			// The following commands does not require player
    			switch (args[0]){
    			case "reload":
    				if (sender.hasPermission("lockettepro.reload")){
    					Config.reload();
    					Utils.sendMessages(sender, Config.getLang("config-reloaded"));
    				} else {
    					Utils.sendMessages(sender, Config.getLang("no-permission"));
    				}
    				return true;
    			case "version":
    				if (sender.hasPermission("lockettepro.version")){
    					sender.sendMessage(plugin.getDescription().getFullName());
    				} else {
    					Utils.sendMessages(sender, Config.getLang("no-permission"));
    				}
    				return true;
    			}
        		// The following commands requires player
        		if (!(sender instanceof Player)){
        	    	Utils.sendMessages(sender, Config.getLang("command-usage"));
        			return false;
        		}
        		Player player = (Player)sender;
    			switch (args[0]){
    			case "1":
    			case "2":
    			case "3":
    			case "4":
    				if (player.hasPermission("lockettepro.edit")){
    					if (args.length == 1){
    				    	Utils.sendMessages(player, Config.getLang("command-usage"));
    					} else {
    		    			Block block = Utils.getSelectedSign(player);
    		    			if (block == null){
    		    				Utils.sendMessages(player, Config.getLang("no-sign-selected"));
    		    			} else if (!LocketteProAPI.isSign(block) || !(player.hasPermission("lockettepro.edit.admin") || LocketteProAPI.isOwnerOfSign(block, player))){
    		    				Utils.sendMessages(player, Config.getLang("sign-need-reselect"));
    		    			} else {
    		    				String message = "";
        		    			for (int i = 1; i < args.length; i++){
        		    				message += args[i];
        		    			}
        		    			message = ChatColor.translateAlternateColorCodes('&', message);
        		    			if (message.length() > 16) {
        		    				Utils.sendMessages(player, Config.getLang("line-is-too-long"));
        		    				return true;
        		    			}
        		    			if (LocketteProAPI.isLockSign(block)){
        		    				switch (args[0]){
        		        			case "1":
        		        				Utils.sendMessages(player, Config.getLang("cannot-change-this-line"));
        		        				break;
        		        			case "2":
        		        				if (!player.hasPermission("lockettepro.admin.edit")){
        		        					Utils.sendMessages(player, Config.getLang("cannot-change-this-line"));
            		        				break;
        		        				}
        		        			case "3":
        		        			case "4":
        		        				Utils.setSignLine(block, Integer.parseInt(args[0])-1, message);
        		        				Utils.sendMessages(player, Config.getLang("sign-changed"));
        		        				if (Config.isUuidEnabled()){
        		        					Utils.updateUuidByUsername(Utils.getSelectedSign(player), Integer.parseInt(args[0])-1);
        		        				}
        		        				break;
        		    				}
        		    			} else if (LocketteProAPI.isAdditionalSign(block)){
        		    				switch (args[0]){
        		        			case "1":
        		        				Utils.sendMessages(player, Config.getLang("cannot-change-this-line"));
        		        				break;
        		        			case "2":
        		        			case "3":
        		        			case "4":
        		        				Utils.setSignLine(block, Integer.parseInt(args[0])-1, message);
        		        				Utils.sendMessages(player, Config.getLang("sign-changed"));
        		        				if (Config.isUuidEnabled()){
        		        					Utils.updateUuidByUsername(Utils.getSelectedSign(player), Integer.parseInt(args[0])-1);
        		        				}
        		        				break;
        		    				}
        		    			} else {
        		    				Utils.sendMessages(player, Config.getLang("sign-need-reselect"));
        		    			}
    		    			}
    					}
    				} else {
    					Utils.sendMessages(player, Config.getLang("no-permission"));
    				}
    				break;
    			case "force":
    				if (debug && player.hasPermission("lockettepro.debug")){
        				Utils.setSignLine(Utils.getSelectedSign(player), Integer.parseInt(args[1]), args[2]);
    					break;
    				}
    			case "update":
    				if (debug && player.hasPermission("lockettepro.debug")){
    					Utils.updateSign(Utils.getSelectedSign(player));
        				break;
    				}
    			case "uuid":
    				if (debug && player.hasPermission("lockettepro.debug")){
    					Utils.updateUuidOnSign(Utils.getSelectedSign(player));
        				break;
    				}
    			default:
    		    	Utils.sendMessages(player, Config.getLang("command-usage"));
    				break;
    			}
    		}
    	}
    	return true;
    }
	
}
