package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flygestav: hold høyreklikk for å ta entity med deg, shift for å slippe.
 * Med staven på villager åpnes ikke trade; med annet i hånden åpnes trade som vanlig.
 */
public final class FlygkølleListener implements Listener {

    private static final String FLYGESTAV_ID = "flygkølle";
    private static final double GRAB_RANGE = 8.0;
    private static final double GRAB_CONE = 0.75;
    private static final double CARRY_DISTANCE = 2.0;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final Map<UUID, Entity> carriedEntities = new ConcurrentHashMap<>();

    public FlygkølleListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        startCarryTask();
    }

    private void startCarryTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                carriedEntities.entrySet().removeIf(entry -> {
                    Player player = plugin.getServer().getPlayer(entry.getKey());
                    Entity entity = entry.getValue();
                    if (player == null || !player.isOnline() || entity == null || !entity.isValid()) return true;
                    if (!isHoldingFlygestav(player)) return true;
                    Vector inFront = player.getLocation().getDirection().normalize().multiply(CARRY_DISTANCE);
                    org.bukkit.Location target = player.getLocation().add(inFront).add(0, 0.5, 0);
                    entity.teleport(target);
                    return false;
                });
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    private boolean isHoldingFlygestav(Player player) {
        var item = player.getInventory().getItemInMainHand();
        return !item.isEmpty() && registry.isOpItem(item) && FLYGESTAV_ID.equals(registry.getOpItemId(item));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!isHoldingFlygestav(player)) return;
        event.setCancelled(true);

        if (carriedEntities.containsKey(player.getUniqueId())) return; // allerede bærer

        Vector direction = player.getLocation().getDirection().normalize();
        Vector origin = player.getEyeLocation().toVector();
        Entity closest = null;
        double closestDist = GRAB_RANGE + 1;

        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), GRAB_RANGE, GRAB_RANGE, GRAB_RANGE)) {
            if (entity == player) continue;
            Vector toEntity = entity.getLocation().toVector().add(new Vector(0, entity.getHeight() / 2, 0)).subtract(origin);
            double len = toEntity.length();
            if (len < 0.5) continue;
            double dot = toEntity.normalize().dot(direction);
            if (dot < GRAB_CONE) continue;
            if (len < closestDist) {
                closestDist = len;
                closest = entity;
            }
        }
        if (closest != null) {
            carriedEntities.put(player.getUniqueId(), closest);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        carriedEntities.remove(event.getPlayer().getUniqueId());
    }

    /** Med Flygestav på villager: ikke åpne trade. Med noe annet: åpne trade som vanlig. */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;
        if (isHoldingFlygestav(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
