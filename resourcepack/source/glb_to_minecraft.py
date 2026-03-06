#!/usr/bin/env python3
"""
Konverterer en GLB-fil (f.eks. Colt 1911) til Minecraft Java item-modell (JSON med elements).
Krever: pip install trimesh numpy
Bruk: python glb_to_minecraft.py colt_1911.glb
Output: pistol_minecraft.json (kopier til resourcepack/assets/lager/models/item/pistol.json)
"""
import json
import sys
import os

def main():
    try:
        import trimesh
        import numpy as np
    except ImportError:
        print("Installer avhengigheter: pip install trimesh numpy")
        sys.exit(1)

    if len(sys.argv) < 2:
        print("Bruk: python glb_to_minecraft.py <fil.glb>")
        sys.exit(1)

    glb_path = sys.argv[1]
    if not os.path.isfile(glb_path):
        print(f"Fil ikke funnet: {glb_path}")
        sys.exit(1)

    # Last GLB – kan være Scene med flere meshes
    scene = trimesh.load(glb_path, force="mesh")
    if isinstance(scene, trimesh.Scene):
        meshes = [g for g in scene.geometry.values() if isinstance(g, trimesh.Trimesh)]
        if not meshes:
            print("Ingen meshes i GLB")
            sys.exit(1)
        mesh = trimesh.util.concatenate(meshes)
    else:
        mesh = scene

    # Skaler og sentrer i boks 0–16
    bounds = mesh.bounds
    min_b, max_b = bounds[0], bounds[1]
    size = max_b - min_b
    scale = 14.0 / max(size)  # litt margin
    center = (min_b + max_b) / 2
    mesh.vertices -= center
    mesh.vertices *= scale
    mesh.vertices += np.array([8, 8, 8])

    # Voxeliser med pitch 1 (én voxel per Minecraft-koordinat)
    try:
        vox = mesh.voxelized(pitch=1.0)
    except Exception as e:
        print(f"Voxelisering feilet (prøv mindre modell eller annen pitch): {e}")
        sys.exit(1)

    # Hent fylte voxels – trimesh VoxelGrid har .matrix eller .points
    if hasattr(vox, "matrix"):
        mat = vox.matrix
        if hasattr(mat, "filled") and callable(getattr(mat, "filled", None)):
            filled = mat.filled(False)
        else:
            filled = np.asarray(mat)
        indices = np.argwhere(filled)
        if hasattr(vox, "transform"):
            # transform index til koordinat
            origin = vox.transform[:3, 3]
            for i in range(min(3, indices.shape[1])):
                indices[:, i] = indices[:, i] + int(round(origin[i]))
        else:
            pass  # indices er allerede i voxel-koordinater
    elif hasattr(vox, "points"):
        pts = vox.points
        indices = np.floor(pts).astype(int)
        indices = np.unique(indices, axis=0)
    else:
        print("Kunne ikke hente voxel-posisjoner fra VoxelGrid")
        sys.exit(1)

    # Begrens til 0–16 og fjern duplikater
    indices = np.clip(indices, 0, 15)
    indices = np.unique(indices, axis=0)

    # Bygg Minecraft elements (kuber 1x1x1)
    elements = []
    for idx in indices:
        x, y, z = int(idx[0]), int(idx[1]), int(idx[2])
        if not (0 <= x < 16 and 0 <= y < 16 and 0 <= z < 16):
            continue
        elem = {
            "from": [x, y, z],
            "to": [x + 1, y + 1, z + 1],
            "faces": {
                "north": {"uv": [0, 0, 16, 16], "texture": "#layer0"},
                "south": {"uv": [0, 0, 16, 16], "texture": "#layer0"},
                "east": {"uv": [0, 0, 16, 16], "texture": "#layer0"},
                "west": {"uv": [0, 0, 16, 16], "texture": "#layer0"},
                "up": {"uv": [0, 0, 16, 16], "texture": "#layer0"},
                "down": {"uv": [0, 0, 16, 16], "texture": "#layer0"},
            },
        }
        elements.append(elem)

    display = {
        "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375]},
        "thirdperson_lefthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375]},
        "firstperson_righthand": {"rotation": [0, 45, 0], "scale": [0.4, 0.4, 0.4]},
        "firstperson_lefthand": {"rotation": [0, 225, 0], "scale": [0.4, 0.4, 0.4]},
        "ground": {"translation": [0, 3, 0], "scale": [0.25, 0.25, 0.25]},
        "gui": {"rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.625, 0.625, 0.625]},
        "fixed": {"scale": [0.5, 0.5, 0.5]},
    }

    out = {
        "textures": {"layer0": "minecraft:item/crossbow_standby"},
        "elements": elements,
        "display": display,
    }

    out_path = os.path.join(os.path.dirname(glb_path), "pistol_minecraft.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(out, f, indent=2, ensure_ascii=False)

    print(f"Skrev {len(elements)} elementer til {out_path}")
    print("Kopier filen til resourcepack/assets/lager/models/item/pistol.json")

if __name__ == "__main__":
    main()
