package com.xreport.godweapon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EffectTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        ItemStack weapon = findWeapon(player);
        if (weapon != null) {
            GodWeaponItem.applyEffects(player, weapon);
        } else {
            GodWeaponItem.clearEffects(player, ItemStack.EMPTY);
        }
    }

    private static ItemStack findWeapon(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof GodWeaponItem) return stack;
        }
        if (player.getOffhandItem().getItem() instanceof GodWeaponItem) {
            return player.getOffhandItem();
        }
        return null;
    }
}
