package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import no.lager.lager.inventory.OpKisteHolder;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Åpner OP-kisten – fylt med masse OP-gear som aldri blir tom (refylles når du tar).
 * Side 1, 2, … med «Neste side» / «Forrige side»-knapper.
 */
public final class KisteCommand implements org.bukkit.command.CommandExecutor {

    public static final String KISTE_TITLE = "OP Kiste";
    /** Slot for «Forrige side»-knappen (nederste rad). */
    public static final int SLOT_PREV = 45;
    /** Slot for «Neste side»-knappen (nederste rad). */
    public static final int SLOT_NEXT = 53;
    public static final int ITEMS_PER_PAGE = 45;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public KisteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    /** Åpner OP-kisten for spilleren på angitt side (0-indeksert). */
    public void openKiste(Player player, int page) {
        List<String> ids = registry.getChestFillIds();
        int totalPages = Math.max(1, (ids.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE);
        page = Math.max(0, Math.min(page, totalPages - 1));

        OpKisteHolder holder = new OpKisteHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text(KISTE_TITLE + " – Side " + (page + 1)));
        holder.setInventory(inv);
        holder.setPage(page);

        int start = page * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int idx = start + i;
            if (idx < ids.size()) {
                ItemStack item = registry.createItem(ids.get(idx));
                if (item != null) {
                    inv.setItem(i, item);
                }
            }
        }

        if (page > 0) {
            inv.setItem(SLOT_PREV, createNavItem("prev", Component.text("← Forrige side").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)));
        }
        if (page < totalPages - 1) {
            inv.setItem(SLOT_NEXT, createNavItem("next", Component.text("Neste side →").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true)));
        }

        player.openInventory(inv);
    }

    private ItemStack createNavItem(String navValue, Component displayName) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "kiste_nav"),
                    PersistentDataType.STRING,
                    navValue
            );
            stack.setItemMeta(meta);
        }
        return stack;
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

        openKiste(player, 0);
        return true;
    }
}
