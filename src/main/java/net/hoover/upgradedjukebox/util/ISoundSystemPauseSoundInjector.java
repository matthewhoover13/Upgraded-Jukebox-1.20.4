package net.hoover.upgradedjukebox.util;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface ISoundSystemPauseSoundInjector {
    public void pauseSound(@Nullable Identifier id, @Nullable SoundCategory category, BlockPos pos);
}
