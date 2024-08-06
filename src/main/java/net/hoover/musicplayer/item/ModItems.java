package net.hoover.musicplayer.item;

import net.hoover.musicplayer.MusicPlayer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MusicPlayer.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MusicPlayer.LOGGER.info("Registering Items for " + MusicPlayer.MOD_ID);
    }
}
