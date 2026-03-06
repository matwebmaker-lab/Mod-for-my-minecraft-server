package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OP Pistol: høyreklikk skyter en pil med kule grafikk (muzzle flash, lyder, treff-partikler).
 */
public final class PistolListener implements Listener {

    private static final String PISTOL_ID = "pistol";
    private static final double PISTOL_SPEED = 3.2;
    private static final long COOLDOWN_MS = 350;
    private static final double MUZZLE_OFFSET = 0.5;

    private final OpItemRegistry registry;
    private final NamespacedKey pistolArrowKey;
    private final Map<UUID, Long> lastShot = new ConcurrentHashMap<>();

    public PistolListener(JavaPlugin plugin) {
        this.registry = new OpItemRegistry(plugin);
        this.pistolArrowKey = new NamespacedKey(plugin, "pistol_arrow");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistolShoot(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || !PISTOL_ID.equals(registry.getOpItemId(item))) return;
        if (!event.getPlayer().isOp()) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - lastShot.getOrDefault(id, 0L) < COOLDOWN_MS) return;
        lastShot.put(id, now);

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        // Skyte pil
        Arrow arrow = player.getWorld().spawnArrow(
                eye.clone().add(dir.clone().multiply(MUZZLE_OFFSET)),
                dir,
                (float) PISTOL_SPEED,
                12f
        );
        arrow.setShooter(player);
        arrow.setCritical(true);
        arrow.setDamage(8.0);
        arrow.getPersistentDataContainer().set(pistolArrowKey, PersistentDataType.STRING, "1");

        // Muzzle flash og lydeffekter
        Location muzzle = eye.clone().add(dir.clone().multiply(MUZZLE_OFFSET));
        World world = player.getWorld();
        world.spawnParticle(Particle.FLAME, muzzle, 12, 0.08, 0.08, 0.08, 0.04);
        world.spawnParticle(Particle.SMOKE, muzzle, 8, 0.1, 0.1, 0.1, 0.02);
        world.spawnParticle(Particle.LARGE_SMOKE, muzzle, 3, 0.06, 0.06, 0.06, 0.01);
        world.spawnParticle(Particle.ENCHANT, muzzle, 6, 0.15, 0.15, 0.15, 0.3);
        world.playSound(muzzle, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.5f, 1.8f);
        world.playSound(muzzle, Sound.ENTITY_GENERIC_EXPLODE, 0.2f, 2.0f);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistolArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!arrow.getPersistentDataContainer().has(pistolArrowKey)) return;

        Location hit = event.getEntity().getLocation();
        World world = hit.getWorld();
        if (world == null) return;

        world.spawnParticle(Particle.CRIT, hit, 15, 0.2, 0.2, 0.2, 0.15);
        world.spawnParticle(Particle.ENCHANTED_HIT, hit, 10, 0.25, 0.25, 0.25, 0.08);
        world.spawnParticle(Particle.FLAME, hit, 4, 0.15, 0.15, 0.15, 0.02);
        world.playSound(hit, Sound.ENTITY_ARROW_HIT, 0.6f, 0.9f);
        world.playSound(hit, Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.3f, 1.2f);
    }
}
