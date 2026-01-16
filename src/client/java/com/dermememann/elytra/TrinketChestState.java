package com.dermememann.elytra;

import net.minecraft.world.item.ItemStack;

public interface TrinketChestState {
    ItemStack getTrinketChestEquipment();
    void setTrinketChestEquipment(ItemStack stack);
}
