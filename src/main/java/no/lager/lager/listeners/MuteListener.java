package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.UUID;

/**
 * Blokkerer chat for spillere som er mutet (/mute).
 */
public final class MuteListener implements Listener {

    private final Set<UUID> mutedPlayers;

    public MuteListener(Set<UUID> mutedPlayers) {
        this.mutedPlayers = mutedPlayers;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (mutedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Du er mutet og kan ikke skrive i chat.").color(NamedTextColor.RED));
        }
    }
}
