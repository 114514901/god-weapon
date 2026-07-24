package com.xreport.godweapon.mixin;

import com.xreport.godweapon.GodWeaponItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(ServerLevel.class)
public class EntityFilterMixin {

    @ModifyVariable(method = "getEntities", at = @At("RETURN"), ordinal = 0)
    private List<Entity> filterStealth(List<Entity> entities) {
        return entities.stream().filter(e -> {
            if (e instanceof Player player) {
                var weapon = GodWeaponItem.findInInventory(player);
                return weapon == null || !GodWeaponItem.isEnabled(weapon, "stealth_enhanced");
            }
            return true;
        }).collect(Collectors.toList());
    }
}
