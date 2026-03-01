package no.lager.lager.listeners;

import no.lager.lager.commands.KisteCommand;
import no.lager.lager.inventory.OpKisteHolder;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Når noen tar et item fra OP-kisten, refylles sloten så kisten aldri blir tom.
 * Klikk på «Neste side» / «Forrige side» bytter side.
 */
public final class OpKisteRefillListener implements Listener {

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final KisteCommand kisteCommand;
    private final NamespacedKey kisteNavKey;

    public OpKisteRefillListener(JavaPlugin plugin, KisteCommand kisteCommand) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        this.kisteCommand = kisteCommand;
        this.kisteNavKey = new NamespacedKey(plugin, "kiste_nav");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof OpKisteHolder holder)) return;
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof OpKisteHolder)) return;

        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();

        // Navigasjonsknapper: Neste side / Forrige side
        if (slot == KisteCommand.SLOT_PREV || slot == KisteCommand.SLOT_NEXT) {
            if (clicked != null && !clicked.getType().isAir() && clicked.hasItemMeta()
                    && clicked.getItemMeta().getPersistentDataContainer().has(kisteNavKey, PersistentDataType.STRING)) {
                String nav = clicked.getItemMeta().getPersistentDataContainer().get(kisteNavKey, PersistentDataType.STRING);
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                if ("next".equals(nav)) {
                    kisteCommand.openKiste(player, holder.getPage() + 1);
                } else if ("prev".equals(nav)) {
                    kisteCommand.openKiste(player, holder.getPage() - 1);
                }
                return;
            }
        }

        // Vanlig refill for OP-items (kun på innholdsslot 0–44)
        if (clicked == null || clicked.getType().isAir() || !registry.isOpItem(clicked)) return;
        String id = registry.getOpItemId(clicked);
        if (id == null) return;

        Player player = (Player) event.getWhoClicked();
        int slotFinal = slot;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getOpenInventory().getTopInventory().getHolder() instanceof OpKisteHolder) {
                    ItemStack refill = registry.createItem(id);
                    if (refill != null) {
                        player.getOpenInventory().getTopInventory().setItem(slotFinal, refill);
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}
