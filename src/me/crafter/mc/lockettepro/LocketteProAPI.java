package me.crafter.mc.lockettepro;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

public class LocketteProAPI {

	public static BlockFace[] newsfaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	public static BlockFace[] allfaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
	
	public static boolean isLocked(Block block){
		if (block == null) return false;
		switch (block.getType()){
		// Double Doors
		case WOODEN_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR_BLOCK:
			Block[] doors = getDoors(block);
			if (doors == null) return false;
			for (BlockFace doorface : newsfaces){
				Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
				if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
					if (isLockedSingleBlock(relative1.getRelative(BlockFace.UP), doorface.getOppositeFace())) return true;
					if (isLockedSingleBlock(relative1, doorface.getOppositeFace())) return true;
					if (isLockedSingleBlock(relative0, doorface.getOppositeFace())) return true; 
					if (isLockedSingleBlock(relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace())) return true; 
				}
			}
			if (isLockedSingleBlock(doors[1].getRelative(BlockFace.UP), null)) return true;
			if (isLockedSingleBlock(doors[1], null)) return true;
			if (isLockedSingleBlock(doors[0], null)) return true; 
			if (isLockedSingleBlock(doors[0].getRelative(BlockFace.DOWN), null)) return true;
			break;
		// Chests (Second block only)
		case CHEST:
		case TRAPPED_CHEST:
			// Check second chest sign
			for (BlockFace chestface : newsfaces){
				Block relativechest = block.getRelative(chestface);
				if (relativechest.getType() == block.getType()){
					if (isLockedSingleBlock(relativechest, chestface.getOppositeFace())) return true;
				}
			}
			// Don't break here
		// Everything else (First block of container check goes here)
		default:
			if (isLockedSingleBlock(block, null)) return true; 
			break;
		}
		return false;
	}
	
	public static boolean isOwner(Block block, Player player){
		switch (block.getType()){
		// Double Doors
		case WOODEN_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR_BLOCK:
			Block[] doors = getDoors(block);
			if (doors == null) return false;
			for (BlockFace doorface : newsfaces){
				Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
				if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
					if (isOwnerSingleBlock(relative1.getRelative(BlockFace.UP), doorface.getOppositeFace(), player)) return true;
					if (isOwnerSingleBlock(relative1, doorface.getOppositeFace(), player)) return true;
					if (isOwnerSingleBlock(relative0, doorface.getOppositeFace(), player)) return true; 
					if (isOwnerSingleBlock(relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace(), player)) return true; 
				}
			}
			if (isOwnerSingleBlock(doors[1].getRelative(BlockFace.UP), null, player)) return true;
			if (isOwnerSingleBlock(doors[1], null, player)) return true;
			if (isOwnerSingleBlock(doors[0], null, player)) return true; 
			if (isOwnerSingleBlock(doors[0].getRelative(BlockFace.DOWN), null, player)) return true;
			break;
		// Chests (Second block only)
		case CHEST:
		case TRAPPED_CHEST:
			// Check second chest sign
			for (BlockFace chestface : newsfaces){
				Block relativechest = block.getRelative(chestface);
				if (relativechest.getType() == block.getType()){
					if (isOwnerSingleBlock(relativechest, chestface.getOppositeFace(), player)) return true;
				}
			}
			// Don't break here
		// Everything else (First block of container check goes here)
		default:
			if (isOwnerSingleBlock(block, null, player)) return true; 
			break;
		}
		return false;
	}

	public static boolean isUser(Block block, Player player){
		switch (block.getType()){
		// Double Doors
		case WOODEN_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR_BLOCK:
			Block[] doors = getDoors(block);
			if (doors == null) return false;
			for (BlockFace doorface : newsfaces){
				Block relative0 = doors[0].getRelative(doorface), relative1 = doors[1].getRelative(doorface);
				if (relative0.getType() == doors[0].getType() && relative1.getType() == doors[1].getType()){
					if (isUserSingleBlock(relative1.getRelative(BlockFace.UP), doorface.getOppositeFace(), player)) return true;
					if (isUserSingleBlock(relative1, doorface.getOppositeFace(), player)) return true;
					if (isUserSingleBlock(relative0, doorface.getOppositeFace(), player)) return true; 
					if (isUserSingleBlock(relative0.getRelative(BlockFace.DOWN), doorface.getOppositeFace(), player)) return true; 
				}
			}
			if (isUserSingleBlock(doors[1].getRelative(BlockFace.UP), null, player)) return true;
			if (isUserSingleBlock(doors[1], null, player)) return true;
			if (isUserSingleBlock(doors[0], null, player)) return true; 
			if (isUserSingleBlock(doors[0].getRelative(BlockFace.DOWN), null, player)) return true;
			break;
		// Chests (Second block only)
		case CHEST:
		case TRAPPED_CHEST:
			// Check second chest sign
			for (BlockFace chestface : newsfaces){
				Block relativechest = block.getRelative(chestface);
				if (relativechest.getType() == block.getType()){
					if (isUserSingleBlock(relativechest, chestface.getOppositeFace(), player)) return true;
				}
			}
			// Don't break here
		// Everything else (First block of container check goes here)
		default:
			if (isUserSingleBlock(block, null, player)) return true; 
			break;
		}
		return false;
	}
	
	public static boolean isProtected(Block block){
		return (isLockSign(block) || isLocked(block) || isUpDownLockedDoor(block));
	}
	
	public static boolean isLockedSingleBlock(Block block, BlockFace exempt){
		for (BlockFace blockface : newsfaces){
			if (blockface == exempt) continue;
			Block relativeblock = block.getRelative(blockface);
			// Find [Private] sign?
			if (isLockSign(relativeblock) && (((org.bukkit.material.Sign)relativeblock.getState().getData()).getFacing() == blockface)){
				// Found [Private] sign, is expire turned on and expired? (relativeblock is now sign)
				if (Config.isLockExpire() && LocketteProAPI.isSignExpired(relativeblock)) {
					continue; // Private sign but expired... But impossible to have 2 [Private] signs anyway?
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOwnerSingleBlock(Block block, BlockFace exempt, Player player){ // Requires isLocked
		for (BlockFace blockface : newsfaces){
			if (blockface == exempt) continue;
			Block relativeblock = block.getRelative(blockface);
			if (isLockSign(relativeblock) && (((org.bukkit.material.Sign)relativeblock.getState().getData()).getFacing() == blockface)){
				if (isOwnerOnSign(relativeblock, player)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isUserSingleBlock(Block block, BlockFace exempt, Player player){ // Requires isLocked
		for (BlockFace blockface : newsfaces){
			if (blockface == exempt) continue;
			Block relativeblock = block.getRelative(blockface);
			if (isLockSignOrAdditionalSign(relativeblock) && (((org.bukkit.material.Sign)relativeblock.getState().getData()).getFacing() == blockface)){
				if (isUserOnSign(relativeblock, player)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isOwnerOfSign(Block block, Player player){ // Requires isSign
		Block protectedblock = getAttachedBlock(block);
		// Normal situation, that block is just locked by an adjacent sign
		if (isOwner(protectedblock, player)) return true;
		// Situation where double door's block
		if (isUpDownLockedDoor(protectedblock) && isOwnerUpDownLockedDoor(protectedblock, player)) return true;
		// Otherwise...
		return false;
	}
	
	public static boolean isLockable(Block block){
		Material material = block.getType();
		//Bad blocks
		switch (material){
		case SIGN:
		case WALL_SIGN:
		case SIGN_POST:
			return false;
		default:
			break;
		}
		if (Config.isLockable(material)){ // Directly lockable
			return true;
		} else { // Indirectly lockable
			Block blockup = block.getRelative(BlockFace.UP);
			if (blockup != null && isUpDownAlsoLockableBlock(blockup)) return true;
			Block blockdown = block.getRelative(BlockFace.DOWN);
			if (blockdown != null && isUpDownAlsoLockableBlock(blockdown)) return true;
			return false;
		}
	}
	

	public static boolean isChest(Block block){
		switch (block.getType()){
		case CHEST:
		case TRAPPED_CHEST:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isUpDownAlsoLockableBlock(Block block){
		if (Config.isLockable(block.getType())){
			switch (block.getType()){
			case WOODEN_DOOR:
			case SPRUCE_DOOR:
			case BIRCH_DOOR:
			case JUNGLE_DOOR:
			case ACACIA_DOOR:
			case DARK_OAK_DOOR:
			case IRON_DOOR_BLOCK:
				return true;
			default:
				return false;
			}
		}
		return false;
	}
	
	public static boolean mayInterfere(Block block, Player player){
		// if LEFT may interfere RIGHT
		switch (block.getType()){
		case WOODEN_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR_BLOCK:
			for (BlockFace blockface : newsfaces){
				Block newblock = block.getRelative(blockface);
				switch (newblock.getType()){
				case WOODEN_DOOR:
				case SPRUCE_DOOR:
				case BIRCH_DOOR:
				case JUNGLE_DOOR:
				case ACACIA_DOOR:
				case DARK_OAK_DOOR:
				case IRON_DOOR_BLOCK:
					if (isLocked(newblock) && !isOwner(newblock, player)){
						return true;
					}
				default:
					break;
				}
			}
			// Temp workaround bad code for checking up and down signs
			Block newblock2 = block.getRelative(BlockFace.UP, 2);
			switch (newblock2.getType()){
			default:
				if (isLocked(newblock2) && !isOwner(newblock2, player)){
					return true;
				}
				break;
			}
			Block newblock3 = block.getRelative(BlockFace.DOWN, 1);
			switch (newblock3.getType()){
			default:
				if (isLocked(newblock3) && !isOwner(newblock3, player)){
					return true;
				}
				break;
			}
			break;
			// End temp workaround bad code for checking up and down signs
		case CHEST:
		case TRAPPED_CHEST:
		case WALL_SIGN:
		case SIGN_POST:
			for (BlockFace blockface : allfaces){
				Block newblock = block.getRelative(blockface);
				switch (newblock.getType()){
				case CHEST:
				case TRAPPED_CHEST:
					if (isLockedSingleBlock(newblock, null) && !isOwnerSingleBlock(newblock, null, player)){
						return true;
					}
				default:
					break;
				}
			}
			break;
		// This is extra interfere block
		case HOPPER:
		case DISPENSER:
		case DROPPER:
			if (!Config.isInterferePlacementBlocked()) return false;
			for (BlockFace blockface : allfaces){
				Block newblock = block.getRelative(blockface);
				switch (newblock.getType()){
				case CHEST:
				case TRAPPED_CHEST:
				case HOPPER:
				case DISPENSER:
				case DROPPER:
					if (isLocked(newblock) && !isOwner(newblock, player)){
						return true;
					}
				default:
					break;
				}
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	public static boolean isSign(Block block){
		return block.getType() == Material.WALL_SIGN;
	}
	
	public static boolean isLockSign(Block block){
		return isSign(block) && isLockString(((Sign)block.getState()).getLine(0));
	}
	
	public static boolean isAdditionalSign(Block block){
		return isSign(block) && isAdditionalString(((Sign)block.getState()).getLine(0));
	}
	
	public static boolean isLockSignOrAdditionalSign(Block block){
		if (isSign(block)){
			String line = ((Sign)block.getState()).getLine(0);
			return isLockStringOrAdditionalString(line);
		} else {
			return false;
		}
	}
	
	public static boolean isOwnerOnSign(Block block, Player player){ // Requires isLockSign
		String[] lines = ((Sign)block.getState()).getLines();
		if (Utils.isPlayerOnLine(player, lines[1])){
			if (Config.isUuidEnabled()){
				Utils.updateLineByPlayer(block, 1, player);
			}
			return true;
		}
		return false;
	}
	
	public static boolean isUserOnSign(Block block, Player player){ // Requires (isLockSign or isAdditionalSign)
		String[] lines = ((Sign)block.getState()).getLines();
		// Normal
		for (int i = 1; i < 4; i ++){
			if (Utils.isPlayerOnLine(player, lines[i])){
				if (Config.isUuidEnabled()){
					Utils.updateLineByPlayer(block, i, player);
				}
				return true;
			} else if (Config.isEveryoneSignString(lines[i])) {
				return true;
			}
		}
		// For Towny & Vault & Scoreboard
		for (int i = 1; i < 4; i ++){
			if (Dependency.isTownyTownOrNationOf(lines[i], player)) return true;
			if (Dependency.isPermissionGroupOf(lines[i], player)) return true;	
			if (Dependency.isScoreboardTeamOf(lines[i], player)) return true;
			if (Dependency.isSimpleClanOf(lines[i], player)) return true;
		}
		
		return false;
	}
	
	public static boolean isSignExpired(Block block){
		if (!isSign(block) || !isLockSign(block)) return false;
		return isLineExpired(((Sign)block.getState()).getLine(0));
	}
	
	public static boolean isLineExpired(String line){
		long createdtime = Utils.getCreatedFromLine(line);
		if (createdtime == -1L) return false; // No expire
		long currenttime = (int)(System.currentTimeMillis()/1000);
		return createdtime + Config.getLockExpireDays() * 86400L < currenttime;
	}
	
	public static boolean isUpDownLockedDoor(Block block){
		Block blockup = block.getRelative(BlockFace.UP);
		if (blockup != null && isUpDownAlsoLockableBlock(blockup) && isLocked(blockup)) return true;
		Block blockdown = block.getRelative(BlockFace.DOWN);
		if (blockdown != null && isUpDownAlsoLockableBlock(blockdown) && isLocked(blockdown)) return true;
		return false;
	}
	
	public static boolean isOwnerUpDownLockedDoor(Block block, Player player){
		Block blockup = block.getRelative(BlockFace.UP);
		if (blockup != null && isUpDownAlsoLockableBlock(blockup) && isOwner(blockup, player)) return true;
		Block blockdown = block.getRelative(BlockFace.DOWN);
		if (blockdown != null && isUpDownAlsoLockableBlock(blockdown) && isOwner(blockdown, player)) return true;
		return false;
	}
	
	public static boolean isUserUpDownLockedDoor(Block block, Player player){
		Block blockup = block.getRelative(BlockFace.UP);
		if (blockup != null && isUpDownAlsoLockableBlock(blockup) && isUser(blockup, player)) return true;
		Block blockdown = block.getRelative(BlockFace.DOWN);
		if (blockdown != null && isUpDownAlsoLockableBlock(blockdown) && isUser(blockdown, player)) return true;
		return false;
	}
	
	public static boolean isLockString(String line){
		if (line.contains("#")) line = line.split("#", 2)[0];
		return Config.isPrivateSignString(line);
	}
	
	public static boolean isAdditionalString(String line){
		if (line.contains("#")) line = line.split("#", 2)[0];
		return Config.isAdditionalSignString(line);
	}
	
	public static boolean isLockStringOrAdditionalString(String line){
		return isLockString(line) || isAdditionalString(line);
	}

	public static Block getAttachedBlock(Block sign){ // Requires isSign
		return sign.getRelative(((org.bukkit.material.Sign)sign.getState().getData()).getFacing().getOppositeFace());
	}
	
	public static int getTimerOnSigns(Block block){
		for (BlockFace blockface : newsfaces){
			Block relative = block.getRelative(blockface);
			if (isSign(relative)){
				Sign sign = (Sign)relative.getState();
				for (String line : sign.getLines()){
					int linetime = Config.getTimer(line);
					if (linetime > 0) return linetime;
				}
			}
		}
		return 0;
	}

	public static int getTimerDoor(Block block){
		int timersingle = getTimerSingleDoor(block);
		if (timersingle > 0) return timersingle;
		for (BlockFace blockface : newsfaces){
			Block relative = block.getRelative(blockface);
			timersingle = getTimerSingleDoor(relative);
			if (timersingle > 0) return timersingle;
		}
		return 0;
	}
	
	public static int getTimerSingleDoor(Block block){
		Block[] doors = getDoors(block);
		if (doors == null) return 0;
		Block relativeup = doors[1].getRelative(BlockFace.UP);
		int relativeuptimer = getTimerOnSigns(relativeup);
		if (relativeuptimer > 0) return relativeuptimer;
		int doors0 = getTimerOnSigns(doors[0]);
		if (doors0 > 0) return doors0;
		int doors1 = getTimerOnSigns(doors[1]);
		if (doors1 > 0) return doors1;
		Block relativedown = doors[0].getRelative(BlockFace.DOWN);
		int relativedowntimer = getTimerOnSigns(relativedown);
		if (relativedowntimer > 0) return relativedowntimer;
		return 0;
	}
	
	public static Block[] getDoors(Block block){
		Block[] doors = new Block[2];
		boolean found = false;
		Block up = block.getRelative(BlockFace.UP), down = block.getRelative(BlockFace.DOWN);
		if (up.getType() == block.getType()){
			found = true;
			doors[0] = block; doors[1] = up;
		}
		if (down.getType() == block.getType()){
			if (found == true){ // error 3 doors
				return null;
			}
			doors[1] = block; doors[0] = down;
			found = true;
		}
		if (!found){ // error 1 door
			return null;
		}
		return doors;
	}
	
	public static boolean isDoubleDoorBlock(Block block){
		switch (block.getType()){
		case WOODEN_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR_BLOCK:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isSingleDoorBlock(Block block){
		switch (block.getType()){
		case FENCE_GATE:
		case TRAP_DOOR:
		case IRON_TRAPDOOR:
			return true;
		default:
			return false;	
		}	
	}
	
	public static Block getBottomDoorBlock(Block block){ // Requires isDoubleDoorBlock || isSingleDoorBlock
		if (isDoubleDoorBlock(block)){
			Block relative = block.getRelative(BlockFace.DOWN);
			if (relative.getType() == block.getType()){
				return relative;
			} else {
				return block;
			}
		} else {
			return block;
		}
	}
	
	public static void toggleDoor(Block block, boolean open){
		BlockState doorstate = block.getState();
		Openable openablestate = (Openable)doorstate.getData();
		openablestate.setOpen(open);
		doorstate.setData((MaterialData)openablestate);
		doorstate.update();
		block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
	}
	
	public static void toggleDoor(Block block){
		BlockState doorstate = block.getState();
		Openable openablestate = (Openable)doorstate.getData();
		openablestate.setOpen(!openablestate.isOpen());
		doorstate.setData((MaterialData)openablestate);
		doorstate.update();
		block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
	}
	
}
