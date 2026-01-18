package com.dermememann.elytra;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public final class TrinketChestplateClient {

    private static final String TRINKET_GROUP = "chest";
    private static final String TRINKET_SLOT = "chestplate";

    private static ItemStack last = ItemStack.EMPTY;
    private static boolean initial = true;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) return;

            LivingEntity player = client.player;
            if (player == null) return;

            ItemStack now = getTrinketChestStack(player);

            if (ItemStack.isSameItem(now, last)) return;

            if (initial) {
                initial = false;
                last = now.copy();
                return;
            }

            playEquipSound(now, player);
            last = now.copy();
        });
    }

    private static void playEquipSound(ItemStack itemStack, LivingEntity livingEntity) {
        if (itemStack.isEmpty()) return;

        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return;

        SoundEvent sound = equippable.equipSound().value();

        livingEntity.playSound(sound, 1.0F, 1.0F);
    }

    private static ItemStack getTrinketChestStack(LivingEntity livingEntity) {
        return TrinketsApi.getTrinketComponent(livingEntity)
                .map(comp -> {
                    var groupMap = comp.getInventory().get(TRINKET_GROUP);
                    if (groupMap == null) return ItemStack.EMPTY;

                    var inv = groupMap.get(TRINKET_SLOT);
                    if (inv == null) return ItemStack.EMPTY;

                    return inv.getItem(0);
                })
                .orElse(ItemStack.EMPTY);
    }
}
