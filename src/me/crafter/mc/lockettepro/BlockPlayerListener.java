package me.crafter.mc.lockettepro;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Openable;

public class BlockPlayerListener implements Listener {

	// Quick protect for chests
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerQuickLockChest(PlayerInteractEvent event){
		// Check quick lock enabled
		if (Config.getQuickProtectAction() == (byte)0) return;
		// Check world enabled
		if (Config.isDisabledWorld(event.getPlayer().getWorld().getName())) return;
		// Get player and action info
		Action action = event.getAction();
		Player player = event.getPlayer();
		// Check action correctness
		if (action == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.SIGN){
			// Check quick lock action correctness
			if (!((event.getPlayer().isSneaking() && Config.getQuickProtectAction() == (byte)2) ||
					(!event.getPlayer().isSneaking() && Config.getQuickProtectAction() == (byte)1))) return;
			// Check permission 
			if (!player.hasPermission("lockettepro.lock")) return;
			// Get target block to lock
			BlockFace blockface = event.getBlockFace();
			if (blockface == BlockFace.NORTH || blockface == BlockFace.WEST || blockface == BlockFace.EAST || blockface == BlockFace.SOUTH){
				Block block = event.getClickedBlock();
				// Check permission with external plugin
				if (Dependency.isProtectedFrom(block, player)) return; // blockwise
				if (Dependency.isProtectedFrom(block.getRelative(event.getBlockFace()), player)) return; // signwise
				// Check whether locking location is obstructed
				if (block.getRelative(blockface).getType() != Material.AIR) return;
				// Check whether this block is lockable
				if (LocketteProAPI.isLockable(block)){
					// Is this block already locked?
					boolean locked = LocketteProAPI.isLocked(block);
					// Cancel event here
					event.setCancelled(true);
					// Check lock info info
					if (!locked && !LocketteProAPI.isUpDownLockedDoor(block)){
						// Not locked, not a locked door nearby
						Utils.removeASign(player);
						// Send message
						Utils.sendMessages(player, Config.getLang("locked-quick"));
						// Put sign on
						Block newsign = Utils.putSignOn(block, blockface, Config.getDefaultPrivateString(), player.getName());
						Utils.resetCache(block);
						// Cleanups - UUID
						if (Config.isUuidEnabled()){
        					Utils.updateLineByPlayer(newsign, 1, player);
        				}
						// Cleanups - Expiracy
						if (Config.isLockExpire()){
							if (player.hasPermission("lockettepro.noexpire")){
								Utils.updateLineWithTime(newsign, true); // set created to -1 (no expire)
							} else {
								Utils.updateLineWithTime(newsign, false); // set created to now
							}
						}
					} else if (!locked && LocketteProAPI.isOwnerUpDownLockedDoor(block, player)){
						// Not locked, (is locked door nearby), is owner of locked door nearby
						Utils.removeASign(player);
						Utils.sendMessages(player, Config.getLang("additional-sign-added-quick"));
						Utils.putSignOn(block, blockface, Config.getDefaultAdditionalString(), "");
					} else if (LocketteProAPI.isOwner(block, player)){
						// Locked, (not locked door nearby), is owner of locked block
						Utils.removeASign(player);
						Utils.putSignOn(block, blockface, Config.getDefaultAdditionalString(), "");
						Utils.sendMessages(player, Config.getLang("additional-sign-added-quick"));
					} else {
						// Cannot lock this block
						Utils.sendMessages(player, Config.getLang("cannot-lock-quick"));
					}
				}
			}
		}
	}
	
	// Manual protection
	@EventHandler(priority = EventPriority.NORMAL)
	public void onManualLock(SignChangeEvent event){
		if (event.getBlock().getType() != Material.WALL_SIGN) return;
		String topline = event.getLine(0);
		Player player = event.getPlayer();
		/*  Issue #46 - Old version of Minecraft trim signs in unexpected way.
		 *  This is caused by Minecraft was doing: (unconfirmed but seemingly)
		 *  Place Sign -> Event Fire -> Trim Sign
		 *  The event.getLine() will be inaccurate if the line has white space to trim
		 * 
		 *  This will cause player without permission will be able to lock chests by
		 *  adding a white space after the [private] word.
		 *  Currently this is fixed by using trimmed line in checking permission. Trimmed
		 *  line should not be used anywhere else.  
		 */
		if (!player.hasPermission("lockettepro.lock")){
			String toplinetrimmed = topline.trim();
			if (LocketteProAPI.isLockString(toplinetrimmed) || LocketteProAPI.isAdditionalString(toplinetrimmed)){
				event.setLine(0, Config.getLang("sign-error"));
				Utils.sendMessages(player, Config.getLang("cannot-lock-manual"));
				return;
			}
		}
		if (LocketteProAPI.isLockString(topline) || LocketteProAPI.isAdditionalString(topline)){
			Block block = LocketteProAPI.getAttachedBlock(event.getBlock());
			if (LocketteProAPI.isLockable(block)){
				if (Dependency.isProtectedFrom(block, player)){ // External check here
					event.setLine(0, Config.getLang("sign-error"));
					Utils.sendMessages(player, Config.getLang("cannot-lock-manual"));
					return; 
				}
				boolean locked = LocketteProAPI.isLocked(block);
				if (!locked && !LocketteProAPI.isUpDownLockedDoor(block)){
					if (LocketteProAPI.isLockString(topline)){
						Utils.sendMessages(player, Config.getLang("locked-manual"));
						if (!player.hasPermission("lockettepro.lockothers")){ // Player with permission can lock with another name
							event.setLine(1, player.getName());
						}
						Utils.resetCache(block);
					} else {
						Utils.sendMessages(player, Config.getLang("not-locked-yet-manual"));
						event.setLine(0, Config.getLang("sign-error"));
					}
				} else if (!locked && LocketteProAPI.isOwnerUpDownLockedDoor(block, player)){
					if (LocketteProAPI.isLockString(topline)){
						Utils.sendMessages(player, Config.getLang("cannot-lock-door-nearby-manual"));
						event.setLine(0, Config.getLang("sign-error"));
					} else {
						Utils.sendMessages(player, Config.getLang("additional-sign-added-manual"));
					}
				} else if (LocketteProAPI.isOwner(block, player)){
					if (LocketteProAPI.isLockString(topline)){
						Utils.sendMessages(player, Config.getLang("block-already-locked-manual"));
						event.setLine(0, Config.getLang("sign-error"));
					} else {
						Utils.sendMessages(player, Config.getLang("additional-sign-added-manual"));
					}
				} else { // Not possible to fall here except override
					Utils.sendMessages(player, Config.getLang("block-already-locked-manual"));
					event.getBlock().breakNaturally();
					Utils.playAccessDenyEffect(player, block);
				}
			} else {
				Utils.sendMessages(player, Config.getLang("block-is-not-lockable"));
				event.setLine(0, Config.getLang("sign-error"));
				Utils.playAccessDenyEffect(player, block);
			}
		}
	}
	
	// Player select sign
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void playerSelectSign(PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WALL_SIGN){
			Block block = event.getClickedBlock();
			Player player = event.getPlayer();
			if (!player.hasPermission("lockettepro.edit")) return;
			if (LocketteProAPI.isOwnerOfSign(block, player) || (LocketteProAPI.isLockSignOrAdditionalSign(block) && player.hasPermission("lockettepro.admin.edit"))){
				Utils.selectSign(player, block);
				Utils.sendMessages(player, Config.getLang("sign-selected"));
				Utils.playLockEffect(player, block);
			}
		}
	}
	
	// Player break sign
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttemptBreakSign(BlockBreakEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (player.hasPermission("lockettepro.admin.break")) return;
		if (LocketteProAPI.isLockSign(block)){
			if (LocketteProAPI.isOwnerOfSign(block, player)){
				Utils.sendMessages(player, Config.getLang("break-own-lock-sign"));
				Utils.resetCache(LocketteProAPI.getAttachedBlock(block));
				// Remove additional signs?
			} else {
				Utils.sendMessages(player, Config.getLang("cannot-break-this-lock-sign"));
				event.setCancelled(true);
				Utils.playAccessDenyEffect(player, block);
			}
		} else if (LocketteProAPI.isAdditionalSign(block)){
			// TODO the next line is spaghetti
			if (!LocketteProAPI.isLocked(LocketteProAPI.getAttachedBlock(block))){
				// phew, the locked block is expired!
				// nothing
			} else if (LocketteProAPI.isOwnerOfSign(block, player)){
				Utils.sendMessages(player, Config.getLang("break-own-additional-sign"));
			} else if (!LocketteProAPI.isProtected(LocketteProAPI.getAttachedBlock(block))){
				Utils.sendMessages(player, Config.getLang("break-redundant-additional-sign"));
			} else {
				Utils.sendMessages(player, Config.getLang("cannot-break-this-additional-sign"));
				event.setCancelled(true);
				Utils.playAccessDenyEffect(player, block);
			}
		}
	}
	
	// Protect block from being destroyed
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttemptBreakLockedBlocks(BlockBreakEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (LocketteProAPI.isLocked(block) || LocketteProAPI.isUpDownLockedDoor(block)){
			Utils.sendMessages(player, Config.getLang("block-is-locked"));
			event.setCancelled(true);
			Utils.playAccessDenyEffect(player, block);
		}
	}

	// Protect block from being used & handle double doors
	@EventHandler(priority = EventPriority.HIGH)
	public void onAttemptInteractLockedBlocks(PlayerInteractEvent event){
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		if (LockettePro.needCheckHand()){
			if (event.getHand() != EquipmentSlot.HAND){
				if (action == Action.RIGHT_CLICK_BLOCK){
					if (LocketteProAPI.isChest(block)){
						// something not right
						event.setCancelled(true);
					}
					return;
				}
			}
		}
		switch (action){
		case LEFT_CLICK_BLOCK:
		case RIGHT_CLICK_BLOCK:
			Player player = event.getPlayer();
			if (((LocketteProAPI.isLocked(block) && !LocketteProAPI.isUser(block, player) && !LocketteProAPI.isExhibit(block) ) || 
					(LocketteProAPI.isUpDownLockedDoor(block) && !LocketteProAPI.isUserUpDownLockedDoor(block, player)))
					&& !player.hasPermission("lockettepro.admin.use")){
				Utils.sendMessages(player, Config.getLang("block-is-locked"));
				event.setCancelled(true);
				Utils.playAccessDenyEffect(player, block);
			} else { // Handle double doors
				if (action == Action.RIGHT_CLICK_BLOCK){
					if(LocketteProAPI.isLocked(block)) {
						if (LocketteProAPI.isDoubleDoorBlock(block) || LocketteProAPI.isSingleDoorBlock(block)){
						Block doorblock = LocketteProAPI.getBottomDoorBlock(block);
						BlockState doorstate = doorblock.getState();
						Openable openablestate = (Openable)doorstate.getData();
						boolean shouldopen = !openablestate.isOpen(); // Move to here
						int closetime = LocketteProAPI.getTimerDoor(doorblock);
						List<Block> doors = new ArrayList<Block>();
						doors.add(doorblock);
						if (doorblock.getType() == Material.IRON_DOOR_BLOCK || doorblock.getType() == Material.IRON_TRAPDOOR){
							LocketteProAPI.toggleDoor(doorblock, shouldopen);
						}
						for (BlockFace blockface : LocketteProAPI.newsfaces){
							Block relative = doorblock.getRelative(blockface);
							if (relative.getType() == doorblock.getType()){
								doors.add(relative);
								LocketteProAPI.toggleDoor(relative, shouldopen);
							}
						}
						if (closetime > 0){
							Bukkit.getScheduler().runTaskLater(LockettePro.getPlugin(), new DoorToggleTask(doors), closetime*20);
						}
						} else if (
								LocketteProAPI.isExhibit(block) && 
								!LocketteProAPI.isUser(block, player) && 
								!player.hasPermission("lockettepro.admin.use")
								) {
							if (block.getState() instanceof Chest && ((Chest)block.getState()).getInventory() instanceof DoubleChestInventory) {
								Utils.getProtectedInventoryHolder().put(player, ((DoubleChestInventory)((Chest)block.getState()).getInventory()).getLeftSide().getHolder());
								//player.sendMessage(("§bProtect DInventoryHolder: " + getCode(((DoubleChestInventory)((Chest)block.getState()).getInventory()).getLeftSide().getHolder().toString())));
							} else if (block.getState() instanceof InventoryHolder) {
								Utils.getProtectedInventoryHolder().put(player, ((InventoryHolder)block.getState()));
								//player.sendMessage("§bProtect InventoryHolder: " + getCode(((InventoryHolder)block.getState()).toString()));
							}
						}
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	/*
	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if(
				event.getPlayer() != null && event.getPlayer() instanceof Player && 
				Utils.getProtectedInventoryHolder().containsKey((Player)event.getPlayer()) && 
				event.getInventory() != null) {
			Player player = (Player)event.getPlayer();
			if(event.getInventory() instanceof DoubleChestInventory && Utils.getProtectedInventoryHolder().get((Player)event.getPlayer()).equals(((DoubleChestInventory)event.getInventory()).getLeftSide().getHolder())) {
				player.sendMessage("§aProtected DoubleChestInventory Open: " + getCode(((DoubleChestInventory)event.getInventory()).getLeftSide().getHolder().toString()));
			} else if (Utils.getProtectedInventoryHolder().get((Player)event.getPlayer()).equals(event.getInventory().getHolder())) {
				player.sendMessage("§aProtected Inventory Open: " + getCode(event.getInventory().getHolder().toString()));
			}
		}
	}
	*/
	
	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if(
				event.getWhoClicked() != null && event.getWhoClicked() instanceof Player && 
				Utils.getProtectedInventoryHolder().containsKey((Player)event.getWhoClicked()) && 
				event.getInventory() != null) {
			event.setCancelled(true);
			/*
			Player player = (Player)event.getWhoClicked();
			if(event.getInventory() instanceof DoubleChestInventory && Utils.getProtectedInventoryHolder().get((Player)event.getWhoClicked()).equals(((DoubleChestInventory)event.getInventory()).getLeftSide().getHolder())) {
				player.sendMessage("§dProtected DoubleChestInventory Click: " + getCode(((DoubleChestInventory)event.getInventory()).getLeftSide().getHolder().toString()));
			} else if (Utils.getProtectedInventoryHolder().get((Player)event.getWhoClicked()).equals(event.getInventory().getHolder())) {
				player.sendMessage("§dProtected Inventory Click: " + getCode(event.getInventory().getHolder().toString()));
			}
			*/
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		if(
				event.getPlayer() != null && event.getPlayer() instanceof Player && 
				Utils.getProtectedInventoryHolder().containsKey((Player)event.getPlayer()) && 
				event.getInventory() != null) {
			/*
			Player player = (Player)event.getPlayer();
			if(event.getInventory() instanceof DoubleChestInventory && Utils.getProtectedInventoryHolder().get((Player)event.getPlayer()).equals(((DoubleChestInventory)event.getInventory()).getLeftSide().getHolder())) {
				player.sendMessage("§cProtected DInventory Close: " + getCode(((DoubleChestInventory)event.getInventory()).getLeftSide().getHolder().toString()));
			} else if (Utils.getProtectedInventoryHolder().get((Player)event.getPlayer()).equals(event.getInventory().getHolder())) {
				player.sendMessage("§cProtected Inventory Close: " + getCode(event.getInventory().getHolder().toString()));
			}
			*/
			Utils.getProtectedInventoryHolder().remove((Player)event.getPlayer());
		}
	}
	
	/*
	private boolean isProtectedInventoryHolder(HumanEntity humanEntity, Inventory inventory) {
		return 
				humanEntity != null && 
				humanEntity instanceof Player && 
				Utils.getProtectedInventoryHolder().containsKey((Player)humanEntity) && 
				inventory != null &&
				(
					(inventory instanceof DoubleChestInventory && Utils.getProtectedInventoryHolder().get((Player)humanEntity).equals(((DoubleChestInventory)inventory).getLeftSide().getHolder())) ||
					(Utils.getProtectedInventoryHolder().get((Player)humanEntity).equals(inventory.getHolder()))
				);
	}
	
	private String getCode(String text) {
		return text.substring(text.lastIndexOf("@") + 1);
	}
	*/
	
	// Protect block from interfere block
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttemptPlaceInterfereBlocks(BlockPlaceEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (player.hasPermission("lockettepro.admin.interfere")) return;
		if (LocketteProAPI.mayInterfere(block, player)){
			Utils.sendMessages(player, Config.getLang("cannot-interfere-with-others"));
			event.setCancelled(true);
			Utils.playAccessDenyEffect(player, block);		
		}
	}
	
	// Tell player about lockettepro
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlaceFirstBlockNotify(BlockPlaceEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (!player.hasPermission("lockettepro.lock")) return;
		if (Utils.shouldNotify(player) && Config.isLockable(block.getType())){
			switch (Config.getQuickProtectAction()){
			case (byte)0:
				Utils.sendMessages(player, Config.getLang("you-can-manual-lock-it"));	
				break;
			case (byte)1:
			case (byte)2:
				Utils.sendMessages(player, Config.getLang("you-can-quick-lock-it"));	
				break;
			}
		}
	}

}
