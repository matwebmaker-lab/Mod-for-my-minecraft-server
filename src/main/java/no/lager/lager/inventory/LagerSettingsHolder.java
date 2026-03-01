package no.lager.lager.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/** Holder for Lager-innstillingsskjermen (kun for OP – armor reach). */
public final class LagerSettingsHolder implements InventoryHolder {

    private Inventory inventory;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory != null ? inventory : org.bukkit.Bukkit.createInventory(null, 0);
    }
}
