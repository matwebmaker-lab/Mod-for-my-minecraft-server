package no.lager.lager;

import no.lager.lager.commands.AdminChestCommand;
import no.lager.lager.commands.LagerCommand;
import no.lager.lager.listeners.OpArmorEffectsListener;
import no.lager.lager.listeners.OpItemListener;
import no.lager.lager.listeners.TridentLightningListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class LagerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("lager").setExecutor(new LagerCommand(this));
        getCommand("lagerkiste").setExecutor(new AdminChestCommand(this));
        getServer().getPluginManager().registerEvents(new OpItemListener(this), this);
        getServer().getPluginManager().registerEvents(new TridentLightningListener(this), this);
        new OpArmorEffectsListener(this);
    }
}
