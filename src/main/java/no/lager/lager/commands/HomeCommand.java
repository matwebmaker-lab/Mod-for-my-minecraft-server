package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.storage.HomeStorage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class HomeCommand implements CommandExecutor, TabCompleter {

    private final HomeStorage homeStorage;

    public HomeCommand(HomeStorage homeStorage) {
        this.homeStorage = homeStorage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke /home.").color(NamedTextColor.RED));
            return true;
        }
        if (!player.hasPermission("lager.home")) {
            player.sendMessage(Component.text("Du har ikke tilgang til /home.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(Component.text("Bruk: /home <1|2|3>").color(NamedTextColor.GRAY));
            return true;
        }

        Integer slot = parseSlot(args[0]);
        if (slot == null) {
            player.sendMessage(Component.text("Ugyldig slot. Bruk 1, 2 eller 3.").color(NamedTextColor.RED));
            return true;
        }

        if (!homeStorage.hasHome(player.getUniqueId(), slot)) {
            player.sendMessage(Component.text("Du har ikke satt home " + slot + " enda. Bruk /sethome " + slot + ".").color(NamedTextColor.YELLOW));
            return true;
        }

        Location homeLoc = homeStorage.getHome(player.getUniqueId(), slot);
        if (homeLoc == null || homeLoc.getWorld() == null) {
            player.sendMessage(Component.text("Home " + slot + " finnes ikke i en gyldig verden lenger.").color(NamedTextColor.RED));
            return true;
        }

        player.teleport(homeLoc);
        player.sendMessage(Component.text("Teleportert til home " + slot + ".").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player player) || !player.hasPermission("lager.home")) return out;
        if (args.length == 1) {
            for (String s : List.of("1", "2", "3")) {
                if (s.startsWith(args[0])) out.add(s);
            }
        }
        return out;
    }

    private Integer parseSlot(String raw) {
        try {
            int slot = Integer.parseInt(raw);
            return (slot >= 1 && slot <= 3) ? slot : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
