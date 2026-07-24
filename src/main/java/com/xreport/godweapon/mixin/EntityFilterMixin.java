package com.xreport.godweapon.mixin;

import com.xreport.godweapon.GodWeaponItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityFilterMixin {

    @Mixin(Level.class)
    public static class LevelMixin {
        @ModifyVariable(method = "getEntities", at = @At("RETURN"), ordinal = 0)
        private List<Entity> filterStealth(List<Entity> list) {
            return filter(list);
        }
    }

    @Mixin(ServerLevel.class)
    public static class ServerLevelMixin {
        @ModifyVariable(method = "getEntities", at = @At("RETURN"), ordinal = 0)
        private List<Entity> filterStealth(List<Entity> list) {
            return filter(list);
        }
    }

    private static List<Entity> filter(List<Entity> list) {
        return list.stream().filter(e -> {
            if (e instanceof Player player) {
                var weapon = GodWeaponItem.findInInventory(player);
                return weapon == null || !GodWeaponItem.isEnabled(weapon, "stealth_enhanced");
            }
            return true;
        }).collect(Collectors.toList());
    }
}
