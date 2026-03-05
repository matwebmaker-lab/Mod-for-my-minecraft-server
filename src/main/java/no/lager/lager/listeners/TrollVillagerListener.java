package no.lager.lager.listeners;

import no.lager.lager.items.TrollItemRegistry;
import no.lager.lager.villager.TrollVillagerType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Troll-landsbyboere: Svindleren (diamant→kull), Gjøgleren (splash + løp), Skatteinnkreveren (ta smaragd),
 * Eksplosiv Selger (creeper-lyd), Glitchen (bytte oppskrifter). + Teleport-eple og Hemsko-støvler.
 */
public final class TrollVillagerListener implements Listener {

    private static final int GLITCH_SHUFFLE_TICKS = 40;
    private static final int JESTER_POTION_DELAY_TICKS = 10;
    private static final int TAX_INTERVAL_TICKS = 20 * 60;
    private static final double TAX_RADIUS = 5.0;
    private static final int TELEPORT_RADIUS = 50;
    private static final int SLOWNESS_AMPLIFIER = 9; // Slowness X

    private final JavaPlugin plugin;
    private final TrollItemRegistry trollItems;
    private final Map<UUID, BukkitRunnable> glitchTasks = new ConcurrentHashMap<>();

    public TrollVillagerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.trollItems = new TrollItemRegistry(plugin);
        startJesterTask();
        startTaxTask();
        startHemskoTask();
    }

    private void startJesterTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (var world : plugin.getServer().getWorlds()) {
                    for (Villager v : world.getEntitiesByClass(Villager.class)) {
                        if (!TrollVillagerType.isTrollVillager(v, plugin)) continue;
                        if (!TrollVillagerType.JESTER.equals(TrollVillagerType.getType(v, plugin))) continue;
                        v.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2, true, false));
                        if (ThreadLocalRandom.current().nextInt(5) == 0) {
                            v.setVelocity(v.getVelocity().setY(0.4));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void startTaxTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (var world : plugin.getServer().getWorlds()) {
                    for (Villager v : world.getEntitiesByClass(Villager.class)) {
                        if (!TrollVillagerType.isTrollVillager(v, plugin)) continue;
                        if (!TrollVillagerType.TAX.equals(TrollVillagerType.getType(v, plugin))) continue;
                        for (Player p : world.getPlayers()) {
                            if (p.getLocation().distance(v.getLocation()) > TAX_RADIUS) continue;
                            ItemStack[] contents = p.getInventory().getContents();
                            for (int i = 0; i < contents.length; i++) {
                                if (contents[i] != null && contents[i].getType() == Material.EMERALD && contents[i].getAmount() > 0) {
                                    contents[i].setAmount(contents[i].getAmount() - 1);
                                    p.getInventory().setItem(i, contents[i].getAmount() > 0 ? contents[i] : null);
                                    p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation().add(0, 1, 0), 12, 0.4, 0.5, 0.4, 0.02);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, TAX_INTERVAL_TICKS, TAX_INTERVAL_TICKS);
    }

    private void startHemskoTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    ItemStack boots = p.getInventory().getBoots();
                    if (boots == null || !trollItems.isTrollItem(boots)) continue;
                    if (!TrollItemRegistry.ID_HEMSKO_BOOTS.equals(trollItems.getTrollItemId(boots))) continue;
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, SLOWNESS_AMPLIFIER, true, true));
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory top = event.getView().getTopInventory();
        if (top.getType() != InventoryType.MERCHANT) return;
        if (!(top instanceof MerchantInventory merchantInv)) return;
        if (!(merchantInv.getHolder() instanceof Villager villager)) return;
        if (!TrollVillagerType.isTrollVillager(villager, plugin)) return;

        // Result slot i merchant er 2
        if (event.getRawSlot() != 2) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;

        String type = TrollVillagerType.getType(villager, plugin);

        if (TrollVillagerType.SCAMMER.equals(type)) {
            ItemStack result = event.getCurrentItem();
            if (trollItems.isTrollItem(result) && TrollItemRegistry.ID_MAGIC_DIAMOND.equals(trollItems.getTrollItemId(result))) {
                plugin.getServer().getScheduler().runTask(plugin, () -> replaceMagicDiamondsInInventory(player));
            }
        }

        if (TrollVillagerType.BOOMER.equals(type)) {
            player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1f, 0.8f);
            player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1f, 0.6f);
        }
    }

    private void replaceMagicDiamondsInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack == null || !trollItems.isTrollItem(stack)) continue;
            if (!TrollItemRegistry.ID_MAGIC_DIAMOND.equals(trollItems.getTrollItemId(stack))) continue;
            int amount = stack.getAmount();
            Material replace = ThreadLocalRandom.current().nextBoolean() ? Material.COAL : Material.BAKED_POTATO;
            player.getInventory().setItem(i, new ItemStack(replace, amount));
        }
        ItemStack cursor = player.getInventory().getItemInMainHand();
        if (trollItems.isTrollItem(cursor) && TrollItemRegistry.ID_MAGIC_DIAMOND.equals(trollItems.getTrollItemId(cursor))) {
            int amount = cursor.getAmount();
            Material replace = ThreadLocalRandom.current().nextBoolean() ? Material.COAL : Material.BAKED_POTATO;
            player.getInventory().setItemInMainHand(new ItemStack(replace, amount));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryOpen(org.bukkit.event.inventory.InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        Inventory top = event.getView().getTopInventory();
        if (top.getType() != InventoryType.MERCHANT || !(top instanceof MerchantInventory merchantInv)) return;
        if (!(merchantInv.getHolder() instanceof Villager villager)) return;
        if (!TrollVillagerType.isTrollVillager(villager, plugin)) return;

        String type = TrollVillagerType.getType(villager, plugin);

        if (TrollVillagerType.JESTER.equals(type)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) return;
                throwRandomPotion(player);
                player.closeInventory();
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 0.7f);
                villager.getWorld().playSound(villager.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 0.5f);
            }, JESTER_POTION_DELAY_TICKS);
        }

        if (TrollVillagerType.GLITCH.equals(type)) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!villager.isValid() || !player.isOnline() || player.getOpenInventory().getTopInventory().getType() != InventoryType.MERCHANT) {
                        glitchTasks.remove(player.getUniqueId());
                        cancel();
                        return;
                    }
                    villager.setRecipes(TrollVillagerType.randomGlitchRecipes(trollItems));
                }
            };
            glitchTasks.put(player.getUniqueId(), task);
            task.runTaskTimer(plugin, GLITCH_SHUFFLE_TICKS, GLITCH_SHUFFLE_TICKS);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        BukkitRunnable task = glitchTasks.remove(event.getPlayer().getUniqueId());
        if (task != null) task.cancel();
    }

    private void throwRandomPotion(Player target) {
        PotionEffectType[] types = { PotionEffectType.LEVITATION, PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS };
        PotionEffectType type = types[ThreadLocalRandom.current().nextInt(types.length)];
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        org.bukkit.inventory.meta.PotionMeta meta = (org.bukkit.inventory.meta.PotionMeta) potion.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(type, 120, 1), true);
            potion.setItemMeta(meta);
        }
        Location spawnAt = target.getLocation().add(0, 1.5, 0);
        ThrownPotion thrown = target.getWorld().spawn(spawnAt, ThrownPotion.class);
        thrown.setItem(potion);
        thrown.setVelocity(new org.bukkit.util.Vector(0, -0.3, 0));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!trollItems.isTrollItem(item)) return;
        if (!TrollItemRegistry.ID_TELEPORT_APPLE.equals(trollItems.getTrollItemId(item))) return;

        Player p = event.getPlayer();
        Location base = p.getLocation();
        World w = base.getWorld();
        if (w == null) return;

        for (int attempt = 0; attempt < 20; attempt++) {
            int x = base.getBlockX() + ThreadLocalRandom.current().nextInt(-TELEPORT_RADIUS, TELEPORT_RADIUS + 1);
            int z = base.getBlockZ() + ThreadLocalRandom.current().nextInt(-TELEPORT_RADIUS, TELEPORT_RADIUS + 1);
            int y = w.getHighestBlockYAt(x, z) + 1;
            Location dest = new Location(w, x + 0.5, y, z + 0.5);
            if (dest.getBlock().getType().isAir() && dest.clone().add(0, 1, 0).getBlock().getType().isAir()) {
                p.teleport(dest);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                break;
            }
        }
    }
}
