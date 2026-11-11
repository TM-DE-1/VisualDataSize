# Building VisualDataSize

VisualDataSize 1.3.2 targets Minecraft 26.3 and Java 25. It is compiled against Fabric API 0.161.0+26.3; the published mod metadata does not pin a Fabric API version. Fabric Loader still checks Fabric API's own Minecraft compatibility metadata.

Use a JDK 25 installation and run:

    ./gradlew build

The release jar is written to `build/libs/`.

Mod Menu 21.0.0 is a development dependency for the optional config screen. It is not bundled and is not required to run the mod.
