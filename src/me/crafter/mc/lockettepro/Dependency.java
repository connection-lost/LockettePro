package me.crafter.mc.lockettepro;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Team;

import com.bekvon.bukkit.residence.Residence;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

import net.milkbowl.vault.permission.Permission;

public class Dependency {
	
	protected static WorldGuardPlugin worldguard = null;
	protected static Plugin residence = null;
	protected static Plugin towny = null;
	protected static Plugin factions = null;
	protected static boolean factionsuuid = false;
	protected static Plugin vault = null;
	protected static Permission permission = null;
	protected static Plugin askyblock = null;
	protected static Plugin plotsquared = null;
	protected static PlotAPI plotapi;
	
	public Dependency(Plugin plugin){
		// WorldGuard
		Plugin worldguardplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	    if (worldguardplugin == null || !(worldguardplugin instanceof WorldGuardPlugin)) {
	    	worldguard = null;
	    } else {
	    	worldguard = (WorldGuardPlugin)worldguardplugin;
	    }
	    // Residence
	    residence = plugin.getServer().getPluginManager().getPlugin("Residence");
	    // Towny
	    towny = plugin.getServer().getPluginManager().getPlugin("Towny");
		// Factions
	    factions = plugin.getServer().getPluginManager().getPlugin("Factions");
	    if (factions != null) {
	    	factionsuuid = (extractVersion(factions.getDescription().getVersion()) == 1.6);
	    }
		// Vault
	    vault = plugin.getServer().getPluginManager().getPlugin("Vault");
	    if (vault != null){
	    	RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
	        permission = rsp.getProvider();
	    }
	    // ASkyblock
	    askyblock = plugin.getServer().getPluginManager().getPlugin("ASkyblock");
	    // PlotSquared
	    plotsquared = plugin.getServer().getPluginManager().getPlugin("PlotSquared");
	    if (plotsquared != null){
	    	plotapi = new PlotAPI();
	    }
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isProtectedFrom(Block block, Player player){
		if (worldguard != null){
			if (!worldguard.canBuild(player, block)) return true;
		}
		if (residence != null){
			try {
				if (!Residence.getInstance().getPermsByLoc(block.getLocation()).playerHas(player.getName(), player.getWorld().getName(), "build", true)) return true;
			} catch (Exception ex){
				LockettePro.getPlugin().getLogger().info("Note from author of LockettePro: If you have encountered the error above, this is because the Residence plugin had an API change that requires LockettePro to move on.");
				LockettePro.getPlugin().getLogger().info("Please update your Residence to 4.5+, if you are not able to do it now, use LockettePro 2.6.4- for now.");
				LockettePro.getPlugin().getLogger().info("It is possible, but not currently, to let LockettePro to support all Residence versions. If you think this is indeed necessary with high priority, please leave me a message at Spigot discussion section.");
			}
		}
		if (towny != null){
			try {
				if (TownyUniverse.getDataSource().getWorld(block.getWorld().getName()).isUsingTowny()){
					// In town only residents can
					if (!PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getTypeId(), (byte) 0, ActionType.BUILD)) return true;
					// Wilderness permissions
					if (TownyUniverse.isWilderness(block)){ // It is wilderness here
						if (!player.hasPermission("lockettepro.towny.wilds")) return true;
					}
				}
			} catch (Exception e) {}
		}
		if (factions != null){
			if (factionsuuid){
				try{
					Faction faction = Board.getFactionAt(new FLocation(block));
					if (faction != null && !faction.isNone()){
						FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
						if (fplayer != null && !fplayer.isAdminBypassing()){
							Faction playerfaction = fplayer.getFaction();
							if  (faction != playerfaction){
								return true;
							}
						}
					}
				} catch (Exception e){}
			}else{
				try{
					Faction faction = BoardColl.get().getFactionAt(PS.valueOf(block));
					if (faction != null && !faction.isNone()){
						MPlayer mplayer = MPlayer.get(player);
						if (mplayer != null && !mplayer.isOverriding()){
							Faction playerfaction = mplayer.getFaction();
							if (faction != playerfaction){
								return true;
							}
						}
					}
				} catch (Exception e){}
			}
		}
		if (askyblock != null){
			try {
				Island island = ASkyBlockAPI.getInstance().getIslandAt(block.getLocation());
				if (island != null) {
					for (UUID memberuuid : island.getMembers()){
						if (memberuuid.equals(player.getUniqueId())) return false;
					}
					return true;
				}
			} catch (Exception e){}
		}
		if (plotsquared != null){
			try {
				Plot plot = plotapi.getPlot(block.getLocation());
				if (plot != null){
					for (UUID uuid : plot.getOwners()){
						if (uuid.equals(player.getUniqueId())) return false;
					}
					for (UUID uuid : plot.getMembers()){
						if (uuid.equals(player.getUniqueId())) return false;
					}
					for (UUID uuid : plot.getTrusted()){
						if (uuid.equals(player.getUniqueId())) return false;
					}
					return true;
				}
			} catch (Exception e){}
		}
		return false;
	}
	
	public static boolean isTownyTownOrNationOf(String line, Player player){
		if (towny != null){
			String name = player.getName();
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(name);
				Town town = resident.getTown();
				if (line.equals("[" + town.getName() + "]")) return true;
				Nation nation = town.getNation();
				if (line.equals("[" + nation.getName() + "]")) return true;
			} catch (Exception e) {}
		}
		if (factions != null){
			try {
				MPlayer mplayer = MPlayer.get(player);
				if (mplayer != null){
					Faction faction = mplayer.getFaction();
					if (faction != null){
						if (line.equals("[" + faction.getName() + "]")) return true;
					}
				}
			} catch (Exception e) {}
		}
		return false;
	}
	
	public static boolean isPermissionGroupOf(String line, Player player){
		if (vault != null){
			try {
				String[] groups = permission.getPlayerGroups(player);
				for (String group : groups){
					if (line.equals("[" + group + "]")) return true;
				}
			} catch (Exception e){}
		}
		return false;
	}
	
	public static boolean isScoreboardTeamOf(String line, Player player){
		Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
		if (team != null){
			if (line.equals("[" + team.getName() + "]")) return true;
		}
		return false;
	}
	
	public static double extractVersion(String version){
		version = version.replaceAll("[^0-9//.]", "");
		String[] split = version.split("\\.");
		if (split.length > 1) version = split[0] + "." + split[1];
		return Double.parseDouble(version);
	}
}
