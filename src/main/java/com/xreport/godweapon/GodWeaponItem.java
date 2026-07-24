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
            clearEntities(level, player);
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.success(stack);
    }

    private void clearEntities(Level level, Player player) {
        int radius = getRadius(player.getMainHandItem(), "clearRadius");
        AABB aabb = player.getBoundingBox().inflate(radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player);
        for (LivingEntity entity : entities) {
            if (entity instanceof ServerPlayer target &&
                    (target.isCreative() || target.isSpectator())) continue;
            try {
                entity.hurt(player.damageSources().playerAttack(player), Float.MAX_VALUE);
            } catch (Exception ignored) {}
            if (entity.isAlive() && !entity.isRemoved()) {
                entity.setRemoved(Entity.RemovalReason.KILLED);
            }
        }
        player.displayClientMessage(
                Component.literal("§c清除了 " + entities.size() + " 个实体"), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§e右键: §c清除附近所有实体"));
        tooltip.add(Component.literal("§e左键: §6范围挖掘"));
        tooltip.add(Component.literal("§e手持按 H: §b打开能力菜单"));
        tooltip.add(Component.literal("§a=== 当前状态 ==="));
        tooltip.add(Component.literal("§7无敌: " + (isEnabled(stack, "invincible") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7飞行: " + (isEnabled(stack, "flight") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7夜视: " + (isEnabled(stack, "nightvision") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7范围挖掘: " + (isEnabled(stack, "veinminer") ? "§a开启" : "§c关闭")));
        tooltip.add(Component.literal("§7生物排斥: " + (isEnabled(stack, "repel") ? "§a开启" : "§c关闭")));
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

    public static int getRadius(ItemStack stack, String key) {
        return Math.max(1, stack.getOrCreateTag().getInt(key));
    }

    public static void setRadius(ItemStack stack, String key, int value) {
        stack.getOrCreateTag().putInt(key, value);
    }

    public static ItemStack findInInventory(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof GodWeaponItem) return stack;
        }
        if (player.getOffhandItem().getItem() instanceof GodWeaponItem) {
            return player.getOffhandItem();
        }
        return null;
    }
}
