package net.hoover.musicplayer.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.networking.packet.SkipSongC2SPacket;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SKIP_SONG_ID = new Identifier(MusicPlayer.MOD_ID, "skip_song");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SKIP_SONG_ID, SkipSongC2SPacket::receive);
    }

    public static void registerS2CPackets() {

    }
}
