package com.leclowndu93150.aspectslib.data;

import com.leclowndu93150.aspectslib.AspectsLib;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModRegistries {
    // Simple map-based approach - much simpler for 1.20.1!
    public static final Map<Identifier, Aspect> ASPECTS = new HashMap<>();

    public static void register() {
        AspectsLib.LOGGER.info("Initialized aspects map for datapack loading");
    }
}