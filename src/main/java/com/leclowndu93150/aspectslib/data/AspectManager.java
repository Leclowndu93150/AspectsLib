package com.leclowndu93150.aspectslib.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.leclowndu93150.aspectslib.AspectsLib;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple map-based aspect loader that reloads from datapacks.
 * Much simpler than trying to use dynamic registries in 1.20.1!
 */
public class AspectManager extends JsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Gson GSON = new Gson();

    /**
     * This parameter holds the Names of aspects against their registration Identifiers.
     */
    public static Map<String, Identifier> NAME_TO_ID = new HashMap<>();

    /**
     * Instantiate the AspectManager
     */
    public AspectManager() {
        super(GSON, "aspects");
    }

    /**
     * This method loads aspects from datapack JSON files into our simple map.
     * Gets called every time datapacks reload.
     */
    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        // Clear existing data
        ModRegistries.ASPECTS.clear();
        NAME_TO_ID.clear();
        
        // Load aspects from datapack files
        AtomicInteger loadedCount = new AtomicInteger();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            Identifier id = entry.getKey();
            JsonElement json = entry.getValue();

            Aspect.CODEC.parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(error -> AspectsLib.LOGGER.error("Failed to parse aspect data {}: {}", id, error))
                    .ifPresent(aspect -> {
                        // Simply add to our map - no registry shenanigans!
                        ModRegistries.ASPECTS.put(id, aspect);
                        NAME_TO_ID.put(aspect.name(), id);
                        loadedCount.getAndIncrement();
                        AspectsLib.LOGGER.debug("Loaded aspect: {} -> {}", aspect.name(), id);
                    });
        }

        AspectsLib.LOGGER.info("Loaded {} aspects from datapacks", loadedCount);
    }

    @Override
    public Identifier getFabricId() {
        return AspectsLib.identifier("aspects");
    }
}