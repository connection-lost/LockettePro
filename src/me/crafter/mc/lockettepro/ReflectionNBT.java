package me.crafter.mc.lockettepro;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ReflectionNBT {
	
	public static String bpName;
	public static String version;
	public static Class <?> EntityPlayerClass, CraftWorldClass, NBTTagCompoundClass, CraftPlayerClass, packetClass, PlayerConnectionClass;
	
	public ReflectionNBT(){
		String bpName = Bukkit.getServer().getClass().getPackage().getName();
		String version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());
		try {
			EntityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
			CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
			NBTTagCompoundClass = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			CraftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
			packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
			PlayerConnectionClass = Class.forName("net.minecraft.server." + version + ".PlayerConnection");
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	


	public static String[] getSignLinesFull(Block block){
		World world = block.getWorld();
		Location location = block.getLocation();
		int[] locations = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		try {
			Object CraftWorld = CraftWorldClass.cast(world);
			
			Method getTileEntityAt = CraftWorld.getClass().getMethod("getTileEntityAt", Integer.TYPE, Integer.TYPE, Integer.TYPE);
			Object tileentitysign = getTileEntityAt.invoke(CraftWorld, locations[0], locations[1], locations[2]);
			
			Object nbttagcompound = NBTTagCompoundClass.newInstance();
			
			Method b = tileentitysign.getClass().getMethod("b", NBTTagCompoundClass);
			b.invoke(tileentitysign, nbttagcompound);
			
			String[] lines = new String[4];
			
			Method getString = nbttagcompound.getClass().getMethod("getString", String.class);
			lines[0] = getString.invoke(nbttagcompound, "Text1").toString();
			lines[1] = getString.invoke(nbttagcompound, "Text2").toString();
			lines[2] = getString.invoke(nbttagcompound, "Text3").toString();
			lines[3] = getString.invoke(nbttagcompound, "Text4").toString();
			
			return lines;
		} catch (Exception e){
			e.printStackTrace();
			String[] lines = {"", "", "", ""};
			return lines;
		}
	}
	
	public static String getSignLineFull(Block block, int line){
		return getSignLinesFull(block)[line];
	}
	
	public static void setSignLineFull(Block block, int line, String text){
		World world = block.getWorld();
		Location location = block.getLocation();
		int[] locations = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		try {
			Object CraftWorld = CraftWorldClass.cast(world);
			
			Method getTileEntityAt = CraftWorld.getClass().getMethod("getTileEntityAt", Integer.TYPE, Integer.TYPE, Integer.TYPE);
			Object tileentitysign = getTileEntityAt.invoke(CraftWorld, locations[0], locations[1], locations[2]);
			
			Object nbttagcompound = NBTTagCompoundClass.newInstance();
			
			Method b = tileentitysign.getClass().getMethod("b", NBTTagCompoundClass);
			b.invoke(tileentitysign, nbttagcompound);
			
			Method setString = nbttagcompound.getClass().getMethod("setString", String.class, String.class);
			setString.invoke(nbttagcompound, "Text" + (line+1), text);
			
			Method a = tileentitysign.getClass().getMethod("a", nbttagcompound.getClass());
			a.invoke(tileentitysign, nbttagcompound);
			
			int distancesquared = Bukkit.getViewDistance() * Bukkit.getViewDistance() * 1024;
			for (Player player : Bukkit.getOnlinePlayers()){
				if (player.getLocation().distanceSquared(block.getLocation()) < distancesquared){
					Object CraftPlayer = CraftPlayerClass.cast(player);
					
					Method getHandle = CraftPlayerClass.getMethod("getHandle", new Class[0]);
					Object EntityPlayer = getHandle.invoke(CraftPlayer, new Object[0]);
					
					Field playerConnectionField = EntityPlayer.getClass().getDeclaredField("playerConnection");
					Object playerConnection = playerConnectionField.get(EntityPlayer);
					
					Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
					
					Method getUpdatePacket = tileentitysign.getClass().getDeclaredMethod("getUpdatePacket", new Class[0]);
					Object Packet = getUpdatePacket.invoke(tileentitysign, new Object[0]);
					
					sendPacket.invoke(playerConnection, Packet);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//{\"extra\":[{\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"extra\":[\"hoverevent\"],\"text\":\"\"}},\"text\":\"line1\"}],\"text\":\"\"}
	
	public static void setSignLinesFull(Block block, List<String> lines){
		for (int l = 0; l < 4; l ++){
			setSignLineFull(block, l, lines.get(l));
		}
	}
	
	public static void setSignLine(Block block, int line, String text, String hover){
		setSignLineFull(block, line, "{\"extra\":[{\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"extra\":[\"" + hover + "\"],\"text\":\"\"}},\"text\":\"" + text + "\"}],\"text\":\"\"}");
	}
	
}
