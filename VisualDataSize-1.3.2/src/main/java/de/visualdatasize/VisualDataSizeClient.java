package de.visualdatasize;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class VisualDataSizeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.load(FabricLoader.getInstance().getConfigDir());
        registerTooltip();
        registerCommands();
    }

    private static void registerTooltip() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (!ModConfig.enabled || stack.isEmpty() || !flag.isAdvanced()) {
                return;
            }

            try {
                ItemSizeCalculator.Result result = ItemSizeCalculator.measure(
                        stack,
                        context.registries(),
                        ModConfig.showSlotSize,
                        ModConfig.showTagCount
                );

                int componentLine = findComponentLine(lines);
                int insertAt = componentLine >= 0 ? componentLine + 1 : lines.size();
                net.minecraft.network.chat.Style style = findTooltipStyle(lines, componentLine);

                StringBuilder sizeLine = new StringBuilder();
                sizeLine.append("Size: ").append(ItemSizeCalculator.format(result.dataBytes()));

                if (ModConfig.showTagCount) {
                    sizeLine.append(" (Tags: ").append(result.tagCount()).append(")");
                }

                lines.add(insertAt++, Component.literal(sizeLine.toString()).withStyle(style));

                if (ModConfig.showSlotSize) {
                    lines.add(insertAt, Component.literal("Slot: " + result.slotBytes() + " B").withStyle(style));
                }
            } catch (java.io.IOException | RuntimeException e) {
                lines.add(Component.literal("Size: unavailable").withStyle(findTooltipStyle(lines, findComponentLine(lines))));
            }
        });
    }

    private static net.minecraft.network.chat.Style findTooltipStyle(java.util.List<Component> lines, int componentLine) {
        if (componentLine >= 0) {
            return lines.get(componentLine).getStyle();
        }

        // Fall back to the muted style used by vanilla advanced tooltips.
        return net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.ChatFormatting.DARK_GRAY);
    }

    private static int findComponentLine(java.util.List<Component> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getContents() instanceof TranslatableContents translatable
                    && translatable.getKey().equals("item.components")) {
                return i;
            }
        }
        return -1;
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommands.literal("visualdatasize")
                        .then(ClientCommands.literal("toggle").executes(context -> {
                            ModConfig.enabled = !ModConfig.enabled;
                            ModConfig.save();
                            context.getSource().sendFeedback(Component.literal(
                                    "VisualDataSize " + (ModConfig.enabled ? "enabled" : "disabled")));
                            return 1;
                        }))
                        .then(ClientCommands.literal("slot").executes(context -> {
                            ModConfig.showSlotSize = !ModConfig.showSlotSize;
                            ModConfig.save();
                            context.getSource().sendFeedback(Component.literal(
                                    "Slot size " + (ModConfig.showSlotSize ? "shown" : "hidden")));
                            return 1;
                        }))
                        .then(ClientCommands.literal("tags").executes(context -> {
                            ModConfig.showTagCount = !ModConfig.showTagCount;
                            ModConfig.save();
                            context.getSource().sendFeedback(Component.literal(
                                    "Tag count " + (ModConfig.showTagCount ? "shown" : "hidden")));
                            return 1;
                        }))
                        .then(ClientCommands.literal("format")
                                .then(ClientCommands.literal("smart")
                                        .executes(context -> setFormat(context.getSource(), ModConfig.Format.SMART)))
                                .then(ClientCommands.literal("bytes")
                                        .executes(context -> setFormat(context.getSource(), ModConfig.Format.BYTES)))
                                .then(ClientCommands.literal("kib")
                                        .executes(context -> setFormat(context.getSource(), ModConfig.Format.KIB)))
                                .then(ClientCommands.literal("mib")
                                        .executes(context -> setFormat(context.getSource(), ModConfig.Format.MIB)))
                                .then(ClientCommands.literal("bits")
                                        .executes(context -> setFormat(context.getSource(), ModConfig.Format.BITS))))
        ));
    }

    private static int setFormat(net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource source,
                                  ModConfig.Format format) {
        ModConfig.format = format;
        ModConfig.save();
        source.sendFeedback(Component.literal("VisualDataSize format: " + format.name().toLowerCase(java.util.Locale.ROOT)));
        return 1;
    }
}
