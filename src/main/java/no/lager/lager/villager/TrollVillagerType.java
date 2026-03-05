package no.lager.lager.villager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.items.TrollItemRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Typer troll-landsbyboere og hjelp til å sette opp trades.
 */
public final class TrollVillagerType {

    public static final String SCAMMER = "scammer";
    public static final String JESTER = "jester";
    public static final String TAX = "tax";
    public static final String BOOMER = "boomer";
    public static final String GLITCH = "glitch";

    public static NamespacedKey getTypeKey(JavaPlugin plugin) {
        return new NamespacedKey(plugin, "troll_villager_type");
    }

    public static void setType(Villager villager, String type, JavaPlugin plugin) {
        villager.getPersistentDataContainer().set(getTypeKey(plugin), PersistentDataType.STRING, type);
    }

    public static String getType(Villager villager, JavaPlugin plugin) {
        return villager.getPersistentDataContainer().get(getTypeKey(plugin), PersistentDataType.STRING);
    }

    public static boolean isTrollVillager(Villager villager, JavaPlugin plugin) {
        return villager.getPersistentDataContainer().has(getTypeKey(plugin), PersistentDataType.STRING);
    }

    /** Sett utseende og navn etter type. */
    public static void applyAppearance(Villager villager, String type) {
        switch (type) {
            case SCAMMER -> {
                villager.customName(Component.text("Svindleren").color(NamedTextColor.GOLD));
                villager.setProfession(Villager.Profession.LIBRARIAN);
            }
            case JESTER -> {
                villager.customName(Component.text("Gjøgleren").color(NamedTextColor.LIGHT_PURPLE));
                villager.setProfession(Villager.Profession.CARTOGRAPHER); // må ha jobb for å kunne trade
            }
            case TAX -> {
                villager.customName(Component.text("Skatteinnkreveren").color(NamedTextColor.RED));
                villager.setProfession(Villager.Profession.CLERIC);
            }
            case BOOMER -> {
                villager.customName(Component.text("Eksplosiv Selger").color(NamedTextColor.DARK_RED));
                villager.setProfession(Villager.Profession.WEAPONSMITH);
            }
            case GLITCH -> {
                villager.customName(Component.text("Glitchen").color(NamedTextColor.AQUA));
                villager.setProfession(Villager.Profession.LIBRARIAN);
            }
            default -> {}
        }
        villager.setCustomNameVisible(true);
        villager.setVillagerType(Villager.Type.PLAINS);
        // nivå settes ETTER oppskrifter i SpawnVillagerCommand (på neste tick)
    }

    /** Lager en gyldig MerchantRecipe som vises i handel (experienceReward + villagerExperience). */
    private static MerchantRecipe recipe(org.bukkit.inventory.ItemStack result, org.bukkit.inventory.ItemStack ingredient) {
        MerchantRecipe r = new MerchantRecipe(result, 0, 999, true, 1, 0.05f);
        r.addIngredient(ingredient);
        return r;
    }

    /** Gjøgleren selger pinne og papir (så handel åpnes – listener kaster potion). */
    public static void setJesterRecipes(Villager villager) {
        List<MerchantRecipe> recipes = new ArrayList<>();
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.STICK, 4), new org.bukkit.inventory.ItemStack(Material.EMERALD, 1)));
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.PAPER, 8), new org.bukkit.inventory.ItemStack(Material.EMERALD, 1)));
        villager.setRecipes(recipes);
    }

    /** Skatteinnkreveren selger papir og bøker. */
    public static void setTaxRecipes(Villager villager) {
        List<MerchantRecipe> recipes = new ArrayList<>();
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.PAPER, 24), new org.bukkit.inventory.ItemStack(Material.EMERALD, 1)));
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.BOOK, 4), new org.bukkit.inventory.ItemStack(Material.EMERALD, 2)));
        villager.setRecipes(recipes);
    }

    /** Svindleren selger Magiske diamanter (blir til kull/potet) + “billig” diamant. */
    public static void setScammerRecipes(Villager villager, TrollItemRegistry trollItems) {
        List<MerchantRecipe> recipes = new ArrayList<>();
        recipes.add(recipe(trollItems.createMagicDiamond(), new org.bukkit.inventory.ItemStack(Material.EMERALD, 5)));
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.DIAMOND), new org.bukkit.inventory.ItemStack(Material.EMERALD, 3)));
        villager.setRecipes(recipes);
    }

    /** Boomer selger Netherite-ingot og diamant. */
    public static void setBoomerRecipes(Villager villager) {
        List<MerchantRecipe> recipes = new ArrayList<>();
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.NETHERITE_INGOT), new org.bukkit.inventory.ItemStack(Material.EMERALD, 1)));
        recipes.add(recipe(new org.bukkit.inventory.ItemStack(Material.DIAMOND, 2), new org.bukkit.inventory.ItemStack(Material.EMERALD, 1)));
        villager.setRecipes(recipes);
    }

    /** Tilfeldige troll-oppskrifter for Glitchen (alle med experienceReward så de vises). */
    public static List<MerchantRecipe> randomGlitchRecipes(TrollItemRegistry trollItems) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        List<MerchantRecipe> list = new ArrayList<>();
        Material[] results = { Material.COAL, Material.BAKED_POTATO, Material.DIRT, Material.COBBLESTONE,
                Material.ROTTEN_FLESH, Material.BONE, Material.STICK };
        String[] trollIds = { TrollItemRegistry.ID_MAGIC_DIAMOND, TrollItemRegistry.ID_UNBREAKABLE_PICKAXE,
                TrollItemRegistry.ID_TELEPORT_APPLE, TrollItemRegistry.ID_HEMSKO_BOOTS };
        int count = 2 + r.nextInt(3);
        for (int i = 0; i < count; i++) {
            if (r.nextBoolean() && trollIds.length > 0) {
                String id = trollIds[r.nextInt(trollIds.length)];
                org.bukkit.inventory.ItemStack result = trollItems.createTrollItem(id);
                if (result != null) {
                    MerchantRecipe rec = new MerchantRecipe(result, 0, 99, true, 1, 0.05f);
                    rec.addIngredient(new org.bukkit.inventory.ItemStack(Material.EMERALD, 1 + r.nextInt(10)));
                    list.add(rec);
                }
            } else {
                Material mat = results[r.nextInt(results.length)];
                MerchantRecipe rec = new MerchantRecipe(new org.bukkit.inventory.ItemStack(mat, 1 + r.nextInt(16)), 0, 99, true, 1, 0.05f);
                rec.addIngredient(new org.bukkit.inventory.ItemStack(Material.EMERALD, 1 + r.nextInt(5)));
                list.add(rec);
            }
        }
        if (list.isEmpty()) {
            list.add(recipe(new org.bukkit.inventory.ItemStack(Material.EMERALD), new org.bukkit.inventory.ItemStack(Material.DIAMOND)));
        }
        return list;
    }
}
