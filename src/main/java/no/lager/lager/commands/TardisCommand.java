package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * /tardis setentrance | setinterior | setexit | setdoor | info
 * Lite hus utvendig – stort innvendig. Når døra er åpen kan du gå inn og se at det er stort inne.
 */
public final class TardisCommand implements CommandExecutor, TabCompleter {

    private static final String KEY_ENTRANCE = "tardis.entrance";
    private static final String KEY_INTERIOR = "tardis.interior";
    private static final String KEY_EXIT = "tardis.exit";
    private static final String KEY_DOOR = "tardis.door";

    private final JavaPlugin plugin;

    public TardisCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke /tardis.").color(NamedTextColor.RED));
            return true;
        }
        if (!player.isOp() && !player.hasPermission("lager.tardis")) {
            player.sendMessage(Component.text("Du har ikke tilgang til /tardis.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        FileConfiguration config = plugin.getConfig();

        switch (sub) {
            case "setentrance" -> {
                Location entranceLoc = player.getLocation();
                Block doorBlock = entranceLoc.getBlock().getRelative(blockFaceFromPlayer(player));
                if (!placeTardisDoor(doorBlock, player)) {
                    player.sendMessage(Component.text("Kunne ikke plassere TARDIS-dør (sjekk at det er ledig foran deg).").color(NamedTextColor.RED));
                    return true;
                }
                saveLoc(config, KEY_ENTRANCE, entranceLoc);
                saveLoc(config, KEY_DOOR, doorBlock.getLocation());
                plugin.saveConfig();
                player.sendMessage(Component.text("TARDIS-inngang og egen dør satt. Åpne døra og gå inn for å komme til det store rommet.").color(NamedTextColor.GREEN));
            }
            case "setinterior" -> {
                saveLoc(config, KEY_INTERIOR, player.getLocation());
                plugin.saveConfig();
                player.sendMessage(Component.text("TARDIS-interiør satt her (stort rom – spillere teleporteres hit når de går inn i det lille huset).").color(NamedTextColor.GREEN));
            }
            case "setexit" -> {
                saveLoc(config, KEY_EXIT, player.getLocation());
                plugin.saveConfig();
                player.sendMessage(Component.text("TARDIS-utgang satt her (stå her inne for å komme tilbake til det lille huset).").color(NamedTextColor.GREEN));
            }
            case "setdoor" -> {
                player.sendMessage(Component.text("Bruk /tardis setentrance – da plasseres TARDIS-døra automatisk.").color(NamedTextColor.GRAY));
            }
            case "info" -> sendInfo(player, config);
            default -> sendUsage(player);
        }
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(Component.text("Bruk: /tardis setentrance | setinterior | setexit | setdoor | info").color(NamedTextColor.GRAY));
        player.sendMessage(Component.text("Lite hus utvendig → stort rom innvendig. Sett inngang, interiør og utgang.").color(NamedTextColor.DARK_GRAY));
    }

    private void sendInfo(Player player, FileConfiguration config) {
        player.sendMessage(Component.text("── TARDIS ──").color(NamedTextColor.GOLD));
        if (hasLoc(config, KEY_ENTRANCE)) {
            player.sendMessage(Component.text("Inngang: " + formatLoc(config, KEY_ENTRANCE)).color(NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("Inngang: ikke satt").color(NamedTextColor.RED));
        }
        if (hasLoc(config, KEY_INTERIOR)) {
            player.sendMessage(Component.text("Interiør: " + formatLoc(config, KEY_INTERIOR)).color(NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("Interiør: ikke satt").color(NamedTextColor.RED));
        }
        if (hasLoc(config, KEY_EXIT)) {
            player.sendMessage(Component.text("Utgang: " + formatLoc(config, KEY_EXIT)).color(NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("Utgang: ikke satt").color(NamedTextColor.RED));
        }
        if (hasLoc(config, KEY_DOOR)) {
            player.sendMessage(Component.text("TARDIS-dør: plassert (kun åpen dør = innpass)").color(NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("TARDIS-dør: ikke satt (bruk setentrance for egen dør)").color(NamedTextColor.GRAY));
        }
    }

    /** Retning fra spiller til blokken foran (der døra skal stå). */
    private static BlockFace blockFaceFromPlayer(Player player) {
        return player.getFacing();
    }

    /** Plasserer TARDIS-dør (jerndør) – nedre og øvre halvdel. Returnerer false hvis plassering feiler. */
    private boolean placeTardisDoor(Block base, Player player) {
        if (base.getWorld() == null) return false;
        Block above = base.getRelative(BlockFace.UP);
        if (!base.getType().isAir()) return false;
        if (!above.getType().isAir()) return false;

        BlockFace facing = BlockFace.NORTH;
        int px = player.getLocation().getBlockX(), pz = player.getLocation().getBlockZ();
        int dx = base.getX(), dz = base.getZ();
        if (pz < dz) facing = BlockFace.NORTH;
        else if (pz > dz) facing = BlockFace.SOUTH;
        else if (px < dx) facing = BlockFace.WEST;
        else if (px > dx) facing = BlockFace.EAST;

        Door bottom = (Door) Material.IRON_DOOR.createBlockData();
        bottom.setHalf(Bisected.Half.BOTTOM);
        bottom.setFacing(facing);
        base.setBlockData(bottom);

        Door top = (Door) Material.IRON_DOOR.createBlockData();
        top.setHalf(Bisected.Half.TOP);
        top.setFacing(facing);
        above.setBlockData(top);
        return true;
    }

    private void saveLoc(FileConfiguration config, String prefix, Location loc) {
        config.set(prefix + ".world", loc.getWorld() != null ? loc.getWorld().getName() : null);
        config.set(prefix + ".x", loc.getBlockX());
        config.set(prefix + ".y", loc.getBlockY());
        config.set(prefix + ".z", loc.getBlockZ());
    }

    private boolean hasLoc(FileConfiguration config, String prefix) {
        return config.contains(prefix + ".world") && config.contains(prefix + ".x");
    }

    private String formatLoc(FileConfiguration config, String prefix) {
        return config.getString(prefix + ".world") + " " + config.getInt(prefix + ".x") + " " + config.getInt(prefix + ".y") + " " + config.getInt(prefix + ".z");
    }

    public static Location getEntrance(JavaPlugin plugin) { return getLoc(plugin.getConfig(), KEY_ENTRANCE, plugin); }
    public static Location getInterior(JavaPlugin plugin) { return getLoc(plugin.getConfig(), KEY_INTERIOR, plugin); }
    public static Location getExit(JavaPlugin plugin) { return getLoc(plugin.getConfig(), KEY_EXIT, plugin); }
    public static Location getDoor(JavaPlugin plugin) { return getLoc(plugin.getConfig(), KEY_DOOR, plugin); }

    public static Location getLoc(FileConfiguration config, String prefix, JavaPlugin plugin) {
        String worldName = config.getString(prefix + ".world");
        if (worldName == null) return null;
        var w = plugin.getServer().getWorld(worldName);
        if (w == null) return null;
        return new Location(w, config.getInt(prefix + ".x") + 0.5, config.getInt(prefix + ".y"), config.getInt(prefix + ".z") + 0.5);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player p) || (!p.isOp() && !p.hasPermission("lager.tardis"))) return out;
        if (args.length != 1) return out;
        String a = args[0].toLowerCase(Locale.ROOT);
        for (String s : List.of("setentrance", "setinterior", "setexit", "setdoor", "info")) {
            if (s.startsWith(a)) out.add(s);
        }
        return out;
    }
}
