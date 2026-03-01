package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Flygkølle: høyreklikk sender entities i retningen du peker flygende.
 */
public final class FlygkølleListener implements Listener {

    private static final String FLYGKØLLE_ID = "flygkølle";
    private static final double RANGE = 10.0;
    private static final double LAUNCH_STRENGTH = 1.8;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public FlygkølleListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.isEmpty() || !registry.isOpItem(item) || !FLYGKØLLE_ID.equals(registry.getOpItemId(item))) return;

        event.setCancelled(true);

        Vector direction = player.getLocation().getDirection().normalize();
        Vector origin = player.getEyeLocation().toVector();

        // Finn alle entities foran spilleren (innen kjegle) og send dem flygende
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), RANGE, RANGE, RANGE)) {
            if (entity == player) continue;

            Vector toEntity = entity.getLocation().toVector().add(new Vector(0, entity.getHeight() / 2, 0)).subtract(origin);
            double len = toEntity.length();
            if (len < 0.5) continue;
            double dot = toEntity.normalize().dot(direction);
            if (dot < 0.6) continue; // innenfor ca. 50° kjegle

            if (len > RANGE) continue;

            Vector velocity = direction.clone().multiply(LAUNCH_STRENGTH);
            velocity.setY(velocity.getY() + 0.25);
            entity.setVelocity(velocity);
        }
    }
}
