package com.dermememann.elytra;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
}
