package me.crafter.mc.lockettepro;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ReflectionBaseComponent {

	public static List<Object> signToBaseComponents(Block block){ // Requires sign
		World world = block.getWorld();
		Location location = block.getLocation();
		int[] locations = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		String bpName = Bukkit.getServer().getClass().getPackage().getName();
		String version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());
		try {
			Class<?> CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
			Object CraftWorld = CraftWorldClass.cast(world);
			
			Method getTileEntityAt = CraftWorld.getClass().getMethod("getTileEntityAt", Integer.TYPE, Integer.TYPE, Integer.TYPE);
			Object tileentitysign = getTileEntityAt.invoke(CraftWorld, locations[0], locations[1], locations[2]);
			
			Field lines = tileentitysign.getClass().getDeclaredField("lines");

			Object[] ichatbasecomponent = (Object[]) lines.get(tileentitysign);
			
			List<Object> basecomponents = new ArrayList<Object>();
			
			for (Object line : ichatbasecomponent){
				basecomponents.add(line);
			}
			return basecomponents;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String baseComponentToText(Object chatbasecomponent){
		try {
			Method getText = chatbasecomponent.getClass().getMethod("getText", new Class[0]);
			Object text = getText.invoke(chatbasecomponent, new Object[0]);
			return text.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String baseComponentToClickable(Object chatbasecomponent){
		try {
			Method getChatModifier = chatbasecomponent.getClass().getMethod("getChatModifier", new Class[0]);
			Object chatmodifier = getChatModifier.invoke(chatbasecomponent, new Object[0]);
			
			Method h = chatmodifier.getClass().getMethod("h", new Class[0]);
			Object chatclickable = h.invoke(chatmodifier, new Object[0]);
			if (chatclickable == null) return "";

			Method b = chatclickable.getClass().getMethod("b", new Class[0]);
			Object text = b.invoke(chatclickable, new Object[0]);
			
			return text.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String baseComponentToHoverable(Object chatbasecomponent){
		try {
			Method getChatModifier = chatbasecomponent.getClass().getMethod("getChatModifier", new Class[0]);
			Object chatmodifier = getChatModifier.invoke(chatbasecomponent, new Object[0]);
			
			Method i = chatmodifier.getClass().getMethod("i", new Class[0]);
			Object chathoverable = i.invoke(chatmodifier, new Object[0]);
			if (chathoverable == null) return "";
			
			Method b = chathoverable.getClass().getMethod("b", new Class[0]);
			Object text = b.invoke(chathoverable, new Object[0]);
			if (text == null) return "";
			
			Method c = text.getClass().getMethod("c", new Class[0]);
			Object result = c.invoke(text, new Object[0]);
			if (result == null) return "";

			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void setLines(Block block, List<String[]> text){
		World world = block.getWorld();
		Location location = block.getLocation();
		int[] locations = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		String bpName = Bukkit.getServer().getClass().getPackage().getName();
		String version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());
		try {
			Class<?> CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
			Object CraftWorld = CraftWorldClass.cast(world);
			
			Method getTileEntityAt = CraftWorld.getClass().getMethod("getTileEntityAt", Integer.TYPE, Integer.TYPE, Integer.TYPE);
			Object tileentitysign = getTileEntityAt.invoke(CraftWorld, locations[0], locations[1], locations[2]);
			
			Field lines = tileentitysign.getClass().getDeclaredField("lines");

			Object[] ichatbasecomponent = (Object[]) lines.get(tileentitysign);
			
			for (int l = 0; l < 4; l++){
				Class<?> ChatSerializerClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
				Method a = ChatSerializerClass.getDeclaredMethod("a", String.class);
				Object singleichatbasecomponent = a.invoke(ChatSerializerClass, "{\"text\":\"" + text.get(l)[0] + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + text.get(l)[2] + "\"}]}}}");
				ichatbasecomponent[l] = singleichatbasecomponent;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		((Sign)block.getState()).update();
	}
	
	public static void setLine(Block block, int line, String text, String click, String hover){
		World world = block.getWorld();
		Location location = block.getLocation();
		int[] locations = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		String bpName = Bukkit.getServer().getClass().getPackage().getName();
		String version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());
		try {
			Class<?> CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
			Object CraftWorld = CraftWorldClass.cast(world);
			
			Method getTileEntityAt = CraftWorld.getClass().getMethod("getTileEntityAt", Integer.TYPE, Integer.TYPE, Integer.TYPE);
			Object tileentitysign = getTileEntityAt.invoke(CraftWorld, locations[0], locations[1], locations[2]);
			
			Field lines = tileentitysign.getClass().getDeclaredField("lines");

			Object[] ichatbasecomponent = (Object[]) lines.get(tileentitysign);
			
			Class<?> ChatSerializerClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
			Method a = ChatSerializerClass.getDeclaredMethod("a", String.class);
			text = text.replace("\\n", "\n");
			Object singleichatbasecomponent = a.invoke(ChatSerializerClass, "{\"text\":\"" + text + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + hover + "\"}]}}}");
			ichatbasecomponent[line] = singleichatbasecomponent;
			//((Sign)block.getState()).setLine(line, text);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void setLine(Block block, int line, String text){
		World world = block.getWorld();
		Location location = block.getLocation();
		int[] locations = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		String bpName = Bukkit.getServer().getClass().getPackage().getName();
		String version = bpName.substring(bpName.lastIndexOf(".") + 1, bpName.length());
		try {
			Class<?> CraftWorldClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
			Object CraftWorld = CraftWorldClass.cast(world);
			
			Method getTileEntityAt = CraftWorld.getClass().getMethod("getTileEntityAt", Integer.TYPE, Integer.TYPE, Integer.TYPE);
			Object tileentitysign = getTileEntityAt.invoke(CraftWorld, locations[0], locations[1], locations[2]);
			
			Field lines = tileentitysign.getClass().getDeclaredField("lines");

			Object[] ichatbasecomponent = (Object[]) lines.get(tileentitysign);
			
			Class<?> ChatSerializerClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
			Method a = ChatSerializerClass.getDeclaredMethod("a", String.class);
			text = text.replace("\\n", "\n");
			Object singleichatbasecomponent = a.invoke(ChatSerializerClass, "{\"text\":\"" + text + "\"}");
			ichatbasecomponent[line] = singleichatbasecomponent;
			//((Sign)block.getState()).setLine(line, text);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	// IChatBaseComponent comp = ChatSerializer.a("{tex
	// ChatBaseComponent c.getChatModifier().h().b()
	
}
