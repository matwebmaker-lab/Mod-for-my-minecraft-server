package no.lager.lager.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Troll-varer: Magiske diamanter (blir til kull/potet), Uknuselig hakke, Teleport-eple, Hemsko-støvler.
 */
public final class TrollItemRegistry {

    public static final String ID_MAGIC_DIAMOND = "magic_diamond";
    public static final String ID_UNBREAKABLE_PICKAXE = "unbreakable_pickaxe";
    public static final String ID_TELEPORT_APPLE = "teleport_apple";
    public static final String ID_HEMSKO_BOOTS = "hemsko_boots";

    private final JavaPlugin plugin;
    private final NamespacedKey trollKey;
    private final NamespacedKey trollIdKey;

    public TrollItemRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.trollKey = new NamespacedKey(plugin, "troll_item");
        this.trollIdKey = new NamespacedKey(plugin, "troll_item_id");
    }

    public NamespacedKey getTrollKey() { return trollKey; }
    public NamespacedKey getTrollIdKey() { return trollIdKey; }

    public boolean isTrollItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(trollKey);
    }

    public String getTrollItemId(ItemStack item) {
        if (item == null || !isTrollItem(item)) return null;
        return item.getItemMeta().getPersistentDataContainer().get(trollIdKey, PersistentDataType.STRING);
    }

    private void markTroll(ItemMeta meta, String id) {
        meta.getPersistentDataContainer().set(trollKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(trollIdKey, PersistentDataType.STRING, id);
    }

    private List<Component> lore(String... lines) {
        List<Component> list = new ArrayList<>();
        for (String line : lines) {
            list.add(Component.text(line).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        return list;
    }

    /** Magiske diamanter – ser bra ut, blir til kull/malt potet når de havner i inventar. */
    public ItemStack createMagicDiamond() {
        ItemStack stack = new ItemStack(Material.DIAMOND);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Magiske diamanter").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore("Så verdifulle!", "Kjøp fra Svindleren."));
            markTroll(meta, ID_MAGIC_DIAMOND);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /** Uknuselig hakke – ser ut som vanlig, 1 durability, knuser med en gang. */
    public ItemStack createUnbreakablePickaxe() {
        ItemStack stack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Uknuselig hakke").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore("Den beste hakken... eller?", "Holder bare én bruk."));
            if (meta instanceof Damageable d) {
                d.setDamage(d.getMaxDamage() - 1); // én bruk igjen
            }
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            markTroll(meta, ID_UNBREAKABLE_PICKAXE);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /** Teleport-eple – teleporterer tilfeldig innen 50 blokker når det spises. */
    public ItemStack createTeleportApple() {
        ItemStack stack = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Teleport-eple").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore("Spis for å teleportere til et mystisk sted!"));
            markTroll(meta, ID_TELEPORT_APPLE);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /** Hemsko-støvler – Slowness X + Curse of Binding. */
    public ItemStack createHemskoBoots() {
        ItemStack stack = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Hemsko-støvler").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore("Super tunge. Du vil ikke ta dem på..."));
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            markTroll(meta, ID_HEMSKO_BOOTS);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public ItemStack createTrollItem(String id) {
        return switch (id) {
            case ID_MAGIC_DIAMOND -> createMagicDiamond();
            case ID_UNBREAKABLE_PICKAXE -> createUnbreakablePickaxe();
            case ID_TELEPORT_APPLE -> createTeleportApple();
            case ID_HEMSKO_BOOTS -> createHemskoBoots();
            default -> null;
        };
    }
}
