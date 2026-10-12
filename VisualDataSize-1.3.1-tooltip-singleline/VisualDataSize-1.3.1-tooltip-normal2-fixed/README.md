# VisualDataSize 1.3.1

A small client-side Fabric mod for Minecraft 26.3.

VisualDataSize adds the size of the current ItemStack's persistent binary NBT data to advanced tooltips (F3 + H).

## What is measured

- `Data` is the exact byte count produced by writing the ItemStack's current persistent NBT tag with Minecraft's binary NBT writer.
- Container data stored inside the ItemStack is included. A full shulker box is therefore larger than an empty one.
- Persistent custom data components from other mods are included when their codecs serialize them into the ItemStack.
- `Nodes` counts the NBT nodes in that tag, including the root.
- `Slot` is the NBT payload size of an `ItemStackWithSlot` entry using slot index 0. It is not the size of an entire inventory or chunk.

The mod does not keep a measurement cache. Every displayed result is calculated from the current ItemStack.

## What is not measured

This is not a disk-size measurement for a complete chunk or region file. It is also not a network-packet size or Java heap measurement.

A server can also have data that the client does not know about. A client-only mod cannot measure data that was never sent to the client.

## Commands

- `/visualdatasize toggle`
- `/visualdatasize slot`
- `/visualdatasize tags`
- `/visualdatasize format bytes`
- `/visualdatasize format kib`
- `/visualdatasize format mib`
- `/visualdatasize format bits`
- `/visualdatasize format smart`

## Configuration

The config file is `config/visualdatasize.properties`.

Mod Menu is optional. If Mod Menu is installed, it provides a simple settings screen.

## Build

Java 25 is required.

```text
./gradlew build
```

The project targets Minecraft 26.3 with Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Fabric Loom 1.18.x, and the Gradle 9.7.1 wrapper. Minecraft 26.3 uses Java 25.

## Code and assets

The VisualDataSize source files were written for this project. No Minecraft source files are redistributed here.

The tooltip integration uses Fabric API directly, so it does not replace or cancel Minecraft tooltip methods.

The mod icon is an original project asset generated for VisualDataSize. It is not taken from Minecraft's files or the Minecraft logo.

## License

Original VisualDataSize source code and original project assets are dedicated to the public domain under CC0 1.0 to the extent permitted by law. See `LICENSE`.

Third-party tools and libraries keep their own licenses. See `THIRD-PARTY-NOTICES.md`.

## Minecraft notice

VisualDataSize is an independent third-party mod. It is not official, approved, sponsored, or endorsed by Mojang Studios or Microsoft. Minecraft and related marks remain the property of their respective owners.
