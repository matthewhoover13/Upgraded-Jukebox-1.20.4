package net.hoover.upgradedjukebox.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hoover.upgradedjukebox.UpgradedJukebox;
import net.hoover.upgradedjukebox.networking.packet.*;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SKIP_SONG_ID = new Identifier(UpgradedJukebox.MOD_ID, "skip_song");
    public static final Identifier CHECK_SHUFFLE_BOX_ID = new Identifier(UpgradedJukebox.MOD_ID, "check_shuffle_box");
    public static final Identifier CHECK_PAUSE_BOX_ID = new Identifier(UpgradedJukebox.MOD_ID, "check_pause_box");
    public static final Identifier CHECK_LOOP_BOX_ID = new Identifier(UpgradedJukebox.MOD_ID, "check_loop_box");

    public static final Identifier RECEIVE_PAUSE_PACKET_ID = new Identifier(UpgradedJukebox.MOD_ID, "receive_pause_packet");
    public static final Identifier RECEIVE_RESUME_PACKET_ID = new Identifier(UpgradedJukebox.MOD_ID, "receive_resume_packet");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SKIP_SONG_ID, SkipSongC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CHECK_SHUFFLE_BOX_ID, CheckShuffleBoxC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CHECK_PAUSE_BOX_ID, CheckPauseBoxC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CHECK_LOOP_BOX_ID, CheckLoopBoxC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_PAUSE_PACKET_ID, PauseSoundS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_RESUME_PACKET_ID, ResumeSoundS2CPacket::receive);
    }
}
