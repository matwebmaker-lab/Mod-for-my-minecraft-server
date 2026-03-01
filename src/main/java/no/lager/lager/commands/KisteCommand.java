package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.inventory.OpKisteHolder;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Åpner OP-kisten – fylt med masse OP-gear som aldri blir tom (refylles når du tar).
 */
public final class KisteCommand implements org.bukkit.command.CommandExecutor {

    public static final String KISTE_TITLE = "OP Kiste";

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public KisteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan åpne kisten.").color(NamedTextColor.RED));
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(Component.text("Kun operatorer kan åpne OP-kisten.").color(NamedTextColor.RED));
            return true;
        }

        OpKisteHolder holder = new OpKisteHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text(KISTE_TITLE));
        holder.setInventory(inv);
        List<String> ids = registry.getChestFillIds();
        for (int i = 0; i < Math.min(54, ids.size()); i++) {
            ItemStack item = registry.createItem(ids.get(i));
            if (item != null) {
                inv.setItem(i, item);
            }
        }
        player.openInventory(inv);
        return true;
    }
}
