package com.leclowndu93150.aspectslib.client.tooltip;

import com.leclowndu93150.aspectslib.data.AspectData;
import net.minecraft.client.item.TooltipData;

public record AspectTooltipData(AspectData aspectData) implements TooltipData {
}