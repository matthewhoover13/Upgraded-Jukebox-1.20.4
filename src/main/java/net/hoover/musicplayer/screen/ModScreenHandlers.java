package net.hoover.musicplayer.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.hoover.musicplayer.MusicPlayer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MusicPlayerScreenHandler> MUSIC_PLAYER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(MusicPlayer.MOD_ID, "music_player"),
                    new ExtendedScreenHandlerType<>(MusicPlayerScreenHandler::new));

    public static void registerScreenHandlers() {
        MusicPlayer.LOGGER.info("Registering Screen Handlers for " + MusicPlayer.MOD_ID);
    }
}
