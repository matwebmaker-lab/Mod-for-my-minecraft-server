package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.inventory.VaultHolder;
import no.lager.lager.storage.VaultStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class VaultCommand implements CommandExecutor, Listener {

    private static final int VAULT_SIZE = 54;
    private final VaultStorage vaultStorage;

    public VaultCommand(VaultStorage vaultStorage) {
        this.vaultStorage = vaultStorage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke /vault.").color(NamedTextColor.RED));
            return true;
        }
        if (!player.hasPermission("lager.vault")) {
            player.sendMessage(Component.text("Du har ikke tilgang til /vault.").color(NamedTextColor.RED));
            return true;
        }

        UUID ownerId = player.getUniqueId();
        VaultHolder holder = new VaultHolder(ownerId);
        Inventory inv = Bukkit.createInventory(holder, VAULT_SIZE, Component.text("Din private Vault").color(NamedTextColor.DARK_AQUA));
        holder.setInventory(inv);
        inv.setContents(vaultStorage.loadVault(ownerId));
        player.openInventory(inv);
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVaultOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof VaultHolder holder)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!player.getUniqueId().equals(holder.getOwner())) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Denne vaulten er privat.").color(NamedTextColor.RED));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVaultClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof VaultHolder holder)) return;
        vaultStorage.saveVault(holder.getOwner(), event.getInventory().getContents());
    }
}
