package no.lager.lager.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class RankStorage {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    public RankStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "ranks.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setRank(UUID playerId, String rank) {
        config.set("players." + playerId + ".rank", rank);
        save();
    }

    public String getRank(UUID playerId) {
        return config.getString("players." + playerId + ".rank");
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Kunne ikke lagre ranks.yml: " + e.getMessage());
        }
    }
}
