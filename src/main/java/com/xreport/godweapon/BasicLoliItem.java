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
        tooltip.add(Component.literal("§d===== 普通萝莉 ====="));
        tooltip.add(Component.literal("§e万能工具 §7| §e可升级"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7=== 升级进度 ==="));
        addLevel(tooltip, stack, "digSpeed", 4, "挖掘速度");
        addLevel(tooltip, stack, "digLevel", 3, "挖掘等级");
        addLevel(tooltip, stack, "digRange", 4, "挖掘范围");
        addLevel(tooltip, stack, "attackSpeed", 3, "攻击速度");
        addLevel(tooltip, stack, "fortune", 3, "时运");
        addLevel(tooltip, stack, "buff", 3, "药水效果");
        addLevel(tooltip, stack, "hitRange", 3, "攻击范围");
        addLevel(tooltip, stack, "dodge", 3, "闪避");
        addLevel(tooltip, stack, "antiInjury", 3, "反伤");
        addLevel(tooltip, stack, "fly", 1, "飞行");
        tooltip.add(Component.literal(""));
        boolean maxed = true;
        for (String k : new String[]{"digSpeed","digLevel","digRange","attackSpeed","fortune","buff","hitRange","dodge","antiInjury","fly"}) {
            if (stack.getOrCreateTag().getInt(k) < (k.equals("digSpeed")||k.equals("digRange")?4:k.equals("fly")?1:3)) {
                maxed = false; break;
            }
        }
        tooltip.add(Component.literal(maxed ? "§a✓ 可升级为氪金萝莉!" : "§7全满后可升级"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    private static void addLevel(List<Component> t, ItemStack s, String key, int max, String label) {
        int cur = s.getOrCreateTag().getInt(key);
        String color = cur == max ? "§a" : "§e";
        t.add(Component.literal("§7" + label + ": " + color + cur + "/" + max));
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
