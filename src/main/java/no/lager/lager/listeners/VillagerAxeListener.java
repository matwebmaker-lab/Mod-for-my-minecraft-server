package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.items.OpItemRegistry;
import no.lager.lager.villager.TrollVillagerType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Når OP peker (høyreklikk) på en villager med Tordenøksen, får de noe bra.
 */
public final class VillagerAxeListener implements Listener {

    private static final String TORDENØKS_ID = "tordenøks";

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public VillagerAxeListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;
        // Troll-landsbyboere skal alltid kunne handles med – ikke kanseller
        if (TrollVillagerType.isTrollVillager(villager, plugin)) return;
        Player player = event.getPlayer();
        if (!player.isOp()) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty() || !registry.isOpItem(item) || !TORDENØKS_ID.equals(registry.getOpItemId(item))) return;

        event.setCancelled(true);

        // Gi noe bra: emeralds, diamant, gulleneple eller totem
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int roll = r.nextInt(100);
        if (roll < 40) {
            giveStack(player, Material.EMERALD, 3 + r.nextInt(10));
        } else if (roll < 70) {
            giveStack(player, Material.DIAMOND, 1 + r.nextInt(3));
        } else if (roll < 88) {
            ItemStack apple = registry.createItem("op_gulleneple");
            if (apple != null) giveOrDrop(player, apple);
        } else {
            ItemStack totem = registry.createItem("op_totem");
            if (totem != null) giveOrDrop(player, totem);
        }
        player.sendMessage(Component.text("Villageren ga deg noe bra!").color(NamedTextColor.GREEN));
    }

    private void giveStack(Player player, Material mat, int amount) {
        ItemStack stack = new ItemStack(mat, amount);
        if (player.getInventory().addItem(stack).isEmpty()) return;
        player.getWorld().dropItem(player.getLocation(), stack);
    }

    private void giveOrDrop(Player player, ItemStack stack) {
        if (player.getInventory().addItem(stack).isEmpty()) return;
        player.getWorld().dropItem(player.getLocation(), stack);
    }
}
