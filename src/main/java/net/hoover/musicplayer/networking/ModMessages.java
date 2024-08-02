package net.hoover.musicplayer.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.networking.packet.CheckAutoplayBoxC2SPacket;
import net.hoover.musicplayer.networking.packet.CheckShuffleBoxC2SPacket;
import net.hoover.musicplayer.networking.packet.SkipSongC2SPacket;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SKIP_SONG_ID = new Identifier(MusicPlayer.MOD_ID, "skip_song");
    public static final Identifier CHECK_SHUFFLE_BOX_ID = new Identifier(MusicPlayer.MOD_ID, "check_shuffle_box");
    public static final Identifier CHECK_AUTOPLAY_BOX_ID = new Identifier(MusicPlayer.MOD_ID, "check_autoplay_box");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SKIP_SONG_ID, SkipSongC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CHECK_SHUFFLE_BOX_ID, CheckShuffleBoxC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CHECK_AUTOPLAY_BOX_ID, CheckAutoplayBoxC2SPacket::receive);
    }

    public static void registerS2CPackets() {

    }
}
