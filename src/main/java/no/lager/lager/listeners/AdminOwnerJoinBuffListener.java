package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Gir Admin_owner Fire Resistance nivå 100 i 10 minutter ved join.
 */
public final class AdminOwnerJoinBuffListener implements Listener {

    private static final int FIRE_RESIST_DURATION_TICKS = 10 * 60 * 20; // 10 minutter
    private static final int FIRE_RESIST_AMPLIFIER = 99; // nivå 100

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.FIRE_RESISTANCE,
                FIRE_RESIST_DURATION_TICKS,
                FIRE_RESIST_AMPLIFIER,
                true,
                true
        ));
        player.sendMessage(Component.text("Du fikk Fire Resistance 100 i 10 minutter.").color(NamedTextColor.GOLD));
    }
}
