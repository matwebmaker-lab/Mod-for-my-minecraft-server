# OP Gear Minecraft Paper Server Mod – kildekode

Kildekode for **OP Gear**-pluginen (Paper 1.21.11). Ferdig JAR lastes ned fra [Mod-for-my-minecraft-server](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server).

## Bygging

Krever **Java 21** og **Gradle**.

```bash
.\gradlew jar
```

JAR: `build/libs/Lager-1.0.0.jar`

## Kjøre server lokalt

```bash
.\gradlew setupServer   # første gang (laster ned Paper)
.\gradlew runServer     # starter server med pluginen
```

## Prosjektstruktur

- `src/main/java/` – Java-kildekode
- `src/main/resources/plugin.yml` – plugin-metadata
- `build.gradle.kts` – byggoppsett

## Krav

- Paper 1.21.11 (Minecraft 1.21.11)
- Java 21
