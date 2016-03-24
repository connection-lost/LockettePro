package me.crafter.mc.lockettepro;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bekvon.bukkit.residence.Residence;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Dependency {
	
	private static WorldGuardPlugin worldguard = null;
	private static Plugin residence = null;
	
	public Dependency(Plugin plugin){
		// WorldGuard
		Plugin worldguardplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	    if (worldguardplugin == null || !(worldguardplugin instanceof WorldGuardPlugin)) {
	    	worldguard = null;
	    } else {
	    	worldguard = (WorldGuardPlugin)worldguardplugin;
	    }
	    // Residence
	    Plugin residenceplugin = plugin.getServer().getPluginManager().getPlugin("Residence");
	    if (residenceplugin == null){
	    	residence = null;
	    } else {
	    	residence = residenceplugin;
	    }
	}
	
	public static boolean isProtectedFrom(Block block, Player player){
		if (worldguard != null){
			if (!worldguard.canBuild(player, block)) return true;
		}
		if (residence != null){
			if (!Residence.getPermsByLoc(block.getLocation()).playerHas(player.getName(), player.getWorld().getName(), "build", true)) return true;
		}
		return false;
	}

}
