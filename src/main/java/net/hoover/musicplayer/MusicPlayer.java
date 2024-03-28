package net.hoover.musicplayer;

import net.fabricmc.api.ModInitializer;

import net.hoover.musicplayer.block.ModBlocks;
import net.hoover.musicplayer.block.entity.ModBlockEntities;
import net.hoover.musicplayer.item.ModItems;
import net.hoover.musicplayer.screen.ModScreenHandlers;
import net.hoover.musicplayer.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicPlayer implements ModInitializer {
	public static final String MOD_ID = "musicplayer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModItems.registerModItems();
		ModScreenHandlers.registerScreenHandlers();
		ModSounds.registerSounds();
	}
}