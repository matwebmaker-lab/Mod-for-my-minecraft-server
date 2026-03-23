package no.lager.lager;

import no.lager.lager.commands.AdminChestCommand;
import no.lager.lager.commands.AdminBuffCommand;
import no.lager.lager.commands.FrysCommand;
import no.lager.lager.commands.InventoryCommand;
import no.lager.lager.commands.KisteCommand;
import no.lager.lager.commands.LagerCommand;
import no.lager.lager.commands.MuteCommand;
import no.lager.lager.commands.OpExtrasCommand;
import no.lager.lager.commands.HomeCommand;
import no.lager.lager.commands.SetHomeCommand;
import no.lager.lager.commands.SpawnVillagerCommand;
import no.lager.lager.commands.TardisCommand;
import no.lager.lager.commands.OpFunCommand;
import no.lager.lager.commands.RankItemCommand;
import no.lager.lager.commands.VaultCommand;
import no.lager.lager.listeners.OpArmorEffectsListener;
import no.lager.lager.listeners.OpItemListener;
import no.lager.lager.listeners.FlygkølleListener;
import no.lager.lager.listeners.OpKisteRefillListener;
import no.lager.lager.inventory.LagerSettingsHolder;
import no.lager.lager.listeners.LagerSettingsListener;
import no.lager.lager.listeners.TotemStakkListener;
import no.lager.lager.listeners.AdminFreezeListener;
import no.lager.lager.listeners.AdminOwnerJoinBuffListener;
import no.lager.lager.listeners.NewOpWeaponsListener;
import no.lager.lager.listeners.PistolListener;
import no.lager.lager.listeners.RankItemListener;
import no.lager.lager.listeners.NewOpArmorListener;
import no.lager.lager.listeners.NewOpSpecialListener;
import no.lager.lager.listeners.NewOpTrollListener;
import no.lager.lager.listeners.TridentLightningListener;
import no.lager.lager.listeners.MuteListener;
import no.lager.lager.listeners.TardisListener;
import no.lager.lager.listeners.TrollVillagerListener;
import no.lager.lager.listeners.VillagerAxeListener;
import no.lager.lager.storage.HomeStorage;
import no.lager.lager.storage.RankStorage;
import no.lager.lager.storage.VaultStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LagerPlugin extends JavaPlugin {

    private static final int ARMOR_REACH_MIN = 0;  // 0 = vanlig (ingen ekstra reach)
    private static final int ARMOR_REACH_MAX = 20;
    private static final String CONFIG_ARMOR_REACH = "armor_reach";
    private VaultStorage vaultStorage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("lager").setExecutor(new LagerCommand(this));
        getCommand("lagerkiste").setExecutor(new AdminChestCommand(this));
        KisteCommand kisteCommand = new KisteCommand(this);
        getCommand("kiste").setExecutor(kisteCommand);
        InventoryCommand inventoryCommand = new InventoryCommand();
        getCommand("inventory").setExecutor(inventoryCommand);
        getCommand("inventory").setTabCompleter(inventoryCommand);
        AdminFreezeListener adminFreezeListener = new AdminFreezeListener(this);
        getServer().getPluginManager().registerEvents(adminFreezeListener, this);
        getServer().getPluginManager().registerEvents(new AdminOwnerJoinBuffListener(this), this);
        getCommand("frys").setExecutor(new FrysCommand(adminFreezeListener));
        AdminBuffCommand faderFalingCommand = new AdminBuffCommand(this, "admin_owner.slow_falling_on_join", "faderfaling");
        getCommand("faderfaling").setExecutor(faderFalingCommand);
        getCommand("faderfaling").setTabCompleter(faderFalingCommand);
        AdminBuffCommand fireResCommand = new AdminBuffCommand(this, "admin_owner.fire_resistance_on_join", "fireresistance");
        getCommand("fireresistance").setExecutor(fireResCommand);
        getCommand("fireresistance").setTabCompleter(fireResCommand);
        OpExtrasCommand opExtras = new OpExtrasCommand(this);
        getCommand("opmode").setExecutor(opExtras);
        getCommand("opmode").setTabCompleter(opExtras);
        getCommand("troll").setExecutor(opExtras);
        getCommand("troll").setTabCompleter(opExtras);
        getCommand("boost").setExecutor(opExtras);
        getCommand("boost").setTabCompleter(opExtras);
        getCommand("arena").setExecutor(opExtras);
        getCommand("spectateplus").setExecutor(opExtras);
        getCommand("spectateplus").setTabCompleter(opExtras);
        HomeStorage homeStorage = new HomeStorage(this);
        SetHomeCommand setHomeCommand = new SetHomeCommand(homeStorage);
        HomeCommand homeCommand = new HomeCommand(homeStorage);
        getCommand("sethome").setExecutor(setHomeCommand);
        getCommand("sethome").setTabCompleter(setHomeCommand);
        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);
        vaultStorage = new VaultStorage(this);
        VaultCommand vaultCommand = new VaultCommand(this, vaultStorage);
        getCommand("vault").setExecutor(vaultCommand);
        getServer().getPluginManager().registerEvents(vaultCommand, this);
        RankStorage rankStorage = new RankStorage(this);
        RankItemCommand rankItemCommand = new RankItemCommand();
        getCommand("rankitem").setExecutor(rankItemCommand);
        getCommand("rankitem").setTabCompleter(rankItemCommand);
        getServer().getPluginManager().registerEvents(new RankItemListener(this, rankStorage), this);
        OpFunCommand opFun = new OpFunCommand(this);
        for (String cmd : new String[]{"blind", "levitate", "anvil", "scare", "spin", "spam", "jail", "tnt", "invclear"}) {
            getCommand(cmd).setExecutor(opFun);
            getCommand(cmd).setTabCompleter(opFun);
        }
        Set<UUID> mutedPlayers = ConcurrentHashMap.newKeySet();
        MuteCommand muteCommand = new MuteCommand(mutedPlayers);
        getCommand("mute").setExecutor(muteCommand);
        getCommand("mute").setTabCompleter(muteCommand);
        getServer().getPluginManager().registerEvents(new MuteListener(mutedPlayers), this);
        getServer().getPluginManager().registerEvents(new OpItemListener(this), this);
        getServer().getPluginManager().registerEvents(new OpKisteRefillListener(this, kisteCommand), this);
        getServer().getPluginManager().registerEvents(new FlygkølleListener(this), this);
        getServer().getPluginManager().registerEvents(new TridentLightningListener(this), this);
        getServer().getPluginManager().registerEvents(new TotemStakkListener(this), this);
        getServer().getPluginManager().registerEvents(new VillagerAxeListener(this), this);
        getServer().getPluginManager().registerEvents(new NewOpWeaponsListener(this), this);
        getServer().getPluginManager().registerEvents(new PistolListener(this), this);
        getServer().getPluginManager().registerEvents(new NewOpArmorListener(this), this);
        getServer().getPluginManager().registerEvents(new NewOpSpecialListener(this), this);
        getServer().getPluginManager().registerEvents(new NewOpTrollListener(this), this);
        SpawnVillagerCommand spawnVillagerCommand = new SpawnVillagerCommand(this);
        getCommand("spawnvillager").setExecutor(spawnVillagerCommand);
        getCommand("spawnvillager").setTabCompleter(spawnVillagerCommand);
        TardisCommand tardisCommand = new TardisCommand(this);
        getCommand("tardis").setExecutor(tardisCommand);
        getCommand("tardis").setTabCompleter(tardisCommand);
        getServer().getPluginManager().registerEvents(new TardisListener(this), this);
        getServer().getPluginManager().registerEvents(new TrollVillagerListener(this), this);
        new OpArmorEffectsListener(this);
        getServer().getPluginManager().registerEvents(new LagerSettingsListener(this), this);
        startFullBrightTask();
    }

    @Override
    public void onDisable() {
        if (vaultStorage != null) {
            vaultStorage.close();
        }
    }

    private static final String CONFIG_FULL_BRIGHT = "full_bright_players";
    private static final int NIGHT_VISION_DURATION = 400; // 20 sekunder

    private void startFullBrightTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String uuidStr : getConfig().getStringList(CONFIG_FULL_BRIGHT)) {
                    try {
                        Player p = getServer().getPlayer(UUID.fromString(uuidStr));
                        if (p != null && p.isOnline() && p.isOp()) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false));
                        }
                    } catch (Exception ignored) {}
                }
            }
        }.runTaskTimer(this, 40L, 40L);
    }

    public boolean isFullBright(UUID playerId) {
        return getConfig().getStringList(CONFIG_FULL_BRIGHT).contains(playerId.toString());
    }

    public void setFullBright(Player player, boolean on) {
        List<String> list = getConfig().getStringList(CONFIG_FULL_BRIGHT);
        String uuid = player.getUniqueId().toString();
        if (on) {
            if (!list.contains(uuid)) list.add(uuid);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false));
        } else {
            list.remove(uuid);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        getConfig().set(CONFIG_FULL_BRIGHT, list);
        saveConfig();
    }

    /** Hent armor-reach fra config (0 = vanlig, 3–20 = antall blokker). */
    public int getArmorReach() {
        return Math.max(ARMOR_REACH_MIN, Math.min(ARMOR_REACH_MAX, getConfig().getInt(CONFIG_ARMOR_REACH, 10)));
    }

    /** Sett armor-reach og lagre config. */
    public void setArmorReach(int reach) {
        reach = Math.max(ARMOR_REACH_MIN, Math.min(ARMOR_REACH_MAX, reach));
        getConfig().set(CONFIG_ARMOR_REACH, reach);
        saveConfig();
    }

    public static int getArmorReachMin() { return ARMOR_REACH_MIN; }
    public static int getArmorReachMax() { return ARMOR_REACH_MAX; }

    /** Åpner innstillingsskjermen (armor reach) – kun for OP. */
    public void openSettingsGui(Player player) {
        if (!player.isOp()) return;
        LagerSettingsHolder holder = new LagerSettingsHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text("Matheo client").color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));
        holder.setInventory(inv);
        int reach = getArmorReach();
        inv.setItem(4, makeInfoItem(reach));
        inv.setItem(11, makeButtonItem("minus", Material.RED_WOOL, Component.text("-1 (mindre reach)").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true)));
        inv.setItem(13, makeButtonItem("vanlig", Material.GRAY_WOOL, Component.text("Vanlig (ingen ekstra reach)").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true)));
        inv.setItem(15, makeButtonItem("plus", Material.LIME_WOOL, Component.text("+1 (større reach)").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)));
        boolean fullBright = isFullBright(player.getUniqueId());
        inv.setItem(22, makeButtonItem("fullbright", fullBright ? Material.TORCH : Material.REDSTONE_TORCH,
                Component.text("Full bright: " + (fullBright ? "På" : "Av")).color(fullBright ? NamedTextColor.YELLOW : NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true)));
        player.openInventory(inv);
    }

    private ItemStack makeInfoItem(int reach) {
        ItemStack stack = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            String title = reach == 0 ? "Vanlig (ingen ekstra reach)" : "Nåværende reach: " + reach + " blokker";
            meta.displayName(Component.text(title).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            meta.lore(java.util.List.of(
                    Component.text("OP-brystplade gir denne").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("rekkevidden for blokk og entity.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            ));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack makeButtonItem(String id, Material mat, Component name) {
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            meta.getPersistentDataContainer().set(new NamespacedKey(this, "lager_settings_btn"), PersistentDataType.STRING, id);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public NamespacedKey getSettingsButtonKey() {
        return new NamespacedKey(this, "lager_settings_btn");
    }
}
