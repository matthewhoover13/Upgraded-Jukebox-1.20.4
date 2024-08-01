package net.hoover.musicplayer.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.hoover.musicplayer.block.entity.MusicPlayerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class SkipSongC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos blockPos = buf.readBlockPos();
        ServerWorld world = player.getServerWorld();
        BlockEntity blockEntity = world.getWorldChunk(blockPos).getBlockEntity(blockPos);
        if (blockEntity instanceof MusicPlayerBlockEntity) {
            ((MusicPlayerBlockEntity) blockEntity).skipCurrentSong();
        }
    }
}
