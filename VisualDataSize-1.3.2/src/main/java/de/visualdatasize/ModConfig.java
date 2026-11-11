package de.visualdatasize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public final class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("VisualDataSize");
    private static final String FILE_NAME = "visualdatasize.properties";

    public enum Format {
        SMART,
        BYTES,
        KIB,
        MIB,
        BITS
    }

    private static Path file;

    public static boolean enabled = true;
    public static Format format = Format.SMART;
    public static boolean showSlotSize = false;
    public static boolean showTagCount = true;

    private ModConfig() {
    }

    public static void load(Path configDir) {
        file = configDir.resolve(FILE_NAME);
        reset();

        if (!Files.isRegularFile(file)) {
            save();
            return;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
        } catch (IOException e) {
            LOGGER.warn("Could not read config", e);
            return;
        }

        enabled = getBoolean(properties, "enabled", enabled);
        format = getFormat(properties.getProperty("format"), format);
        showSlotSize = getBoolean(properties, "showSlotSize", showSlotSize);
        showTagCount = getBoolean(properties, "showTagCount", showTagCount);
    }

    public static void save() {
        if (file == null) {
            return;
        }

        Properties properties = new Properties();
        properties.setProperty("enabled", Boolean.toString(enabled));
        properties.setProperty("format", format.name());
        properties.setProperty("showSlotSize", Boolean.toString(showSlotSize));
        properties.setProperty("showTagCount", Boolean.toString(showTagCount));

        try {
            Files.createDirectories(file.getParent());
            try (OutputStream output = Files.newOutputStream(file)) {
                properties.store(output, "VisualDataSize settings");
            }
        } catch (IOException e) {
            LOGGER.warn("Could not save config", e);
        }
    }

    public static void reset() {
        enabled = true;
        format = Format.SMART;
        showSlotSize = false;
        showTagCount = true;
    }

    private static boolean getBoolean(Properties properties, String key, boolean fallback) {
        String value = properties.getProperty(key);
        if (value == null) {
            return fallback;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "1", "yes", "on" -> true;
            case "false", "0", "no", "off" -> false;
            default -> fallback;
        };
    }

    private static Format getFormat(String value, Format fallback) {
        if (value == null) {
            return fallback;
        }

        try {
            return Format.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
