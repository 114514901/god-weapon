package com.xreport.godweapon;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("loli_reborn")
public class GodWeaponMod {

    public static final String MODID = "loli_reborn";

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<Item> LOLI =
            ITEMS.register("loli_pickaxe", GodWeaponItem::new);
    public static final RegistryObject<Item> BASIC_LOLI =
            ITEMS.register("basic_loli", BasicLoliItem::new);

    public static final RegistryObject<RecipeSerializer<?>> LOLI_UPGRADE_SERIALIZER =
            SERIALIZERS.register("loli_upgrade",
                    () -> new SimpleCraftingRecipeSerializer<>(LoliUpgradeRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> LOLI_MATERIAL_SERIALIZER =
            SERIALIZERS.register("loli_material",
                    LoliMaterialRecipe.Serializer::new);

    public GodWeaponMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SERIALIZERS.register(bus);
        MinecraftForge.EVENT_BUS.register(new KeyBindingHandler());
        NetworkHandler.register();
    }
}

