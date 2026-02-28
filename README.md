# OP Gear – Minecraft Paper Plugin

Source code for the **OP Gear** plugin (Paper 1.21.11). A pre-built JAR is also available from [Mod-for-my-minecraft-server](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server).

## Requirements

- **Java 21**
- **Gradle** (or use the included wrapper)

## Building

```bash
.\gradlew jar
```

Output: `build/libs/potato-<version>.jar` (version is set in `build.gradle.kts`).

## Running the server locally

```bash
.\gradlew setupServer   # first time only (downloads Paper)
.\gradlew runServer     # starts the server with the plugin
```

## Project structure

- `src/main/java/` – Java source
- `src/main/resources/plugin.yml` – plugin metadata
- `build.gradle.kts` – build configuration

## Compatibility

- Paper 1.21.11 (Minecraft 1.21.11)
- Java 21
