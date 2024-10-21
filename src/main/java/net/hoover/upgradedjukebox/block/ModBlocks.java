package net.hoover.upgradedjukebox.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.hoover.upgradedjukebox.UpgradedJukebox;
import net.hoover.upgradedjukebox.block.custom.UpgradedJukeboxBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block UPGRADED_JUKEBOX = registerBlock("upgraded_jukebox",
            new UpgradedJukeboxBlock(FabricBlockSettings.copyOf(Blocks.JUKEBOX).nonOpaque().luminance(state -> 7)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(UpgradedJukebox.MOD_ID, name), block);
    }

    public static void registerModBlocks() {
        UpgradedJukebox.LOGGER.info("Registering Blocks for " + UpgradedJukebox.MOD_ID);
    }
}
