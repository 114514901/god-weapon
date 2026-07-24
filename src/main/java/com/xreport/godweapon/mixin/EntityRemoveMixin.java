package com.xreport.godweapon.mixin;

import com.xreport.godweapon.GodWeaponItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityRemoveMixin {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player && !player.level().isClientSide) {
            var weapon = GodWeaponItem.findInInventory(player);
            if (weapon != null && GodWeaponItem.isEnabled(weapon, "invincible")) {
                ci.cancel();
            }
        }
    }
}
