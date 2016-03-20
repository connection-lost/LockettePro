package me.crafter.mc.lockettepro;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockDebugListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDebugClick(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if (p.isSneaking() && event.getAction() == Action.LEFT_CLICK_BLOCK){
			event.setCancelled(true);
			Block b = event.getClickedBlock();
			p.sendMessage(ChatColor.GREEN + "===========================");
			p.sendMessage("isLockable: " + formatBoolean(LocketteProAPI.isLockable(b)));
			p.sendMessage("isLocked: " + formatBoolean(LocketteProAPI.isLocked(b)));
			p.sendMessage(" - isOwner/User: " + formatBoolean(LocketteProAPI.isOwner(b, p.getName())) + ChatColor.RESET + "/" + formatBoolean(LocketteProAPI.isUser(b, p.getName())));
			p.sendMessage("isLockedSingle: " + formatBoolean(LocketteProAPI.isLockedSingleBlock(b)));
			p.sendMessage(" - isOwner/UserSingle: " + formatBoolean(LocketteProAPI.isOwnerSingleBlock(b, p.getName())) + ChatColor.RESET + "/" + formatBoolean(LocketteProAPI.isUserSingleBlock(b, p.getName())));
			p.sendMessage("isLockedUpDownLockedDoor: " + formatBoolean(LocketteProAPI.isUpDownLockedDoor(b)));
			p.sendMessage(" - isOwner/UserSingle: " + formatBoolean(LocketteProAPI.isOwnerUpDownLockedDoor(b, p.getName())) + ChatColor.RESET + "/" + formatBoolean(LocketteProAPI.isOwnerUpDownLockedDoor(b, p.getName())));

			
//			p.sendMessage("isLockSign: " + formatBoolean(LocketteProAPI.isLockSign(b)));
//			if (LocketteProAPI.isLockSign(b)){
//				p.sendMessage(" - isOwnerOnSign: " + formatBoolean(LocketteProAPI.isOwnerOnSign(b, p.getName())));
//			}
//			p.sendMessage("isAdditionalSign: " + formatBoolean(LocketteProAPI.isAdditionalSign(b)));
//			if (LocketteProAPI.isAdditionalSign(b)){
//				p.sendMessage(" - isUserOnSign: " + formatBoolean(LocketteProAPI.isUserOnSign(b, p.getName())));
//			}
//			p.sendMessage("isContainer: " + formatBoolean(LocketteProAPI.isContainer(b)));
			p.sendMessage("BlockData: " + b.getData());
		}	
	}
	
	public String formatBoolean(boolean tf){
		if (tf){
			return ChatColor.GREEN + "true";
		} else {
			return ChatColor.RED + "false";
		}
	}

}


