package net.hoover.musicplayer.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_MUSIC_PLAYER = "key.category.music_player";
    public static final String KEY_TEST = "key.music_player.test";

    public static KeyBinding testKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (testKey.wasPressed()) {
                client.player.sendMessage(Text.of("Test"));
                BlockPos pos = client.player.getBlockPos();
                PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR.getId(), SoundCategory.MASTER, 1.0f, 1.0f, Random.create(0), false, 0, SoundInstance.AttenuationType.NONE, pos.getX(), pos.getY(), pos.getZ(), false);
                client.getSoundManager().play(positionedSoundInstance, 0);
            }
        });
    }

    public static void register() {
        testKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TEST,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                KEY_CATEGORY_MUSIC_PLAYER
        ));

        registerKeyInputs();
    }
}
