package net.hoover.upgradedjukebox.util;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface ISoundManagerResumeSoundInjector {
    void resumeSound(@Nullable Identifier id, @Nullable SoundCategory soundCategory, @Nullable BlockPos pos);
}
