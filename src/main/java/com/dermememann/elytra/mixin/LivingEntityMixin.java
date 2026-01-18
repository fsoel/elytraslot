package com.dermememann.elytra.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private static final String TRINKET_GROUP = "chest";
    @Unique
    private static final String TRINKET_SLOT  = "chestplate";

    @Inject(method = "isEquippableInSlot", at = @At("HEAD"), cancellable = true)
    private static void isEquippableInSlot(ItemStack stack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot != EquipmentSlot.CHEST) {
            return;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            cir.setReturnValue(false);
            return;
        }

        cir.setReturnValue(stack.is(Items.ELYTRA));
    }

    @Inject(method = "canEquipWithDispenser", at = @At("HEAD"), cancellable = true)
    private void canEquipWithDispenser(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.isAlive() || self.isSpectator()) return;

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || !equippable.dispensable()) return;

        if (equippable.slot() != EquipmentSlot.CHEST) return;
        if (stack.is(Items.ELYTRA)) return;

        boolean hasRoom = TrinketsApi.getTrinketComponent(self)
                .map(comp -> {
                    var group = comp.getInventory().get(TRINKET_GROUP);
                    if (group == null) return false;
                    var inv = group.get(TRINKET_SLOT);
                    if (inv == null) return false;

                    ItemStack itemStack = inv.getItem(0);
                    return itemStack.isEmpty();
                })
                .orElse(false);

        cir.setReturnValue(hasRoom);
    }
}
