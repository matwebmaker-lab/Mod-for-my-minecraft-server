package no.lager.lager.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class VaultHolder implements InventoryHolder {

    private final UUID owner;
    private Inventory inventory;

    public VaultHolder(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory != null ? inventory : org.bukkit.Bukkit.createInventory(null, 0);
    }
}
