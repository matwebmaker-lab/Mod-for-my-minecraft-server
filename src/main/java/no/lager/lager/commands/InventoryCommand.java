package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * /inventory view <spiller>
 * - Kun OP (eller med permission lager.inventoryview)
 * - Åpner inventaret til spilleren slik at du kan se og ta ting.
 */
public final class InventoryCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (!(sender instanceof Player viewer)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke /inventory.").color(NamedTextColor.RED));
            return true;
        }

        if (!viewer.isOp() && !viewer.hasPermission("lager.inventoryview")) {
            viewer.sendMessage(Component.text("Kun OP kan bruke /inventory.").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("view")) {
            viewer.sendMessage(Component.text("Bruk: /inventory view <spiller>").color(NamedTextColor.GRAY));
            return true;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            viewer.sendMessage(Component.text("Spiller \"" + targetName + "\" ble ikke funnet.").color(NamedTextColor.RED));
            return true;
        }

        if (target.equals(viewer)) {
            viewer.sendMessage(Component.text("Bruk heller ditt eget inventory direkte.").color(NamedTextColor.GRAY));
            return true;
        }

        // Åpner live-inventory til spilleren – du kan ta og legge inn ting.
        viewer.openInventory(target.getInventory());
        viewer.sendMessage(Component.text("Du ser nå inventory til " + target.getName() + ".").color(NamedTextColor.YELLOW));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player player)) return out;
        if (!player.isOp() && !player.hasPermission("lager.inventoryview")) return out;

        if (args.length == 1) {
            String a = args[0].toLowerCase(Locale.ROOT);
            if ("view".startsWith(a)) out.add("view");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("view")) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    out.add(p.getName());
                }
            }
        }
        return out;
    }
}

