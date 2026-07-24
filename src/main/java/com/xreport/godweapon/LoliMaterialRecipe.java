package com.xreport.godweapon;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class LoliMaterialRecipe extends CustomRecipe {

    private final Item material;
    private final String nbtKey;
    private final int maxLevel;

    public LoliMaterialRecipe(ResourceLocation id, CraftingBookCategory cat,
                               Item material, String nbtKey, int maxLevel) {
        super(id, cat);
        this.material = material;
        this.nbtKey = nbtKey;
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean hasLoli = false, hasMat = false;
        int loliLevel = -1;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() instanceof BasicLoliItem) {
                if (hasLoli) return false;
                hasLoli = true;
                loliLevel = s.getOrCreateTag().getInt(nbtKey);
            } else if (s.is(material) || s.is(material)) {
                if (hasMat) return false;
                hasMat = true;
            } else {
                return false;
            }
        }
        return hasLoli && hasMat && loliLevel < maxLevel;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack loli = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.getItem() instanceof BasicLoliItem) {
                loli = s.copy();
                break;
            }
        }
        if (!loli.isEmpty()) {
            int cur = loli.getOrCreateTag().getInt(nbtKey);
            loli.getTag().putInt(nbtKey, cur + 1);
        }
        return loli;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return w * h >= 2; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GodWeaponMod.LOLI_MATERIAL_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<LoliMaterialRecipe> {
        @Override
        public LoliMaterialRecipe fromJson(ResourceLocation id, JsonObject json) {
            Item mat = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("material").getAsString()));
            String key = json.get("nbt_key").getAsString();
            int max = json.get("max_level").getAsInt();
            return new LoliMaterialRecipe(id, CraftingBookCategory.MISC, mat, key, max);
        }

        @Override
        public LoliMaterialRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new LoliMaterialRecipe(id, CraftingBookCategory.MISC,
                    buf.readRegistryId(), buf.readUtf(), buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, LoliMaterialRecipe r) {
            buf.writeRegistryId(ForgeRegistries.ITEMS, r.material);
            buf.writeUtf(r.nbtKey);
            buf.writeInt(r.maxLevel);
        }
    }
}
