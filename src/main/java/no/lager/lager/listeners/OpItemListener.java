package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * - OP-spillere får OP-items i inventaret når de kobler til.
 * - Kun OP kan eie OP-items: ikke-OP som plukker opp fjernes itemet.
 */
public final class OpItemListener implements Listener {

    private static final List<String> OP_JOIN_ITEMS = List.of(
            "op_helm", "op_bryst", "op_bukser", "op_støvler",
            "storm_sverd", "dødspiler", "tordenøks", "krossbue", "trident"
    );

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public OpItemListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;

        for (String id : OP_JOIN_ITEMS) {
            ItemStack item = registry.createItem(id);
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }
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
