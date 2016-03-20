package me.crafter.mc.lockettepro;

import java.util.List;

import org.bukkit.block.Block;

public class DoorToggleTask implements Runnable{

	private List<Block> doors;
	
	public DoorToggleTask(List<Block> doors_){
		doors = doors_;
	}
	
	@Override
	public void run() {
		for (Block door : doors){
			if (LocketteProAPI.isDoubleDoorBlock(door)){
				Block doorbottom = LocketteProAPI.getBottomDoorBlock(door);
				//LocketteProAPI.toggleDoor(doorbottom, open);
				LocketteProAPI.toggleDoor(doorbottom);
			}
		}
	}
	
}
