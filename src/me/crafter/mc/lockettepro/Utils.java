package me.crafter.mc.lockettepro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

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
		updateSign(newsign);
		Sign sign = (Sign)newsign.getState();
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.update();
	}
	
	public static void setSignLine(Block block, int line, String text){ // Requires isSign
		Sign sign = (Sign)block.getState();
		sign.setLine(line, text);
		sign.update();
	}
	
	public static void removeASign(Player player){
		if (player.getGameMode() == GameMode.CREATIVE) return;
		if (player.getItemInHand().getAmount() == 1){
			player.setItemInHand(null);
		} else {
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		}
	}
	
	public static void updateSign(Block block){
		((Sign)block.getState()).update();
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
	
	public static void sendMessages(CommandSender sender, String messages){
		if (messages == null || messages.equals("")) return;
		sender.sendMessage(messages);
	}

	public static boolean shouldNotify(Player player){
		if (notified.contains(player)){
			return false;
		} else {
			notified.add(player);
			return true;
		}
	}
	
	public static boolean hasValidCache(Block block){
		List<MetadataValue> metadatas = block.getMetadata("expires");
		if (!metadatas.isEmpty()){
			long expires = metadatas.get(0).asLong();
			if (expires > System.currentTimeMillis()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean getAccess(Block block){ // Requires hasValidCache()
		List<MetadataValue> metadatas = block.getMetadata("locked");
		return metadatas.get(0).asBoolean();
	}
	
	public static void setCache(Block block, boolean access){
		block.removeMetadata("expires", LockettePro.getPlugin());
		block.removeMetadata("locked", LockettePro.getPlugin());
		block.setMetadata("expires", new FixedMetadataValue(LockettePro.getPlugin(), System.currentTimeMillis() + Config.getCacheTimeMillis()));
		block.setMetadata("locked", new FixedMetadataValue(LockettePro.getPlugin(), access));
	}
	
	public static void resetCache(Block block){
		block.removeMetadata("expires", LockettePro.getPlugin());
		block.removeMetadata("locked", LockettePro.getPlugin());
		for (BlockFace blockface : LocketteProAPI.newsfaces){
			Block relative = block.getRelative(blockface);
			if (relative.getType() == block.getType()){
				relative.removeMetadata("expires", LockettePro.getPlugin());
				relative.removeMetadata("locked", LockettePro.getPlugin());
			}
		}
	}
	
}
