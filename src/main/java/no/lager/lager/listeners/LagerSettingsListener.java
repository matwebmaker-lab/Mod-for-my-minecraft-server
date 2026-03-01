package no.lager.lager.listeners;

import no.lager.lager.inventory.LagerSettingsHolder;
import no.lager.lager.items.OpItemRegistry;
import no.lager.lager.LagerPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Åpner Matheo client (innstillinger) når OP holder Matheo client-item og trykker høyre shift.
 * Håndterer +/- for armor-reach i skjermen.
 */
public final class LagerSettingsListener implements Listener {

    private static final String INSTILLINGER_ID = "instillinger";

    private final LagerPlugin plugin;
    private final OpItemRegistry registry;

    public LagerSettingsListener(LagerPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (!player.isOp()) return;
        if (AdminFreezeListener.isAdminOwner(player)) return; // Admin_owner bruker shift til frys
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main.isEmpty() || !registry.isOpItem(main) || !INSTILLINGER_ID.equals(registry.getOpItemId(main))) return;
        plugin.openSettingsGui(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof LagerSettingsHolder)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof LagerSettingsHolder)) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir() || !clicked.hasItemMeta()) return;
        if (!clicked.getItemMeta().getPersistentDataContainer().has(plugin.getSettingsButtonKey(), PersistentDataType.STRING)) return;

        String btn = clicked.getItemMeta().getPersistentDataContainer().get(plugin.getSettingsButtonKey(), PersistentDataType.STRING);
        Player player = (Player) event.getWhoClicked();
        int reach = plugin.getArmorReach();
        if ("minus".equals(btn)) {
            plugin.setArmorReach(reach - 1);
            int now = plugin.getArmorReach();
            player.sendMessage(net.kyori.adventure.text.Component.text(now == 0 ? "Reach satt til vanlig." : "Armor reach satt til " + now + " blokker.").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
        } else if ("plus".equals(btn)) {
            plugin.setArmorReach(reach + 1);
            player.sendMessage(net.kyori.adventure.text.Component.text("Armor reach satt til " + plugin.getArmorReach() + " blokker.").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
        } else if ("vanlig".equals(btn)) {
            plugin.setArmorReach(0);
            player.sendMessage(net.kyori.adventure.text.Component.text("Reach satt til vanlig (ingen ekstra reach).").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
        } else if ("fullbright".equals(btn)) {
            boolean now = !plugin.isFullBright(player.getUniqueId());
            plugin.setFullBright(player, now);
            player.sendMessage(net.kyori.adventure.text.Component.text("Full bright " + (now ? "på." : "av.")).color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
        }
        plugin.openSettingsGui(player);
    }
}
