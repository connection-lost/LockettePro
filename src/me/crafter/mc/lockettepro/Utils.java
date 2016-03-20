package me.crafter.mc.lockettepro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Utils {
	
	private static Map<Player, Block> selectedsign = new HashMap<Player, Block>();
	private static Set<Player> notified = new HashSet<Player>();
	
	// Helper functions
	@SuppressWarnings("deprecation")
	public static void putSignOn(Block block, BlockFace blockface, String line1, String line2){
		Block newsign = block.getRelative(blockface);
		newsign.setType(Material.WALL_SIGN);
		byte data = 0;
		switch (blockface){
		case NORTH:
			data = 2;
			break;
		case EAST:
			data = 5;
			break;
		case WEST:
			data = 4;
			break;
		case SOUTH:
			data = 3;
			break;
		default:
			return;
		}
		newsign.setData(data, true);
		Sign signstate = (Sign)newsign.getState();
		signstate.setLine(0, line1);
		signstate.setLine(1, line2);
		signstate.update();
	}
	
	public static void setSignLine(Block block, int line, String message){ // Requires isSign
		Sign signstate = (Sign)block.getState();
		signstate.setLine(line-1, message);
		signstate.update();
	}
	
	public static void removeASign(Player player){
		if (player.getGameMode() == GameMode.CREATIVE) return;
		if (player.getItemInHand().getAmount() == 1){
			player.setItemInHand(null);
		} else {
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		}
	}
	
	public static Block getSelectedSign(Player player){
		return selectedsign.get(player);
	}
	
	public static void selectSign(Player player, Block block){
		selectedsign.put(player, block);
	}
	
	public static void playLockEffect(Player player, Block block){
//		player.playSound(block.getLocation(), Sound.DOOR_CLOSE, 0.3F, 1.4F);
//		player.spigot().playEffect(block.getLocation().add(0.5, 0.5, 0.5), Effect.CRIT, 0, 0, 0.3F, 0.3F, 0.3F, 0.1F, 64, 64);
	}
	
	public static void playAccessDenyEffect(Player player, Block block){
//		player.playSound(block.getLocation(), Sound.VILLAGER_NO, 0.3F, 0.9F);
//		player.spigot().playEffect(block.getLocation().add(0.5, 0.5, 0.5), Effect.FLAME, 0, 0, 0.3F, 0.3F, 0.3F, 0.01F, 64, 64);
	}
	
	public static void sendMessages(Player player, String messages){
		if (messages == null || messages.equals("")) return;
		player.sendMessage(messages);
	}

	public static boolean shouldNotify(Player player){
		if (notified.contains(player)){
			return false;
		} else {
			notified.add(player);
			return true;
		}
	}
	
}
