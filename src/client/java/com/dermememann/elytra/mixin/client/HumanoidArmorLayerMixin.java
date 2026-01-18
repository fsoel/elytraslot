package com.dermememann.elytra.mixin.client;

import com.dermememann.elytra.TrinketChestState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState> {

    @Invoker("renderArmorPiece")
    protected abstract void invokeRenderArmorPiece(PoseStack poseStack, SubmitNodeCollector nodeCollector, ItemStack item, EquipmentSlot slot, int packedLight, S renderState);

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("TAIL"))
    private void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, S humanoidRenderState, float f, float g, CallbackInfo ci) {
        ItemStack chestTrinket = ((TrinketChestState) humanoidRenderState).elytra$getTrinketChestEquipment();
        if (chestTrinket == null || chestTrinket.isEmpty()) {
            return;
        }

        if (humanoidRenderState.chestEquipment.getItem() == ItemStack.EMPTY.getItem() || humanoidRenderState.chestEquipment.getItem() == Items.ELYTRA) {
            invokeRenderArmorPiece(poseStack, submitNodeCollector, chestTrinket, EquipmentSlot.CHEST, i, humanoidRenderState);
        }
    }
}
