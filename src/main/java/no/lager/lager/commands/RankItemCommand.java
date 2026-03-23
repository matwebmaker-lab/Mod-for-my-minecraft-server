package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import no.lager.lager.listeners.AdminFreezeListener;
import no.lager.lager.listeners.RankItemListener;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class RankItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke /rankitem.").color(NamedTextColor.RED));
            return true;
        }
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) {
            player.sendMessage(Component.text("Kun Admin_owner kan lage rank-items.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length < 2 || !args[0].equalsIgnoreCase("create")) {
            player.sendMessage(Component.text("Bruk: /rankitem create <rank>").color(NamedTextColor.GRAY));
            return true;
        }

        String rank = sanitizeRank(args[1]);
        if (rank == null) {
            player.sendMessage(Component.text("Ugyldig rank. Bruk kun bokstaver/tall/_/- (1-24 tegn).").color(NamedTextColor.RED));
            return true;
        }

        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Rank-item: " + rank).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Høyreklikk for å få ranken: " + rank).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("Forbrukes ved bruk.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
            ));
            meta.getPersistentDataContainer().set(RankItemListener.RANK_ITEM_KEY, org.bukkit.persistence.PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(RankItemListener.RANK_ID_KEY, org.bukkit.persistence.PersistentDataType.STRING, rank);
            item.setItemMeta(meta);
        }

        if (player.getInventory().addItem(item).isEmpty()) {
            player.sendMessage(Component.text("Du laget rank-item for: " + rank).color(NamedTextColor.GREEN));
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(Component.text("Inventory fullt: rank-item droppet på bakken.").color(NamedTextColor.YELLOW));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1 && "create".startsWith(args[0].toLowerCase(Locale.ROOT))) {
            out.add("create");
        }
        return out;
    }

    private String sanitizeRank(String input) {
        if (input == null) return null;
        String rank = input.trim();
        if (rank.length() < 1 || rank.length() > 24) return null;
        if (!rank.matches("[A-Za-z0-9_-]+")) return null;
        return rank;
    }
}
