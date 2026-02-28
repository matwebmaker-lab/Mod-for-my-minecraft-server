package no.lager.lager.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registry av OP-spesielle våpen og rustninger.
 * Kun operatorer kan se og eie disse itemene.
 */
public final class OpItemRegistry {

    private final JavaPlugin plugin;
    private final NamespacedKey opItemKey;
    private final NamespacedKey opItemIdKey;

    public OpItemRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.opItemKey = new NamespacedKey(plugin, "op_item");
        this.opItemIdKey = new NamespacedKey(plugin, "op_item_id");
    }

    public NamespacedKey getOpItemKey() {
        return opItemKey;
    }

    public NamespacedKey getOpItemIdKey() {
        return opItemIdKey;
    }

    public boolean isOpItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(opItemKey);
    }

    public String getOpItemId(ItemStack item) {
        if (item == null || !isOpItem(item)) return null;
        return item.getItemMeta().getPersistentDataContainer().get(opItemIdKey, org.bukkit.persistence.PersistentDataType.STRING);
    }

    private ItemStack createOpItem(String id, Material material, Component name, List<Component> lore,
                                   Map<Enchantment, Integer> enchants, ItemFlag[] flags) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;

        meta.displayName(name);
        meta.lore(lore);
        if (enchants != null) {
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                meta.addEnchant(e.getKey(), e.getValue(), true);
            }
        }
        if (flags != null) {
            meta.addItemFlags(flags);
        }
        meta.getPersistentDataContainer().set(opItemKey, org.bukkit.persistence.PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(opItemIdKey, org.bukkit.persistence.PersistentDataType.STRING, id);
        stack.setItemMeta(meta);
        return stack;
    }

    private List<Component> lore(String... lines) {
        List<Component> list = new ArrayList<>();
        for (String line : lines) {
            list.add(Component.text(line).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        return list;
    }

    public ItemStack createItem(String id) {
        return switch (id) {
            // —— VÅPEN ——
            case "storm_sverd" -> createOpItem(id, Material.NETHERITE_SWORD,
                    Component.text("Storm Sverd").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                    lore("Kraftig sverd for operatorer.", "Ødelegger alt i veien."),
                    Map.of(
                            Enchantment.SHARPNESS, 10,
                            Enchantment.FIRE_ASPECT, 5,
                            Enchantment.LOOTING, 5,
                            Enchantment.SWEEPING_EDGE, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "dødspiler" -> createOpItem(id, Material.BOW,
                    Component.text("Dødspiler").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false),
                    lore("Bue som sender fiender til evigheten."),
                    Map.of(
                            Enchantment.POWER, 10,
                            Enchantment.FLAME, 1,
                            Enchantment.INFINITY, 1,
                            Enchantment.PUNCH, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "tordenøks" -> createOpItem(id, Material.NETHERITE_AXE,
                    Component.text("Tordenøks").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("En øks som slår ned som torden."),
                    Map.of(
                            Enchantment.SHARPNESS, 8,
                            Enchantment.EFFICIENCY, 8,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "krossbue" -> createOpItem(id, Material.CROSSBOW,
                    Component.text("Mester-krossbue").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Rask og dødelig krossbue."),
                    Map.of(
                            Enchantment.QUICK_CHARGE, 5,
                            Enchantment.MULTISHOT, 1,
                            Enchantment.PIERCING, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "trident" -> createOpItem(id, Material.TRIDENT,
                    Component.text("Havets Vrede").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false),
                    lore("Trident som kaller på storm."),
                    Map.of(
                            Enchantment.IMPALING, 8,
                            Enchantment.LOYALTY, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            // —— RUSTNING ——
            case "op_helm" -> createOpItem(id, Material.NETHERITE_HELMET,
                    Component.text("Operator Hjelm").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Full beskyttelse for operatorer."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.RESPIRATION, 5,
                            Enchantment.AQUA_AFFINITY, 1,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_bryst" -> createOpItem(id, Material.NETHERITE_CHESTPLATE,
                    Component.text("Operator Brystplade").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Uovervinnelig brystbeskyttelse."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_bukser" -> createOpItem(id, Material.NETHERITE_LEGGINGS,
                    Component.text("Operator Bukse").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Leggbeskyttelse for de som styrer."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_støvler" -> createOpItem(id, Material.NETHERITE_BOOTS,
                    Component.text("Operator Støvler").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Feather Falling og beskyttelse."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.FEATHER_FALLING, 5,
                            Enchantment.DEPTH_STRIDER, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_rustning_sett" -> null; // Håndteres av kommandoen som fullt sett

            // —— VERKTØY & EKSTRA ——
            case "op_spade" -> createOpItem(id, Material.NETHERITE_SHOVEL,
                    Component.text("Operator Spade").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Ultra-effektiv spade."),
                    Map.of(
                            Enchantment.EFFICIENCY, 10,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_pickaxe" -> createOpItem(id, Material.NETHERITE_PICKAXE,
                    Component.text("Operator Hakke").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Hakke som river gjennom stein."),
                    Map.of(
                            Enchantment.EFFICIENCY, 10,
                            Enchantment.FORTUNE, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_skjold" -> createOpItem(id, Material.SHIELD,
                    Component.text("Operator Skjold").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Uovervinnelig forsvar."),
                    Map.of(Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_elytra" -> createOpItem(id, Material.ELYTRA,
                    Component.text("Operator Elytra").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Evig flyging."),
                    Map.of(
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "op_totem" -> createOpItem(id, Material.TOTEM_OF_UNDYING,
                    Component.text("Operator Totem").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Ekstra liv."),
                    null,
                    null);

            case "op_gulleneple" -> createOpItem(id, Material.ENCHANTED_GOLDEN_APPLE,
                    Component.text("Operator Gulleneple").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Full helbredelse og styrke."),
                    null,
                    null);

            default -> null;
        };
    }

    /** Alle item-ids som skal fylle admin-kisten (inkl. duplikater for mange slots). */
    public List<String> getChestFillIds() {
        return List.of(
                "storm_sverd", "storm_sverd", "dødspiler", "dødspiler", "tordenøks", "krossbue", "trident",
                "op_helm", "op_bryst", "op_bukser", "op_støvler",
                "op_helm", "op_bryst", "op_bukser", "op_støvler",
                "op_spade", "op_pickaxe", "op_spade", "op_pickaxe",
                "op_skjold", "op_elytra", "op_totem", "op_gulleneple",
                "op_totem", "op_gulleneple", "op_gulleneple", "op_gulleneple",
                "storm_sverd", "dødspiler", "tordenøks", "krossbue", "trident",
                "op_helm", "op_bryst", "op_bukser", "op_støvler",
                "op_spade", "op_pickaxe", "op_skjold", "op_elytra",
                "op_totem", "op_gulleneple", "storm_sverd", "dødspiler",
                "tordenøks", "krossbue", "trident", "op_helm", "op_bryst", "op_bukser", "op_støvler"
        );
    }

    public Map<String, String> getAllItems() {
        return Map.ofEntries(
                Map.entry("storm_sverd", "Storm Sverd (Netherite sverd)"),
                Map.entry("dødspiler", "Dødspiler (Kraftig bue)"),
                Map.entry("tordenøks", "Tordenøks (Netherite øks)"),
                Map.entry("krossbue", "Mester-krossbue"),
                Map.entry("trident", "Havets Vrede (Trident)"),
                Map.entry("op_helm", "Operator Hjelm"),
                Map.entry("op_bryst", "Operator Brystplade"),
                Map.entry("op_bukser", "Operator Bukse"),
                Map.entry("op_støvler", "Operator Støvler"),
                Map.entry("op_rustning_sett", "Hele OP-rustningsett (helm, bryst, bukse, støvler)"),
                Map.entry("op_spade", "Operator Spade"),
                Map.entry("op_pickaxe", "Operator Hakke"),
                Map.entry("op_skjold", "Operator Skjold"),
                Map.entry("op_elytra", "Operator Elytra"),
                Map.entry("op_totem", "Operator Totem"),
                Map.entry("op_gulleneple", "Operator Gulleneple")
        );
    }
}
