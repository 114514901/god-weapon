package com.xreport.godweapon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class NetworkHandler {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("loli_reborn", "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.registerMessage(0, TogglePacket.class,
                TogglePacket::encode, TogglePacket::decode, TogglePacket::handle);
        CHANNEL.registerMessage(1, CycleRadiusPacket.class,
                CycleRadiusPacket::encode, CycleRadiusPacket::decode, CycleRadiusPacket::handle);
    }

    public record TogglePacket(String key) {
        public void encode(FriendlyByteBuf buf) { buf.writeUtf(key); }
        public static TogglePacket decode(FriendlyByteBuf buf) {
            return new TogglePacket(buf.readUtf());
        }
        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof GodWeaponItem) {
                    GodWeaponItem.toggle(stack, key);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public record CycleRadiusPacket(String key, int value) {
        public void encode(FriendlyByteBuf buf) { buf.writeUtf(key); buf.writeInt(value); }
        public static CycleRadiusPacket decode(FriendlyByteBuf buf) {
            return new CycleRadiusPacket(buf.readUtf(), buf.readInt());
        }
        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof GodWeaponItem) {
                    GodWeaponItem.setRadius(stack, key, value);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
