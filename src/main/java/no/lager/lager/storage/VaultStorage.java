package no.lager.lager.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class VaultStorage {

    private static final int VAULT_SIZE = 54;

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;

    public VaultStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "vaults.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public ItemStack[] loadVault(UUID playerId) {
        String path = "vaults." + playerId + ".contents";
        List<?> list = config.getList(path);
        ItemStack[] out = new ItemStack[VAULT_SIZE];
        if (list == null) return out;
        int limit = Math.min(VAULT_SIZE, list.size());
        for (int i = 0; i < limit; i++) {
            Object obj = list.get(i);
            if (obj instanceof ItemStack item) {
                out[i] = item;
            }
        }
        return out;
    }

    public void saveVault(UUID playerId, ItemStack[] contents) {
        String path = "vaults." + playerId + ".contents";
        config.set(path, contents);
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Kunne ikke lagre vaults.yml: " + e.getMessage());
        }
    }
}
