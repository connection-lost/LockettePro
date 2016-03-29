package me.crafter.mc.lockettepro;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bekvon.bukkit.residence.Residence;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Dependency {
	
	protected static WorldGuardPlugin worldguard = null;
	protected static Plugin residence = null;
	protected static Plugin towny = null;
	
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
	    // Towny
	    Plugin townyplugin = plugin.getServer().getPluginManager().getPlugin("Towny");
	    if (townyplugin == null){
	    	towny = null;
	    } else {
	    	towny = townyplugin;
	    }
	}
	
	public static boolean isProtectedFrom(Block block, Player player){
		if (worldguard != null){
			if (!worldguard.canBuild(player, block)) return true;
		}
		if (residence != null){
			if (!Residence.getPermsByLoc(block.getLocation()).playerHas(player.getName(), player.getWorld().getName(), "build", true)) return true;
		}
		if (towny != null){
			try {
				if (TownyUniverse.getDataSource().getWorld(block.getWorld().getName()).isUsingTowny()){
					// In town only residents can
					TownBlock townblock = TownyUniverse.getTownBlock(block.getLocation());
					if (townblock != null && townblock.hasTown()){ // There is a town there
						if (!townblock.getTown().hasResident(player.getName())) return true;
					}
					// Wilderness permissions
					if (TownyUniverse.isWilderness(block)){ // It is wilderness here
						if (!player.hasPermission("lockettepro.towny.wilds")) return true;
					}
				}
			} catch (Exception e) {}
		}
		return false;
	}
	
	public static boolean isTownyTownOrNationOf(String line, String name){
		if (towny != null){
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(name);
				Town town = resident.getTown();
				if (line.equals("[" + town.getName() + "]")) return true;
				Nation nation = town.getNation();
				if (line.equals("[" + nation.getName() + "]")) return true;
			} catch (Exception e) {}
		}
		return false;
	}

}
