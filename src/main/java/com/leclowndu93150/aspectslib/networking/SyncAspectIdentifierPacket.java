package com.leclowndu93150.aspectslib.networking;

import com.leclowndu93150.aspectslib.AspectsLib;
import com.leclowndu93150.aspectslib.data.Aspect;
import com.leclowndu93150.aspectslib.data.AspectManager;
import com.leclowndu93150.aspectslib.data.ModRegistries;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * This packet syncs both the aspect name mappings and the actual aspect data from server to client.
 */
public class SyncAspectIdentifierPacket {
    public static final Identifier ID = AspectsLib.identifier("sync_aspect_packet");

    /**
     * Writes the name-to-ID mapping to the buffer
     */
    public static void writeNameMap(PacketByteBuf buf, Map<String, Identifier> nameMap) {
        buf.writeInt(nameMap.size());
        for (Map.Entry<String, Identifier> entry : nameMap.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeIdentifier(entry.getValue());
        }
    }

    /**
     * Writes the full aspect data to the buffer
     */
    public static void writeAspectData(PacketByteBuf buf, Map<Identifier, Aspect> aspectMap) {
        buf.writeInt(aspectMap.size());
        for (Map.Entry<Identifier, Aspect> entry : aspectMap.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            Aspect.PACKET_CODEC.encode(buf, entry.getValue());
        }
    }

    /**
     * Writes both the name mapping and aspect data to the buffer
     */
    public static void writeFullData(PacketByteBuf buf, Map<String, Identifier> nameMap, Map<Identifier, Aspect> aspectMap) {
        writeNameMap(buf, nameMap);
        writeAspectData(buf, aspectMap);
    }

    /**
     * Reads the name-to-ID mapping from the buffer
     */
    public static Map<String, Identifier> readNameMap(PacketByteBuf buf) {
        int size = buf.readInt();
        Map<String, Identifier> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = buf.readString();
            Identifier value = buf.readIdentifier();
            map.put(key, value);
        }
        return map;
    }

    /**
     * Reads the aspect data from the buffer
     */
    public static Map<Identifier, Aspect> readAspectData(PacketByteBuf buf) {
        int size = buf.readInt();
        Map<Identifier, Aspect> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Identifier id = buf.readIdentifier();
            Aspect aspect = Aspect.PACKET_CODEC.decode(buf);
            map.put(id, aspect);
        }
        return map;
    }

    /**
     * Reads both the name mapping and aspect data from the buffer
     */
    public static void readFullData(PacketByteBuf buf) {
        Map<String, Identifier> nameMap = readNameMap(buf);
        Map<Identifier, Aspect> aspectMap = readAspectData(buf);
        
        // Update client-side data
        AspectManager.NAME_TO_ID.clear();
        AspectManager.NAME_TO_ID.putAll(nameMap);
        
        ModRegistries.ASPECTS.clear();
        ModRegistries.ASPECTS.putAll(aspectMap);
        
        AspectsLib.LOGGER.info("Synced {} aspects from server", aspectMap.size());
    }

    /**
     * Legacy method - sends just the name mapping 
     */
    public static void sendMap(ServerPlayerEntity player, Map<String, Identifier> map) {
        PacketByteBuf buf = PacketByteBufs.create();
        writeNameMap(buf, map);
        ServerPlayNetworking.send(player, ID, buf);
    }

    /**
     * Sends the full aspect data (names + aspects) from server to client
     */
    public static void sendFullData(ServerPlayerEntity player, Map<String, Identifier> nameMap, Map<Identifier, Aspect> aspectMap) {
        PacketByteBuf buf = PacketByteBufs.create();
        writeFullData(buf, nameMap, aspectMap);
        ServerPlayNetworking.send(player, ID, buf);
    }

    /**
     * Sends all current aspect data from server to client
     */
    public static void sendAllData(ServerPlayerEntity player) {
        AspectsLib.LOGGER.debug("Sending {} aspects and {} name mappings to client", 
                ModRegistries.ASPECTS.size(), AspectManager.NAME_TO_ID.size());
        sendFullData(player, AspectManager.NAME_TO_ID, ModRegistries.ASPECTS);
    }

    // Legacy methods for backward compatibility
    public static PacketByteBuf toBuffer(Map<String, Identifier> map) {
        PacketByteBuf buf = PacketByteBufs.create();
        writeNameMap(buf, map);
        return buf;
    }

    public static Map<String, Identifier> fromBuffer(PacketByteBuf buf) {
        return readNameMap(buf);
    }
}