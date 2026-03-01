package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Nye OP-våpen: Dommens Sverd, Vakuumbue, Kaosstav, Gravitasjonsøks, Blodtrident, Tidsknuser.
 */
public final class NewOpWeaponsListener implements Listener {

    private static final double DOMMERSVERD_HEART_THRESHOLD = 10.0; // 5 hjerter
    private static final double GRAVITASJON_VELOCITY_Y = 1.8;
    private static final int TIDSKNUSER_DURATION_TICKS = 60; // 3 sek
    private static final int TIDSKNUSER_AMPLIFIER = 9;       // Slowness 10 = amplifier 9
    private static final double BLODTRIDENT_HEAL_RATIO = 0.5; // 50% av damage som liv
    private static final double VAKUUM_PULL_STRENGTH = 1.2;
    private static final double VAKUUM_RADIUS = 6.0;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final NamespacedKey vakuumArrowKey;

    public NewOpWeaponsListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        this.vakuumArrowKey = new NamespacedKey(plugin, "vakuumbue");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        Entity victim = event.getEntity();
        ItemStack weapon = null;
        Player attacker = null;

        if (damagerEntity instanceof Player p) {
            weapon = p.getInventory().getItemInMainHand();
            attacker = p;
        } else if (damagerEntity instanceof Trident trident && trident.getShooter() instanceof Player p) {
            attacker = p;
            weapon = trident.getItem();
        }

        if (weapon == null || weapon.getType().isAir() || !registry.isOpItem(weapon)) return;
        String id = registry.getOpItemId(weapon);
        if (id == null) return;

        switch (id) {
            case "dommersverd" -> {
                if (victim instanceof Player victimPlayer && victimPlayer.getHealth() <= DOMMERSVERD_HEART_THRESHOLD) {
                    event.setDamage(1000.0);
                }
            }
            case "gravitasjonsøks" -> victim.setVelocity(new Vector(0, GRAVITASJON_VELOCITY_Y, 0));
            case "tidsknuser" -> {
                if (victim instanceof LivingEntity living) {
                    living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, TIDSKNUSER_DURATION_TICKS, TIDSKNUSER_AMPLIFIER, true, true));
                }
            }
            case "blodtrident" -> {
                if (attacker != null && attacker.isOnline()) {
                    double heal = event.getFinalDamage() * BLODTRIDENT_HEAL_RATIO;
                    org.bukkit.attribute.AttributeInstance maxAttr = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                    double max = maxAttr != null ? maxAttr.getValue() : 20.0;
                    attacker.setHealth(Math.min(max, attacker.getHealth() + heal));
                }
            }
            default -> {}
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (!player.isOp()) return;

        ItemStack bow = player.getInventory().getItemInMainHand();
        if (bow.getType() != Material.BOW || !"vakuumbue".equals(registry.getOpItemId(bow))) return;

        arrow.getPersistentDataContainer().set(vakuumArrowKey, PersistentDataType.STRING, "1");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!arrow.getPersistentDataContainer().has(vakuumArrowKey)) return;

        Location hitLoc = event.getEntity().getLocation();
        World world = hitLoc.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(hitLoc, VAKUUM_RADIUS, VAKUUM_RADIUS, VAKUUM_RADIUS)) {
            if (entity instanceof LivingEntity living && !(entity instanceof ArmorStand)) {
                Vector toCenter = hitLoc.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(VAKUUM_PULL_STRENGTH);
                entity.setVelocity(toCenter);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onKaosStavInteract(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || !"kaosstav".equals(registry.getOpItemId(item))) return;
        if (!event.getPlayer().isOp()) return;

        event.setCancelled(true);

        PotionEffectType[] effects = {
                PotionEffectType.STRENGTH, PotionEffectType.LEVITATION, PotionEffectType.WITHER,
                PotionEffectType.SPEED, PotionEffectType.JUMP_BOOST, PotionEffectType.REGENERATION,
                PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS
        };
        PotionEffectType chosen = effects[ThreadLocalRandom.current().nextInt(effects.length)];
        int duration = 80 + ThreadLocalRandom.current().nextInt(100);
        int amplifier = ThreadLocalRandom.current().nextInt(3);

        Player player = event.getPlayer();
        LivingEntity target = getNearestLivingEntity(player, 8.0);
        if (target == null) target = player;

        target.addPotionEffect(new PotionEffect(chosen, duration, amplifier, true, true));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 0.8f, 1f);
    }

    private LivingEntity getNearestLivingEntity(Player player, double range) {
        LivingEntity nearest = null;
        double nearestDist = range * range;
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof LivingEntity living && e != player) {
                double d = e.getLocation().distanceSquared(player.getLocation());
                if (d < nearestDist) {
                    nearestDist = d;
                    nearest = living;
                }
            }
        }
        return nearest;
    }
}
