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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nano Banana: høyreklikk skyter en gul kule med grafikk (muzzle, trail, treff).
 * Virker overalt – også uten å peke på blokk (HIGHEST prioritet + cancel).
 */
public final class PistolListener implements Listener {

    private static final String PISTOL_ID = "pistol";
    private static final double PISTOL_SPEED = 3.2;
    private static final long COOLDOWN_MS = 350;
    private static final double MUZZLE_OFFSET = 0.5;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final NamespacedKey pistolArrowKey;
    private final Map<UUID, Long> lastShot = new ConcurrentHashMap<>();

    public PistolListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        this.pistolArrowKey = new NamespacedKey(plugin, "pistol_arrow");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

        // Skyte pil (kulen)
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

        // Muzzle flash – gul/oransje "banana"-stil
        Location muzzle = eye.clone().add(dir.clone().multiply(MUZZLE_OFFSET));
        World world = player.getWorld();
        world.spawnParticle(Particle.FLAME, muzzle, 10, 0.08, 0.08, 0.08, 0.04);
        world.spawnParticle(Particle.SMOKE, muzzle, 6, 0.1, 0.1, 0.1, 0.02);
        world.spawnParticle(Particle.DUST, muzzle, 8, 0.12, 0.12, 0.12, 0, new Particle.DustOptions(Color.fromRGB(255, 220, 100), 1.2f));
        world.spawnParticle(Particle.ENCHANT, muzzle, 5, 0.15, 0.15, 0.15, 0.25);
        world.playSound(muzzle, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.5f, 1.8f);
        world.playSound(muzzle, Sound.ENTITY_GENERIC_EXPLODE, 0.2f, 2.0f);

        // Kulens trail – gul/oransje partikler som følger prosjektilet
        startBulletTrail(arrow);
    }

    private void startBulletTrail(Arrow arrow) {
        Particle.DustOptions yellow = new Particle.DustOptions(Color.fromRGB(255, 230, 80), 0.9f);
        Particle.DustOptions orange = new Particle.DustOptions(Color.fromRGB(255, 160, 40), 0.6f);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isValid() || arrow.isOnGround() || arrow.isDead()) {
                    cancel();
                    return;
                }
                Location loc = arrow.getLocation();
                World w = loc.getWorld();
                if (w == null) {
                    cancel();
                    return;
                }
                w.spawnParticle(Particle.DUST, loc, 3, 0.05, 0.05, 0.05, 0, yellow);
                w.spawnParticle(Particle.DUST, loc, 1, 0.02, 0.02, 0.02, 0, orange);
                w.spawnParticle(Particle.END_ROD, loc, 1, 0.03, 0.03, 0.03, 0.01);
            }
        }.runTaskTimer(plugin, 0L, 1L);
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
        world.spawnParticle(Particle.DUST, hit, 12, 0.2, 0.2, 0.2, 0, new Particle.DustOptions(Color.fromRGB(255, 200, 50), 1.0f));
        world.spawnParticle(Particle.FLAME, hit, 4, 0.15, 0.15, 0.15, 0.02);
        world.playSound(hit, Sound.ENTITY_ARROW_HIT, 0.6f, 0.9f);
        world.playSound(hit, Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.3f, 1.2f);
    }
}
