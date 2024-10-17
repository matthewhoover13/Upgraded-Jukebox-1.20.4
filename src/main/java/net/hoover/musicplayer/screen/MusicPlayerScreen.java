package net.hoover.musicplayer.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.client.gui.widget.ToggleableWidget;
import net.hoover.musicplayer.networking.ModMessages;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MusicPlayerScreen extends HandledScreen<MusicPlayerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(MusicPlayer.MOD_ID, "textures/gui/jukebox_gui.png");

    private ShuffleButtonWidget shuffleButton;
    private PauseButtonWidget pauseButton;
    private LoopButtonWidget loopButton;

    public MusicPlayerScreen(MusicPlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.init();
    }

    @Override
    protected void init() {
        super.init();
        this.titleY = 6;
        this.backgroundHeight = 243;
        this.playerInventoryTitleY = this.backgroundHeight - 89;
        int x = getStartingX() + 25;
        int y = getStartingY() + 71;
        this.addDrawableChild(new SkipButtonWidget(x + 36, y));
        if (this.textRenderer != null) {
            shuffleButton = new ShuffleButtonWidget(x + 73, y);
            this.addDrawableChild(shuffleButton);
            pauseButton = new PauseButtonWidget(x, y);
            this.addDrawableChild(pauseButton);
            loopButton = new LoopButtonWidget(x + 109, y);
            this.addDrawableChild(loopButton);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = getStartingX();
        int y = getStartingY();
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderSongProgress(context, x, y);
    }

    private void renderSongProgress(DrawContext context, int x, int y) {
        if (handler.isPlaying()) {
            context.drawTexture(TEXTURE, x + 8, y + 92, 8, 247, handler.getScaledProgress(), 3);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //renderBackground(context, mouseX, mouseY, delta);
        updateWidgets();
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void updateWidgets() {
        shuffleButton.setChecked(handler.toShuffle());
        if (pauseButton.isChecked() != handler.toPause()) {
            pauseButton.setChecked(handler.toPause());
            pauseButton.setTooltip(Tooltip.of(Text.translatable(pauseButton.isChecked() ? "block.musicplayer.music_player_block.play_button" : "block.musicplayer.music_player_block.pause_button")));
        }
        loopButton.setChecked(handler.toLoop());
    }

    private int getStartingX() {
        return (width - backgroundWidth) / 2;
    }

    private int getStartingY() {
        return (height - backgroundHeight) / 2;
    }

    @Environment(value=EnvType.CLIENT)
    class SkipButtonWidget
    extends PressableWidget {
        private static final Identifier SKIP_BUTTON_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/skip_button");
        private static final Identifier SKIP_BUTTON_PRESSED_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/skip_button_pressed");

        protected SkipButtonWidget(int x, int y) {
            super(x, y, 22, 22, ScreenTexts.DONE);
            init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.skip_button")));
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawGuiTexture(SKIP_BUTTON_TEXTURE, this.getX(), this.getY(), 17, 17);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        @Override
        public void onPress() {
            ClientPlayNetworking.send(ModMessages.SKIP_SONG_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ShuffleButtonWidget
    extends ToggleableWidget {
        private static final Identifier SHUFFLE_BUTTON_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/shuffle_button");
        private static final Identifier SHUFFLE_BUTTON_PRESSED_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/shuffle_button_pressed");

        ShuffleButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toShuffle(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_SHUFFLE_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            }, SHUFFLE_BUTTON_PRESSED_TEXTURE, SHUFFLE_BUTTON_TEXTURE);
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.shuffle_button")));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PauseButtonWidget
    extends ToggleableWidget {
        private static final Identifier PLAY_BUTTON_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/play_button");
        private static final Identifier PLAY_BUTTON_PRESSED_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/play_button_pressed");
        private static final Identifier PAUSE_BUTTON_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/pause_button");
        private static final Identifier PAUSE_BUTTON_PRESSED_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/pause_button_pressed");

        PauseButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toPause(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_PAUSE_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            }, PLAY_BUTTON_PRESSED_TEXTURE, PLAY_BUTTON_TEXTURE, PAUSE_BUTTON_PRESSED_TEXTURE, PAUSE_BUTTON_TEXTURE);
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.pause_button")));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class LoopButtonWidget
    extends ToggleableWidget {
        private static final Identifier LOOP_BUTTON_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/loop_button");
        private static final Identifier LOOP_BUTTON_PRESSED_TEXTURE = new Identifier(MusicPlayer.MOD_ID, "container/music_player_block/loop_button_pressed");

        LoopButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toLoop(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_LOOP_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            }, LOOP_BUTTON_PRESSED_TEXTURE, LOOP_BUTTON_TEXTURE);
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.loop_button")));
        }
    }
}
