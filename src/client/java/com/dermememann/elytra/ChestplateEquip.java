package com.dermememann.elytra;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

public final class ChestplateEquip {

    public static void register() {
        Trinket soundTrinket = new Trinket() {

            @Override
            public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                if (!entity.level().isClientSide()) return;

                if (Minecraft.getInstance().player != entity) return;

                Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                if (equippable == null) return;

                SoundEvent sound = equippable.equipSound().value();

                entity.playSound(sound, 1.0F, 1.0F);
            }
        };

        registerItem(soundTrinket, Items.LEATHER_CHESTPLATE);
        registerItem(soundTrinket, Items.CHAINMAIL_CHESTPLATE);
        registerItem(soundTrinket, Items.COPPER_CHESTPLATE);
        registerItem(soundTrinket, Items.IRON_CHESTPLATE);
        registerItem(soundTrinket, Items.GOLDEN_CHESTPLATE);
        registerItem(soundTrinket, Items.DIAMOND_CHESTPLATE);
        registerItem(soundTrinket, Items.NETHERITE_CHESTPLATE);
    }

    private static void registerItem(Trinket trinket, Item item) {
        TrinketsApi.registerTrinket(item, trinket);
    }
}
