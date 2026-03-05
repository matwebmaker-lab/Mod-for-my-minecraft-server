package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import java.util.Set;
import java.util.UUID;

/**
 * /mute [spiller] – toggle mute (hindre spiller i å skrive i chat). Kun OP.
 */
public final class MuteCommand implements CommandExecutor, TabCompleter {

    private final Set<UUID> mutedPlayers;

    public MuteCommand(Set<UUID> mutedPlayers) {
        this.mutedPlayers = mutedPlayers;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() && !sender.hasPermission("lager.mute")) {
            sender.sendMessage(Component.text("Kun OP kan bruke /mute.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text("Bruk: /mute <spiller>").color(NamedTextColor.GRAY));
            return true;
        }

        Player target = sender.getServer().getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(Component.text("Spiller ikke funnet: " + args[0]).color(NamedTextColor.RED));
            return true;
        }

        UUID id = target.getUniqueId();
        if (mutedPlayers.contains(id)) {
            mutedPlayers.remove(id);
            sender.sendMessage(Component.text(target.getName() + " kan skrive i chat igjen.").color(NamedTextColor.YELLOW));
            target.sendMessage(Component.text("Du er ikke lenger mutet.").color(NamedTextColor.GREEN));
        } else {
            mutedPlayers.add(id);
            sender.sendMessage(Component.text(target.getName() + " er mutet.").color(NamedTextColor.YELLOW));
            target.sendMessage(Component.text("Du er mutet og kan ikke skrive i chat.").color(NamedTextColor.RED));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player p) || (!p.isOp() && !p.hasPermission("lager.mute"))) return out;
        if (args.length != 1) return out;
        String prefix = args[0].toLowerCase(Locale.ROOT);
        for (Player online : sender.getServer().getOnlinePlayers()) {
            if (online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                out.add(online.getName());
            }
        }
        return out;
    }
}
