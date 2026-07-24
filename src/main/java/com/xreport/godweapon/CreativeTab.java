package com.xreport.godweapon;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "loli_reborn");

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("氪金萝莉-重生"))
                    .icon(() -> new ItemStack(GodWeaponMod.LOLI.get()))
                    .displayItems((params, output) -> {
                        output.accept(GodWeaponMod.LOLI.get());
                        output.accept(GodWeaponMod.BASIC_LOLI.get());
                    })
                    .build());
}
