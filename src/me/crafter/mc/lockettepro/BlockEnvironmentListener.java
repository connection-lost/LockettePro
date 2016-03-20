package me.crafter.mc.lockettepro;

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockEnvironmentListener implements Listener{

	// Prevent explosion break block
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityExplode(EntityExplodeEvent event){
		Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (LocketteProAPI.isProtected(block)) it.remove();
        }
	}
	
	// Prevent tree break block
	@EventHandler(priority = EventPriority.MONITOR)
	public void onStructureGrow(StructureGrowEvent event){
		for (BlockState blockstate : event.getBlocks()){
			if (LocketteProAPI.isProtected(blockstate.getBlock())){
				event.setCancelled(true);
				return;
			}
		}
	}

	// Prevent piston break lock
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPistonExtend(BlockPistonExtendEvent event){
		for (Block block : event.getBlocks()){
			if (LocketteProAPI.isProtected(block)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	// Prevent piston break lock
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPistonExtend(BlockPistonRetractEvent event){
		// 1.8 +
		for (Block block : event.getBlocks()){
			if (LocketteProAPI.isProtected(block)){
				event.setCancelled(true);
				return;
			}
		}
		// 1.7
//		if (LocketteProAPI.isProtected(event.getBlock())){
//			event.setCancelled(true);
//			return;
//		}
	}
	
	// Prevent redstone current open doors
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChange(BlockRedstoneEvent event){
		if (LocketteProAPI.isProtected(event.getBlock())){
			event.setNewCurrent(event.getOldCurrent());
		}
	}

}
