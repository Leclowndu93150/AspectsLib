package com.leclowndu93150.aspectslib.client;

import com.leclowndu93150.aspectslib.AspectsLib;
import com.leclowndu93150.aspectslib.client.tooltip.AspectTooltipComponent;
import com.leclowndu93150.aspectslib.client.tooltip.AspectTooltipData;
import com.leclowndu93150.aspectslib.data.Aspect;
import com.leclowndu93150.aspectslib.data.AspectManager;
import com.leclowndu93150.aspectslib.data.ModRegistries;
import com.leclowndu93150.aspectslib.networking.SyncAspectIdentifierPacket;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class AspectsLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof AspectTooltipData aspectTooltipData) {
                return new AspectTooltipComponent(aspectTooltipData);
            }
            return null;
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncAspectIdentifierPacket.ID, (client, handler, buf, responseSender) -> {
            try {
                Map<String, Identifier> nameMap = SyncAspectIdentifierPacket.readNameMap(buf);
                Map<Identifier, Aspect> aspectMap;

                if (buf.readableBytes() > 0) {
                    aspectMap = SyncAspectIdentifierPacket.readAspectData(buf);
                } else {
                    aspectMap = new HashMap<>();
                    AspectsLib.LOGGER.warn("Received legacy packet format - only name mapping synced");
                }

                final Map<String, Identifier> finalNameMap = nameMap;
                final Map<Identifier, Aspect> finalAspectMap = aspectMap;
                
                client.execute(() -> {
                    AspectManager.NAME_TO_ID.clear();
                    AspectManager.NAME_TO_ID.putAll(finalNameMap);
                    
                    if (!finalAspectMap.isEmpty()) {
                        ModRegistries.ASPECTS.clear();
                        ModRegistries.ASPECTS.putAll(finalAspectMap);
                    }
                    
                    AspectsLib.LOGGER.info("Synced {} aspects from server (name mappings: {})", 
                            finalAspectMap.size(), finalNameMap.size());
                });
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Failed to read aspect sync packet: {}", e.getMessage());
            }
        });

        AspectsLib.LOGGER.info("AspectsLib Client initialized!");
    }
}