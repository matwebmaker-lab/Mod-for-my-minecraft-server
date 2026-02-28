package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Kommando som åpner admin-kisten – kun for spilleren Admin_owner.
 * Kisten er fylt med mange OP-items.
 */
public final class AdminChestCommand implements org.bukkit.command.CommandExecutor {

    public static final String ADMIN_CHEST_TITLE = "Admin OP-kiste";
    public static final String ALLOWED_PLAYER = "Admin_owner";

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public AdminChestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan åpne kisten.").color(NamedTextColor.RED));
            return true;
        }

        if (!ALLOWED_PLAYER.equals(player.getName())) {
            player.sendMessage(Component.text("Kun Admin_owner kan åpne denne kisten.").color(NamedTextColor.RED));
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 54, Component.text(ADMIN_CHEST_TITLE));
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
