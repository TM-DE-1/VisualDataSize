package de.visualdatasize;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueOutput;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Locale;

public final class ItemSizeCalculator {
    public record Result(long dataBytes, long slotBytes, long tagCount) {
    }

    private ItemSizeCalculator() {
    }

    public static Result measure(
            ItemStack stack,
            HolderLookup.Provider registries,
            boolean showSlot,
            boolean showTags
    ) throws IOException {
        if (stack.isEmpty()) {
            return new Result(0L, 0L, 0L);
        }

        CompoundTag tag = toTag(stack, registries);
        long dataBytes = countNbtBytes(tag);
        long slotBytes = showSlot ? countSlotBytes(stack, registries) : 0L;
        long tagCount = showTags ? countTags(tag) : 0L;

        return new Result(dataBytes, slotBytes, tagCount);
    }

    private static CompoundTag toTag(ItemStack stack, HolderLookup.Provider registries) throws IOException {
        ProblemReporter.Collector problems = new ProblemReporter.Collector(
                () -> "VisualDataSize ItemStack"
        );

        TagValueOutput output = TagValueOutput.createWithContext(problems, registries);
        output.store(ItemStack.MAP_CODEC, stack);

        if (!problems.isEmpty()) {
            throw new IOException("ItemStack serialization failed: " + problems.getReport());
        }

        return output.buildResult();
    }

    private static long countNbtBytes(CompoundTag tag) throws IOException {
        CountingOutputStream counter = new CountingOutputStream();
        DataOutputStream out = new DataOutputStream(counter);
        NbtIo.write(tag, out);
        out.flush();
        return counter.size();
    }

    private static long countSlotBytes(ItemStack stack, HolderLookup.Provider registries) throws IOException {
        ProblemReporter.Collector problems = new ProblemReporter.Collector(
                () -> "VisualDataSize ItemStackWithSlot"
        );

        TagValueOutput output = TagValueOutput.createWithContext(problems, registries);
        output.store("slot", ItemStackWithSlot.CODEC, new ItemStackWithSlot(0, stack));

        if (!problems.isEmpty()) {
            throw new IOException("ItemStackWithSlot serialization failed: " + problems.getReport());
        }

        Tag slotTag = output.buildResult().get("slot");
        if (slotTag == null) {
            throw new IOException("ItemStackWithSlot produced no data");
        }

        CountingOutputStream counter = new CountingOutputStream();
        DataOutputStream out = new DataOutputStream(counter);
        slotTag.write(out);
        out.flush();
        return counter.size();
    }

    private static long countTags(Tag root) {
        long count = 0L;
        ArrayDeque<Tag> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Tag tag = stack.pop();
            count++;

            if (tag instanceof CompoundTag compound) {
                for (Tag child : compound.values()) {
                    stack.push(child);
                }
            } else if (tag instanceof ListTag list) {
                for (int i = 0; i < list.size(); i++) {
                    stack.push(list.get(i));
                }
            }
        }

        return count;
    }

    public static String format(long bytes) {
        return switch (ModConfig.format) {
            case BYTES -> bytes + " B";
            case KIB -> String.format(Locale.ROOT, "%.6f KiB (%d B)", bytes / 1024.0, bytes);
            case MIB -> String.format(Locale.ROOT, "%.6f MiB (%d B)", bytes / 1_048_576.0, bytes);
            case BITS -> bytesToBits(bytes);
            case SMART -> smart(bytes);
        };
    }

    private static String bytesToBits(long bytes) {
        if (bytes > Long.MAX_VALUE / 8L) {
            return bytes + " B";
        }
        return (bytes * 8L) + " bit (" + bytes + " B)";
    }

    private static String smart(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }
        if (bytes < 1_048_576L) {
            return String.format(Locale.ROOT, "%.3f KiB (%d B)", bytes / 1024.0, bytes);
        }
        return String.format(Locale.ROOT, "%.3f MiB (%d B)", bytes / 1_048_576.0, bytes);
    }

    private static final class CountingOutputStream extends OutputStream {
        private long count;

        @Override
        public void write(int value) throws IOException {
            add(1L);
        }

        @Override
        public void write(byte[] bytes, int offset, int length) throws IOException {
            if (bytes == null) {
                throw new NullPointerException("bytes");
            }
            if (offset < 0 || length < 0 || offset > bytes.length - length) {
                throw new IndexOutOfBoundsException("Invalid byte range");
            }
            add(length);
        }

        private void add(long amount) throws IOException {
            if (amount > Long.MAX_VALUE - count) {
                throw new IOException("Serialized size is too large");
            }
            count += amount;
        }

        private long size() {
            return count;
        }
    }
}
