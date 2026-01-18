package com.dermememann.elytra;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class TrinketSlotManager {

    private static final String TRINKET_GROUP = "chest";
    private static final String TRINKET_SLOT = "chestplate";

    public static boolean isTrinketChestSlotEmpty(LivingEntity entity) {
        var trinketComponent = TrinketsApi.getTrinketComponent(entity);
        if (trinketComponent.isEmpty()) {
            return true;
        }

        return trinketComponent.map(comp -> {
                    var groupMap = comp.getInventory().get(TRINKET_GROUP);
                    if (groupMap == null) return true;

                    var inv = groupMap.get(TRINKET_SLOT);
                    if (inv == null) return true;

                    ItemStack stack = inv.getItem(0);
                    return stack.isEmpty();
                })
                .orElse(true);
    }

    public static boolean tryInsertIntoTrinketChestSlot(LivingEntity entity, ItemStack stackToInsert) {
        var trinketComponent = TrinketsApi.getTrinketComponent(entity);
        if (trinketComponent.isEmpty()) {
            return false;
        }

        return trinketComponent.map(comp -> {
                    var groupMap = comp.getInventory().get(TRINKET_GROUP);
                    if (groupMap == null) return false;

                    var inv = groupMap.get(TRINKET_SLOT);
                    if (inv == null) return false;

                    ItemStack stack = inv.getItem(0);
                    if (stack.isEmpty()) {
                        inv.setItem(0, stackToInsert);
                        // TrinketChestArmorAttributes.sync(entity, stackToInsert);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public static boolean trySwapTrinketChestSlot(Player player, InteractionHand hand) {
        var trinketComponent = TrinketsApi.getTrinketComponent(player);
        if (trinketComponent.isEmpty()) {
            return false;
        }

        return trinketComponent.map(comp -> {
                    var groupMap = comp.getInventory().get(TRINKET_GROUP);
                    if (groupMap == null) return false;

                    var inv = groupMap.get(TRINKET_SLOT);
                    if (inv == null) return false;

                    ItemStack handStack = player.getItemInHand(hand);
                    if (handStack.isEmpty()) return false;

                    ItemStack oneItem = handStack.split(1);
                    ItemStack previousItem = inv.getItem(0);
                    inv.setItem(0, oneItem);
                    // TrinketChestArmorAttributes.sync(player, oneItem);

                    if (previousItem != null && !previousItem.isEmpty()) {
                        ItemStack inHandNow = player.getItemInHand(hand);

                        if (inHandNow.isEmpty()) {
                            player.setItemInHand(hand, previousItem);
                        } else if (!player.getInventory().add(previousItem)) {
                            player.drop(previousItem, false);
                        }
                    }

                    return true;
                })
                .orElse(false);
    }

    public static void tryHurtAndBreakTrinketChestSlot(LivingEntity entity, DamageSource source, int finalDamage) {
        var trinketComponent = TrinketsApi.getTrinketComponent(entity);
        if (trinketComponent.isEmpty()) {
            return;
        }

        trinketComponent.map(comp -> {
            var groupMap = comp.getInventory().get(TRINKET_GROUP);
            if (groupMap == null) return false;

            var inv = groupMap.get(TRINKET_SLOT);
            if (inv == null) return false;

            ItemStack stack = inv.getItem(0);
            if (stack.isEmpty()) return false;

            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if (equippable == null) return false;

            if (!equippable.damageOnHurt()) return false;
            if (!stack.isDamageableItem()) return false;
            if (!stack.canBeHurtBy(source)) return false;

            stack.hurtAndBreak(finalDamage, entity, EquipmentSlot.CHEST);

            inv.setItem(0, stack.isEmpty() ? ItemStack.EMPTY : stack);
            return true;
        });
    }
}
