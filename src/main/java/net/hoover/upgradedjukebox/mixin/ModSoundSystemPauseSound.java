package net.hoover.upgradedjukebox.mixin;

import com.google.common.collect.Multimap;
import net.hoover.upgradedjukebox.util.IPositionedSoundInstancePauseInjector;
import net.hoover.upgradedjukebox.util.ISoundSystemPauseSoundInjector;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SoundSystem.class)
public class ModSoundSystemPauseSound implements ISoundSystemPauseSoundInjector {
    @Shadow
    private Multimap<SoundCategory, SoundInstance> sounds;

    @Shadow
    private Map<SoundInstance, Channel.SourceManager> sources;

    @Override
    public void pauseSound(@Nullable Identifier id, @Nullable SoundCategory category, @Nullable BlockPos pos) {
        if (category != null) {
            for (SoundInstance soundInstance : sounds.get(category)) {
                if (!(soundInstance instanceof PositionedSoundInstance)) {
                    continue;
                }
                if (id != null && !soundInstance.getId().equals(id)) {
                    continue;
                }
                if (pos == null || ((int)soundInstance.getX() == pos.getX() && (int)soundInstance.getY() == pos.getY() && (int)soundInstance.getZ() == pos.getZ())) {
                    pause(soundInstance);
                }
            }
        } else if (id != null) {
            for (SoundInstance soundInstance : sources.keySet()) {
                if (!soundInstance.getId().equals(id)) {
                    continue;
                }
                if (pos == null || ((int)soundInstance.getX() == pos.getX() && (int)soundInstance.getY() == pos.getY() && (int)soundInstance.getZ() == pos.getZ())) {
                    pause(soundInstance);
                }
            }
        }
    }

    private void pause(SoundInstance soundInstance) {
        Channel.SourceManager sourceManager;
        sourceManager = sources.get(soundInstance);
        sourceManager.run(Source::pause);
        IPositionedSoundInstancePauseInjector sound = (IPositionedSoundInstancePauseInjector) soundInstance;
        sound.setPaused(true);
    }
}
