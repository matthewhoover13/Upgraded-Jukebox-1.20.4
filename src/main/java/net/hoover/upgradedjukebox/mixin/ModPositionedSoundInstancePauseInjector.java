package net.hoover.upgradedjukebox.mixin;

import net.hoover.upgradedjukebox.util.IPositionedSoundInstancePauseInjector;
import net.minecraft.client.sound.PositionedSoundInstance;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PositionedSoundInstance.class)
public class ModPositionedSoundInstancePauseInjector implements IPositionedSoundInstancePauseInjector {
    private boolean paused;

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
