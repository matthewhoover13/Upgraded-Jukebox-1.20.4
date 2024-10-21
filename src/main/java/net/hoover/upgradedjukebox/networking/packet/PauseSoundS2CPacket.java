package net.hoover.upgradedjukebox.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.hoover.upgradedjukebox.util.ISoundManagerPauseSoundInjector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;

public class PauseSoundS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ISoundManagerPauseSoundInjector soundManager = (ISoundManagerPauseSoundInjector) client.getSoundManager();
        soundManager.pauseSound(buf.readIdentifier(), buf.readEnumConstant(SoundCategory.class), buf.readBlockPos());
    }
}
