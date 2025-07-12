package com.leclowndu93150.aspectslib.api;

import com.leclowndu93150.aspectslib.data.Aspect;
import com.leclowndu93150.aspectslib.data.AspectData;
import com.leclowndu93150.aspectslib.data.AspectManager;
import com.leclowndu93150.aspectslib.data.ItemAspectRegistry;
import com.leclowndu93150.aspectslib.data.ModRegistries;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Public API for AspectsLib
 */
public class AspectsAPI {

    /**
     * Gets the aspect data from an ItemStack
     * @param stack The ItemStack to check
     * @return The aspect data, or AspectData.DEFAULT if none
     */
    public static AspectData getAspectData(ItemStack stack) {
        return ((IAspectDataProvider) (Object) stack).aspectslib$getAspectData();
    }

    /**
     * Sets the aspect data on an ItemStack
     * @param stack The ItemStack to modify
     * @param data The aspect data to set, or null to clear
     */
    public static void setAspectData(ItemStack stack, @Nullable AspectData data) {
        ((IAspectDataProvider) (Object) stack).aspectslib$setAspectData(data);
    }

    /**
     * Adds aspects to an ItemStack
     * @param stack The ItemStack to modify
     * @param aspectId The identifier of the aspect to add
     * @param amount The amount to add
     * @return true if successful, false if aspect not found
     */
    public static boolean addAspect(ItemStack stack, Identifier aspectId, int amount) {
        Aspect aspect = ModRegistries.ASPECTS.get(aspectId);
        if (aspect == null) {
            return false;
        }

        AspectData currentData = getAspectData(stack);
        AspectData.Builder builder = new AspectData.Builder(currentData);
        builder.add(aspectId, amount);
        setAspectData(stack, builder.build());
        return true;
    }

    /**
     * Adds aspects to an ItemStack by name
     * @param stack The ItemStack to modify
     * @param aspectName The name of the aspect to add
     * @param amount The amount to add
     * @return true if successful, false if aspect not found
     */
    public static boolean addAspectByName(ItemStack stack, String aspectName, int amount) {
        AspectData currentData = getAspectData(stack);
        AspectData.Builder builder = new AspectData.Builder(currentData);
        builder.addByName(aspectName, amount);
        setAspectData(stack, builder.build());
        return true;
    }

    /**
     * Registers default aspects for an item
     * @param item The item to register aspects for
     * @param aspectId The identifier of the aspect
     * @param amount The default amount
     */
    public static void registerItemAspect(Item item, Identifier aspectId, int amount) {
        Identifier itemId = Registries.ITEM.getId(item);
        Aspect aspect = ModRegistries.ASPECTS.get(aspectId);
        
        if (aspect != null) {
            Object2IntOpenHashMap<Identifier> aspects = new Object2IntOpenHashMap<>();
            aspects.put(aspectId, amount);
            ItemAspectRegistry.register(itemId, new AspectData(aspects));
        }
    }

    /**
     * Registers default aspects for an item by name
     * @param item The item to register aspects for
     * @param aspectName The name of the aspect
     * @param amount The default amount
     */
    public static void registerItemAspectByName(Item item, String aspectName, int amount) {
        Identifier itemId = Registries.ITEM.getId(item);
        AspectData.Builder builder = new AspectData.Builder(AspectData.DEFAULT);
        builder.addByName(aspectName, amount);
        ItemAspectRegistry.register(itemId, builder.build());
    }

    /**
     * Gets an aspect by its identifier
     * @param aspectId The identifier of the aspect
     * @return The aspect, or empty if not found
     */
    public static Optional<Aspect> getAspect(Identifier aspectId) {
        return Optional.ofNullable(ModRegistries.ASPECTS.get(aspectId));
    }

    /**
     * Gets an aspect by its name
     * @param aspectName The name of the aspect
     * @return The aspect, or empty if not found
     */
    public static Optional<Aspect> getAspectByName(String aspectName) {
        Identifier aspectId = AspectManager.NAME_TO_ID.get(aspectName);
        return aspectId != null ? getAspect(aspectId) : Optional.empty();
    }

    /**
     * Creates a new AspectData builder
     * @return A new builder instance
     */
    public static AspectData.Builder createAspectDataBuilder() {
        return new AspectData.Builder(AspectData.DEFAULT);
    }

    /**
     * Gets all loaded aspects
     * @return A map of all loaded aspects
     */
    public static java.util.Map<Identifier, Aspect> getAllAspects() {
        return java.util.Collections.unmodifiableMap(ModRegistries.ASPECTS);
    }
}