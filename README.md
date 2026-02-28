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

## Deploying to a server

1. Build the plugin (see above):
   ```bash
   .\gradlew jar
   ```
2. Copy the JAR to the server’s `plugins/` folder. Use a **binary** transfer so the file isn’t corrupted:
   - **SCP (Linux/macOS or WSL):**  
     `scp build/libs/potato-1.0.0.jar user@yourserver:~/minecraft/plugins/`
   - **WinSCP / SFTP:** transfer in “binary” mode (default for .jar).
   - **Other:** copy `build/libs/potato-1.0.0.jar` as-is; do not paste contents or use a text transfer.

If the server logs **`zip END header not found`** for the plugin, the JAR was corrupted during copy. Re-copy the file as binary and try again.

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
