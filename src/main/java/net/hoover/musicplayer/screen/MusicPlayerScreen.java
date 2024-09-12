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
    private static final Identifier TEXTURE = new Identifier(MusicPlayer.MOD_ID, "textures/gui/new_music_player_gui.png");
    static final Identifier BUTTON_DISABLED_TEXTURE = new Identifier("container/beacon/button_disabled");
    static final Identifier BUTTON_SELECTED_TEXTURE = new Identifier("container/beacon/button_selected");
    static final Identifier BUTTON_HIGHLIGHTED_TEXTURE = new Identifier("container/beacon/button_highlighted");
    static final Identifier BUTTON_TEXTURE = new Identifier("container/beacon/button");
    static final Identifier CONFIRM_TEXTURE = new Identifier("container/beacon/confirm");

    private ShuffleButtonWidget shuffleButton;
    private AutoplayButtonWidget autoplayButton;
    private PauseButtonWidget pauseButton;
    private LoopButtonWidget loopButton;

    public MusicPlayerScreen(MusicPlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.init();
    }

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        this.backgroundHeight = 114 + 6 * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.addDrawableChild(new SkipButtonWidget(0, 0));
        if (this.textRenderer != null) {
            shuffleButton = new ShuffleButtonWidget(50, 0);
            this.addDrawableChild(shuffleButton);
            autoplayButton = new AutoplayButtonWidget(50, 30);
            this.addDrawableChild(autoplayButton);
            pauseButton = new PauseButtonWidget(50, 60);
            this.addDrawableChild(pauseButton);
            loopButton = new LoopButtonWidget(50, 90);
            this.addDrawableChild(loopButton);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, 6 * 18 + 17);
        context.drawTexture(TEXTURE, x, y + 6 * 18 + 17, 0, 126, backgroundWidth, 96);
        //renderProgressArrow(context, x, y);
        //MusicPlayer.LOGGER.info("FPS: " + MinecraftClient.getInstance().getCurrentFps());
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if (handler.isPlaying()) {
            context.drawTexture(TEXTURE, x + 85, y + 30, 176, 0, 8, handler.getScaledProgress());
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
        autoplayButton.setChecked(handler.toAutoplay());
        if (pauseButton.isChecked() != handler.toPause()) {
            pauseButton.setChecked(handler.toPause());
            pauseButton.setTooltip(Tooltip.of(Text.translatable(pauseButton.isChecked() ? "block.musicplayer.music_player_block.play_button" : "block.musicplayer.music_player_block.pause_button")));
        }
        loopButton.setChecked(handler.toLoop());
    }

    @Environment(value=EnvType.CLIENT)
    class SkipButtonWidget
    extends PressableWidget {
        private final Identifier texture = CONFIRM_TEXTURE;
        private boolean disabled;

        protected SkipButtonWidget(int x, int y) {
            super(x, y, 22, 22, ScreenTexts.DONE);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            Identifier identifier = !this.active ? BUTTON_DISABLED_TEXTURE : (this.disabled ? BUTTON_SELECTED_TEXTURE : (this.isSelected() ? BUTTON_HIGHLIGHTED_TEXTURE : BUTTON_TEXTURE));
            context.drawGuiTexture(identifier, this.getX(), this.getY(), this.width, this.height);
            context.drawGuiTexture(this.texture, this.getX() + 2, this.getY() + 2, 18, 18);
        }

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
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
        ShuffleButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toShuffle(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_SHUFFLE_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            });
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.shuffle_button")));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class AutoplayButtonWidget
    extends ToggleableWidget {
        AutoplayButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toAutoplay(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_AUTOPLAY_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            });
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.autoplay_button")));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PauseButtonWidget
    extends ToggleableWidget {
        PauseButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toPause(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_PAUSE_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            });
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.pause_button")));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class LoopButtonWidget
    extends ToggleableWidget {
        LoopButtonWidget(int x, int y) {
            super(x, y, textRenderer, handler.toLoop(), (checkbox, checked) -> {
                ClientPlayNetworking.send(ModMessages.CHECK_LOOP_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked));
            });
            this.init();
        }

        private void init() {
            this.setTooltip(Tooltip.of(Text.translatable("block.musicplayer.music_player_block.loop_button")));
        }
    }
}
