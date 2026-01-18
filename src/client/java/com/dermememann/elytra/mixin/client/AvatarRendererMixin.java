package com.dermememann.elytra.mixin.client;

import com.dermememann.elytra.TrinketChestState;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {

    @Unique
    private static final String TRINKET_GROUP = "chest";

    @Unique
    private static final String TRINKET_SLOT  = "chestplate";

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void extractRenderState(AvatarlikeEntity avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo ci) {
        ItemStack trinketChestEquipment = getTrinketChestStack(avatar);
        ((TrinketChestState) avatarRenderState).elytra$setTrinketChestEquipment(trinketChestEquipment);
    }

    @Unique
    private ItemStack getTrinketChestStack(AvatarlikeEntity avatarlikeEntity) {
        var trinketComponent = TrinketsApi.getTrinketComponent((LivingEntity) avatarlikeEntity);
        if (trinketComponent.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return trinketComponent.map(comp -> {
                var groupMap = comp.getInventory().get(TRINKET_GROUP);
                if (groupMap == null) return ItemStack.EMPTY;

                var inv = groupMap.get(TRINKET_SLOT);
                if (inv == null) return ItemStack.EMPTY;

                ItemStack stack = inv.getItem(0);
                if (!stack.isEmpty()) {
                    return stack;
                }
                return ItemStack.EMPTY;
            })
            .orElse(ItemStack.EMPTY);
    }
}
