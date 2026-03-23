package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Gir Admin_owner Fire Resistance nivå 100 i 10 minutter ved join.
 */
public final class AdminOwnerJoinBuffListener implements Listener {

    private static final int FIRE_RESIST_DURATION_TICKS = 10 * 60 * 20; // 10 minutter
    private static final int FIRE_RESIST_AMPLIFIER = 99; // nivå 100
    private static final int SLOW_FALLING_DURATION_TICKS = 10 * 20; // 10 sekunder

    private final JavaPlugin plugin;

    public AdminOwnerJoinBuffListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) return;

        boolean fireResOn = plugin.getConfig().getBoolean("admin_owner.fire_resistance_on_join", true);
        boolean slowFallingOn = plugin.getConfig().getBoolean("admin_owner.slow_falling_on_join", true);

        if (fireResOn) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.FIRE_RESISTANCE,
                    FIRE_RESIST_DURATION_TICKS,
                    FIRE_RESIST_AMPLIFIER,
                    true,
                    true
            ));
            player.sendMessage(Component.text("Du fikk Fire Resistance 100 i 10 minutter.").color(NamedTextColor.GOLD));
        }
        if (slowFallingOn) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOW_FALLING,
                    SLOW_FALLING_DURATION_TICKS,
                    0,
                    true,
                    true
            ));
            player.sendMessage(Component.text("Du fikk Slow Falling i 10 sekunder.").color(NamedTextColor.AQUA));
        }

        // Reparer armor til full durability ved join.
        ItemStack[] armor = player.getInventory().getArmorContents();
        boolean repaired = false;
        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];
            if (piece == null || piece.getType().isAir()) continue;
            ItemMeta meta = piece.getItemMeta();
            if (meta instanceof Damageable damageable) {
                if (damageable.getDamage() > 0) {
                    damageable.setDamage(0);
                    piece.setItemMeta((ItemMeta) damageable);
                    armor[i] = piece;
                    repaired = true;
                }
            }
        }
        if (repaired) {
            player.getInventory().setArmorContents(armor);
            player.sendMessage(Component.text("Armor ble reparert til full holdbarhet.").color(NamedTextColor.GREEN));
        }
    }
}
