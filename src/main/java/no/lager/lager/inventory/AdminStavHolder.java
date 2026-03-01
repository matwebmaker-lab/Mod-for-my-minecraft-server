package no.lager.lager.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holder for Adminstav-GUI – lagrer målet (spilleren som ble høyreklikket).
 */
public final class AdminStavHolder implements InventoryHolder {

    private Inventory inventory;
    private final Player target;

    public AdminStavHolder(@Nullable Player target) {
        this.target = target;
    }

    @Nullable
    public Player getTarget() {
        return target;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory != null ? inventory : org.bukkit.Bukkit.createInventory(null, 0);
    }
}
