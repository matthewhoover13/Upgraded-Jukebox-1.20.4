package net.hoover.upgradedjukebox;

import net.fabricmc.api.ModInitializer;
import net.hoover.upgradedjukebox.block.ModBlocks;
import net.hoover.upgradedjukebox.block.entity.ModBlockEntities;
import net.hoover.upgradedjukebox.item.ModItems;
import net.hoover.upgradedjukebox.networking.ModMessages;
import net.hoover.upgradedjukebox.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradedJukebox implements ModInitializer {
	public static final String MOD_ID = "upgradedjukebox";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModItems.registerModItems();
		ModItems.registerModItemsToGroups();
		ModMessages.registerC2SPackets();
		ModScreenHandlers.registerScreenHandlers();
	}
}
