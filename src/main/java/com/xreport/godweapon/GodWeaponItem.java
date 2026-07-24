package com.xreport.godweapon;

import net.minecraft.core.BlockPos;
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
        tooltip.add(Component.literal("§d===== 氪金萝莉 ====="));
        tooltip.add(Component.literal("§e右键§7清除实体 §e左键§7范围挖掘"));
        tooltip.add(Component.literal("§eH§7打开能力配置"));
        tooltip.add(Component.literal(""));
        addStatus(tooltip, stack, "invincible", "§c无敌");
        addStatus(tooltip, stack, "flight", "§b飞行");
        addStatus(tooltip, stack, "nightvision", "§d夜视");
        addStatusWithRange(tooltip, stack, "veinminer", "§6范围挖掘", "mineRadius");
        addStatusWithRange(tooltip, stack, "repel", "§a生物排斥", "repelRadius");
        addStatus(tooltip, stack, "stealth", "§8隐身");
        addStatus(tooltip, stack, "stealth_enhanced", "§7增强隐身");
        tooltip.add(Component.literal("§7清除范围: §f" + getRadius(stack, "clearRadius")));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    private static void addStatus(List<Component> t, ItemStack s, String k, String label) {
        t.add(Component.literal(label + ": " + (isEnabled(s, k) ? "§a●" : "§8●")));
    }

    private static void addStatusWithRange(List<Component> t, ItemStack s, String k, String label, String rk) {
        t.add(Component.literal(label + ": " + (isEnabled(s, k) ? "§a●" : "§8●")
                + " §7[" + getRadius(s, rk) + "]"));
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (!player.level().isClientSide && !player.isCreative()) {
            player.level().destroyBlock(pos, true, player);
        }
        return true;
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
