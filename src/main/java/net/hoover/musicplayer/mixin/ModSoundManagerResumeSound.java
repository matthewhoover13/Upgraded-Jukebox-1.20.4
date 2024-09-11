package net.hoover.musicplayer.mixin;

import net.hoover.musicplayer.util.ISoundManagerResumeSoundInjector;
import net.hoover.musicplayer.util.ISoundSystemResumeSoundInjector;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundManager.class)
public class ModSoundManagerResumeSound implements ISoundManagerResumeSoundInjector {
    @Shadow
    private SoundSystem soundSystem;

    @Override
    public void resumeSound(@Nullable Identifier id, @Nullable SoundCategory soundCategory, @Nullable BlockPos pos) {
        ISoundSystemResumeSoundInjector soundSystemResumeSoundInjector = (ISoundSystemResumeSoundInjector)soundSystem;
        soundSystemResumeSoundInjector.resumeSound(id, soundCategory, pos);
    }
}
