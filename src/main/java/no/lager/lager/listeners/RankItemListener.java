package no.lager.lager.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.storage.RankStorage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

public final class RankItemListener implements Listener {

    public static NamespacedKey RANK_ITEM_KEY;
    public static NamespacedKey RANK_ID_KEY;

    private final RankStorage rankStorage;

    public RankItemListener(JavaPlugin plugin, RankStorage rankStorage) {
        this.rankStorage = rankStorage;
        RANK_ITEM_KEY = new NamespacedKey(plugin, "rank_item");
        RANK_ID_KEY = new NamespacedKey(plugin, "rank_id");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onUseRankItem(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Boolean isRankItem = meta.getPersistentDataContainer().get(RANK_ITEM_KEY, PersistentDataType.BOOLEAN);
        if (isRankItem == null || !isRankItem) return;

        String rank = meta.getPersistentDataContainer().get(RANK_ID_KEY, PersistentDataType.STRING);
        if (rank == null || rank.isBlank()) {
            player.sendMessage(Component.text("Dette rank-itemet er ugyldig.").color(NamedTextColor.RED));
            return;
        }

        event.setCancelled(true);
        rankStorage.setRank(player.getUniqueId(), rank);
        player.sendMessage(Component.text("Du fikk rank: " + rank).color(NamedTextColor.GREEN));

        int amount = item.getAmount();
        if (amount <= 1) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(amount - 1);
            player.getInventory().setItemInMainHand(item);
        }
    }
}
