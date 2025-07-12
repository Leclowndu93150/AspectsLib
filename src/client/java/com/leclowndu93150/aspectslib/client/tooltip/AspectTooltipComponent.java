package com.leclowndu93150.aspectslib.client.tooltip;

import com.leclowndu93150.aspectslib.data.Aspect;
import com.leclowndu93150.aspectslib.data.AspectData;
import com.leclowndu93150.aspectslib.data.ModRegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class AspectTooltipComponent implements TooltipComponent {
    private final AspectData aspectData;

    public AspectTooltipComponent(AspectTooltipData data) {
        this.aspectData = data.aspectData();
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int width = 0;
        for (var entry : aspectData.getMap().object2IntEntrySet()) {
            Identifier aspectId = entry.getKey();
            Aspect aspect = ModRegistries.ASPECTS.get(aspectId);
            if (aspect != null) {
                Text aspectName = aspect.getTranslatedName();
                int valueWidth = textRenderer.getWidth(aspectName);
                width += 16 + 2 + valueWidth + 4;
            }
        }
        return width;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int currentX = x;
        final int TEXT_COLOR = 0xFFFFFFFF;

        for (Object2IntMap.Entry<Identifier> entry : aspectData.getMap().object2IntEntrySet()) {
            int value = entry.getIntValue();
            Identifier aspectId = entry.getKey();
            
            Aspect aspect = ModRegistries.ASPECTS.get(aspectId);
            if (aspect == null) continue;
            
            Identifier texture = aspect.textureLocation();

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, texture);

            context.drawTexture(texture, currentX, y, 0, 0, 16, 16, 16, 16);

            int textY = y + 5;

            Text aspectName = aspect.getTranslatedName().formatted(Formatting.WHITE);
            String text = aspectName.getString() + " x" + value;
            context.drawText(textRenderer, text, currentX + 18, textY, TEXT_COLOR, false);

            currentX += 16 + textRenderer.getWidth(text) + 6;
        }
    }
}