package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.items.TrollItemRegistry;
import no.lager.lager.villager.TrollVillagerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * /spawnvillager <scammer|jester|tax|boomer|glitch> – spawn en troll-landsbyboer (kun OP).
 */
public final class SpawnVillagerCommand implements CommandExecutor, TabCompleter {

    private static final String[] TYPES = { TrollVillagerType.SCAMMER, TrollVillagerType.JESTER, TrollVillagerType.TAX, TrollVillagerType.BOOMER, TrollVillagerType.GLITCH };

    private final JavaPlugin plugin;
    private final TrollItemRegistry trollItems;

    public SpawnVillagerCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.trollItems = new TrollItemRegistry(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke denne kommandoen.").color(NamedTextColor.RED));
            return true;
        }
        if (!player.isOp() && !player.hasPermission("lager.spawnvillager")) {
            player.sendMessage(Component.text("Kun OP kan spawne troll-landsbyboere.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Component.text("Bruk: /spawnvillager <scammer|jester|tax|boomer|glitch>").color(NamedTextColor.GRAY));
            return true;
        }

        String type = args[0].toLowerCase(Locale.ROOT);
        if (!List.of(TYPES).contains(type)) {
            player.sendMessage(Component.text("Ukjent type. Bruk: scammer, jester, tax, boomer, glitch").color(NamedTextColor.RED));
            return true;
        }

        Villager v = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        TrollVillagerType.setType(v, type, plugin);
        TrollVillagerType.applyAppearance(v, type);

        switch (type) {
            case TrollVillagerType.SCAMMER -> TrollVillagerType.setScammerRecipes(v, trollItems);
            case TrollVillagerType.BOOMER -> TrollVillagerType.setBoomerRecipes(v);
            case TrollVillagerType.GLITCH -> v.setRecipes(TrollVillagerType.randomGlitchRecipes(trollItems));
            case TrollVillagerType.JESTER -> TrollVillagerType.setJesterRecipes(v);
            case TrollVillagerType.TAX -> TrollVillagerType.setTaxRecipes(v);
        }

        player.sendMessage(Component.text("Troll-landsbyboer spawnet: " + type).color(NamedTextColor.YELLOW));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player p) || (!p.isOp() && !p.hasPermission("lager.spawnvillager"))) return out;
        if (args.length != 1) return out;
        String prefix = args[0].toLowerCase(Locale.ROOT);
        for (String t : TYPES) {
            if (t.startsWith(prefix)) out.add(t);
        }
        return out;
    }
}
