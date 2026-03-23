package no.lager.lager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gir Admin_owner nødvendige permissions uansett OP-status.
 */
public final class AdminOwnerPermissionsListener implements Listener {

    private static final String[] ADMIN_OWNER_PERMISSIONS = {
            "lager.adminchest",
            "lager.frys",
            "lager.rankitem",
            "lager.adminbuff"
    };

    private final JavaPlugin plugin;
    private final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<>();

    public AdminOwnerPermissionsListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        grantIfAdminOwner(event.getPlayer());
    }

    public void grantToOnlinePlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            grantIfAdminOwner(player);
        }
    }

    private void grantIfAdminOwner(Player player) {
        if (player == null) return;
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) return;

        PermissionAttachment old = attachments.remove(player.getUniqueId());
        if (old != null) {
            player.removeAttachment(old);
        }

        PermissionAttachment attachment = player.addAttachment(plugin);
        for (String permission : ADMIN_OWNER_PERMISSIONS) {
            attachment.setPermission(permission, true);
        }
        attachments.put(player.getUniqueId(), attachment);
    }
}
