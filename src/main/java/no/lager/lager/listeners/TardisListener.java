package no.lager.lager.listeners;

import no.lager.lager.commands.TardisCommand;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TARDIS-hus: lite utvendig, stort innvendig. Går du inn i inngangen → teleport til interiør.
 * Når døra er åpen kan du gå inn; står du på utgangen inne → teleport tilbake til inngangen.
 */
public final class TardisListener implements Listener {

    private static final int COOLDOWN_TICKS = 30; // 1.5 sekunder mellom teleporter
    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastTeleport = new ConcurrentHashMap<>();

    public TardisListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        Location to = event.getTo();
        Player player = event.getPlayer();
        int bx = to.getBlockX(), by = to.getBlockY(), bz = to.getBlockZ();

        Location entrance = TardisCommand.getEntrance(plugin);
        Location interior = TardisCommand.getInterior(plugin);
        Location exit = TardisCommand.getExit(plugin);
        Location doorLoc = TardisCommand.getDoor(plugin);

        if (entrance == null || interior == null) return;

        long now = System.currentTimeMillis();
        if (now - lastTeleport.getOrDefault(player.getUniqueId(), 0L) < COOLDOWN_TICKS * 50L) return;

        // Står på utgangen inne → tilbake til inngangen (lite hus)
        if (exit != null && sameBlockInWorld(player, bx, by, bz, exit)) {
            lastTeleport.put(player.getUniqueId(), now);
            player.teleport(entrance);
            return;
        }

        // Står på inngangen (lite hus) og går inn → til det store rommet
        if (!sameBlockInWorld(player, bx, by, bz, entrance)) return;
        if (doorLoc != null && !isDoorOpen(doorLoc)) return; // dør satt og lukket = ikke inn

        lastTeleport.put(player.getUniqueId(), now);
        player.teleport(interior);
    }

    private boolean sameBlockInWorld(Player player, int bx, int by, int bz, Location loc) {
        if (loc == null || loc.getWorld() == null || player.getWorld() != loc.getWorld()) return false;
        return loc.getBlockX() == bx && loc.getBlockY() == by && loc.getBlockZ() == bz;
    }

    private boolean isDoorOpen(Location doorLoc) {
        if (doorLoc.getWorld() == null) return true;
        Block block = doorLoc.getBlock();
        if (block.getBlockData() instanceof Openable openable) return openable.isOpen();
        return true;
    }
}
