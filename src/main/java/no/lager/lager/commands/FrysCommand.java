package no.lager.lager.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import no.lager.lager.listeners.AdminFreezeListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /frys – kun Admin_owner kan bruke. Slår frys på/av.
 */
public final class FrysCommand implements CommandExecutor {

    private final AdminFreezeListener freezeListener;

    public FrysCommand(AdminFreezeListener freezeListener) {
        this.freezeListener = freezeListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Kun spillere kan bruke /frys.").color(NamedTextColor.RED));
            return true;
        }
        if (!AdminFreezeListener.ADMIN_OWNER_NAME.equals(player.getName())) {
            player.sendMessage(Component.text("Kun Admin_owner kan bruke /frys.").color(NamedTextColor.RED));
            return true;
        }
        freezeListener.toggleFreeze(player);
        return true;
    }
}
