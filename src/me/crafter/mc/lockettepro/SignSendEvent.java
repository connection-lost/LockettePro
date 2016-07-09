package me.crafter.mc.lockettepro;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class SignSendEvent extends Event {

	private final Player player;
	private String[] lines;
	private boolean modified = false;
	
	public SignSendEvent(Player player, String[] lines){
		this.player = player;
		this.lines = lines;
	}
	
	public String getLine(int linenumber){
		return lines[linenumber];
	}
	
	public String[] getLines(){
		return lines;
	}
	
	public WrappedChatComponent[] getLinesWrappedChatComponent(){
		WrappedChatComponent[] wrappedchatcomponent = new WrappedChatComponent[4];
		for (int i = 0; i < 4; i ++){
			wrappedchatcomponent[i] = WrappedChatComponent.fromJson(lines[i]);
		}
		return wrappedchatcomponent;
	}
	
	public void setLine(int linenumber, String text){
		lines[linenumber] = text;
		modified = true;
	}
	
	public boolean isModified(){
		return modified;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
