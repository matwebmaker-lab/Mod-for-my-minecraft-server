package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * OP-rustning gir ekstra effekter når den er på:
 * - Hjelm: +20 max liv (40 totalt)
 * - Brystplade: 10 blokker reach (blokk + entity)
 * - Bukse: styrke (Strength) + vanndråper
 * - Støvler: Speed
 * - Flygestøvler (iron boots): fly som i creative
 */
public final class OpArmorEffectsListener implements Listener {

    private static final double HELMET_EXTRA_HEALTH = 20.0;
    private static final double DEFAULT_BLOCK_REACH = 4.5;
    private static final double DEFAULT_ENTITY_REACH = 3.0;
    private static final int LEGGINGS_WATER_BREATHING_DURATION = 400;
    private static final int LEGGINGS_STRENGTH_DURATION = 200;     // fornyes ofte
    private static final int LEGGINGS_STRENGTH_AMPLIFIER = 1;       // Styrke II
    private static final int SPEED_DURATION = 200;                 // 10 sekunder
    private static final int SPEED_AMPLIFIER = 1;                  // Speed II
    private static final float FLY_SPEED_CREATIVE = 0.1f;          // som creative

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final NamespacedKey keyHealth;
    private final NamespacedKey keyBlockReach;
    private final NamespacedKey keyEntityReach;

    public OpArmorEffectsListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        this.keyHealth = new NamespacedKey(plugin, "op_helm_health");
        this.keyBlockReach = new NamespacedKey(plugin, "op_chest_block_reach");
        this.keyEntityReach = new NamespacedKey(plugin, "op_chest_entity_reach");
        startTickTask();
    }

    private void startTickTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    applyArmorEffects(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // hvert sekund
    }

    private void applyArmorEffects(Player player) {
        ItemStack helm = player.getInventory().getHelmet();
        ItemStack chest = player.getInventory().getChestplate();
        ItemStack legs = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        boolean hasOpHelm = isOpPiece(helm, "op_helm");
        boolean hasOpChest = isOpPiece(chest, "op_bryst");
        boolean hasOpLegs = isOpPiece(legs, "op_bukser");
        boolean hasOpBoots = isOpPiece(boots, "op_støvler");
        boolean hasFlyBoots = isOpPiece(boots, "fly_støvler");

        // Hjelm: +20 max liv (Paper 1.21.11: Attribute.MAX_HEALTH)
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            removeModifierByKey(maxHealth, keyHealth);
            if (hasOpHelm) {
                maxHealth.addModifier(new AttributeModifier(
                        keyHealth,
                        HELMET_EXTRA_HEALTH,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
            }
        }

        // Brystplade: reach fra config (0 = vanlig, ellers antall blokker)
        int configReach = plugin.getConfig().getInt("armor_reach", 10);
        configReach = Math.max(0, Math.min(20, configReach));
        double blockReachAdd = configReach <= 0 ? 0 : configReach - DEFAULT_BLOCK_REACH;
        double entityReachAdd = configReach <= 0 ? 0 : configReach - DEFAULT_ENTITY_REACH;
        AttributeInstance blockReach = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
        if (blockReach != null) {
            removeModifierByKey(blockReach, keyBlockReach);
            if (hasOpChest && blockReachAdd > 0) {
                blockReach.addModifier(new AttributeModifier(
                        keyBlockReach,
                        blockReachAdd,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
            }
        }
        AttributeInstance entityReach = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        if (entityReach != null) {
            removeModifierByKey(entityReach, keyEntityReach);
            if (hasOpChest && entityReachAdd > 0) {
                entityReach.addModifier(new AttributeModifier(
                        keyEntityReach,
                        entityReachAdd,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
            }
        }

        // Bukse: styrke (Strength II) + vanndråper (water breathing)
        if (hasOpLegs) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, LEGGINGS_STRENGTH_DURATION, LEGGINGS_STRENGTH_AMPLIFIER, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, LEGGINGS_WATER_BREATHING_DURATION, 0, true, false));
        }

        // Støvler: speed
        if (hasOpBoots) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_DURATION, SPEED_AMPLIFIER, true, false));
        }

        // Flygestøvler: fly som i creative (allowFlight + fly speed)
        if (hasFlyBoots) {
            player.setAllowFlight(true);
            player.setFlySpeed(FLY_SPEED_CREATIVE);
        } else if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            player.setAllowFlight(false);
            player.setFlySpeed(0.05f); // default survival
        }
    }

    private boolean isOpPiece(ItemStack item, String opId) {
        return item != null && opId.equals(registry.getOpItemId(item));
    }

    private void removeModifierByKey(AttributeInstance attr, NamespacedKey key) {
        attr.getModifiers().stream()
                .filter(m -> key.equals(m.getKey()))
                .forEach(attr::removeModifier);
    }
}
