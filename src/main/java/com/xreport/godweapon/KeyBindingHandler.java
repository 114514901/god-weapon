package com.xreport.godweapon;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KeyBindingHandler {

    private static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.godweapon.open_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.godweapon"
    );

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (event.getKey() == GLFW.GLFW_KEY_H && event.getAction() == GLFW.GLFW_PRESS) {
            ItemStack mainHand = mc.player.getMainHandItem();
            if (mainHand.getItem() instanceof GodWeaponItem) {
                mc.setScreen(new GodWeaponScreen(mainHand));
            }
        }
    }
}
