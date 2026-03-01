package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.inventory.AdminStavHolder;
import no.lager.lager.items.OpItemRegistry;
import org.bukkit.*;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Spesialitems: Adminstav (GUI), Tidsklokke (rewind), Voidperle, Massehealer, Verdensbryter.
 */
public final class NewOpSpecialListener implements Listener {

    private static final int REWIND_SECONDS = 5;
    private static final int HISTORY_INTERVAL_TICKS = 20; // lagre hvert sekund
    private static final int HISTORY_MAX_ENTRIES = 10;
    private static final double MASSEHEALER_RADIUS = 10.0;
    private static final int MASSEHEALER_HEALTH = 20;
    private static final int VERDENSSBRYTER_SIZE_3 = 1; // 3x3 = radius 1
    private static final int VERDENSSBRYTER_SIZE_5 = 2;  // 5x5 = radius 2

    private final JavaPlugin plugin;
    private final OpItemRegistry registry;
    private final NamespacedKey adminStavButtonKey;
    private final Map<UUID, Deque<RewindEntry>> rewindHistory = new HashMap<>();
    private final Map<UUID, Long> lastHistorySave = new HashMap<>();

    public NewOpSpecialListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = new OpItemRegistry(plugin);
        this.adminStavButtonKey = new NamespacedKey(plugin, "admin_stav_btn");
        startRewindHistoryTask();
    }

    private static final class RewindEntry {
        final long time;
        final Location loc;
        final double health;
        final double food;

        RewindEntry(long time, Location loc, double health, double food) {
            this.time = time;
            this.loc = loc;
            this.health = health;
            this.food = food;
        }
    }

    private void startRewindHistoryTask() {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!p.isOp()) continue;
                    if (now - lastHistorySave.getOrDefault(p.getUniqueId(), 0L) < HISTORY_INTERVAL_TICKS * 50) continue;
                    lastHistorySave.put(p.getUniqueId(), now);
                    Deque<RewindEntry> list = rewindHistory.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>(HISTORY_MAX_ENTRIES + 2));
                    list.addLast(new RewindEntry(now, p.getLocation().clone(), p.getHealth(), p.getFoodLevel()));
                    while (list.size() > HISTORY_MAX_ENTRIES) list.removeFirst();
                }
            }
        }.runTaskTimer(plugin, 40L, HISTORY_INTERVAL_TICKS);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;
        String id = registry.getOpItemId(item);
        if (id == null) return;

        if ("admin_stav".equals(id)) {
            event.setCancelled(true);
            if (event.getRightClicked() instanceof Player target) {
                openAdminStavGui(player, target);
            }
            return;
        }

        if ("void_perle".equals(id)) {
            event.setCancelled(true);
            if (event.getRightClicked() instanceof Player target) {
                Location below = target.getLocation().clone();
                below.setY(target.getWorld().getMinHeight());
                target.teleport(below);
                player.sendMessage(Component.text("Spilleren ble sendt til void (Y=-64).").color(NamedTextColor.GRAY));
            }
            return;
        }
    }

    private void openAdminStavGui(Player opener, Player target) {
        AdminStavHolder holder = new AdminStavHolder(target);
        Inventory inv = plugin.getServer().createInventory(holder, 9,
                Component.text("Admin: " + target.getName()).color(NamedTextColor.RED));
        holder.setInventory(inv);
        inv.setItem(1, makeAdminButton(Material.RED_BED, "Kick", "kick"));
        inv.setItem(2, makeAdminButton(Material.PACKED_ICE, "Frys", "frys"));
        inv.setItem(3, makeAdminButton(Material.GOLDEN_APPLE, "Heal", "heal"));
        inv.setItem(4, makeAdminButton(Material.BARRIER, "Clear inv", "clear"));
        inv.setItem(5, makeAdminButton(Material.EMERALD, "TP hit", "tp"));
        opener.openInventory(inv);
    }

    private ItemStack makeAdminButton(Material mat, String name, String btnId) {
        ItemStack stack = new ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name).color(NamedTextColor.GOLD).decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, true));
            meta.getPersistentDataContainer().set(adminStavButtonKey, PersistentDataType.STRING, btnId);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAdminStavClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AdminStavHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player admin)) return;
        if (!admin.isOp()) return;

        Player target = holder.getTarget();
        if (target == null || !target.isOnline()) {
            admin.closeInventory();
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;
        String btnId = clicked.getItemMeta() == null ? null : clicked.getItemMeta().getPersistentDataContainer().get(adminStavButtonKey, PersistentDataType.STRING);
        if (btnId == null) return;

        switch (btnId) {
            case "kick" -> {
                target.kick(Component.text("Kicked by operator.").color(NamedTextColor.RED));
                admin.sendMessage(Component.text("Du kicket " + target.getName() + ".").color(NamedTextColor.YELLOW));
            }
            case "frys" -> {
                target.setWalkSpeed(0);
                target.setFlySpeed(0);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    target.setWalkSpeed(0.2f);
                    target.setFlySpeed(0.05f);
                }, 200L);
                admin.sendMessage(Component.text(target.getName() + " fryst i 10 sek.").color(NamedTextColor.YELLOW));
            }
            case "heal" -> {
                target.setHealth(target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
                target.setFoodLevel(20);
                target.setSaturation(20);
                target.getActivePotionEffects().forEach(e -> target.removePotionEffect(e.getType()));
                admin.sendMessage(Component.text(target.getName() + " healet.").color(NamedTextColor.GREEN));
            }
            case "clear" -> {
                target.getInventory().clear();
                admin.sendMessage(Component.text("Inventory cleared for " + target.getName() + ".").color(NamedTextColor.YELLOW));
            }
            case "tp" -> {
                admin.teleport(target.getLocation());
                admin.sendMessage(Component.text("Teleportert til " + target.getName() + ".").color(NamedTextColor.GREEN));
            }
            default -> {}
        }
        admin.closeInventory();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRewindUse(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getPlayer().isSneaking()) return;
        ItemStack item = event.getItem();
        if (item == null || !"rewind_klokke".equals(registry.getOpItemId(item))) return;
        if (!event.getPlayer().isOp()) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Deque<RewindEntry> list = rewindHistory.get(player.getUniqueId());
        if (list == null || list.isEmpty()) {
            player.sendMessage(Component.text("Ingen historikk å spole tilbake.").color(NamedTextColor.GRAY));
            return;
        }

        long targetTime = System.currentTimeMillis() - REWIND_SECONDS * 1000L;
        RewindEntry best = null;
        for (RewindEntry e : list) {
            if (e.time <= targetTime) best = e;
        }
        if (best == null) best = list.peekFirst();
        player.teleport(best.loc);
        player.setHealth(Math.min(player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue(), best.health));
        player.setFoodLevel((int) Math.min(20, best.food));
        player.sendMessage(Component.text("Tidsklokke: 5 sek tilbake.").color(NamedTextColor.GREEN));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMassehealerUse(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || !"massehealer".equals(registry.getOpItemId(item))) return;
        if (!event.getPlayer().isOp()) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        int count = 0;
        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), MASSEHEALER_RADIUS, MASSEHEALER_RADIUS, MASSEHEALER_RADIUS)) {
            if (e instanceof Player other) {
                other.setHealth(Math.min(other.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue(), MASSEHEALER_HEALTH));
                other.setFoodLevel(20);
                count++;
            }
        }
        player.sendMessage(Component.text("Healet " + count + " spillere innen 10 blokker.").color(NamedTextColor.GREEN));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVerdensbryterBreak(BlockBreakEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool == null || !"verdensbryter".equals(registry.getOpItemId(tool))) return;
        if (!event.getPlayer().isOp()) return;

        Block center = event.getBlock();
        int radius = event.getPlayer().isSneaking() ? VERDENSSBRYTER_SIZE_5 : VERDENSSBRYTER_SIZE_3;
        Material type = center.getType();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    Block b = center.getRelative(dx, dy, dz);
                    if (b.getType().isAir() || !b.getType().isBlock()) continue;
                    b.breakNaturally(tool);
                }
            }
        }
    }
}
