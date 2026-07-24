package com.xreport.godweapon;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LoliUpgradeRecipe extends CustomRecipe {

    public LoliUpgradeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean hasBasic = false, hasStar = false;
        int stars = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() instanceof BasicLoliItem) {
                if (hasBasic) return false;
                hasBasic = true;
            } else if (s.is(Items.NETHER_STAR)) {
                stars++;
            } else {
                return false;
            }
        }
        return hasBasic && stars == 4;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack basic = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.getItem() instanceof BasicLoliItem) {
                basic = s;
                break;
            }
        }
        ItemStack result = new ItemStack(GodWeaponMod.LOLI.get());
        if (basic.hasTag()) {
            result.setTag(basic.getTag().copy());
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 5;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GodWeaponMod.LOLI_UPGRADE_SERIALIZER.get();
    }
}
