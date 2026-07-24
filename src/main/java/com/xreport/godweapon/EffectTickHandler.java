package com.xreport.godweapon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class EffectTickHandler {

    private static final Set<String> flyingPlayers = new HashSet<>();
    private static final Queue<BlockPos> mineQueue = new ArrayDeque<>();
    private static Player mineOwner;

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack weapon = GodWeaponItem.findInInventory(player);
        String name = player.getName().getString();

        boolean flying = (weapon != null && GodWeaponItem.isEnabled(weapon, "flight"));

        if (flying) {
            flyingPlayers.add(name);
            player.getAbilities().mayfly = true;
        }

        if (weapon != null) {
            if (GodWeaponItem.isEnabled(weapon, "nightvision")) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
            }
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0, false, false));
            if (GodWeaponItem.isEnabled(weapon, "repel")) {
                repelEntities(player, weapon);
            }
        }

        if (!flying && flyingPlayers.contains(name)) {
            flyingPlayers.remove(name);
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }

        if (player == mineOwner) {
            int batch = Math.max(50, mineQueue.size() / 5);
            while (!mineQueue.isEmpty() && batch > 0) {
                BlockPos pos = mineQueue.poll();
                BlockState state = player.level().getBlockState(pos);
                if (!state.isAir() && state.getDestroySpeed(player.level(), pos) >= 0
                        && !pos.equals(player.blockPosition())) {
                    player.level().destroyBlock(pos, true, player);
                }
                batch--;
            }
            if (mineQueue.isEmpty()) mineOwner = null;
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        ItemStack weapon = GodWeaponItem.findInInventory(player);
        if (weapon == null) return;
        if (GodWeaponItem.isEnabled(weapon, "invincible")) event.setCanceled(true);
        if (GodWeaponItem.isEnabled(weapon, "flight")
                && event.getSource().getMsgId().equals("fall")) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        ItemStack weapon = GodWeaponItem.findInInventory(player);
        if (weapon == null || !GodWeaponItem.isEnabled(weapon, "invincible")) return;

        Entity source = event.getSource().getDirectEntity();
        LivingEntity attacker = null;
        if (source instanceof Arrow arrow && arrow.getOwner() instanceof LivingEntity) {
            attacker = (LivingEntity) arrow.getOwner();
        } else if (source instanceof LivingEntity && source != player) {
            attacker = (LivingEntity) source;
        }
        if (attacker == null) return;
        player.attack(attacker);
        float dmg = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.3F;
        player.setHealth(Math.min(player.getHealth() + dmg, player.getMaxHealth()));
    }

    private static void repelEntities(Player player, ItemStack weapon) {
        int radius = GodWeaponItem.getRadius(weapon, "repelRadius");
        AABB aabb = player.getBoundingBox().inflate(radius);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class, aabb, e -> e != player);
        Vec3 center = player.position();
        for (LivingEntity entity : entities) {
            Vec3 dir = entity.position().subtract(center).normalize();
            entity.setDeltaMovement(entity.getDeltaMovement().add(dir.scale(0.5)));
            entity.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        ItemStack weapon = GodWeaponItem.findInInventory(player);
        if (weapon == null || !GodWeaponItem.isEnabled(weapon, "veinminer")) return;
        if (player.getMainHandItem().getItem() instanceof GodWeaponItem) {
            event.setCanceled(true);
            mineQueue.clear();
            int radius = GodWeaponItem.getRadius(weapon, "mineRadius");
            BlockPos center = player.blockPosition();
            for (int x = -radius; x <= radius; x++)
                for (int y = -radius; y <= radius; y++)
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        if (pos.equals(player.blockPosition())) continue;
                        BlockState state = player.level().getBlockState(pos);
                        if (state.isAir()) continue;
                        if (state.getDestroySpeed(player.level(), pos) < 0) continue;
                        mineQueue.add(pos.immutable());
                    }
            mineOwner = player;
        }
    }
}
