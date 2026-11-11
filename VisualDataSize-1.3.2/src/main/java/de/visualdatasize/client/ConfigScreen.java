package de.visualdatasize.client;

import de.visualdatasize.ModConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("VisualDataSize"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 120;
        int top = height / 2 - 55;

        addRenderableWidget(Button.builder(enabledText(), button -> {
            ModConfig.enabled = !ModConfig.enabled;
            button.setMessage(enabledText());
        }).bounds(left, top, 240, 20).build());

        addRenderableWidget(Button.builder(formatText(), button -> {
            ModConfig.format = nextFormat(ModConfig.format);
            button.setMessage(formatText());
        }).bounds(left, top + 25, 240, 20).build());

        addRenderableWidget(Button.builder(slotText(), button -> {
            ModConfig.showSlotSize = !ModConfig.showSlotSize;
            button.setMessage(slotText());
        }).bounds(left, top + 50, 240, 20).build());

        addRenderableWidget(Button.builder(tagsText(), button -> {
            ModConfig.showTagCount = !ModConfig.showTagCount;
            button.setMessage(tagsText());
        }).bounds(left, top + 75, 240, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            ModConfig.save();
            minecraft.setScreenAndShow(parent);
        }).bounds(left, top + 110, 240, 20).build());
    }

    private static ModConfig.Format nextFormat(ModConfig.Format current) {
        ModConfig.Format[] formats = ModConfig.Format.values();
        return formats[(current.ordinal() + 1) % formats.length];
    }

    private static Component enabledText() {
        return Component.literal("Enabled: " + (ModConfig.enabled ? "Yes" : "No"));
    }

    private static Component formatText() {
        return Component.literal("Format: " + ModConfig.format.name().toLowerCase(java.util.Locale.ROOT));
    }

    private static Component slotText() {
        return Component.literal("Show slot size: " + (ModConfig.showSlotSize ? "Yes" : "No"));
    }

    private static Component tagsText() {
        return Component.literal("Show tag count: " + (ModConfig.showTagCount ? "Yes" : "No"));
    }
}
