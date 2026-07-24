package com.xreport.godweapon;

import net.minecraft.world.item.Item;
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

    public static final RegistryObject<Item> LOLI =
            ITEMS.register("loli_pickaxe", GodWeaponItem::new);

    public GodWeaponMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        MinecraftForge.EVENT_BUS.register(new KeyBindingHandler());
        NetworkHandler.register();
    }
}
