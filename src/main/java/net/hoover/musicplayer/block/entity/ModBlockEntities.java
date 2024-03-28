package net.hoover.musicplayer.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MusicPlayerBlockEntity> MUSIC_PLAYER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MusicPlayer.MOD_ID, "music_player_be"),
                    FabricBlockEntityTypeBuilder.create(MusicPlayerBlockEntity::new,
                            ModBlocks.MUSIC_PLAYER_BLOCK).build());

    public static void registerBlockEntities() {
        MusicPlayer.LOGGER.info("Registering Block Entities for " + MusicPlayer.MOD_ID);
    }
}
