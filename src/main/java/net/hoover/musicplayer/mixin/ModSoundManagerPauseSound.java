package net.hoover.musicplayer.mixin;

import net.hoover.musicplayer.util.ISoundManagerPauseSoundInjector;
import net.hoover.musicplayer.util.ISoundSystemPauseSoundInjector;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundManager.class)
public class ModSoundManagerPauseSound implements ISoundManagerPauseSoundInjector {
    @Shadow
    private SoundSystem soundSystem;

    @Override
    public void pauseSound(@Nullable Identifier id, @Nullable SoundCategory soundCategory, @Nullable BlockPos pos) {
        ISoundSystemPauseSoundInjector soundSystemPauseSoundInjector = (ISoundSystemPauseSoundInjector)soundSystem;
        soundSystemPauseSoundInjector.pauseSound(id, soundCategory, pos);
    }
}
