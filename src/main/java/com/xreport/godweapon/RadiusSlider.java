package com.xreport.godweapon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class RadiusSlider extends AbstractWidget {

    private static final int[] VALUES = {4, 8, 16, 32};
    private final ItemStack stack;
    private final String key;
    private int valueIndex;

    public RadiusSlider(int x, int y, int width, int height, ItemStack stack, String key) {
        super(x, y, width, height, Component.empty());
        this.stack = stack;
        this.key = key;
        int val = GodWeaponItem.getRadius(stack, key);
        this.valueIndex = indexOf(val);
    }

    private int indexOf(int val) {
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i] == val) return i;
        }
        return 0;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        valueIndex = (valueIndex + 1) % VALUES.length;
        GodWeaponItem.setRadius(stack, key, VALUES[valueIndex]);
        NetworkHandler.CHANNEL.sendToServer(
                new NetworkHandler.CycleRadiusPacket(key, VALUES[valueIndex]));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int val = VALUES[valueIndex];
        double fill = (double) (valueIndex + 1) / VALUES.length;
        int fillW = (int) (width * fill);

        graphics.fill(getX(), getY(), getX() + fillW, getY() + height, 0xFF00AA00);
        graphics.fill(getX() + fillW, getY(), getX() + width, getY() + height, 0xFF444444);
        graphics.renderOutline(getX(), getY(), width, height, 0xFFAAAAAA);

        graphics.drawCenteredString(Minecraft.getInstance().font,
                Component.literal("§f范围: " + val),
                getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFF);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isHovered) {
            double pos = (mouseX - getX()) / (double) width;
            valueIndex = Mth.clamp((int) (pos * VALUES.length), 0, VALUES.length - 1);
            GodWeaponItem.setRadius(stack, key, VALUES[valueIndex]);
        NetworkHandler.CHANNEL.sendToServer(
                new NetworkHandler.CycleRadiusPacket(key, VALUES[valueIndex]));
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
