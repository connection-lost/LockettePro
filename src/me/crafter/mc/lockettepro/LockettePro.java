package me.crafter.mc.lockettepro;

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

	public void onEnable(){
		// Read config
		new Config(this);
		// Register Listeners
    	//getServer().getPluginManager().registerEvents(new BlockDebugListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockPlayerListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockEnvironmentListener(), this);
    	getServer().getPluginManager().registerEvents(new BlockInventoryMoveListener(), this);
    	// Other
    	plugin = this;
    	// Metrics
    	try {
    		Metrics metrics = new Metrics(this);
	        metrics.start();
    	} catch (Exception ex){}
    }
	
	
    public void onDisable(){
    }
    
    public static Plugin getPlugin(){
    	return plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, final String[] args){
    	if (cmd.getName().equals("lockettepro")){
    		if (!(sender instanceof Player)) return false;
    		Player player = (Player)sender;
    		if (args.length == 0){
    	    	Utils.sendMessages(player, Config.getLang("command-usage"));
    		} else {
    			switch (args[0]){
    			case "reload":
    				if (player.hasPermission("lockettepro.reload")){
    					Config.reload();
    					Utils.sendMessages(player, Config.getLang("config-reloaded"));
    				} else {
    					Utils.sendMessages(player, Config.getLang("no-permission"));
    				}
    				break;
    			case "1":
    			case "2":
    			case "3":
    			case "4":
    				if (player.hasPermission("lockettepro.edit")){
    					if (args.length == 1){
    				    	Utils.sendMessages(player, Config.getLang("howtoedit"));
    					} else {
    		    			Block block = Utils.getSelectedSign(player);
    		    			if (block == null){
    		    				Utils.sendMessages(player, Config.getLang("no-sign-selected"));
    		    			} else if (!LocketteProAPI.isSign(block) || !LocketteProAPI.isOwnerOfSign(block, player.getName())){
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
        		        			case "2":
        		        				Utils.sendMessages(player, Config.getLang("cannot-change-this-line"));
        		        				break;
        		        			case "3":
        		        			case "4":
        		        				Utils.setSignLine(block, Integer.parseInt(args[0]), message);
        		        				Utils.sendMessages(player, Config.getLang("sign-changed"));
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
        		        				Utils.setSignLine(block, Integer.parseInt(args[0]), message);
        		        				Utils.sendMessages(player, Config.getLang("sign-changed"));
        		        				break;
        		    				}
        		    			}
    		    			}
    						
    					}
    				} else {
    					Utils.sendMessages(player, Config.getLang("no-permission"));
    				}
    				break;
    			default:
    		    	Utils.sendMessages(player, Config.getLang("command-usage"));
    				break;
    			}
    		}
    	}
    	return true;
    }
	
}
