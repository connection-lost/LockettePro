package me.crafter.mc.lockettepro;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class SignSendListener implements Listener {
	
	@EventHandler
	public void onSignSend(SignSendEvent event){
		if (LocketteProAPI.isLockStringOrAdditionalString(Utils.getSignLineFromUnknown(event.getLine(0)))){
			// Private line
			String line0 = Utils.getSignLineFromUnknown(event.getLine(0));
			if (LocketteProAPI.isLineExpired(line0)){
				event.setLine(0, WrappedChatComponent.fromText(Config.getLockExpireString()).getJson());
			} else {
				event.setLine(0, WrappedChatComponent.fromText(Utils.StripSharpSign(line0)).getJson());
			}
			// Other line
			for (int i = 1; i < 4; i ++){
				String line = Utils.getSignLineFromUnknown(event.getLine(i));
				if (Utils.isUsernameUuidLine(line)){
					event.setLine(i, WrappedChatComponent.fromText(Utils.getUsernameFromLine(line)).getJson());
				} else if (Utils.isPrivateTimeLine(line)){
					event.setLine(i, WrappedChatComponent.fromText(Utils.getUsernameFromLine(line)).getJson());
				}
			}
		}
	}
	
}