# OP Gear – Minecraft Paper Plugin (versjon 4)

**Kildekode og bygg** for **Lager**-pluginen (OP Gear) for Paper 1.21.11.

- **Versjon:** 4.0.5  
- **GitHub-repo:** [matwebmaker-lab/Mod-for-my-minecraft-server](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server)  
- Ferdigbygd JAR kan lastes ned fra [Releases](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server/releases) på GitHub (når publisert).

## Nyheter (oversikt)

- **OP-gear:** Våpen, rustning og verktøy kun for OP; ikke-OP kan ikke eie dem.
- **`/lager`:** Liste, gi items (`give <id>`), hele utstyret (`gear`), åpne Matheo client (`instillinger`).
- **`/lager gear`:** Gir hele OP-utstyret (rustning + våpen) – ingen auto-utdeling ved join.
- **`/kiste`:** OP-kiste med uendelig OP-gear (refylles når du tar); **sider** med «Neste side» / «Forrige side».
- **`/lagerkiste`:** Admin-kiste (kun **Admin_owner**), fylt med OP-items.
- **Flygestav** (`flygkølle`): Høyreklikk tar entity med deg, shift slipper. På villager åpnes ikke trade.
- **Flygestøvler** (`fly_støvler`): Jernstøvler – fly som i creative.
- **Matheo client** (`instillinger`): Klokke – shift åpner innstillinger. **Armor reach** (inkl. «Vanlig») og **Full bright** (Night Vision).
- **Totem-stakk** (`totem_stakk`): Høyreklikk gir 1 Operator Totem (16 bruk per stakk).
- **Tordenøks på villager:** Høyreklikk på villager som OP gir belønning (emeralds, diamant, gulleneple, totem).
- **Havets Vrede (trident):** Lyn slår ned der tridenten treffer.
- **Admin_owner frys:** `/frys` eller **bytte-hender**-tasten (f.eks. R) – fryser spillere og entities i 15 blokker radius; kun Admin_owner kan bevege seg.

## Matheo client (innstillinger)

Åpnes med **shift** mens du holder **Matheo client**-itemet (klokke), eller med `/lager instillinger`. Kun for OP.

- **Armor reach:** +/- for rekkevidde når du har OP-brystplade. «Vanlig» = ingen ekstra reach.
- **Full bright:** Slå på/av Night Vision (lys i mørke).

## Kommandoer

| Kommando | Beskrivelse | Hvem |
|----------|-------------|------|
| `/lager` | Vis liste over alle OP-items | OP |
| `/lager list` | Samme som `/lager` | OP |
| `/lager give <id>` | Gi deg ett OP-item (f.eks. `storm_sverd`, `flygkølle`, `fly_støvler`) | OP |
| `/lager gear` | Gi deg hele OP-utstyret (rustning + våpen) | OP |
| `/lager instillinger` | Åpne Matheo client (armor reach, full bright) | OP |
| `/lagerkiste` | Åpne admin-kisten fylt med OP-items | Kun **Admin_owner** |
| `/kiste` | Åpne OP-kisten (uendelig gear, refylles når du tar) | OP |
| `/frys` | Slå frys på/av (fryser spillere og entities rundt deg; kun du kan bevege deg) | Kun **Admin_owner** |

Admin_owner kan også bruke **bytte-hender**-tasten (f.eks. R hvis du binder den) for å slå frys på/av.

## OP-items (og innhold i kista)

Disse itemene kan gis med `/lager give <id>` og finnes i **OP-kisten** (`/kiste`) og **admin-kisten** (`/lagerkiste`).

### Våpen

| Id | Navn | Beskrivelse |
|----|------|-------------|
| `storm_sverd` | Storm Sverd | Netherite-sverd, høye enchantments |
| `dødspiler` | Dødspiler | Kraftig bue |
| `tordenøks` | Tordenøks | Netherite-øks; høyreklikk på villager som OP gir belønning (emeralds, diamant, gulleneple, totem) |
| `krossbue` | Mester-krossbue | Krossbue med høye enchantments |
| `trident` | Havets Vrede | Trident; lyn slår ned der den treffer |

### Rustning

| Id | Navn | Effekt |
|----|------|--------|
| `op_helm` | Operator Hjelm | +20 max liv |
| `op_bryst` | Operator Brystplade | Ekstra reach (antall blokker styres i Matheo client) |
| `op_bukser` | Operator Bukse | Styrke II + vanndråper |
| `op_støvler` | Operator Støvler | Speed II |
| `op_rustning_sett` | (spesial) | Gir hele settet med `/lager give op_rustning_sett` |
| `fly_støvler` | Flygestøvler | Jernstøvler – fly som i creative (dobbeltklikk hopp) |

### Verktøy og annet

| Id | Navn | Beskrivelse |
|----|------|-------------|
| `op_spade` | Operator Spade | Netherite-spade, høye enchantments |
| `op_pickaxe` | Operator Hakke | Netherite-hakke, høye enchantments |
| `op_skjold` | Operator Skjold | Skjold med Unbreaking |
| `op_elytra` | Operator Elytra | Elytra med Unbreaking/Mending |
| `op_totem` | Operator Totem | Totem of Undying |
| `op_gulleneple` | Operator Gulleneple | Enchanted Golden Apple |

### Spesialitems

| Id | Navn | Bruk |
|----|------|------|
| `flygkølle` | Flygestav | Høyreklikk = ta entity med deg, shift = slippe. På villager åpnes ikke trade. |
| `instillinger` | Matheo client | Klokke – shift åpner innstillinger (armor reach, full bright). |
| `totem_stakk` | Totem-stakk | Papir (16 stk) – høyreklikk gir 1 Operator Totem og bruker én fra stakken. |

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
     `scp build/libs/potato-4.0.5.jar user@yourserver:~/minecraft/plugins/`
   - **WinSCP / SFTP:** transfer in “binary” mode (default for .jar).
   - **Other:** copy `build/libs/potato-4.0.5.jar` as-is; do not paste contents or use a text transfer.

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
