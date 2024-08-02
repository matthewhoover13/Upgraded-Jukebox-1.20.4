package net.hoover.musicplayer.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hoover.musicplayer.MusicPlayer;
import net.hoover.musicplayer.networking.ModMessages;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MusicPlayerScreen extends HandledScreen<MusicPlayerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(MusicPlayer.MOD_ID, "textures/gui/new_music_player_gui.png");

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
        addDrawableChild(ButtonWidget.builder(Text.of("Skip"), button -> ClientPlayNetworking.send(ModMessages.SKIP_SONG_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()))).dimensions(0, 0, 40, 20).build());
        addDrawableChild(CheckboxWidget.builder(Text.of("Shuffle"), textRenderer).callback((checkbox, checked) -> ClientPlayNetworking.send(ModMessages.CHECK_SHUFFLE_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked))).pos(50, 0).checked(handler.toShuffle()).build());
        addDrawableChild(CheckboxWidget.builder(Text.of("Autoplay"), textRenderer).callback((checkbox, checked) -> ClientPlayNetworking.send(ModMessages.CHECK_AUTOPLAY_BOX_ID, PacketByteBufs.create().writeBlockPos(handler.blockEntity.getPos()).writeBoolean(checked))).pos(50, 30).checked(handler.toAutoplay()).build());
        //renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if (handler.isPlaying()) {
            context.drawTexture(TEXTURE, x + 85, y + 30, 176, 0, 8, handler.getScaledProgress());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
