# OP Gear – Minecraft Paper Plugin (versjon 4)

**Kildekode og bygg** for **Lager**-pluginen (OP Gear) for Paper 1.21.11.

- **Versjon:** 4.1.5  
- **GitHub-repo:** [matwebmaker-lab/Mod-for-my-minecraft-server](https://github.com/matwebmaker-lab/Mod-for-my-minecraft-server)  
- Ferdigbygd JAR: `build/libs/potato-4.1.5.jar` (eller nyere ved `.\gradlew jar`).

---

## Alle kommandoer (komplett liste)

| Kommando | Bruk | Beskrivelse |
|----------|------|--------------|
| `/lager` | `/lager` | Vis liste over alle OP-items |
| `/lager list` | `/lager list` | Samme som `/lager` |
| `/lager give <id>` | f.eks. `/lager give storm_sverd` | Gi deg ett OP-item (alle id'er står under OP-items) |
| `/lager gear` | `/lager gear` | Gi hele OP-utstyret (rustning + våpen) |
| `/lager instillinger` | `/lager instillinger` | Åpne Matheo client (armor reach, full bright) |
| `/lagerkiste` | `/lagerkiste` | Åpne admin-kisten (kun **Admin_owner**) |
| `/kiste` | `/kiste` | Åpne OP-kisten (uendelig gear, sider med Neste/Forrige) |
| `/frys` | `/frys` | Slå frys på/av (kun **Admin_owner**; også med bytte-hender-tasten) |
| `/opmode` | `/opmode` eller `/opmode 120` | Midlertidig OP (creative, speed, night vision); valgfri sekunder |
| `/troll` | `/troll <spillernavn>` | Random troll-effekt på spiller |
| `/boost` | `/boost` eller `/boost <spiller>` | Strength + speed + regen i 30 sek |
| `/arena` | `/arena` | Teleporterer til PvP-arena |
| `/spectateplus` | `/spectateplus <spillernavn>` | Spectate – teleporterer til spiller og setter spectator |
| `/vault` | `/vault` | Åpner din private, persistente vault (MySQL) |
| `/sethome` | `/sethome <1|2|3>` | Lagrer home-posisjon i valgt slot |
| `/home` | `/home <1|2|3>` | Teleporterer til lagret home-slot |
| `/rankitem` | `/rankitem create <rank>` | Admin_owner lager rank-item som gir plugin-rank ved høyreklikk |
| `/faderfaling` | `/faderfaling <på|av>` | Admin_owner slår Slow Falling ved join på/av |
| `/fireresistance` | `/fireresistance <på|av>` | Admin_owner slår Fire Resistance ved join på/av |
| `/inventory view <spiller>` | `/inventory view Steve` | Se og manipulere en spillers inventar (live) |
| `/blind <spiller>` | `/blind Steve` | Gjør skjermen mørk (Darkness + Blindness) i 10 sek |
| `/levitate <spiller>` | `/levitate Steve` | Får spilleren til å flyte oppover |
| `/anvil <spiller>` | `/anvil Steve` | Slipper et fallende ambolt over spilleren |
| `/scare <spiller>` | `/scare Steve` | Jump-scare (tittel + lyder) |
| `/spin <spiller>` | `/spin Steve` | Får spilleren til å snurre; bruk igjen for å stoppe |
| `/spam <spiller>` | `/spam Steve` | Fyller chatten deres med tull |
| `/jail <spiller>` | `/jail Steve` | Låser spilleren i et glassbur; bruk igjen for å løslate |
| `/tnt <spiller>` | `/tnt Steve` | Spawner TNT ved føttene deres |
| `/invclear <spiller>` | `/invclear Steve` | Tømmer inventaret deres |
| `/mute <spiller>` | `/mute Steve` | Toggle mute – spilleren kan ikke skrive i chat |
| `/spawnvillager <type>` | `/spawnvillager scammer` | Spawner en troll-landsbyboer (scammer, jester, tax, boomer, glitch) |
| `/tardis setentrance` | Stå inni huset, rett mot der døra skal være | Setter TARDIS-inngang og plasserer egen jerndør (lite hus → stort rom) |
| `/tardis setinterior` | Stå i det store rommet | Setter hvor spillere teleporteres når de går inn |
| `/tardis setexit` | Stå på utgangsblokken inne | Setter hvor man står for å komme tilbake |
| `/tardis info` | `/tardis info` | Viser inngang, interiør, utgang og dør |

**Tilgang:** `/vault`, `/sethome`, `/home` er åpne for alle spillere. Øvrige kommandoer krever OP (eller tilsvarende permission). `/lagerkiste` og `/frys` er i praksis for **Admin_owner**.

---

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
- **Nye våpen:** Dommens Sverd, Vakuumbue, Kaosstav, Gravitasjonsøks, Blodtrident, Tidsknuser.
- **Ny rustning:** Operator Kappe (immun fall), Void Hjelm (Night Vision + glow), Titan Brystplate (50 % mindre skade), Magnetbukse (trekker drops), Froststøvler (fryser vann).
- **Spesialitems:** Adminstav (GUI kick/frys/heal/clear), Tidsklokke (5 sek rewind), Voidperle (teleporter til Y=-64), Massehealer (heal 10 blokker), Verdensbryter (3x3/5x5 mining).
- **Admin/Troll:** Fake Ban-bok, Tordenværstav, Frysbombe, Inverterstav (WASD byttes), Size Orb (liten/gigant).
- **Mythic:** Void Kongekrone (creative flight + resist), Dommedagsknapp (sletter alle mobs).
- **Nye kommandoer:** `/opmode`, `/troll <spiller>`, `/boost`, `/arena`, `/spectateplus <spiller>`.
- **Homes + Vault:** `/sethome <1|2|3>` lagrer tre home-slots, `/home <1|2|3>` teleporterer, `/vault` åpner privat vault per spiller (MySQL).
- **Rank item system:** `Admin_owner` kan lage rank-items via `/rankitem create <rank>`. Høyreklikk bruker itemet, setter rank og itemet forsvinner.
- **Admin_owner join-buff:** Får Fire Resistance 100 i 10 minutter hver gang han joiner.
- **Admin_owner buff toggle:** `/faderfaling <på|av>` og `/fireresistance <på|av>` styrer join-buffs.
- **OP-fun (4.0.8):** `/inventory view <spiller>`, `/blind`, `/levitate`, `/anvil`, `/scare`, `/spin`, `/spam`, `/jail`, `/tnt`, `/invclear`, `/mute`.
- **Troll-landsbyboere (4.0.8):** `/spawnvillager <type>` – Svindleren (magiske diamanter → kull/potet), Gjøgleren (splash-potion + løp), Skatteinnkreveren (tar 1 smaragd/min), Eksplosiv Selger (creeper-lyd ved handel), Glitchen (oppskrifter byttes hele tiden). Troll-varer: Magiske diamanter, Uknuselig hakke, Teleport-eple, Hemsko-støvler.
- **TARDIS-hus (4.1.1):** `/tardis setentrance` plasserer en egen jerndør og setter inngangen – lite hus utvendig, stort rom innvendig. `/tardis setinterior` bygger et stort lukket rom (gulv, vegger, tak) – utsiden ses ikke. Kun når døra er åpen kan man gå inn. `/tardis setexit` for utgang inne.
- **Nano Banana (4.1.5):** OP-pistol – høyreklikk skyter en gul «kule» med muzzle-flash, gul/oransje trail under flukt og treff-partikler. Virker overalt ( også uten å peke på blokk). Rustning har kule armor-trim og partikler når du har OP-rustning på.

---

## Gamle ting vs. nye ting (for git / release)

**Opprinnelige / gamle ting (før 4.1.1):**
- `/lager`, `/lager give`, `/lager gear`, `/lager list`, `/lager instillinger`
- `/lagerkiste` (Admin_owner), `/kiste` (OP-kiste med sider)
- `/frys` (Admin_owner) + bytte-hender-tasten
- Våpen: Storm Sverd, Dødspiler, Tordenøks, Krossbue, Havets Vrede (trident)
- Rustning: OP hjelm/bryst/bukse/støvler, Flygestøvler
- Verktøy: OP spade, hakke, skjold, elytra, totem, gulleneple
- Spesial: Flygestav (flygkølle), Matheo client (instillinger), Totem-stakk
- Tordenøks på villager → belønning; Havets Vrede → lyn; Admin_owner frys

**Nye ting (4.0.6–4.1.1):**
- **Kommandoer:** `/opmode`, `/troll <spiller>`, `/boost`, `/arena`, `/spectateplus <spiller>`, `/inventory view`, `/blind`, `/levitate`, `/anvil`, `/scare`, `/spin`, `/spam`, `/jail`, `/tnt`, `/invclear`, `/mute`, `/spawnvillager`, `/tardis`
- **Våpen:** Dommens Sverd, Vakuumbue, Kaosstav, Gravitasjonsøks, Blodtrident, Tidsknuser
- **Rustning:** Operator Kappe, Void Hjelm, Titan Brystplate, Magnetbukse, Froststøvler, Void Kongekrone
- **Spesial:** Adminstav (GUI), Tidsklokke (rewind), Voidperle, Massehealer, Verdensbryter
- **Admin/Troll:** Fake Ban-bok, Tordenværstav, Frysbombe, Inverterstav, Size Orb, Dommedagsknapp
- **Troll-landsbyboere (4.0.8):** Svindleren, Gjøgleren, Skatteinnkreveren, Eksplosiv Selger, Glitchen. Troll-varer: Magiske diamanter, Uknuselig hakke, Teleport-eple, Hemsko-støvler
- **TARDIS-hus (4.1.1):** Egen jerndør ved setentrance; setinterior bygger stort lukket rom (utsiden ses ikke). Kun åpen dør = innpass.
- **4.1.2–4.1.5:** Kule armor-trim på OP-rustning og spesialrustning; partikler når du har OP-rustning på. **Nano Banana** (pistol): gul kule med muzzle-flash, trail og treff-grafikk; skyte uten å peke på blokk (HIGHEST prioritet).

---

## Matheo client (innstillinger)

Åpnes med **shift** mens du holder **Matheo client**-itemet (klokke), eller med `/lager instillinger`. Kun for OP.

- **Armor reach:** +/- for rekkevidde når du har OP-brystplade. «Vanlig» = ingen ekstra reach.
- **Full bright:** Slå på/av Night Vision (lys i mørke).

## Kommandoer (detaljert)

| Kommando | Beskrivelse | Hvem |
|----------|-------------|------|
| `/lager` | Vis liste over alle OP-items | OP |
| `/lager list` | Samme som `/lager` | OP |
| `/lager give <id>` | Gi deg ett OP-item (f.eks. `storm_sverd`, `flygkølle`, `dommersverd`) | OP |
| `/lager gear` | Gi deg hele OP-utstyret (rustning + våpen) | OP |
| `/lager instillinger` | Åpne Matheo client (armor reach, full bright) | OP |
| `/lagerkiste` | Åpne admin-kisten fylt med OP-items | Kun **Admin_owner** |
| `/kiste` | Åpne OP-kisten (uendelig gear, refylles når du tar) | OP |
| `/frys` | Slå frys på/av (fryser spillere og entities rundt deg) | Kun **Admin_owner** |
| `/opmode` [sek] | Midlertidig OP-powers (creative, speed, night vision) | OP |
| `/troll <spiller>` | Random troll-effekt på spiller (levitation, poison, lyn, etc.) | OP |
| `/boost` [spiller] | Midlertidig OP-kit (strength, speed, regen 30 sek) | OP |
| `/arena` | Teleporterer til PvP-arena (koordinater i config) | OP |
| `/spectateplus <spiller>` | Spectate-modus, teleporterer til spiller | OP |
| `/vault` | Åpner din private vault (lagres i MySQL) | Alle |
| `/sethome <1|2|3>` | Setter home-slot 1, 2 eller 3 (lagres i `homes.yml`) | Alle |
| `/home <1|2|3>` | Teleporterer til valgt home-slot | Alle |
| `/rankitem create <rank>` | Lager rank-item (plugin-intern rank i `ranks.yml`) | Kun **Admin_owner** |
| `/faderfaling <på|av>` | Slår Slow Falling ved join på/av | Kun **Admin_owner** |
| `/fireresistance <på|av>` | Slår Fire Resistance ved join på/av | Kun **Admin_owner** |
| `/inventory view <spiller>` | Se og ta/legge inn ting i spillers inventar | OP |
| `/blind <spiller>` | Mørk skjerm (Darkness + Blindness) 10 sek | OP |
| `/levitate <spiller>` | Spilleren flyter oppover | OP |
| `/anvil <spiller>` | Fallende ambolt over spiller | OP |
| `/scare <spiller>` | Jump-scare (tittel + lyder) | OP |
| `/spin <spiller>` | Spilleren snurrer; bruk igjen for å stoppe | OP |
| `/spam <spiller>` | Fyll chatten med tull | OP |
| `/jail <spiller>` | Glassbur rundt spiller; bruk igjen for å fjerne | OP |
| `/tnt <spiller>` | TNT ved føttene | OP |
| `/invclear <spiller>` | Tøm spillers inventar | OP |
| `/mute <spiller>` | Toggle mute i chat | OP |
| `/spawnvillager <type>` | Spawn troll-landsbyboer (scammer, jester, tax, boomer, glitch) | OP |
| `/tardis setentrance` | Setter inngang og plasserer egen TARDIS-dør (jerndør) | OP |
| `/tardis setinterior` | Setter det store rommet (dit spillere teleporteres) | OP |
| `/tardis setexit` | Setter utgangsblokken inne (stå her for å komme tilbake) | OP |
| `/tardis info` | Viser inngang, interiør, utgang og dør | OP |

Admin_owner kan også bruke **bytte-hender**-tasten (f.eks. R) for å slå frys på/av.

### TARDIS-hus (`/tardis`)

Lite hus utvendig, stort rom innvendig – **utsiden ses ikke** inne. **Egen jerndør** plasseres ved `/tardis setentrance` (stå inni huset, rett mot der døra skal være). `/tardis setinterior` bygger automatisk et stort lukket rom (gulv, vegger, tak) rundt deg – 21×21 blokker, 6 blokker høyt. Kun når døra er **åpen** kan spillere gå inn og teleporteres dit. Sett utgang med `setexit` (én blokk inne som sender tilbake til inngangen).

### Troll-landsbyboere (`/spawnvillager`)

Alle troll-landsbyboere kan **alltid** høyreklikkes for å åpne handel (uavhengig av hva du holder i hånden – tordenøks, flygestav, adminstav osv. blokkerer ikke handel med troll-villagere).

| Type | Navn | Oppførsel |
|------|------|------------|
| `scammer` | Svindleren | Selger «Magiske diamanter» for 5 smaragder – blir til kull eller malt potet i inventar. |
| `jester` | Gjøgleren | Løper fort og hopper. Ved handel: kaster splash-potion på deg og lukker handelen. |
| `tax` | Skatteinnkreveren | Tar 1 smaragd per minutt fra spillere innen 5 blokker. |
| `boomer` | Eksplosiv Selger | Selger Netherite-ingot for 1 smaragd; creeper-lyd når du handler. |
| `glitch` | Glitchen | Oppskriftene byttes ca. hvert 2. sekund mens handel er åpen. |

Troll-varer (fra Glitchen/Svindleren): **Magiske diamanter** (→ kull/potet), **Uknuselig hakke** (1 bruk), **Teleport-eple** (tilfeldig teleport innen 50 blokker), **Hemsko-støvler** (Slowness X + Curse of Binding).

## OP-items (og innhold i kista)

Disse itemene kan gis med `/lager give <id>` og finnes i **OP-kisten** (`/kiste`) og **admin-kisten** (`/lagerkiste`).

### Våpen

| Id | Navn | Beskrivelse |
|----|------|-------------|
| `storm_sverd` | Storm Sverd | Netherite-sverd, høye enchantments |
| `dommersverd` | Dommens Sverd | Dreper spillere under 5 hjerter umiddelbart |
| `vakuumbue` | Vakuumbue | Trekker entity mot pila der den lander |
| `kaosstav` | Kaosstav | Høyreklikk = tilfeldig effekt (Strength, Levitation, Wither, Speed, etc.) |
| `gravitasjonsøks` | Gravitasjonsøks | Slår mobs opp i lufta |
| `blodtrident` | Blodets Trident | Gir deg liv tilbake basert på damage |
| `tidsknuser` | Tidsknuser | Treffer = Slowness 10 i 3 sek |
| `dødspiler` | Dødspiler | Kraftig bue |
| `tordenøks` | Tordenøks | Netherite-øks; høyreklikk på villager som OP gir belønning |
| `krossbue` | Mester-krossbue | Krossbue med høye enchantments |
| `trident` | Havets Vrede | Trident; lyn slår ned der den treffer |
| `pistol` | Nano Banana | Høyreklikk skyter gul kule med muzzle/trail/treff-grafikk; virker overalt (uten å peke på blokk) |

### Rustning

| Id | Navn | Effekt |
|----|------|--------|
| `op_helm` | Operator Hjelm | +20 max liv; gull armor-trim; partikler når på |
| `op_bryst` | Operator Brystplade | Ekstra reach (antall blokker styres i Matheo client); gull trim |
| `op_bukser` | Operator Bukse | Styrke II + vanndråper; gull trim |
| `op_støvler` | Operator Støvler | Speed II; gull trim |
| `op_kappe` | Operator Kappe | Immun mot fall damage |
| `void_helm` | Void Hjelm | Night Vision + ser spillere gjennom vegger (glow innen 64 blokker) |
| `titan_bryst` | Titan Brystplate | 50 % mindre damage |
| `magnet_bukse` | Magnetbukse | Trekker drops automatisk til deg |
| `frost_støvler` | Froststøvler | Fryser vann når du går (Frost Walker + permanent) |
| `op_rustning_sett` | (spesial) | Gir hele settet med `/lager give op_rustning_sett` |
| `fly_støvler` | Flygestøvler | Jernstøvler – fly som i creative |
| `void_kongekrone` | Void Kongekrone (Mythic) | Creative flight + Resistance III |

### Verktøy og annet

| Id | Navn | Beskrivelse |
|----|------|-------------|
| `op_spade` | Operator Spade | Netherite-spade, høye enchantments |
| `op_pickaxe` | Operator Hakke | Netherite-hakke, høye enchantments |
| `verdensbryter` | Verdensbryter | 3x3 eller 5x5 mining (sneak for 5x5) |
| `op_skjold` | Operator Skjold | Skjold med Unbreaking |
| `op_elytra` | Operator Elytra | Elytra med Unbreaking/Mending |
| `op_totem` | Operator Totem | Totem of Undying |
| `op_gulleneple` | Operator Gulleneple | Enchanted Golden Apple |

### Spesialitems

| Id | Navn | Bruk |
|----|------|------|
| `flygkølle` | Flygestav | Høyreklikk = ta entity med deg, shift = slippe. På villager åpnes ikke trade. |
| `admin_stav` | Adminstav | Høyreklikk spiller = GUI med kick, frys, heal, clear inv, tp |
| `rewind_klokke` | Tidsklokke | Shift + høyreklikk = 5 sek tilbake (posisjon + HP) |
| `void_perle` | Voidperle | Høyreklikk spiller = teleporterer dem til Y = -64 |
| `massehealer` | Massehealer | Høyreklikk = healer alle innen 10 blokker |
| `instillinger` | Matheo client | Klokke – shift åpner innstillinger (armor reach, full bright). |
| `totem_stakk` | Totem-stakk | Papir (16 stk) – høyreklikk gir 1 Operator Totem. |

### Admin / Troll

| Id | Navn | Effekt |
|----|------|--------|
| `fakeban` | Fake Ban-bok | Høyreklikk spiller = viser «You are banned» (kick) |
| `torden_regn` | Tordenværstav | Høyreklikk = lynstorm |
| `frysbombe` | Frysbombe | Kast snøball = fryser alle spillere i radius 10 sek |
| `inverter` | Inverterstav | Høyreklikk spiller = bytter WASD i 10 sek |
| `størrelse_orb` | Size Orb | Høyreklikk spiller = liten / gigantisk (krever Paper 1.20.5+) |
| `dommedagsknapp` | Dommedagsknapp | Høyreklikk = sletter alle mobs i alle verdener |

## Requirements

- **Java 21**
- **Gradle** (or use the included wrapper)
- **MySQL 8+** (for `/vault`)

## MySQL setup for /vault (step-by-step)

1. Opprett database og bruker i MySQL:
   ```sql
   CREATE DATABASE lager;
   CREATE USER 'lager_user'@'%' IDENTIFIED BY 'STRONG_PASSWORD';
   GRANT ALL PRIVILEGES ON lager.* TO 'lager_user'@'%';
   FLUSH PRIVILEGES;
   ```
2. I `config.yml`, sett:
   - `mysql.enabled: true`
   - `mysql.host`, `mysql.port`, `mysql.database`, `mysql.user`, `mysql.password`, `mysql.ssl`
3. Start serveren på nytt.
4. Sjekk logg etter oppstart:
   - `Connected to MySQL and ensured player_vaults table exists`
5. Test lagring:
   - Kjør `/vault`, legg inn item, lukk vault
   - Restart server
   - Åpne `/vault` igjen og verifiser at item fortsatt er der

### Troubleshooting

- Hvis `/vault` sier at mysql er av: sett `mysql.enabled: true`.
- Hvis vault ikke lagrer: sjekk database-tilgang (firewall, host/port, user/pass).
- Hvis serveren bruker flere noder: bruk samme database for delt vault-data.

## Building

```bash
.\gradlew jar
```

Output: `build/libs/potato-4.1.5.jar` (versjon står i `build.gradle.kts`).

## Deploying to a server

1. Build the plugin (see above):
   ```bash
   .\gradlew jar
   ```
2. Copy the JAR to the server’s `plugins/` folder. Use a **binary** transfer so the file isn’t corrupted:
   - **SCP (Linux/macOS or WSL):**  
     `scp build/libs/potato-4.1.5.jar user@yourserver:~/minecraft/plugins/`
   - **WinSCP / SFTP:** transfer in “binary” mode (default for .jar).
   - **Other:** copy `build/libs/potato-4.1.5.jar` as-is; do not paste contents or use a text transfer.

If the server logs **`zip END header not found`** for the plugin, the JAR was corrupted during copy. Re-copy the file as binary and try again. Current build output: `potato-4.1.5.jar`.

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
