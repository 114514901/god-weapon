package com.xreport.godweapon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GodWeaponScreen extends Screen {

    private final ItemStack stack;
    private static final int BTN_W = 120;
    private static final int BTN_H = 20;
    private static final int SLIDER_W = 60;

    public GodWeaponScreen(ItemStack stack) {
        super(Component.literal("氪金萝莉 能力菜单"));
        this.stack = stack;
    }

    @Override
    protected void init() {
        int cx = width / 2 - BTN_W / 2;
        int y = 40;

        addToggle(cx, y, "invincible", "无敌");
        y += 24;
        addToggle(cx, y, "flight", "飞行");
        y += 24;
        addToggle(cx, y, "nightvision", "夜视");
        y += 24;
        addRadius(cx, y, "veinminer", "mineRadius", "范围挖掘");
        y += 24;
        addRadius(cx, y, "repel", "repelRadius", "生物排斥");
        y += 24;
        addSliderOnly(cx, y, "clearRadius", "清除范围");
        y += 36;

        addRenderableWidget(Button.builder(Component.literal("关闭"), b -> onClose())
                .pos(cx, y).size(BTN_W + SLIDER_W + 4, BTN_H).build());
    }

    private void addToggle(int cx, int y, String key, String label) {
        addRenderableWidget(Button.builder(statusText(key, label), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket(key));
            GodWeaponItem.toggle(stack, key);
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W + SLIDER_W + 4, BTN_H).build());
    }

    private void addRadius(int cx, int y, String key, String radiusKey, String label) {
        addRenderableWidget(Button.builder(statusText(key, label), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket(key));
            GodWeaponItem.toggle(stack, key);
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W, BTN_H).build());

        addRenderableWidget(new RadiusSlider(
                cx + BTN_W + 4, y, SLIDER_W, BTN_H, stack, radiusKey));
    }

    private void addSliderOnly(int cx, int y, String radiusKey, String label) {
        addRenderableWidget(Button.builder(Component.literal("§7" + label + ":"), b -> {})
                .pos(cx, y).size(BTN_W, BTN_H).build());
        addRenderableWidget(new RadiusSlider(
                cx + BTN_W + 4, y, SLIDER_W, BTN_H, stack, radiusKey));
    }

    private Component statusText(String key, String label) {
        boolean on = GodWeaponItem.isEnabled(stack, key);
        return Component.literal((on ? "§a✓ " : "§c✗ ") + label);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, "氪金萝莉 能力菜单", width / 2, 15, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
