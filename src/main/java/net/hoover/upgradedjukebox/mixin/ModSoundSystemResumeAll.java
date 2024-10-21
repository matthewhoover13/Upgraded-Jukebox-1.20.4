package net.hoover.upgradedjukebox.mixin;

import net.hoover.upgradedjukebox.util.IPositionedSoundInstancePauseInjector;
import net.minecraft.client.sound.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SoundSystem.class)
public class ModSoundSystemResumeAll {
    @Shadow
    private boolean started;

    @Shadow
    private Map<SoundInstance, Channel.SourceManager> sources;

    @Overwrite
    public void resumeAll() {
        if (this.started) {
            for (SoundInstance soundInstance : sources.keySet()) {
                if (!(soundInstance instanceof PositionedSoundInstance && ((IPositionedSoundInstancePauseInjector)soundInstance).isPaused())) {
                    Channel.SourceManager sourceManager;
                    sourceManager = sources.get(soundInstance);
                    sourceManager.run(Source::resume);
                }
            }
        }
    }
}
