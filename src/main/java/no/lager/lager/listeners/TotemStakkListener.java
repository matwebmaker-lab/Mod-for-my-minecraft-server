package no.lager.lager.listeners;

import no.lager.lager.items.OpItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Totem-stakk: høyreklikk gir ett Operator Totem og bruker én fra stakken.
 */
public final class TotemStakkListener implements Listener {

    private static final String TOTEM_STAKK_ID = "totem_stakk";

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public TotemStakkListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main.isEmpty() || !registry.isOpItem(main) || !TOTEM_STAKK_ID.equals(registry.getOpItemId(main))) return;

        event.setCancelled(true);
        ItemStack totem = registry.createItem("op_totem");
        if (totem == null) return;

        if (player.getInventory().addItem(totem).isEmpty()) {
            main.setAmount(main.getAmount() - 1);
            if (main.getAmount() <= 0) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
        } else {
            player.getWorld().dropItem(player.getLocation(), totem);
            main.setAmount(main.getAmount() - 1);
            if (main.getAmount() <= 0) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
        }
    }
}
