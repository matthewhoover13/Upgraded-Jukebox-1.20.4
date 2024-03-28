package net.hoover.musicplayer;

import net.fabricmc.api.ClientModInitializer;
import net.hoover.musicplayer.screen.ModScreenHandlers;
import net.hoover.musicplayer.screen.MusicPlayerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MusicPlayerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.MUSIC_PLAYER_SCREEN_HANDLER, MusicPlayerScreen::new);
    }
}
