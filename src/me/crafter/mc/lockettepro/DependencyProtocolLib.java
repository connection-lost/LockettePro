package me.crafter.mc.lockettepro;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;

public class DependencyProtocolLib {

	public static void setUpProtocolLib(Plugin plugin){
		try {
			PacketAdapter.AdapterParameteters params = new PacketAdapter.AdapterParameteters();
            params.plugin(plugin).serverSide().types(new PacketType[] { PacketType.Play.Server.UPDATE_SIGN }).listenerPriority(ListenerPriority.LOW);
            ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolSignPacketListener(params));
		} catch (Exception e) {}
	}
	
	public static void cleanUpProtocolLib(Plugin plugin){
		try {
			if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null){
		    	ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
			}
		} catch (Exception e) {}
	}
	
}
