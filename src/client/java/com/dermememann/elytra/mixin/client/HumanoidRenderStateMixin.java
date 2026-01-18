package com.dermememann.elytra.mixin.client;

import com.dermememann.elytra.TrinketChestState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements TrinketChestState {

    @Unique
    private ItemStack trinketChestEquipment = ItemStack.EMPTY;

    @Override
    public ItemStack getTrinketChestEquipment() {
        return trinketChestEquipment;
    }

    @Override
    public void setTrinketChestEquipment(ItemStack stack) {
        trinketChestEquipment = (stack == null) ? ItemStack.EMPTY : stack;
    }
}
