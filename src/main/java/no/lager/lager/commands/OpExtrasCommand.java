package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Kommandoer: /opmode, /troll, /boost, /arena, /spectateplus.
 */
public final class OpExtrasCommand implements CommandExecutor, TabCompleter {

    private static final int OPMODE_DEFAULT_SEC = 60;
    private static final int BOOST_DURATION_TICKS = 600; // 30 sek
    private static final double ARENA_DEFAULT_X = 0;
    private static final double ARENA_DEFAULT_Y = 100;
    private static final double ARENA_DEFAULT_Z = 0;

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final Set<UUID> opmodeActive = new HashSet<>();
    private final Map<UUID, GameMode> opmodePreviousMode = new HashMap<>();
    private final Set<UUID> spectating = new HashSet<>();

    public OpExtrasCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase(Locale.ROOT);
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke denne kommandoen.").color(NamedTextColor.RED));
            return true;
        }
        if (!player.isOp()) {
            player.sendMessage(Component.text("Du har ikke tilgang.").color(NamedTextColor.RED));
            return true;
        }

        switch (cmd) {
            case "opmode" -> {
                int sec = args.length >= 1 ? parseInt(args[0], OPMODE_DEFAULT_SEC) : OPMODE_DEFAULT_SEC;
                sec = Math.min(300, Math.max(10, sec));
                opmodeActive.add(player.getUniqueId());
                opmodePreviousMode.put(player.getUniqueId(), player.getGameMode());
                player.setGameMode(GameMode.CREATIVE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, sec * 20, 1, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, sec * 20, 0, true, false));
                player.sendMessage(Component.text("OP-mode aktiv i " + sec + " sek.").color(NamedTextColor.GREEN));
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    opmodeActive.remove(player.getUniqueId());
                    GameMode prev = opmodePreviousMode.remove(player.getUniqueId());
                    if (prev != null && player.isOnline()) player.setGameMode(prev);
                }, sec * 20L);
                return true;
            }
            case "troll" -> {
                if (args.length < 1) {
                    player.sendMessage(Component.text("Bruk: /troll <spiller>").color(NamedTextColor.GRAY));
                    return true;
                }
                Player target = plugin.getServer().getPlayerExact(args[0]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage(Component.text("Spiller ikke funnet.").color(NamedTextColor.RED));
                    return true;
                }
                applyRandomTroll(target);
                player.sendMessage(Component.text("Troll-effekt på " + target.getName() + ".").color(NamedTextColor.YELLOW));
                return true;
            }
            case "boost" -> {
                Player target = args.length >= 1 ? plugin.getServer().getPlayerExact(args[0]) : player;
                if (target == null || !target.isOnline()) target = player;
                target.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, BOOST_DURATION_TICKS, 1, true, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, BOOST_DURATION_TICKS, 1, true, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, BOOST_DURATION_TICKS, 0, true, false));
                if (target != player) {
                    player.sendMessage(Component.text("Boost gitt til " + target.getName() + ".").color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("Boost aktiv i 30 sek.").color(NamedTextColor.GREEN));
                }
                return true;
            }
            case "arena" -> {
                World w = player.getWorld();
                Location arena = new Location(w, ARENA_DEFAULT_X, ARENA_DEFAULT_Y, ARENA_DEFAULT_Z);
                if (plugin.getConfig().contains("arena.x")) {
                    arena = new Location(w,
                            plugin.getConfig().getDouble("arena.x"),
                            plugin.getConfig().getDouble("arena.y"),
                            plugin.getConfig().getDouble("arena.z"));
                }
                player.teleport(arena);
                player.sendMessage(Component.text("Teleportert til arena.").color(NamedTextColor.GREEN));
                return true;
            }
            case "spectateplus" -> {
                if (args.length < 1) {
                    player.sendMessage(Component.text("Bruk: /spectateplus <spiller>").color(NamedTextColor.GRAY));
                    return true;
                }
                Player target = plugin.getServer().getPlayerExact(args[0]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage(Component.text("Spiller ikke funnet.").color(NamedTextColor.RED));
                    return true;
                }
                spectating.add(player.getUniqueId());
                player.teleport(target.getLocation());
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Component.text("Du spectater nå " + target.getName() + ".").color(NamedTextColor.GRAY));
                return true;
            }
            default -> { return false; }
        }
    }

    private void applyRandomTroll(Player target) {
        int r = ThreadLocalRandom.current().nextInt(6);
        switch (r) {
            case 0 -> target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0, true, true));
            case 1 -> target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2, true, true));
            case 2 -> target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0, true, true));
            case 3 -> target.getWorld().strikeLightning(target.getLocation());
            case 4 -> target.setVelocity(target.getLocation().getDirection().multiply(-1.5));
            default -> target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true, true));
        }
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && (command.getName().equalsIgnoreCase("troll") || command.getName().equalsIgnoreCase("boost") || command.getName().equalsIgnoreCase("spectateplus"))) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) names.add(p.getName());
            }
            return names;
        }
        return Collections.emptyList();
    }
}
