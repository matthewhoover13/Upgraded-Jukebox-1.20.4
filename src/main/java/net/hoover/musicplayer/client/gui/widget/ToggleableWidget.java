package net.hoover.musicplayer.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ToggleableWidget extends PressableWidget {
    private static final int TEXT_COLOR = 0xE0E0E0;
    private static final int field_47105 = 4;
    private static final int field_47106 = 8;
    protected Identifier selectedHighlightedTexture = new Identifier("widget/checkbox_selected_highlighted");
    protected Identifier selectedTexture = new Identifier("widget/checkbox_selected");
    protected Identifier highlightedTexture = new Identifier("widget/checkbox_highlighted");
    protected Identifier texture = new Identifier("widget/checkbox");
    private boolean checked;
    private final Callback callback;

    public ToggleableWidget(int x, int y, TextRenderer textRenderer, boolean checked, Callback callback) {
        super(x, y, ToggleableWidget.getSize(textRenderer), ToggleableWidget.getSize(textRenderer), Text.of(""));
        this.checked = checked;
        this.callback = callback;
    }

    public ToggleableWidget(int x, int y, TextRenderer textRenderer, boolean checked, Callback callback, Identifier selectedTexture, Identifier texture) {
        this(x, y, textRenderer, checked, callback, selectedTexture, selectedTexture, texture, texture);
    }

    public ToggleableWidget(int x, int y, TextRenderer textRenderer, boolean checked, Callback callback, Identifier selectedHighlightedTexture, Identifier selectedTexture, Identifier highlightedTexture, Identifier texture) {
        this(x, y, textRenderer, checked, callback);
        this.selectedHighlightedTexture = selectedHighlightedTexture;
        this.selectedTexture = selectedTexture;
        this.highlightedTexture = highlightedTexture;
        this.texture = texture;
    }

    public static Builder builder(TextRenderer textRenderer) {
        return new Builder(textRenderer);
    }

    private static int getSize(TextRenderer textRenderer) {
        return textRenderer.fontHeight + 8;
    }

    @Override
    public void onPress() {
        this.checked = !this.checked;
        this.callback.onValueChange(this, this.checked);
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.checkbox.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        RenderSystem.enableDepthTest();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        Identifier identifier = this.checked ? (this.isFocused() ? selectedHighlightedTexture : selectedTexture) : (this.isFocused() ? highlightedTexture : texture);
        int i = getSize(textRenderer);
        int j = this.getX() + i + 4;
        int k = this.getY() + (this.height >> 1) - (textRenderer.fontHeight >> 1);
        context.drawGuiTexture(identifier, this.getX(), this.getY(), i, i);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Callback {
        public static final Callback EMPTY = (checkbox, checked) -> {};

        public void onValueChange(ToggleableWidget var1, boolean var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final TextRenderer textRenderer;
        private int x = 0;
        private int y = 0;
        private Callback callback = Callback.EMPTY;
        private boolean checked = false;
        @Nullable
        private SimpleOption<Boolean> option = null;
        @Nullable
        private Tooltip tooltip = null;

        Builder(TextRenderer textRenderer) {
            this.textRenderer = textRenderer;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder callback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder checked(boolean checked) {
            this.checked = checked;
            this.option = null;
            return this;
        }

        public Builder option(SimpleOption<Boolean> option) {
            this.option = option;
            this.checked = option.getValue();
            return this;
        }

        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public ToggleableWidget build() {
            Callback callback = this.option == null ? this.callback : (checkbox, checked) -> {
                this.option.setValue(checked);
                this.callback.onValueChange(checkbox, checked);
            };
            ToggleableWidget toggleableWidget = new ToggleableWidget(this.x, this.y, this.textRenderer, this.checked, callback);
            toggleableWidget.setTooltip(this.tooltip);
            return toggleableWidget;
        }
    }
}
