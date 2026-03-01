package no.lager.lager.listeners;

import no.lager.lager.inventory.OpKisteHolder;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Når noen tar et item fra OP-kisten, refylles sloten så kisten aldri blir tom.
 */
public final class OpKisteRefillListener implements Listener {

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public OpKisteRefillListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof OpKisteHolder)) return;
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof OpKisteHolder)) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir() || !registry.isOpItem(clicked)) return;

        String id = registry.getOpItemId(clicked);
        if (id == null) return;

        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getOpenInventory().getTopInventory().getHolder() instanceof OpKisteHolder) {
                    ItemStack refill = registry.createItem(id);
                    if (refill != null) {
                        player.getOpenInventory().getTopInventory().setItem(slot, refill);
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}
