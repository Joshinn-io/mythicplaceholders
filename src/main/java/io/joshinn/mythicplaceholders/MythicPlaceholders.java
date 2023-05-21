package io.joshinn.mythicplaceholders;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import jdk.jpackage.internal.Log;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;

public final class MythicPlaceholders extends LuminePlugin {

    private static MythicPlaceholders plugin;

    private MobDeathListener mobDeathListener;
    private PlaceholderManager placeholderManager;



    @Override
    public void load(){
        plugin = this;
    }
    @Override
    public void enable() {
        // Plugin startup logic

        this.mobDeathListener = new MobDeathListener();
        this.placeholderManager = new PlaceholderManager(plugin);
        placeholderManager.register();
    }

    @Override
    public void disable() {
        this.mobDeathListener.saveData();
    }

    public static MythicPlaceholders inst(){
        return plugin;
    }

    public MobDeathListener getMobDeathListener(){
        return mobDeathListener;
    }
}
