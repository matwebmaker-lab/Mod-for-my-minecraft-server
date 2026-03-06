Pistol (Nano Banana) – resource pack for Lager/OP Gear
=====================================================

Bruk:
1. Zip mappen "resourcepack" (så pack.mcmeta ligger i roten av zip-filen).
2. Legg zip-filen i .minecraft/resourcepacks/ (eller server resource pack).
3. Aktiver pack i Minecraft (Options → Resource Packs).

Pluginen setter Custom Model Data 19001 på Nano Banana (blaze rod).
Denne packen viser da pistolen som en flat sprite (pixels) med handheld-form,
bruker crossbow_standby som placeholder-tekstur.

Egen pistol-tekstur (pixels):
- Lag en 16x16 eller 32x32 PNG og lagre som:
  assets/lager/textures/item/pistol.png
- I assets/lager/models/item/pistol.json, endre "layer0" til:
  "layer0": "lager:item/pistol"
- Pakk på nytt og aktiver.

Pack format 34 = Minecraft 1.21.x.
