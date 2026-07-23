package com.xreport.godweapon;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class GodWeaponItem extends Item {

    public GodWeaponItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (player.isShiftKeyDown() && isEnabled(stack, "veinminer")) {
                mineArea(level, player, stack);
            } else {
                clearEntities(level, player);
            }
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.success(stack);
    }

    private void mineArea(Level level, Player player, ItemStack stack) {
        int radius = 3;
        BlockPos center = player.blockPosition();
        int count = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (pos.equals(center)) continue;
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getDestroySpeed(level, pos) < 0) continue;
                    level.destroyBlock(pos, true, player);
                    count++;
                }
            }
        }
        player.displayClientMessage(
                Component.literal("§e挖掘了 " + count + " 个方块"), true);
    }

    private void clearEntities(Level level, Player player) {
        AABB aabb = player.getBoundingBox().inflate(16);
        List<Entity> entities = level.getEntities(player, aabb,
                e -> e != player && e instanceof LivingEntity);
        for (Entity entity : entities) {
            if (entity instanceof ServerPlayer target) {
                if (target.isCreative() || target.isSpectator()) continue;
            }
            entity.setRemoved(Entity.RemovalReason.KILLED);
        }
        player.displayClientMessage(
                Component.literal("§c清除了 " + entities.size() + " 个实体"), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§e右键: §c清除附近所有实体"));
        tooltip.add(Component.literal("§e潜行+右键: §6范围挖掘"));
        tooltip.add(Component.literal("§e手持按 H: §b打开能力菜单"));
        tooltip.add(Component.literal("§a=== 当前状态 ==="));
        tooltip.add(Component.literal("§7无敌: " + (isEnabled(stack, "invincible") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7飞行: " + (isEnabled(stack, "flight") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7夜视: " + (isEnabled(stack, "nightvision") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7范围挖掘: " + (isEnabled(stack, "veinminer") ? "§a开启" : "§c关闭")));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack,
                                                ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public static boolean isEnabled(ItemStack stack, String key) {
        return stack.getOrCreateTag().getBoolean(key);
    }

    public static void toggle(ItemStack stack, String key) {
        boolean current = isEnabled(stack, key);
        stack.getOrCreateTag().putBoolean(key, !current);
    }

    public static void applyEffects(Player player, ItemStack stack) {
        if (isEnabled(stack, "invincible")) {
            player.getAbilities().invulnerable = true;
        }
        if (isEnabled(stack, "flight")) {
            player.getAbilities().mayfly = true;
        }
        if (isEnabled(stack, "nightvision")) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }
    }

    public static void clearEffects(Player player, ItemStack stack) {
        if (!isEnabled(stack, "invincible")) {
            player.getAbilities().invulnerable = false;
        }
        if (!isEnabled(stack, "flight")) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }
    }
}
