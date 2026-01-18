package com.dermememann.elytra.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.dermememann.elytra.TrinketSlotManager.tryHurtAndBreakTrinketChestSlot;

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

    @Inject(method = "doHurtEquipment", at = @At("TAIL"))
    private void doHurtEquipment(DamageSource damageSource, float damageAmount, EquipmentSlot[] slots, CallbackInfo ci) {
        if (damageAmount <= 0.0F) return;

        boolean includesChest = false;
        for (EquipmentSlot slot : slots) {
            if (slot == EquipmentSlot.CHEST) {
                includesChest = true;
                break;
            }
        }
        if (!includesChest) return;

        LivingEntity self = (LivingEntity) (Object) this;

        int finalDamage = (int) Math.max(1.0F, damageAmount / 4.0F);

        tryHurtAndBreakTrinketChestSlot(self, damageSource, finalDamage);
    }
}
