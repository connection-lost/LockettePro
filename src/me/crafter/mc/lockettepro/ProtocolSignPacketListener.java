package me.crafter.mc.lockettepro;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProtocolSignPacketListener extends PacketAdapter{

	/*  This class is a ProtocolLib packet listener. It is used to modify packets from a lock sign.
	 *  Whenever a lock sign conatining username with UUID (eg. "user#uuid-uuid-xxxxxx"), this
	 *  class will prevent the uuid from sending to the client, making the sign human readable.
	 */
	
	public ProtocolSignPacketListener(AdapterParameteters params) {
		super(params);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		try {
			boolean modified = false;
			WrappedChatComponent[] lines = packet.getChatComponentArrays().read(0);
			if (LocketteProAPI.isLockStringOrAdditionalString(getSignLineFromUnknown(lines[0]))){
				for (int i = 1; i < 4; i ++){
					String line = getSignLineFromUnknown(lines[i]);
					if (Utils.isUsernameUuidLine(line)){
						lines[i] = WrappedChatComponent.fromText(Utils.getUsernameFromLine(line));
						modified = true;
					}
				}
			}
			if (modified){
				packet.getChatComponentArrays().write(0, lines);
			}
		} catch (Exception ex){}
	}
	
	public static String getSignLineFromUnknown(WrappedChatComponent rawline){
		String json = rawline.getJson();
		try {
			JsonObject line = new JsonParser().parse(json).getAsJsonObject();
			return line.get("extra").getAsJsonArray().get(0).getAsString();
		} catch (Exception ex){
			return json;
		}
	}

}
