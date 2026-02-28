# Lager – OP-items for Paper 1.21.11

Plugin for [Paper](https://papermc.io/software/paper/) 1.21.11 som legger til spesielle våpen og rustninger som **kun operatorer (OP) kan se og bruke**.

## Funksjoner

- **Kun OP kan bruke** kommandoen og eie itemene.
- Hvis en ikke-OP prøver å plukke opp en OP-item (f.eks. fra bakken), fjernes itemet – dermed «ser» bare OP disse itemene i praksis.
- Liste med sterke våpen og rustninger med høye enchantments.

## Kommandoer

| Kommando | Beskrivelse |
|----------|-------------|
| `/lager` | Viser liste over alle OP-items |
| `/lager list` | Samme som `/lager` |
| `/lager give <id>` | Gir deg OP-item med angitt id (kun for OP) |

## OP-items (liste)

| ID | Beskrivelse |
|----|-------------|
| `storm_sverd` | Storm Sverd – Netherite sverd med Sharpness 10, Fire Aspect, Looting, osv. |
| `dødspiler` | Dødspiler – Bue med Power 10, Flame, Infinity, Punch |
| `tordenøks` | Tordenøks – Netherite øks |
| `krossbue` | Mester-krossbue – Quick Charge, Multishot, Piercing |
| `trident` | Havets Vrede – Trident med Impaling, Loyalty |
| `op_helm` | Operator Hjelm – Netherite hjelm med Protection 8 |
| `op_bryst` | Operator Brystplade |
| `op_bukser` | Operator Bukse |
| `op_støvler` | Operator Støvler – med Feather Falling, Depth Strider |
| `op_rustning_sett` | Gir hele rustningsettet (helm, bryst, bukse, støvler) |

## Bygging

Krever **Java 21** og **Gradle**.

1. Installer [Gradle](https://gradle.org/install/) hvis du ikke har det.
2. Generer wrapper (første gang):
   ```bash
   gradle wrapper
   ```
3. Bygg plugin-JAR:
   ```bash
   .\gradlew jar
   ```
4. JAR-filen ligger i `build/libs/Lager-1.0.0.jar`. Kopier den til serverens `plugins`-mappe og start serveren på nytt.

## Installasjon på server

1. Bygg JAR (se over) eller last ned ferdig bygget JAR.
2. Legg `Lager-1.0.0.jar` i `plugins`-mappen på Paper-serveren.
3. Start eller restart serveren.
4. Bruk `/lager` som OP for å se og gi OP-items.

## Krav

- Paper 1.21.11 (Minecraft 1.21.11)
- Java 21

## Testing av pluginen

1. **Start serveren** (fra prosjektmappen):
   ```bash
   .\gradlew runServer
   ```
2. **Åpne Minecraft 1.21.11** og legg til server: `localhost` (eller `127.0.0.1`).
3. **Koble til** og vent til du er i verden.
4. **Gjør deg selv OP**: I terminalen der serveren kjører, skriv:
   ```bash
   op DittMinecraftBrukernavn
   ```
   (Bytt ut med brukernavnet du spiller med.)
5. **I spillet**: Skriv `/lager` for å se listen, deretter f.eks. `/lager give storm_sverd` eller `/lager give op_rustning_sett`.

## Tillatelser

- `lager.use` – bruk av `/lager` (standard: `op`, dvs. kun operatorer).
- `lager.adminchest` – åpne admin-kisten med `/lagerkiste` (kun Admin_owner).

## Publisere på GitHub

1. Opprett et **nytt repository** på [GitHub](https://github.com/new) (f.eks. `Lager` eller `lager-paper-plugin`).  
   Velg **ikke** «Add a README» – prosjektet har allerede filer.

2. Koble det lokale prosjektet til GitHub og push (bytt ut `DITT_BRUKERNAVN` og `REPO-NAVN`):

   ```bash
   cd "d:\Projects\Lager mod til paper server"
   git remote add origin https://github.com/DITT_BRUKERNAVN/REPO-NAVN.git
   git branch -M main
   git push -u origin main
   ```

   Hvis du bruker SSH: `git@github.com:DITT_BRUKERNAVN/REPO-NAVN.git`
