package me.crafter.mc.lockettepro;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class DependencyProtocolLib {

	public static void setUpProtocolLib(Plugin plugin){
		switch (LockettePro.getBukkitVersion()){
		case v1_8_R1:
		case v1_8_R2:
		case v1_8_R3:
			addUpdateSignListener(plugin);
			break;
		case v1_9_R1:
			addUpdateSignListener(plugin);
			addTileEntityDataListener(plugin);
			break;
		case v1_9_R2:
			addTileEntityDataListener(plugin);
			addMapChunkListener(plugin);
			break;
		case v1_10_R1:
			addTileEntityDataListener(plugin);
			addMapChunkListener(plugin);
			break;
		case v1_11_R1:
			addTileEntityDataListener(plugin);
			addMapChunkListener(plugin);
			break;
		case v1_12_R1:
			addTileEntityDataListener(plugin);
			addMapChunkListener(plugin);
			break;
		case UNKNOWN:
		default:
			addUpdateSignListener(plugin);
			addTileEntityDataListener(plugin);
			addMapChunkListener(plugin);
			break;
		}
	}
	
	public static void cleanUpProtocolLib(Plugin plugin){
		try {
			if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null){
		    	ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addUpdateSignListener(Plugin plugin){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOW, PacketType.Play.Server.UPDATE_SIGN) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				WrappedChatComponent[] lines = packet.getChatComponentArrays().read(0);
				String[] liness = new String[4];
				for (int i = 0; i < 4; i++){
					liness[i] = lines[i].getJson();
				}
				SignSendEvent signsendevent = new SignSendEvent(event.getPlayer(), liness);
				Bukkit.getPluginManager().callEvent(signsendevent);
				if (signsendevent.isModified()){
					packet.getChatComponentArrays().write(0, signsendevent.getLinesWrappedChatComponent());
				}
			}
		});
	}
	
	public static void addTileEntityDataListener(Plugin plugin){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOW, PacketType.Play.Server.TILE_ENTITY_DATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				if (packet.getIntegers().read(0) != 9) return;
				NbtCompound nbtcompound = (NbtCompound) packet.getNbtModifier().read(0);
				String[] liness = new String[4];
				for (int i = 0; i < 4; i++){
					liness[i] = nbtcompound.getString("Text" + (i+1));
				}
				SignSendEvent signsendevent = new SignSendEvent(event.getPlayer(), liness);
				Bukkit.getPluginManager().callEvent(signsendevent);
				if (signsendevent.isModified()){
					for (int i = 0; i < 4; i++){
						nbtcompound.put("Text" + (i+1), signsendevent.getLine(i));
					}
				}
			}
		});
	}
	
	public static void addMapChunkListener(Plugin plugin){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOW, PacketType.Play.Server.MAP_CHUNK) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				List<?> tileentitydatas = packet.getSpecificModifier(List.class).read(0);
				for (Object tileentitydata : tileentitydatas) {
					NbtCompound nbtcompound = NbtFactory.fromNMSCompound(tileentitydata);
					if (nbtcompound == null || nbtcompound.getString("id") == null) continue;
					if (!(nbtcompound.getString("id").equals("Sign") || nbtcompound.getString("id").equals("minecraft:sign"))) continue;
					String[] liness = new String[4];
					for (int i = 0; i < 4; i++){
						liness[i] = nbtcompound.getString("Text" + (i+1));
					}
					SignSendEvent signsendevent = new SignSendEvent(event.getPlayer(), liness);
					Bukkit.getPluginManager().callEvent(signsendevent);
					if (signsendevent.isModified()){
						for (int i = 0; i < 4; i++){
							nbtcompound.put("Text" + (i+1), signsendevent.getLine(i));
						}
					}
				}
			}
		});
	}
	
	
	
}
