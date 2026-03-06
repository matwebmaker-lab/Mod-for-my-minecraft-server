Pistol (Nano Banana) – resource pack for Lager/OP Gear
=====================================================

Bruk:
1. Zip mappen "resourcepack" (så pack.mcmeta ligger i roten av zip-filen).
2. Legg zip-filen i .minecraft/resourcepacks/ (eller server resource pack).
3. Aktiver pack i Minecraft (Options → Resource Packs).

Pluginen setter Custom Model Data 19001 på Nano Banana (blaze rod).
Denne packen viser da en 3D pistol-modell (tre kuber: skrog, løp, grep)
med crossbow_standby som placeholder-tekstur.

3D-modellen:
- pistol.json er generert fra colt_1911.glb (Colt 1911 med silenser + lommelykt,
  MKoegler3D, Sketchfab, CC Attribution) ved voxelisering.
- For å regenerere: pip install trimesh numpy, deretter
  python resourcepack/source/glb_to_minecraft.py resourcepack/source/colt_1911.glb
  og kopier resourcepack/source/pistol_minecraft.json til
  resourcepack/assets/lager/models/item/pistol.json.

Egen pistol-tekstur:
- Lag en 16x16 eller 32x32 PNG og lagre som:
  assets/lager/textures/item/pistol.png
- I assets/lager/models/item/pistol.json, endre "layer0" til:
  "layer0": "lager:item/pistol"
- Pakk på nytt og aktiver.

Pack format 34 = Minecraft 1.21.x.
