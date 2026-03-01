package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Kun OP kan eie OP-items: ikke-OP som plukker opp fjernes itemet.
 * (OP får utstyr via /lager gear, ikke lenger automatisk ved join.)
 */
public final class OpItemListener implements Listener {

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public OpItemListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isOp()) return;

        ItemStack item = event.getItem().getItemStack();
        if (!registry.isOpItem(item)) return;

        event.setCancelled(true);
        event.getItem().remove();
    }
}
