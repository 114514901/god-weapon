package com.xreport.godweapon;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BasicLoliItem extends DiggerItem {

    public BasicLoliItem() {
        super(1, -2.8f, Tiers.DIAMOND, BlockTags.MINEABLE_WITH_PICKAXE,
                new Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant());
    }

    @Override
    public boolean isBarVisible(ItemStack stack) { return false; }

    @Override
    public int getMaxDamage(ItemStack stack) { return 0; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§e无限耐久多功能工具"));
        tooltip.add(Component.literal("§7可合成升级为氪金萝莉"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        return isCorrectToolForDrops(stack, state) ? 8.0f : 1.0f;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) { return false; }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack,
                                                ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
