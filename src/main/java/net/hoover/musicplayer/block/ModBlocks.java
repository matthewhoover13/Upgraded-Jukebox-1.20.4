package net.hoover.musicplayer.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.block.custom.MusicPlayerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MUSIC_PLAYER_BLOCK = registerBlock("music_player_block",
            new MusicPlayerBlock(FabricBlockSettings.copyOf(Blocks.JUKEBOX).nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(MusicPlayer.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(MusicPlayer.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        MusicPlayer.LOGGER.info("Registering Blocks for " + MusicPlayer.MOD_ID);
    }
}
