package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OP-kommandoer: /blind, /levitate, /anvil, /scare, /spin, /spam, /jail, /tnt, /invclear
 * Kun OP (eller lager.opfun).
 */
public final class OpFunCommand implements CommandExecutor, TabCompleter {

    private static final String[] SPAM_LINES = {
            "§c§lTRØBBEL!", "§6§lOP SA DET!", "§e§lSPAM!", "§a§lHEI!", "§b§lTULL!",
            "§d§lWOW!", "§4§lADMIN!", "§2§lLOL!", "§3§lHAHA!", "§5§l???",
            "§f§l...", "§7§l....", "§8§l!!!!", "§9§l???", "§0§l!!!"
    };
    private static final int JAIL_RADIUS = 2;
    private static final int JAIL_HEIGHT = 3;
    private static final int SPIN_TICKS = 60;
    private static final int SPAM_COUNT = 15;

    private final JavaPlugin plugin;
    private final Set<UUID> spinning = ConcurrentHashMap.newKeySet();
    private final Map<UUID, List<BlockState>> jailedBlocks = new ConcurrentHashMap<>();

    public OpFunCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase(Locale.ROOT);
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke denne kommandoen.").color(NamedTextColor.RED));
            return true;
        }
        if (!executor.isOp() && !executor.hasPermission("lager.opfun")) {
            executor.sendMessage(Component.text("Kun OP kan bruke denne kommandoen.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sendUsage(executor, cmd);
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            executor.sendMessage(Component.text("Spiller ikke funnet: " + args[0]).color(NamedTextColor.RED));
            return true;
        }

        switch (cmd) {
            case "blind" -> doBlind(executor, target);
            case "levitate" -> doLevitate(executor, target);
            case "anvil" -> doAnvil(executor, target);
            case "scare" -> doScare(executor, target);
            case "spin" -> doSpin(executor, target);
            case "spam" -> doSpam(executor, target);
            case "jail" -> doJail(executor, target);
            case "tnt" -> doTnt(executor, target);
            case "invclear" -> doInvClear(executor, target);
            default -> sendUsage(executor, cmd);
        }
        return true;
    }

    private void sendUsage(Player p, String cmd) {
        p.sendMessage(Component.text("Bruk: /" + cmd + " <spiller>").color(NamedTextColor.GRAY));
    }

    private void doBlind(Player executor, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 0, true, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, true, true));
        executor.sendMessage(Component.text(target.getName() + " er nå blind i 10 sek.").color(NamedTextColor.YELLOW));
    }

    private void doLevitate(Player executor, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 2, true, true));
        executor.sendMessage(Component.text(target.getName() + " flyter oppover.").color(NamedTextColor.YELLOW));
    }

    private void doAnvil(Player executor, Player target) {
        Location loc = target.getLocation().add(0, 5, 0);
        World w = target.getWorld();
        if (w != null) {
            FallingBlock falling = w.spawnFallingBlock(loc, Material.ANVIL.createBlockData());
            falling.setHurtEntities(true);
            falling.setDropItem(false);
        }
        executor.sendMessage(Component.text("Ambolt slippet over " + target.getName()).color(NamedTextColor.YELLOW));
    }

    private void doScare(Player executor, Player target) {
        target.showTitle(Title.title(
                Component.text("§4§lBOO!").color(NamedTextColor.DARK_RED),
                Component.text("Jump scare!").color(NamedTextColor.GRAY),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofMillis(500))
        ));
        target.playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.5f);
        target.playSound(target.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1f, 0.8f);
        executor.sendMessage(Component.text("Jump-scare sendt til " + target.getName()).color(NamedTextColor.YELLOW));
    }

    private void doSpin(Player executor, Player target) {
        if (spinning.contains(target.getUniqueId())) {
            spinning.remove(target.getUniqueId());
            executor.sendMessage(Component.text(target.getName() + " slutter å snurre.").color(NamedTextColor.YELLOW));
            return;
        }
        spinning.add(target.getUniqueId());
        executor.sendMessage(Component.text(target.getName() + " snurrer.").color(NamedTextColor.YELLOW));
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!target.isOnline() || !spinning.contains(target.getUniqueId())) {
                    cancel();
                    return;
                }
                Location loc = target.getLocation();
                loc.setYaw(loc.getYaw() + 45);
                target.teleport(loc);
                if (++ticks >= SPIN_TICKS) {
                    spinning.remove(target.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void doSpam(Player executor, Player target) {
        Random r = new Random();
        for (int i = 0; i < SPAM_COUNT; i++) {
            String line = SPAM_LINES[r.nextInt(SPAM_LINES.length)];
            final int delay = i * 2;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (target.isOnline()) {
                    target.sendMessage(Component.text(line));
                }
            }, delay);
        }
        executor.sendMessage(Component.text("Chatten til " + target.getName() + " fylt med tull.").color(NamedTextColor.YELLOW));
    }

    private void doJail(Player executor, Player target) {
        UUID id = target.getUniqueId();
        if (jailedBlocks.containsKey(id)) {
            unjail(target);
            executor.sendMessage(Component.text(target.getName() + " er løslatt fra glassburet.").color(NamedTextColor.YELLOW));
            return;
        }
        Location base = target.getLocation().getBlock().getLocation();
        World w = target.getWorld();
        if (w == null) return;
        List<BlockState> saved = new ArrayList<>();
        int bx = base.getBlockX(), by = base.getBlockY(), bz = base.getBlockZ();
        for (int dx = -JAIL_RADIUS; dx <= JAIL_RADIUS; dx++) {
            for (int dz = -JAIL_RADIUS; dz <= JAIL_RADIUS; dz++) {
                for (int dy = 0; dy <= JAIL_HEIGHT; dy++) {
                    if (Math.abs(dx) != JAIL_RADIUS && Math.abs(dz) != JAIL_RADIUS && dy != 0 && dy != JAIL_HEIGHT) continue;
                    Block b = w.getBlockAt(bx + dx, by + dy, bz + dz);
                    saved.add(b.getState());
                    b.setType(Material.GLASS);
                }
            }
        }
        jailedBlocks.put(id, saved);
        target.teleport(base.add(0.5, 1, 0.5));
        executor.sendMessage(Component.text(target.getName() + " er i glassbur.").color(NamedTextColor.YELLOW));
    }

    private void unjail(Player target) {
        List<BlockState> states = jailedBlocks.remove(target.getUniqueId());
        if (states != null) {
            for (BlockState state : states) {
                state.update(true, false);
            }
        }
    }

    public boolean isJailed(UUID playerId) {
        return jailedBlocks.containsKey(playerId);
    }

    public void unjail(UUID playerId) {
        Player p = plugin.getServer().getPlayer(playerId);
        if (p != null) unjail(p);
    }

    private void doTnt(Player executor, Player target) {
        Location loc = target.getLocation();
        World w = target.getWorld();
        if (w != null) {
            TNTPrimed tnt = (TNTPrimed) w.spawnEntity(loc, EntityType.TNT);
            tnt.setFuseTicks(40);
        }
        executor.sendMessage(Component.text("TNT spawnet ved føttene til " + target.getName()).color(NamedTextColor.YELLOW));
    }

    private void doInvClear(Player executor, Player target) {
        target.getInventory().clear();
        executor.sendMessage(Component.text("Inventar til " + target.getName() + " er tømt.").color(NamedTextColor.YELLOW));
        target.sendMessage(Component.text("Inventaret ditt ble tømt av en operator.").color(NamedTextColor.RED));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player p) || (!p.isOp() && !p.hasPermission("lager.opfun"))) return out;
        if (args.length != 1) return out;
        String prefix = args[0].toLowerCase(Locale.ROOT);
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                out.add(online.getName());
            }
        }
        return out;
    }
}
