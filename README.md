Visual Data Size

Visual Data Size is a lightweight, purely client-side diagnostic tool for Minecraft 26.3. It makes the serialized data size and internal structure of items visible directly in your inventory tooltips.

Whether you are a mod developer checking item data, a server administrator investigating large item structures, or simply interested in the technical side of Minecraft, Visual Data Size gives you a quick way to inspect item data at a glance.
Key Features

    Exact Data Measurement: Measures the serialized data size of the current item or full stack in Bytes (B), Kibibytes (KiB), or Mebibytes (MiB).
    Structural Tag Counter: Counts tags and nested data stored inside an item, including complex items such as filled Shulker Boxes, books, and modded items with custom data.
    Container Support: Nested item data is included in the measurement, making it useful for inspecting items such as Shulker Boxes and other container items.
    Custom Item Support: Persistent data from modded items and custom data components is included when it is part of the item's serialized data.
    Live In-Game Commands: Toggle the display and change the size format directly from chat using simple client-side commands with Tab completion.
    No Cache: The current ItemStack is measured again when needed, so changes to the item's data are reflected immediately.
    100% Client-Side: Runs entirely on your client. No server-side installation or admin permissions are required.
    Vanilla-Style Tooltip: The additional information uses the same simple dark-gray style as Minecraft's advanced tooltip data.

How to Use

    Place the generated .jar file into your Fabric mods folder.
    Start Minecraft 26.3.
    Join a multiplayer server or open a singleplayer world.
    Press F3 + H to enable Minecraft's Advanced Tooltips.
    Hover over an item to see its serialized data size and tag count at the bottom of the tooltip.

Chat Commands

Use /visualdatasize to configure the mod. All commands support Tab completion.

    /visualdatasize toggle — Turns the tooltip display ON or OFF.
    /visualdatasize format smart — Automatically chooses a readable size unit.
    /visualdatasize format bytes — Shows the exact size in Bytes.
    /visualdatasize format kib — Shows the size in KiB.
    /visualdatasize format mib — Shows the size in MiB.
    /visualdatasize format bits — Shows the size in Bits.

Example Tooltip Output

The exact value depends on the item and its serialized data.

Size: 42 B (Tags: 3)
