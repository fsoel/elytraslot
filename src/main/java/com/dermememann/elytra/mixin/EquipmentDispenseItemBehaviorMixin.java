package com.dermememann.elytra.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EquipmentDispenseItemBehavior.class)
public class EquipmentDispenseItemBehaviorMixin {

    @Unique
    private static final String TRINKET_GROUP = "chest";
    @Unique
    private static final String TRINKET_SLOT  = "chestplate";

    @Inject(method = "dispenseEquipment", at = @At("HEAD"), cancellable = true)
    private static void dispenseEquipment(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        Equippable equippable = item.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return;

        if (equippable.slot() != EquipmentSlot.CHEST) return;

        if (item.is(Items.ELYTRA)) return;

        BlockPos frontPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> targets = blockSource.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(frontPos),
                e -> e.canEquipWithDispenser(item)
        );

        if (targets.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        LivingEntity target = targets.getFirst();

        if (isTrinketChestSlotEmpty(target)) {
            ItemStack one = item.split(1);
            if (tryInsertIntoTrinketChestSlot(target, one)) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }

    @Unique
    private static boolean isTrinketChestSlotEmpty(LivingEntity entity) {
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

    @Unique
    private static boolean tryInsertIntoTrinketChestSlot(LivingEntity entity, ItemStack stackToInsert) {
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
}
