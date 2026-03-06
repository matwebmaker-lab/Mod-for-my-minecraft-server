package no.lager.lager.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
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

    /** Legger til kule armor-trim på rustning (1.20+). */
    private ItemStack createOpArmorItem(String id, Material material, Component name, List<Component> lore,
                                        Map<Enchantment, Integer> enchants, ItemFlag[] flags,
                                        TrimMaterial trimMaterial, TrimPattern trimPattern) {
        ItemStack stack = createOpItem(id, material, name, lore, enchants, flags);
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            stack.setItemMeta(armorMeta);
        }
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

            case "pistol" -> createOpItem(id, Material.BLAZE_ROD,
                    Component.text("OP Pistol").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Høyreklikk for å skyte. Lynrask og med kule effekter."),
                    Map.of(Enchantment.UNBREAKING, 3),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            // —— RUSTNING (med kule armor-trim) ——
            case "op_helm" -> createOpArmorItem(id, Material.NETHERITE_HELMET,
                    Component.text("Operator Hjelm").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Full beskyttelse for operatorer."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.RESPIRATION, 5,
                            Enchantment.AQUA_AFFINITY, 1,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE},
                    TrimMaterial.GOLD, TrimPattern.SENTRY);

            case "op_bryst" -> createOpArmorItem(id, Material.NETHERITE_CHESTPLATE,
                    Component.text("Operator Brystplade").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Uovervinnelig brystbeskyttelse."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE},
                    TrimMaterial.GOLD, TrimPattern.SENTRY);

            case "op_bukser" -> createOpArmorItem(id, Material.NETHERITE_LEGGINGS,
                    Component.text("Operator Bukse").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Leggbeskyttelse for de som styrer."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE},
                    TrimMaterial.GOLD, TrimPattern.SENTRY);

            case "op_støvler" -> createOpArmorItem(id, Material.NETHERITE_BOOTS,
                    Component.text("Operator Støvler").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Feather Falling og beskyttelse."),
                    Map.of(
                            Enchantment.PROTECTION, 8,
                            Enchantment.FEATHER_FALLING, 5,
                            Enchantment.DEPTH_STRIDER, 5,
                            Enchantment.UNBREAKING, 5,
                            Enchantment.MENDING, 1
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE},
                    TrimMaterial.GOLD, TrimPattern.SENTRY);

            case "fly_støvler" -> createOpItem(id, Material.IRON_BOOTS,
                    Component.text("Flygestøvler").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                    lore("Fly som i creative – dobbeltklikk hopp for å fly."),
                    Map.of(
                            Enchantment.UNBREAKING, 3,
                            Enchantment.FEATHER_FALLING, 2
                    ),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

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

            case "flygkølle" -> createOpItem(id, Material.BLAZE_ROD,
                    Component.text("Flygestav").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("Hold høyreklikk for å ta entity med deg.", "Slipp (shift) for å slippe."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "instillinger" -> createOpItem(id, Material.CLOCK,
                    Component.text("Matheo client").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Høyre shift for å åpne.", "Styr armor-reach (rekkevidde)."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "totem_stakk" -> {
                ItemStack stakk = createOpItem(id, Material.PAPER,
                        Component.text("Totem-stakk").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                        lore("Høyreklikk: få ett Operator Totem.", "Stablet – én totem per bruk."),
                        null,
                        new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
                if (stakk != null) stakk.setAmount(16);
                yield stakk;
            }

            // —— NYE VÅPEN ——
            case "dommersverd" -> createOpItem(id, Material.NETHERITE_SWORD,
                    Component.text("Dommens Sverd").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false),
                    lore("Dreper spillere under 5 hjerter umiddelbart."),
                    Map.of(Enchantment.SHARPNESS, 8, Enchantment.UNBREAKING, 5, Enchantment.MENDING, 1),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});

            case "vakuumbue" -> createOpItem(id, Material.BOW,
                    Component.text("Vakuumbue").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("Trekker entity mot pila der den lander."),
                    Map.of(Enchantment.POWER, 5, Enchantment.INFINITY, 1, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "kaosstav" -> createOpItem(id, Material.BLAZE_ROD,
                    Component.text("Kaosstav").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                    lore("Høyreklikk = tilfeldig effekt (Strength, Levitation, Wither, Speed, etc.)."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "gravitasjonsøks" -> createOpItem(id, Material.NETHERITE_AXE,
                    Component.text("Gravitasjonsøks").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("Slår mobs opp i lufta."),
                    Map.of(Enchantment.SHARPNESS, 6, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "blodtrident" -> createOpItem(id, Material.TRIDENT,
                    Component.text("Blodets Trident").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false),
                    lore("Gir deg liv tilbake basert på damage."),
                    Map.of(Enchantment.IMPALING, 5, Enchantment.LOYALTY, 3, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "tidsknuser" -> createOpItem(id, Material.NETHERITE_SWORD,
                    Component.text("Tidsknuser").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    lore("Treffer = Slowness 10 i 3 sek."),
                    Map.of(Enchantment.SHARPNESS, 5, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            // —— NY RUSTNING ——
            case "op_kappe" -> createOpItem(id, Material.ELYTRA,
                    Component.text("Operator Kappe").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("Immun mot fall damage."),
                    Map.of(Enchantment.UNBREAKING, 5, Enchantment.MENDING, 1),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "void_helm" -> createOpArmorItem(id, Material.NETHERITE_HELMET,
                    Component.text("Void Hjelm").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false),
                    lore("Night Vision + ser spillere gjennom vegger (glow)."),
                    Map.of(Enchantment.PROTECTION, 5, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS},
                    TrimMaterial.AMETHYST, TrimPattern.EYE);

            case "titan_bryst" -> createOpArmorItem(id, Material.NETHERITE_CHESTPLATE,
                    Component.text("Titan Brystplate").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("50% mindre damage."),
                    Map.of(Enchantment.PROTECTION, 8, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS},
                    TrimMaterial.NETHERITE, TrimPattern.RAISER);

            case "magnet_bukse" -> createOpArmorItem(id, Material.NETHERITE_LEGGINGS,
                    Component.text("Magnetbukse").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("Trekker drops automatisk til deg."),
                    Map.of(Enchantment.PROTECTION, 5, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS},
                    TrimMaterial.REDSTONE, TrimPattern.RIB);

            case "frost_støvler" -> createOpArmorItem(id, Material.NETHERITE_BOOTS,
                    Component.text("Froststøvler").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("Fryser vann når du går (Frost Walker + permanent)."),
                    Map.of(Enchantment.FROST_WALKER, 2, Enchantment.PROTECTION, 5, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS},
                    TrimMaterial.DIAMOND, TrimPattern.FLOW);

            // —— SPESIALITEMS ——
            case "admin_stav" -> createOpItem(id, Material.STICK,
                    Component.text("Adminstav").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                    lore("Høyreklikk spiller = GUI med kick/frys/heal/clear inv."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "rewind_klokke" -> createOpItem(id, Material.CLOCK,
                    Component.text("Tidsklokke").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                    lore("Shift + høyreklikk = går 5 sek tilbake (posisjon + HP)."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "void_perle" -> createOpItem(id, Material.ENDER_PEARL,
                    Component.text("Voidperle").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false),
                    lore("Teleporterer spilleren du treffer til Y = -64."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "massehealer" -> createOpItem(id, Material.GOLDEN_APPLE,
                    Component.text("Massehealer").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false),
                    lore("Høyreklikk = healer alle innen 10 blokker."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "verdensbryter" -> createOpItem(id, Material.NETHERITE_PICKAXE,
                    Component.text("Verdensbryter").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                    lore("3x3 eller 5x5 mining."),
                    Map.of(Enchantment.EFFICIENCY, 10, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            // —— ADMIN / TROLL ——
            case "fakeban" -> createOpItem(id, Material.BOOK,
                    Component.text("Fake Ban-bok").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                    lore("Viser falsk 'You are banned' i 5 sek."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "torden_regn" -> createOpItem(id, Material.BLAZE_ROD,
                    Component.text("Tordenværstav").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                    lore("Starter lynstorm."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "frysbombe" -> createOpItem(id, Material.SNOWBALL,
                    Component.text("Frysbombe").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                    lore("Fryser alle spillere i radius."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "inverter" -> createOpItem(id, Material.STICK,
                    Component.text("Inverterstav").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false),
                    lore("Bytter WASD-kontroller i 10 sek."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            case "størrelse_orb" -> createOpItem(id, Material.ENDER_PEARL,
                    Component.text("Size Orb").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                    lore("Gjør spiller liten eller gigantisk."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            // —— MYTHIC ——
            case "void_kongekrone" -> createOpArmorItem(id, Material.NETHERITE_HELMET,
                    Component.text("Void Kongekrone").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                    lore("Creative flight + Resistance III."),
                    Map.of(Enchantment.PROTECTION, 5, Enchantment.UNBREAKING, 5),
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS},
                    TrimMaterial.AMETHYST, TrimPattern.HOST);

            case "dommedagsknapp" -> createOpItem(id, Material.NETHERITE_SCRAP,
                    Component.text("Dommedagsknapp").color(NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false),
                    lore("Sletter alle mobs i verden."),
                    null,
                    new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

            default -> null;
        };
    }

    /** Alle item-ids som skal fylle admin-kisten (inkl. duplikater for mange slots). */
    public List<String> getChestFillIds() {
        return List.of(
                "storm_sverd", "storm_sverd", "dommersverd", "vakuumbue", "pistol", "tordenøks", "gravitasjonsøks", "blodtrident", "tidsknuser", "kaosstav",
                "op_helm", "op_bryst", "op_bukser", "op_støvler",
                "op_kappe", "void_helm", "titan_bryst", "magnet_bukse", "frost_støvler",
                "op_spade", "op_pickaxe", "verdensbryter", "flygkølle",
                "op_skjold", "op_elytra", "op_totem", "op_gulleneple", "fly_støvler", "instillinger", "totem_stakk", "totem_stakk",
                "admin_stav", "rewind_klokke", "void_perle", "massehealer",
                "fakeban", "torden_regn", "frysbombe", "inverter", "størrelse_orb",
                "storm_sverd", "dødspiler", "tordenøks", "krossbue", "trident", "pistol",
                "op_helm", "op_bryst", "op_bukser", "op_støvler",
                "op_spade", "op_pickaxe", "op_skjold", "op_elytra",
                "void_kongekrone", "dommedagsknapp",
                "op_totem", "op_gulleneple", "storm_sverd", "dommersverd", "vakuumbue",
                "tordenøks", "krossbue", "trident", "op_helm", "op_bryst", "op_bukser", "op_støvler"
        );
    }

    public Map<String, String> getAllItems() {
        return Map.ofEntries(
                Map.entry("storm_sverd", "Storm Sverd (Netherite sverd)"),
                Map.entry("dommersverd", "Dommens Sverd (dreper under 5 hjerter)"),
                Map.entry("vakuumbue", "Vakuumbue (trekker entity mot pil)"),
                Map.entry("kaosstav", "Kaosstav (tilfeldig effekt)"),
                Map.entry("gravitasjonsøks", "Gravitasjonsøks (slår mobs opp)"),
                Map.entry("blodtrident", "Blodets Trident (liv tilbake)"),
                Map.entry("tidsknuser", "Tidsknuser (Slowness 10)"),
                Map.entry("dødspiler", "Dødspiler (Kraftig bue)"),
                Map.entry("tordenøks", "Tordenøks (Netherite øks)"),
                Map.entry("krossbue", "Mester-krossbue"),
                Map.entry("trident", "Havets Vrede (Trident)"),
                Map.entry("pistol", "OP Pistol (skyter med kule effekter)"),
                Map.entry("op_helm", "Operator Hjelm"),
                Map.entry("op_bryst", "Operator Brystplade"),
                Map.entry("op_bukser", "Operator Bukse"),
                Map.entry("op_støvler", "Operator Støvler"),
                Map.entry("op_kappe", "Operator Kappe (immun fall damage)"),
                Map.entry("void_helm", "Void Hjelm (Night Vision + glow)"),
                Map.entry("titan_bryst", "Titan Brystplate (50% mindre damage)"),
                Map.entry("magnet_bukse", "Magnetbukse (trekker drops)"),
                Map.entry("frost_støvler", "Froststøvler (fryser vann)"),
                Map.entry("fly_støvler", "Flygestøvler (fly som creative)"),
                Map.entry("op_rustning_sett", "Hele OP-rustningsett (helm, bryst, bukse, støvler)"),
                Map.entry("op_spade", "Operator Spade"),
                Map.entry("op_pickaxe", "Operator Hakke"),
                Map.entry("verdensbryter", "Verdensbryter (3x3/5x5 mining)"),
                Map.entry("op_skjold", "Operator Skjold"),
                Map.entry("op_elytra", "Operator Elytra"),
                Map.entry("op_totem", "Operator Totem"),
                Map.entry("op_gulleneple", "Operator Gulleneple"),
                Map.entry("flygkølle", "Flygestav (ta entity med deg, shift for å slippe)"),
                Map.entry("admin_stav", "Adminstav (kick/frys/heal GUI)"),
                Map.entry("rewind_klokke", "Tidsklokke (5 sek tilbake)"),
                Map.entry("void_perle", "Voidperle (teleporter til Y=-64)"),
                Map.entry("massehealer", "Massehealer (heal 10 blokker)"),
                Map.entry("instillinger", "Matheo client (høyre shift for å åpne)"),
                Map.entry("totem_stakk", "Totem-stakk (høyreklikk = 1 totem)"),
                Map.entry("fakeban", "Fake Ban-bok"),
                Map.entry("torden_regn", "Tordenværstav (lynstorm)"),
                Map.entry("frysbombe", "Frysbombe"),
                Map.entry("inverter", "Inverterstav (bytter WASD)"),
                Map.entry("størrelse_orb", "Size Orb (liten/gigantisk)"),
                Map.entry("void_kongekrone", "Void Kongekrone (Mythic)"),
                Map.entry("dommedagsknapp", "Dommedagsknapp (sletter alle mobs)")
        );
    }
}
