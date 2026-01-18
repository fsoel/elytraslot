package com.dermememann.elytra.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.dermememann.elytra.TrinketSlotManager.*;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);

        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return;

        if (equippable.slot() != EquipmentSlot.CHEST) return;
        if (itemStack.is(Items.ELYTRA)) return;

        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResult.CONSUME);
            return;
        }

        boolean swapped = trySwapTrinketChestSlot(player, hand);

        cir.setReturnValue(swapped ? InteractionResult.SUCCESS : InteractionResult.PASS);
    }
}
