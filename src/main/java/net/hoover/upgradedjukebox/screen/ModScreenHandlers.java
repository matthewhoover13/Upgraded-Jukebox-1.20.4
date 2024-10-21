package net.hoover.upgradedjukebox.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.hoover.upgradedjukebox.UpgradedJukebox;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<UpgradedJukeboxScreenHandler> UPGRADED_JUKEBOX_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(UpgradedJukebox.MOD_ID, "upgraded_jukebox"),
                    new ExtendedScreenHandlerType<>(UpgradedJukeboxScreenHandler::new));

    public static void registerScreenHandlers() {
        UpgradedJukebox.LOGGER.info("Registering Screen Handlers for " + UpgradedJukebox.MOD_ID);
    }
}
