package net.hoover.musicplayer.sound;

import net.hoover.musicplayer.MusicPlayer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent MUSIC_DISC_TEST = registerSoundEvent("music_disc.test");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(MusicPlayer.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        MusicPlayer.LOGGER.info("Registering Sounds for " + MusicPlayer.MOD_ID);
    }
}
