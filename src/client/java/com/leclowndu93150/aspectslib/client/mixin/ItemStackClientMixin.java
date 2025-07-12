package com.leclowndu93150.aspectslib.client.mixin;

import com.leclowndu93150.aspectslib.api.IAspectDataProvider;
import com.leclowndu93150.aspectslib.client.tooltip.AspectTooltipData;
import com.leclowndu93150.aspectslib.data.AspectData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin implements IAspectDataProvider{

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void addAspectTooltipData(CallbackInfoReturnable<Optional<TooltipData>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        IAspectDataProvider copyProvider = (IAspectDataProvider) (Object) stack;
        AspectData aspectData = copyProvider.aspectslib$getAspectData();

        if (aspectData == null || aspectData.isEmpty()) {
            return;
        }

        cir.setReturnValue(Optional.of(new AspectTooltipData(aspectData)));
    }
}