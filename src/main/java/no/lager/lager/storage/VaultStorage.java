package no.lager.lager.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public final class VaultStorage implements AutoCloseable {

    private static final int VAULT_SIZE = 54;
    private static final String TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS player_vaults (
                player_uuid VARCHAR(36) PRIMARY KEY,
                vault_blob MEDIUMTEXT NOT NULL,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

    private final JavaPlugin plugin;
    private final HikariDataSource dataSource;
    private final boolean enabled;
    private final File yamlFile;
    private final FileConfiguration yamlConfig;

    public VaultStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.yamlFile = new File(plugin.getDataFolder(), "vaults.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);
        this.enabled = plugin.getConfig().getBoolean("mysql.enabled", false);
        HikariDataSource tmpDataSource = null;
        if (!enabled) {
            plugin.getLogger().warning("[Vault] MySQL er av. Bruker lokal vaults.yml fallback.");
        } else {
            HikariConfig cfg = new HikariConfig();
            String host = plugin.getConfig().getString("mysql.host", "127.0.0.1");
            int port = plugin.getConfig().getInt("mysql.port", 3306);
            String db = plugin.getConfig().getString("mysql.database", "lager");
            String user = plugin.getConfig().getString("mysql.user", "lager_user");
            String pass = plugin.getConfig().getString("mysql.password", "");
            boolean ssl = plugin.getConfig().getBoolean("mysql.ssl", false);
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db +
                    "?useSSL=" + ssl + "&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            cfg.setJdbcUrl(jdbcUrl);
            cfg.setUsername(user);
            cfg.setPassword(pass);
            cfg.setMaximumPoolSize(8);
            cfg.setMinimumIdle(1);
            cfg.setConnectionTimeout(10000);
            cfg.setPoolName("LagerVaultPool");

            try {
                tmpDataSource = new HikariDataSource(cfg);
                ensureTable(tmpDataSource);
                plugin.getLogger().info("[Vault] Connected to MySQL and ensured player_vaults table exists.");
            } catch (Exception e) {
                plugin.getLogger().warning("[Vault] MySQL feilet (" + e.getMessage() + "). Bruker lokal vaults.yml fallback.");
            }
        }
        this.dataSource = tmpDataSource;
    }

    public boolean isEnabled() {
        return true;
    }

    public ItemStack[] loadVault(UUID playerId) {
        ItemStack[] empty = new ItemStack[VAULT_SIZE];
        if (!enabled || dataSource == null) return loadVaultFromYaml(playerId);

        String sql = "SELECT vault_blob FROM player_vaults WHERE player_uuid = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return empty;
                String blob = rs.getString("vault_blob");
                if (blob == null || blob.isBlank()) return empty;
                return deserialize(blob);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[Vault] load failed for " + playerId + ": " + e.getMessage());
            return loadVaultFromYaml(playerId);
        }
    }

    public boolean saveVault(UUID playerId, ItemStack[] contents) {
        if (!enabled || dataSource == null) return saveVaultToYaml(playerId, contents);
        String sql = "INSERT INTO player_vaults(player_uuid, vault_blob) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE vault_blob = VALUES(vault_blob)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, playerId.toString());
            ps.setString(2, serialize(contents));
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("[Vault] save failed for " + playerId + ": " + e.getMessage());
            return saveVaultToYaml(playerId, contents);
        }
    }

    private ItemStack[] loadVaultFromYaml(UUID playerId) {
        String path = "vaults." + playerId + ".contents";
        List<?> list = yamlConfig.getList(path);
        ItemStack[] out = new ItemStack[VAULT_SIZE];
        if (list == null) return out;
        int limit = Math.min(VAULT_SIZE, list.size());
        for (int i = 0; i < limit; i++) {
            Object obj = list.get(i);
            if (obj instanceof ItemStack item) out[i] = item;
        }
        return out;
    }

    private boolean saveVaultToYaml(UUID playerId, ItemStack[] contents) {
        String path = "vaults." + playerId + ".contents";
        yamlConfig.set(path, contents);
        try {
            yamlConfig.save(yamlFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("[Vault] YAML save failed for " + playerId + ": " + e.getMessage());
            return false;
        }
    }

    private void ensureTable(HikariDataSource ds) {
        if (ds == null) return;
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(TABLE_SQL)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("[Vault] Failed to ensure table: " + e.getMessage());
        }
    }

    private String serialize(ItemStack[] items) throws Exception {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             BukkitObjectOutputStream out = new BukkitObjectOutputStream(byteOut)) {
            out.writeInt(items.length);
            for (ItemStack item : items) {
                out.writeObject(item);
            }
            out.flush();
            return Base64.getEncoder().encodeToString(byteOut.toByteArray());
        }
    }

    private ItemStack[] deserialize(String value) {
        ItemStack[] empty = new ItemStack[VAULT_SIZE];
        try (ByteArrayInputStream base64In = new ByteArrayInputStream(Base64.getDecoder().decode(value));
             BukkitObjectInputStream in = new BukkitObjectInputStream(base64In)) {
            int len = in.readInt();
            ItemStack[] out = new ItemStack[VAULT_SIZE];
            int limit = Math.min(VAULT_SIZE, len);
            for (int i = 0; i < limit; i++) {
                Object obj = in.readObject();
                if (obj instanceof ItemStack item) out[i] = item;
            }
            return out;
        } catch (Exception e) {
            plugin.getLogger().warning("[Vault] deserialize failed: " + e.getMessage());
            return empty;
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
