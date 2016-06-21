package me.crafter.mc.lockettepro;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProtocolSignPacketListener extends PacketAdapter{

	/*  This class is a ProtocolLib packet listener. It is used to modify packets from a lock sign.
	 *  Whenever a lock sign conatining username with UUID (eg. "user#uuid-uuid-xxxxxx"), this
	 *  class will prevent the uuid from sending to the client, making the sign human readable.
	 *  This class will not be loaded if UUID support is turned off.
	 */
	
	public ProtocolSignPacketListener(AdapterParameteters params) {
		super(params);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		if (!LockettePro.is19()){ // Legacy 1.8
			if (packet.getType() == PacketType.Play.Server.UPDATE_SIGN){
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
		} else { // 1.9
			if (packet.getType() == PacketType.Play.Server.TILE_ENTITY_DATA){
				if (packet.getIntegers().read(0) == 9){
					try {
						boolean modified = false;
						NbtCompound nbtbase = (NbtCompound)packet.getNbtModifier().read(0);
						if (LocketteProAPI.isLockStringOrAdditionalString(getSignLineFromUnknown(nbtbase.getString("Text1")))){
							for (int i = 2; i <= 4; i ++){
								String line = getSignLineFromUnknown(nbtbase.getString("Text" + i));
								if (Utils.isUsernameUuidLine(line)){
									nbtbase.put("Text" + i, WrappedChatComponent.fromText(Utils.getUsernameFromLine(line)).getJson());
									modified = true;
								}
							}
						}
						if (modified){
							packet.getNbtModifier().write(0, nbtbase);
						}
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		
	}
	
	public static String getSignLineFromUnknown(WrappedChatComponent rawline){
		String json = rawline.getJson();
		return getSignLineFromUnknown(json);
	}
	
	public static String getSignLineFromUnknown(String json){
		try { // 1.8-
			JsonObject line = new JsonParser().parse(json).getAsJsonObject();
			return line.get("extra").getAsJsonArray().get(0).getAsString();
		} catch (Exception ex){}
		try { // 1.9+
			JsonObject line = new JsonParser().parse(json).getAsJsonObject();
			return line.get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
		} catch (Exception ex){}
		return json;

	}

}
