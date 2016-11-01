package com.turqmelon.MelonEco;


/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/

import com.turqmelon.MelonEco.commands.*;
import com.turqmelon.MelonEco.data.DataStore;
import com.turqmelon.MelonEco.data.MySQLStorage;
import com.turqmelon.MelonEco.data.YamlStorage;
import com.turqmelon.MelonEco.listeners.JoinListener;
import com.turqmelon.MelonEco.utils.RedisManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class MelonEco extends JavaPlugin {

    private static DataStore dataStore = null;
    private static MelonEco instance;
    private RedisManager redisManager = null;

    public static MelonEco getInstance() {
        return instance;
    }

    public static DataStore getDataStore() {
        return dataStore;
    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        String strategy = getConfig().getString("storage-strategy");

        if (strategy.equalsIgnoreCase("yaml")) {
            dataStore = new YamlStorage("YAML", false, new File(getDataFolder(), getConfig().getString("yaml.file-name")));
        } else if (strategy.equalsIgnoreCase("mysql")) {
            String host = getConfig().getString("mysql.host");
            int port = getConfig().getInt("mysql.port");
            String user = getConfig().getString("mysql.username");
            String pass = getConfig().getString("mysql.password");
            String prefix = getConfig().getString("mysql.tableprefix");
            String db = getConfig().getString("mysql.database");
            dataStore = new MySQLStorage("MySQL", true, host, port, user, pass, db, prefix);
        } else {
            getLogger().log(Level.SEVERE, "Unknown storage strategy. Check your config file.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;

        getDataStore().initalize();

        if (dataStore instanceof MySQLStorage) {
            if (((MySQLStorage) dataStore).getConnection() == null) {
                getLogger().log(Level.SEVERE, "Failed to connect to MySQL server. Please check your information then try again.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        getDataStore().loadCurrencies();

        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        getCommand("balance").setExecutor(new BalanceCommand());
        getCommand("baltop").setExecutor(new BalTopCommand());
        getCommand("ecogive").setExecutor(new EcoGiveCommand());
        getCommand("ecoset").setExecutor(new EcoSetCommand());
        getCommand("ecotake").setExecutor(new EcoTakeCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("currencies").setExecutor(new CurrencyCommand());

        Plugin redisConnect = getServer().getPluginManager().getPlugin("RedisConnect");
        if (getDataStore().getName().equalsIgnoreCase("mysql") && redisConnect != null && redisConnect.isEnabled()) {
            getLogger().log(Level.INFO, "RedisConnect found! Hooking...");
            this.redisManager = new RedisManager();
        }

    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    @Override
    public void onDisable() {
        if (getDataStore() != null){
            getDataStore().close();
        }
    }
}
