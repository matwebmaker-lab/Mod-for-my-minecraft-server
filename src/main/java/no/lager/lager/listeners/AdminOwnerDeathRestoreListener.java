package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gjenoppretter inventory for Admin_owner etter død.
 */
public final class AdminOwnerDeathRestoreListener implements Listener {

    private record DeathSnapshot(ItemStack[] contents, ItemStack[] armor, ItemStack offHand, int totalExp, int level) {}

    private final JavaPlugin plugin;
    private final Map<UUID, DeathSnapshot> snapshots = new ConcurrentHashMap<>();

    public AdminOwnerDeathRestoreListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) return;

        DeathSnapshot snap = new DeathSnapshot(
                player.getInventory().getContents().clone(),
                player.getInventory().getArmorContents().clone(),
                player.getInventory().getItemInOffHand().clone(),
                player.getTotalExperience(),
                player.getLevel()
        );
        snapshots.put(player.getUniqueId(), snap);

        // Hindrer dropp/tap av nivå.
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepLevel(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) return;

        DeathSnapshot snap = snapshots.remove(player.getUniqueId());
        if (snap == null) return;

        // Vent én tick slik at inventory er klart etter respawn.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.getInventory().setContents(snap.contents());
            player.getInventory().setArmorContents(snap.armor());
            player.getInventory().setItemInOffHand(snap.offHand());
            player.setLevel(snap.level());
            player.setTotalExperience(snap.totalExp());
            player.sendMessage(Component.text("Dine ting ble gjenopprettet etter døden.").color(NamedTextColor.GREEN));
        });
    }
}
