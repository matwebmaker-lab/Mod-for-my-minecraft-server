# OP Gear – Minecraft Paper Plugin (versjon 4)

**Kildekode og bygg** for **Lager**-pluginen (OP Gear) for Paper 1.21.11.

- **Versjon:** 4.0.3  
- **GitHub-repo:** [matwebmaker-lab/Mod-for-my-minecraft-server](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server)  
- Ferdigbygd JAR kan lastes ned fra [Releases](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server/releases) på GitHub (når publisert).

## Matheo client (innstillinger)

Åpnes med **shift** mens du holder **Matheo client**-itemet (klokke), eller med `/lager instillinger`. Kun for OP.

- **Armor reach:** +/- for rekkevidde når du har OP-brystplade. «Vanlig» = ingen ekstra reach.
- **Full bright:** Slå på/av Night Vision (lys i mørke).

## Kommandoer

| Kommando | Beskrivelse | Hvem |
|----------|-------------|------|
| `/lager` | Vis liste over alle OP-items | OP |
| `/lager list` | Samme som `/lager` | OP |
| `/lager give <id>` | Gi deg ett OP-item (f.eks. `storm_sverd`, `flygestav`, `fly_støvler`) | OP |
| `/lager gear` | Gi deg hele OP-utstyret (rustning + våpen) | OP |
| `/lager instillinger` | Åpne Matheo client (innstillinger for armor-reach) | OP |
| `/lagerkiste` | Åpne admin-kisten fylt med OP-items | Kun **Admin_owner** |
| `/kiste` | Åpne OP-kisten (uendelig gear, refylles når du tar) | OP |

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
     `scp build/libs/potato-4.0.3.jar user@yourserver:~/minecraft/plugins/`
   - **WinSCP / SFTP:** transfer in “binary” mode (default for .jar).
   - **Other:** copy `build/libs/potato-4.0.3.jar` as-is; do not paste contents or use a text transfer.

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
