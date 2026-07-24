package com.xreport.godweapon;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class BasicLoliItem extends Item {

    public BasicLoliItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (player.isShiftKeyDown()) {
                boolean flying = stack.getOrCreateTag().getBoolean("flight");
                stack.getOrCreateTag().putBoolean("flight", !flying);
                player.displayClientMessage(
                        Component.literal("§b飞行: " + (!flying ? "§a开启" : "§c关闭")), true);
            } else {
                clearEntities(level, player);
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    private void clearEntities(Level level, Player player) {
        AABB aabb = player.getBoundingBox().inflate(8);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != player);
        for (LivingEntity entity : entities) {
            if (entity instanceof ServerPlayer target &&
                    (target.isCreative() || target.isSpectator())) continue;
            try { entity.hurt(player.damageSources().playerAttack(player), Float.MAX_VALUE); }
            catch (Exception ignored) {}
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
        tooltip.add(Component.literal("§e右键: §c清除附近实体"));
        tooltip.add(Component.literal("§e潜行+右键: §b切换飞行"));
        tooltip.add(Component.literal("§7飞行: " + (stack.getOrCreateTag().getBoolean("flight")
                ? "§a开启" : "§c关闭")));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack,
                                                ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
