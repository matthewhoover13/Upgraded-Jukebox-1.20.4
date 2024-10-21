package net.hoover.upgradedjukebox.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.hoover.upgradedjukebox.UpgradedJukebox;
import net.hoover.upgradedjukebox.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<UpgradedJukeboxBlockEntity> UPGRADED_JUKEBOX_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(UpgradedJukebox.MOD_ID, "upgraded_jukebox_be"),
                    FabricBlockEntityTypeBuilder.create(UpgradedJukeboxBlockEntity::new,
                            ModBlocks.UPGRADED_JUKEBOX).build());

    public static void registerBlockEntities() {
        UpgradedJukebox.LOGGER.info("Registering Block Entities for " + UpgradedJukebox.MOD_ID);
    }
}
