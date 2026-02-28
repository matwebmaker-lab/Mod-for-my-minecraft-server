package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Havets Vrede (OP-trident): lyn slår ned der tridenten treffer.
 */
public final class TridentLightningListener implements Listener {

    private static final String TRIDENT_OP_ID = "trident";

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public TridentLightningListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    private boolean isHavetsVrede(ItemStack item) {
        return item != null && TRIDENT_OP_ID.equals(registry.getOpItemId(item));
    }

    private void strikeLightningAt(Location loc) {
        if (loc != null && loc.getWorld() != null) {
            loc.getWorld().strikeLightning(loc);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;

        ItemStack item = trident.getItem();
        if (!isHavetsVrede(item)) return;

        Location loc = trident.getLocation();
        strikeLightningAt(loc);
    }
}
