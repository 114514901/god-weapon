package com.xreport.godweapon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class EffectTickHandler {

    private static final Set<String> flyingPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack weapon = GodWeaponItem.findInInventory(player);
        if (weapon != null) {
            String name = player.getName().getString();

            if (GodWeaponItem.isEnabled(weapon, "flight")) {
                if (!flyingPlayers.contains(name)) {
                    flyingPlayers.add(name);
                }
                player.getAbilities().mayfly = true;
            } else if (flyingPlayers.contains(name)) {
                flyingPlayers.remove(name);
                if (!player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                }
            }

            if (GodWeaponItem.isEnabled(weapon, "nightvision")) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.NIGHT_VISION, 400, 0, false, false));
            }
        } else {
            String name = player.getName().getString();
            if (flyingPlayers.contains(name)) {
                flyingPlayers.remove(name);
                if (!player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack weapon = GodWeaponItem.findInInventory(player);
        if (weapon != null && GodWeaponItem.isEnabled(weapon, "invincible")) {
            event.setCanceled(true);
        }
    }
}
