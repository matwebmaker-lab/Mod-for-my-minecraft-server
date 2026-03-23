package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.listeners.AdminFreezeListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class AdminBuffCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final String configPath;
    private final String labelName;

    public AdminBuffCommand(JavaPlugin plugin, String configPath, String labelName) {
        this.plugin = plugin;
        this.configPath = configPath;
        this.labelName = labelName;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke denne kommandoen.").color(NamedTextColor.RED));
            return true;
        }
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) {
            player.sendMessage(Component.text("Kun Admin_owner kan bruke /" + labelName + ".").color(NamedTextColor.RED));
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(Component.text("Bruk: /" + labelName + " <på|av>").color(NamedTextColor.GRAY));
            return true;
        }
        boolean on;
        String arg = args[0].toLowerCase();
        if (arg.equals("på") || arg.equals("pa") || arg.equals("on")) {
            on = true;
        } else if (arg.equals("av") || arg.equals("off")) {
            on = false;
        } else {
            player.sendMessage(Component.text("Bruk: /" + labelName + " <på|av>").color(NamedTextColor.GRAY));
            return true;
        }

        plugin.getConfig().set(configPath, on);
        plugin.saveConfig();
        player.sendMessage(Component.text(labelName + " ved join er nå " + (on ? "PÅ" : "AV") + ".").color(on ? NamedTextColor.GREEN : NamedTextColor.YELLOW));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            if ("på".startsWith(args[0].toLowerCase())) out.add("på");
            if ("av".startsWith(args[0].toLowerCase())) out.add("av");
        }
        return out;
    }
}
