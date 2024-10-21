package net.hoover.upgradedjukebox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.hoover.upgradedjukebox.block.ModBlocks;
import net.hoover.upgradedjukebox.networking.ModMessages;
import net.hoover.upgradedjukebox.screen.ModScreenHandlers;
import net.hoover.upgradedjukebox.screen.UpgradedJukeboxScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class UpgradedJukeboxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.UPGRADED_JUKEBOX_SCREEN_HANDLER, UpgradedJukeboxScreen::new);
        ModMessages.registerS2CPackets();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.UPGRADED_JUKEBOX, RenderLayer.getTranslucent());
    }
}
