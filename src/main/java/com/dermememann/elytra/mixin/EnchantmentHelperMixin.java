package com.dermememann.elytra.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.dermememann.elytra.TrinketSlotManager.getTrinketChestSlotItemStack;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getRandomItemWith", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Util;getRandomSafe(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;",
            shift = At.Shift.BEFORE
    ), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void getRandomItemWith(DataComponentType<?> componentType, LivingEntity entity, Predicate<ItemStack> filter, CallbackInfoReturnable<Optional<EnchantedItemInUse>> cir, List<EnchantedItemInUse> list) {
        if (componentType == EnchantmentEffectComponents.REPAIR_WITH_XP) {
            ItemStack itemStack = getTrinketChestSlotItemStack(entity);
            if (itemStack.isEmpty()) return;
            if (!filter.test(itemStack)) return;

            ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

            for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
                Holder<Enchantment> holder = (Holder<Enchantment>) entry.getKey();

                if (holder.value().effects().has(componentType) && holder.value().matchingSlot(EquipmentSlot.CHEST)) {
                    list.add(new EnchantedItemInUse(itemStack, EquipmentSlot.CHEST, entity));
                }
            }
        }
    }
}
