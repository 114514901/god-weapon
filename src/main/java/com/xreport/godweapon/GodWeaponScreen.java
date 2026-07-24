package com.xreport.godweapon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GodWeaponScreen extends Screen {

    private final ItemStack stack;
    private static final int BTN_W = 150;
    private static final int BTN_H = 20;

    public GodWeaponScreen(ItemStack stack) {
        super(Component.literal("God Weapon 能力菜单"));
        this.stack = stack;
    }

    @Override
    protected void init() {
        int cx = width / 2 - BTN_W / 2;
        int y = 40;

        addRenderableWidget(Button.builder(statusText("invincible", "无敌"), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("invincible"));
            GodWeaponItem.toggle(stack, "invincible");
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W, BTN_H).build());
        y += 24;

        addRenderableWidget(Button.builder(statusText("flight", "飞行"), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("flight"));
            GodWeaponItem.toggle(stack, "flight");
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W, BTN_H).build());
        y += 24;

        addRenderableWidget(Button.builder(statusText("nightvision", "夜视"), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("nightvision"));
            GodWeaponItem.toggle(stack, "nightvision");
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W, BTN_H).build());
        y += 24;

        addRenderableWidget(Button.builder(statusText("veinminer", "范围挖掘"), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("veinminer"));
            GodWeaponItem.toggle(stack, "veinminer");
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W, BTN_H).build());
        y += 24;

        addRenderableWidget(Button.builder(statusText("repel", "生物排斥"), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("repel"));
            GodWeaponItem.toggle(stack, "repel");
            rebuildWidgets();
        }).pos(cx, y).size(BTN_W, BTN_H).build());
        y += 36;

        addRenderableWidget(Button.builder(Component.literal("关闭"), b -> onClose())
                .pos(cx, y).size(BTN_W, BTN_H).build());
    }

    private Component statusText(String key, String label) {
        boolean on = GodWeaponItem.isEnabled(stack, key);
        return Component.literal((on ? "§a✓ " : "§c✗ ") + label);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, "God Weapon 能力菜单", width / 2, 15, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
