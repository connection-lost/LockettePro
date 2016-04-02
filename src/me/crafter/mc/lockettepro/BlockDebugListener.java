package me.crafter.mc.lockettepro;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
			p.sendMessage(" - isOwner/User: " + formatBoolean(LocketteProAPI.isOwner(b, p)) + ChatColor.RESET + "/" + formatBoolean(LocketteProAPI.isUser(b, p)));
			p.sendMessage("isLockedSingle: " + formatBoolean(LocketteProAPI.isLockedSingleBlock(b)));
			p.sendMessage(" - isOwner/UserSingle: " + formatBoolean(LocketteProAPI.isOwnerSingleBlock(b, p)) + ChatColor.RESET + "/" + formatBoolean(LocketteProAPI.isUserSingleBlock(b, p)));
			p.sendMessage("isLockedUpDownLockedDoor: " + formatBoolean(LocketteProAPI.isUpDownLockedDoor(b)));
			p.sendMessage(" - isOwner/UserSingle: " + formatBoolean(LocketteProAPI.isOwnerUpDownLockedDoor(b, p)) + ChatColor.RESET + "/" + formatBoolean(LocketteProAPI.isOwnerUpDownLockedDoor(b, p)));
			
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
			
//			if (b.getType() == Material.WALL_SIGN){
//				for (Object line : Reflection.signToBaseComponents(b)){
//					Bukkit.broadcastMessage(line.toString());
//				}
//			}
			if (b.getType() == Material.WALL_SIGN){
//				List<Object> basecomponents = Reflection.signToBaseComponents(b);
//				p.sendMessage("Text:Clickable:Hoverable");
//				for (Object basecomponent : basecomponents){
//					//p.sendMessage(ChatColor.RED + basecomponent.toString());
//					p.sendMessage(ChatColor.YELLOW + Reflection.baseComponentToText(basecomponent) + ":" + Reflection.baseComponentToClickable(basecomponent) + ":" + Reflection.baseComponentToHoverable(basecomponent));
//				}
//				for (String line : ((Sign)b.getState()).getLines()){
//					p.sendMessage(ChatColor.GREEN + line);
//				}
//				Object basecomponent = basecomponents.get(0);
//				p.sendMessage(ChatColor.RED + basecomponent.toString());
//				p.sendMessage(ChatColor.YELLOW + Reflection.baseComponentToText(basecomponent) + ":" + Reflection.baseComponentToClickable(basecomponent) + ":" + Reflection.baseComponentToHoverable(basecomponent));
//				p.sendMessage(ChatColor.GREEN + ((Sign)b.getState()).getLines()[0]);
				for (String line : ReflectionNBT.getSignLinesFull(b)){
					Bukkit.broadcastMessage(line);
				}
			}
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


