package com.dermememann.elytra;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;

public final class TrinketChestplate {

    private static final class Chestplate implements Trinket {

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(
                ItemStack stack,
                SlotReference slot,
                LivingEntity entity,
                Identifier identifier
        ) {
            var out = Multimaps.<Holder<Attribute>, AttributeModifier>newMultimap(
                    Maps.newLinkedHashMap(),
                    ArrayList::new
            );

            ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (modifiers == null) return out;

            modifiers.forEach(EquipmentSlot.CHEST, ((attributeHolder, attributeModifier) -> {
                Identifier id = Identifier.tryBuild(
                        identifier.getNamespace(),
                        identifier.getPath() + "/" + attributeModifier.id().getNamespace() + "/" + attributeModifier.id().getPath()
                );

                out.put(attributeHolder, new AttributeModifier(id, attributeModifier.amount(), attributeModifier.operation()));
            }));

            return out;
        }
    }

    public static void register() {
        Trinket trinket = new Chestplate();

        registerItem(trinket, Items.LEATHER_CHESTPLATE);
        registerItem(trinket, Items.CHAINMAIL_CHESTPLATE);
        registerItem(trinket, Items.COPPER_CHESTPLATE);
        registerItem(trinket, Items.IRON_CHESTPLATE);
        registerItem(trinket, Items.GOLDEN_CHESTPLATE);
        registerItem(trinket, Items.DIAMOND_CHESTPLATE);
        registerItem(trinket, Items.NETHERITE_CHESTPLATE);
    }

    private static void registerItem(Trinket trinket, Item item) {
        TrinketsApi.registerTrinket(item, trinket);
    }
}
