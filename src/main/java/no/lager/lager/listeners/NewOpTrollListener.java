package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.items.OpItemRegistry;
import no.lager.lager.villager.TrollVillagerType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Admin/Troll-items: Fakeban, Tordenværstav, Frysbombe, Inverterstav, Size Orb, Dommedagsknapp.
 */
public final class NewOpTrollListener implements Listener {

    private static final double FRYSBOMBE_RADIUS = 8.0;
    private static final int FRYSBOMBE_TICKS = 200; // 10 sek
    private static final int INVERTER_TICKS = 200;  // 10 sek
    private static final float SCALE_SMALL = 0.5f;
    private static final float SCALE_GIANT = 2.0f;
    private static final float SCALE_NORMAL = 1.0f;
    private static final int LIGHTNING_STORM_TICKS = 100;
    private static final double LIGHTNING_RADIUS = 20.0;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final Map<UUID, Long> invertedUntil = new ConcurrentHashMap<>();
    private final Map<UUID, Long> frozenUntil = new ConcurrentHashMap<>();
    private final Map<UUID, Float> playerScale = new ConcurrentHashMap<>();

    public NewOpTrollListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        startInverterTask();
        startFrysbombeTask();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager v && TrollVillagerType.isTrollVillager(v, plugin)) return;
        Player player = event.getPlayer();
        if (!player.isOp()) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;
        String id = registry.getOpItemId(item);
        if (id == null) return;

        if ("fakeban".equals(id)) {
            event.setCancelled(true);
            if (event.getRightClicked() instanceof Player target) {
                target.kick(Component.text("You are banned from this server!\n\nReason: Banned by an operator.").color(NamedTextColor.RED));
                player.sendMessage(Component.text("Fake ban sendt til " + target.getName() + ".").color(NamedTextColor.GRAY));
            }
            return;
        }

        if ("inverter".equals(id)) {
            event.setCancelled(true);
            if (event.getRightClicked() instanceof Player target) {
                invertedUntil.put(target.getUniqueId(), System.currentTimeMillis() + INVERTER_TICKS * 50L);
                player.sendMessage(Component.text(target.getName() + " har invertert WASD i 10 sek.").color(NamedTextColor.YELLOW));
                target.sendMessage(Component.text("WASD er invertert i 10 sekunder!").color(NamedTextColor.RED));
            }
            return;
        }

        if ("størrelse_orb".equals(id)) {
            event.setCancelled(true);
            if (event.getRightClicked() instanceof Player target) {
                float current = playerScale.getOrDefault(target.getUniqueId(), SCALE_NORMAL);
                float next = current == SCALE_NORMAL ? SCALE_SMALL : (current == SCALE_SMALL ? SCALE_GIANT : SCALE_NORMAL);
                playerScale.put(target.getUniqueId(), next);
                try {
                    target.getClass().getMethod("setScale", float.class).invoke(target, next);
                } catch (Exception ignored) {
                    player.sendMessage(Component.text("Størrelse krever Paper 1.20.5+.").color(NamedTextColor.GRAY));
                }
                String sizeStr = next == SCALE_SMALL ? "liten" : (next == SCALE_GIANT ? "gigantisk" : "normal");
                player.sendMessage(Component.text(target.getName() + " er nå " + sizeStr + ".").color(NamedTextColor.GREEN));
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || !event.getPlayer().isOp()) return;
        String id = registry.getOpItemId(item);
        if (id == null) return;

        if ("torden_regn".equals(id)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            startLightningStorm(player.getLocation());
            player.sendMessage(Component.text("Lynstorm startet.").color(NamedTextColor.YELLOW));
            return;
        }

        if ("dommedagsknapp".equals(id)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            int count = 0;
            for (World w : plugin.getServer().getWorlds()) {
                for (Entity e : new java.util.ArrayList<>(w.getLivingEntities())) {
                    if (!(e instanceof Player) && !(e instanceof ArmorStand)) {
                        e.remove();
                        count++;
                    }
                }
            }
            player.sendMessage(Component.text("Slettet " + count + " mobs i alle verdener.").color(NamedTextColor.DARK_RED));
        }
    }

    private void startLightningStorm(Location center) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= LIGHTNING_STORM_TICKS) {
                    cancel();
                    return;
                }
                if (center.getWorld() == null) {
                    cancel();
                    return;
                }
                double angle = Math.random() * Math.PI * 2;
                double r = Math.random() * LIGHTNING_RADIUS;
                Location strike = center.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
                center.getWorld().strikeLightning(strike);
                ticks += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        if (!shooter.isOp()) return;

        ItemStack main = shooter.getInventory().getItemInMainHand();
        if (main.getType() != Material.SNOWBALL || !"frysbombe".equals(registry.getOpItemId(main))) return;

        Location loc = event.getEntity().getLocation();
        for (Entity e : loc.getWorld().getNearbyEntities(loc, FRYSBOMBE_RADIUS, FRYSBOMBE_RADIUS, FRYSBOMBE_RADIUS)) {
            if (e instanceof Player p) {
                frozenUntil.put(p.getUniqueId(), System.currentTimeMillis() + FRYSBOMBE_TICKS * 50L);
            }
        }
        shooter.sendMessage(Component.text("Frysbombe: spillere i radius fryst i 10 sek.").color(NamedTextColor.AQUA));
    }

    private void startFrysbombeTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Map.Entry<UUID, Long> entry : frozenUntil.entrySet()) {
                    Player p = plugin.getServer().getPlayer(entry.getKey());
                    if (entry.getValue() > now) {
                        if (p != null && p.isOnline()) {
                            p.setVelocity(new Vector(0, 0, 0));
                            p.setWalkSpeed(0);
                            p.setFlySpeed(0);
                        }
                    } else {
                        if (p != null && p.isOnline()) {
                            p.setWalkSpeed(0.2f);
                            p.setFlySpeed(0.05f);
                        }
                    }
                }
                frozenUntil.entrySet().removeIf(e -> e.getValue() <= now);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void startInverterTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                invertedUntil.entrySet().removeIf(e -> e.getValue() <= now);
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        UUID id = event.getPlayer().getUniqueId();
        if (invertedUntil.getOrDefault(id, 0L) <= System.currentTimeMillis()) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        Location inverted = from.clone().add(-dx, dy, -dz);
        inverted.setYaw(to.getYaw());
        inverted.setPitch(to.getPitch());
        event.setTo(inverted);
    }
}
