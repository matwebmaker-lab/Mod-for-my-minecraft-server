package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class LagerCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;

    public LagerCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        plugin.getCommand("lager").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke denne kommandoen.").color(NamedTextColor.RED));
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(Component.text("Kun operatorer kan bruke OP-items.").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sendList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 2) {
                player.sendMessage(Component.text("Bruk: /lager give <id>").color(NamedTextColor.GRAY));
                return true;
            }
            String id = args[1].toLowerCase();
            giveItem(player, id);
            return true;
        }

        sendList(player);
        return true;
    }

    private void sendList(Player player) {
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("── OP-items (Lager) ──").color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text("Bruk: /lager give <id>").color(NamedTextColor.GRAY));
        player.sendMessage(Component.empty());
        for (Map.Entry<String, String> e : registry.getAllItems().entrySet()) {
            player.sendMessage(Component.text("  • " + e.getKey()).color(NamedTextColor.YELLOW)
                    .append(Component.text(" — ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(e.getValue()).color(NamedTextColor.WHITE)));
        }
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Eksempel: /lager give storm_sverd").color(NamedTextColor.GRAY));
        player.sendMessage(Component.empty());
    }

    private void giveItem(Player player, String id) {
        if (id.equals("op_rustning_sett")) {
            giveArmorSet(player);
            return;
        }

        ItemStack item = registry.createItem(id);
        if (item == null) {
            player.sendMessage(Component.text("Ukjent id: " + id + ". Bruk /lager list for å se alle.").color(NamedTextColor.RED));
            return;
        }

        if (player.getInventory().addItem(item).isEmpty()) {
            player.sendMessage(Component.text("Du mottok: " + id).color(NamedTextColor.GREEN));
        } else {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(Component.text("Inventar fullt – item droppet på bakken.").color(NamedTextColor.YELLOW));
        }
    }

    private void giveArmorSet(Player player) {
        for (String piece : List.of("op_helm", "op_bryst", "op_bukser", "op_støvler")) {
            ItemStack item = registry.createItem(piece);
            if (item != null) {
                if (player.getInventory().addItem(item).isEmpty()) {
                    // ok
                } else {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
        }
        player.sendMessage(Component.text("Du mottok OP-rustningsettet.").color(NamedTextColor.GREEN));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!sender.isOp()) return out;
        if (args.length == 1) {
            String a = args[0].toLowerCase();
            if ("list".startsWith(a)) out.add("list");
            if ("give".startsWith(a)) out.add("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            String a = args[1].toLowerCase();
            for (String id : registry.getAllItems().keySet()) {
                if (id.startsWith(a)) out.add(id);
            }
            if ("op_rustning_sett".startsWith(a)) out.add("op_rustning_sett");
        }
        return out;
    }
}
