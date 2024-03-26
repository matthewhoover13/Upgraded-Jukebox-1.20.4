package net.hoover.musicplayer.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item TEST_MUSIC_DISC = registerItem("music_disc_test", new MusicDiscItem(1, ModSounds.MUSIC_DISC_TEST, new FabricItemSettings().maxCount(1), 70));

    private static void addItemsToUtilitiesItemGroup(FabricItemGroupEntries entries) {
        entries.add(TEST_MUSIC_DISC);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MusicPlayer.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MusicPlayer.LOGGER.info("Registering Items for " + MusicPlayer.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToUtilitiesItemGroup);
    }
}
