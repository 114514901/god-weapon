package com.xreport.godweapon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GodWeaponScreen extends Screen {

    private final ItemStack stack;
    private int page;
    private static final int BTN_W = 140;
    private static final int BTN_H = 20;
    private static final int SLIDER_W = 60;
    private static final int ITEMS_PER_PAGE = 6;

    public GodWeaponScreen(ItemStack stack) {
        super(Component.literal("氪金萝莉 能力菜单"));
        this.stack = stack;
        this.page = 0;
    }

    @Override
    protected void init() {
        int cx = width / 2 - (BTN_W + SLIDER_W + 4) / 2;
        int[] y = {40};

        List<Runnable> items = buildItems(cx, y);
        int totalPages = (items.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, items.size());

        y[0] = 40;
        for (int i = start; i < end; i++) {
            items.get(i).run();
        }

        int bottomY = 40 + ITEMS_PER_PAGE * 24;
        if (page > 0) {
            addRenderableWidget(Button.builder(Component.literal("◀"), b -> {
                page--;
                rebuildWidgets();
            }).pos(cx - 60, bottomY).size(20, 20).build());
        }
        Component pageText = Component.literal("§7" + (page + 1) + "/" + totalPages);
        int pw = font.width(pageText) + 10;
        addRenderableWidget(Button.builder(pageText, b -> {}).pos(cx - pw / 2, bottomY).size(pw, 20).build());
        if (page < totalPages - 1) {
            addRenderableWidget(Button.builder(Component.literal("▶"), b -> {
                page++;
                rebuildWidgets();
            }).pos(cx + 60, bottomY).size(20, 20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("关闭"), b -> onClose())
                .pos(width / 2 - 40, bottomY).size(80, 20).build());
    }

    private List<Runnable> buildItems(int cx, int[] y) {
        List<Runnable> items = new ArrayList<>();
        items.add(() -> addToggle(cx, y, "invincible", "§c无敌"));
        items.add(() -> addToggle(cx, y, "flight", "§b飞行"));
        items.add(() -> addToggle(cx, y, "nightvision", "§d夜视"));
        items.add(() -> addRadius(cx, y, "veinminer", "mineRadius", "§6范围挖掘"));
        items.add(() -> addRadius(cx, y, "repel", "repelRadius", "§a生物排斥"));
        items.add(() -> addStealth(cx, y));
        items.add(() -> addSliderOnly(cx, y, "clearRadius", "§4清除范围"));
        return items;
    }

    private void addToggle(int cx, int[] y, String key, String label) {
        boolean on = GodWeaponItem.isEnabled(stack, key);
        Component text = Component.literal((on ? "§a✓ " : "§7✗ ") + label);
        addRenderableWidget(Button.builder(text, b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket(key));
            GodWeaponItem.toggle(stack, key);
            rebuildWidgets();
        }).pos(cx, y[0]).size(BTN_W + SLIDER_W + 4, BTN_H).build());
        y[0] += 24;
    }

    private void addRadius(int cx, int[] y, String key, String radiusKey, String label) {
        boolean on = GodWeaponItem.isEnabled(stack, key);
        Component text = Component.literal((on ? "§a✓ " : "§7✗ ") + label);
        addRenderableWidget(Button.builder(text, b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket(key));
            GodWeaponItem.toggle(stack, key);
            rebuildWidgets();
        }).pos(cx, y[0]).size(BTN_W, BTN_H).build());
        addRenderableWidget(new RadiusSlider(
                cx + BTN_W + 4, y[0], SLIDER_W, BTN_H, stack, radiusKey));
        y[0] += 24;
    }

    private void addStealth(int cx, int[] y) {
        boolean on = GodWeaponItem.isEnabled(stack, "stealth");
        addRenderableWidget(Button.builder(
                Component.literal((on ? "§a✓ " : "§7✗ ") + "§8隐身"), b -> {
            NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("stealth"));
            GodWeaponItem.toggle(stack, "stealth");
            rebuildWidgets();
        }).pos(cx, y[0]).size(BTN_W, BTN_H).build());

        boolean enhanced = GodWeaponItem.isEnabled(stack, "stealth_enhanced");
        Component cb = on
                ? Component.literal((enhanced ? "§a☑" : "§7☐") + " §7增强")
                : Component.literal("§8☐ §7增强");
        addRenderableWidget(Button.builder(cb, b -> {
            if (on) {
                NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.TogglePacket("stealth_enhanced"));
                GodWeaponItem.toggle(stack, "stealth_enhanced");
                rebuildWidgets();
            }
        }).pos(cx + BTN_W + 4, y[0]).size(60, BTN_H).build());
        y[0] += 24;
    }
        addRenderableWidget(Button.builder(Component.literal(label), b -> {})
                .pos(cx, y[0]).size(BTN_W, BTN_H).build());
        addRenderableWidget(new RadiusSlider(
                cx + BTN_W + 4, y[0], SLIDER_W, BTN_H, stack, radiusKey));
        y[0] += 24;
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
