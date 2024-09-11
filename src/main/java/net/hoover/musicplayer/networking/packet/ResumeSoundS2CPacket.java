package net.hoover.musicplayer.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.hoover.musicplayer.util.ISoundManagerResumeSoundInjector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;

public class ResumeSoundS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ISoundManagerResumeSoundInjector soundManager = (ISoundManagerResumeSoundInjector) client.getSoundManager();
        soundManager.resumeSound(buf.readIdentifier(), buf.readEnumConstant(SoundCategory.class), buf.readBlockPos());
    }
}
