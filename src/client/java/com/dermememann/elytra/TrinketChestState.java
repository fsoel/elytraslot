package com.dermememann.elytra;

import net.minecraft.world.item.ItemStack;

public interface TrinketChestState {
    ItemStack elytra$getTrinketChestEquipment();
    void elytra$setTrinketChestEquipment(ItemStack stack);
}
