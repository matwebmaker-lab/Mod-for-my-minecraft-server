package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Admin_owner: trykk shift for å fryse/avfryse spillere og entities rundt seg.
 * (Ctrl kan ikke oppdages på server, derfor brukes shift.)
 */
public final class AdminFreezeListener implements Listener {

    public static final String ADMIN_OWNER_NAME = "Admin_owner";
    private static final double FREEZE_RADIUS = 15.0;

    private final JavaPlugin plugin;
    private final Set<UUID> freezeActive = ConcurrentHashMap.newKeySet();

    public AdminFreezeListener(JavaPlugin plugin) {
        this.plugin = plugin;
        startFreezeTask();
    }

    private void startFreezeTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID adminId : freezeActive) {
                    Player admin = plugin.getServer().getPlayer(adminId);
                    if (admin == null || !admin.isOnline()) continue;
                    Location center = admin.getLocation();
                    for (Entity entity : admin.getWorld().getNearbyEntities(center, FREEZE_RADIUS, FREEZE_RADIUS, FREEZE_RADIUS)) {
                        if (entity.getUniqueId().equals(adminId)) continue;
                        entity.setVelocity(new Vector(0, 0, 0));
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!ADMIN_OWNER_NAME.equals(event.getPlayer().getName())) return;
        if (!event.isSneaking()) return;
        UUID id = event.getPlayer().getUniqueId();
        if (freezeActive.contains(id)) {
            freezeActive.remove(id);
            event.getPlayer().sendMessage(Component.text("Frys av – spillere og entities kan bevege seg igjen.").color(NamedTextColor.YELLOW));
        } else {
            freezeActive.add(id);
            event.getPlayer().sendMessage(Component.text("Frys på – spillere og entities rundt deg er fryst.").color(NamedTextColor.GREEN));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        Player mover = event.getPlayer();
        for (UUID adminId : freezeActive) {
            Player admin = plugin.getServer().getPlayer(adminId);
            if (admin == null || !admin.isOnline()) continue;
            if (admin.getWorld() != mover.getWorld()) continue;
            if (admin.getLocation().distance(mover.getLocation()) <= FREEZE_RADIUS) {
                event.setCancelled(true);
                mover.teleport(event.getFrom());
                return;
            }
        }
    }

    /** Sjekk om spilleren er Admin_owner (brukes f.eks. for å ikke åpne Matheo client på shift). */
    public static boolean isAdminOwner(Player player) {
        return player != null && ADMIN_OWNER_NAME.equals(player.getName());
    }
}
