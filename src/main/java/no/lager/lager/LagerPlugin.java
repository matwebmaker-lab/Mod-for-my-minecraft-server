package no.lager.lager;

import no.lager.lager.commands.AdminChestCommand;
import no.lager.lager.commands.KisteCommand;
import no.lager.lager.commands.LagerCommand;
import no.lager.lager.listeners.OpArmorEffectsListener;
import no.lager.lager.listeners.OpItemListener;
import no.lager.lager.listeners.FlygkølleListener;
import no.lager.lager.listeners.OpKisteRefillListener;
import no.lager.lager.inventory.LagerSettingsHolder;
import no.lager.lager.listeners.LagerSettingsListener;
import no.lager.lager.listeners.TotemStakkListener;
import no.lager.lager.listeners.TridentLightningListener;
import no.lager.lager.listeners.VillagerAxeListener;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class LagerPlugin extends JavaPlugin {

    private static final int ARMOR_REACH_MIN = 3;
    private static final int ARMOR_REACH_MAX = 20;
    private static final String CONFIG_ARMOR_REACH = "armor_reach";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("lager").setExecutor(new LagerCommand(this));
        getCommand("lagerkiste").setExecutor(new AdminChestCommand(this));
        KisteCommand kisteCommand = new KisteCommand(this);
        getCommand("kiste").setExecutor(kisteCommand);
        getServer().getPluginManager().registerEvents(new OpItemListener(this), this);
        getServer().getPluginManager().registerEvents(new OpKisteRefillListener(this, kisteCommand), this);
        getServer().getPluginManager().registerEvents(new FlygkølleListener(this), this);
        getServer().getPluginManager().registerEvents(new TridentLightningListener(this), this);
        getServer().getPluginManager().registerEvents(new TotemStakkListener(this), this);
        getServer().getPluginManager().registerEvents(new VillagerAxeListener(this), this);
        new OpArmorEffectsListener(this);
        getServer().getPluginManager().registerEvents(new LagerSettingsListener(this), this);
    }

    /** Hent armor-reach fra config (antall blokker, 3–20). */
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
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text("Lager – Armor reach").color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));
        holder.setInventory(inv);
        int reach = getArmorReach();
        inv.setItem(4, makeInfoItem(reach));
        inv.setItem(11, makeButtonItem("minus", Material.RED_WOOL, Component.text("-1 (mindre reach)").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true)));
        inv.setItem(15, makeButtonItem("plus", Material.LIME_WOOL, Component.text("+1 (større reach)").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)));
        player.openInventory(inv);
    }

    private ItemStack makeInfoItem(int reach) {
        ItemStack stack = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Nåværende reach: " + reach + " blokker").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
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
