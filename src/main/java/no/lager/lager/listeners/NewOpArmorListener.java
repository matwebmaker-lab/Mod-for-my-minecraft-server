package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Ny rustning: op_kappe (fall damage), void_helm (glow + NV), titan_bryst (50% damage),
 * magnet_bukse (trekker drops), frost_støvler (frost walker), void_kongekrone (flight + resist).
 */
public final class NewOpArmorListener implements Listener {

    private static final double MAGNET_RADIUS = 8.0;
    private static final double MAGNET_SPEED = 0.4;
    private static final double TITAN_REDUCTION = 0.5;
    private static final int FROST_WALKER_RADIUS = 2;
    private static final int FROST_EFFECT_TICKS = 100;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public NewOpArmorListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        startMagnetTask();
        startVoidHelmTask();
        startFrostTask();
        startVoidKroneTask();
    }

    private boolean hasOpPiece(Player player, String slotId) {
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (piece != null && slotId.equals(registry.getOpItemId(piece))) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!player.isOp()) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && hasOpPiece(player, "op_kappe")) {
            event.setCancelled(true);
            return;
        }

        if (hasOpPiece(player, "titan_bryst")) {
            event.setDamage(event.getDamage() * TITAN_REDUCTION);
        }
    }

    private void startMagnetTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!p.isOp() || !hasOpPiece(p, "magnet_bukse")) continue;
                    Location loc = p.getLocation();
                    for (Entity e : p.getWorld().getNearbyEntities(loc, MAGNET_RADIUS, MAGNET_RADIUS, MAGNET_RADIUS)) {
                        if (!(e instanceof Item item)) continue;
                        Vector toPlayer = p.getLocation().toVector().add(new Vector(0, 0.5, 0)).subtract(item.getLocation().toVector()).normalize().multiply(MAGNET_SPEED);
                        item.setVelocity(toPlayer);
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 5L);
    }

    private static final double VOID_HELM_GLOW_RANGE = 64.0;

    private void startVoidHelmTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                java.util.Set<Player> shouldGlow = new java.util.HashSet<>();
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!p.isOp()) continue;
                    ItemStack helm = p.getInventory().getHelmet();
                    if (helm == null || !"void_helm".equals(registry.getOpItemId(helm))) continue;
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, true, false));
                    for (Player other : p.getWorld().getPlayers()) {
                        if (other != p && p.getLocation().distanceSquared(other.getLocation()) <= VOID_HELM_GLOW_RANGE * VOID_HELM_GLOW_RANGE) {
                            shouldGlow.add(other);
                        }
                    }
                }
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    p.setGlowing(shouldGlow.contains(p));
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    private void startFrostTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!p.isOp()) continue;
                    ItemStack boots = p.getInventory().getBoots();
                    if (boots == null || !"frost_støvler".equals(registry.getOpItemId(boots))) continue;
                    Location base = p.getLocation().getBlock().getLocation();
                    World w = p.getWorld();
                    for (int x = -FROST_WALKER_RADIUS; x <= FROST_WALKER_RADIUS; x++) {
                        for (int z = -FROST_WALKER_RADIUS; z <= FROST_WALKER_RADIUS; z++) {
                            if (x * x + z * z > FROST_WALKER_RADIUS * FROST_WALKER_RADIUS) continue;
                            Location check = base.clone().add(x, 0, z);
                            if (w.getBlockAt(check).getType() == Material.WATER) {
                                w.getBlockAt(check).setType(Material.FROSTED_ICE);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void startVoidKroneTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!p.isOp()) continue;
                    ItemStack helm = p.getInventory().getHelmet();
                    if (helm == null || !"void_kongekrone".equals(registry.getOpItemId(helm))) continue;
                    p.setAllowFlight(true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 2, true, false));
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
