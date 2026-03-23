package no.lager.lager.storage;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class HomeStorage {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;

    public HomeStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setHome(UUID playerId, int slot, Location loc) {
        String base = base(playerId, slot);
        config.set(base + ".world", loc.getWorld() != null ? loc.getWorld().getName() : null);
        config.set(base + ".x", loc.getX());
        config.set(base + ".y", loc.getY());
        config.set(base + ".z", loc.getZ());
        config.set(base + ".yaw", loc.getYaw());
        config.set(base + ".pitch", loc.getPitch());
        save();
    }

    public Location getHome(UUID playerId, int slot) {
        String base = base(playerId, slot);
        String worldName = config.getString(base + ".world");
        if (worldName == null || worldName.isBlank()) return null;
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;
        return new Location(
                world,
                config.getDouble(base + ".x"),
                config.getDouble(base + ".y"),
                config.getDouble(base + ".z"),
                (float) config.getDouble(base + ".yaw"),
                (float) config.getDouble(base + ".pitch")
        );
    }

    public boolean hasHome(UUID playerId, int slot) {
        String base = base(playerId, slot);
        return config.contains(base + ".world");
    }

    private String base(UUID playerId, int slot) {
        return "homes." + playerId + "." + slot;
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Kunne ikke lagre homes.yml: " + e.getMessage());
        }
    }
}
