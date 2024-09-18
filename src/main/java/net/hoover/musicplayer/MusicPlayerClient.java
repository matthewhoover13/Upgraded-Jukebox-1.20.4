package net.hoover.musicplayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.hoover.musicplayer.block.ModBlocks;
import net.hoover.musicplayer.event.KeyInputHandler;
import net.hoover.musicplayer.networking.ModMessages;
import net.hoover.musicplayer.screen.ModScreenHandlers;
import net.hoover.musicplayer.screen.MusicPlayerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class MusicPlayerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.MUSIC_PLAYER_SCREEN_HANDLER, MusicPlayerScreen::new);
        KeyInputHandler.register();
        ModMessages.registerS2CPackets();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MUSIC_PLAYER_BLOCK, RenderLayer.getTranslucent());
    }
}
