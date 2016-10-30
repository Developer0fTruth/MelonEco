package com.turqmelon.MelonEco.data;

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/
public class YamlStorage extends DataStore {

    private YamlConfiguration configuration;
    private File file;

    public YamlStorage(String name, boolean topSupported, File file) {
        super(name, topSupported);
        this.file = file;
    }

    @Override
    public void initalize() {
        if (!getFile().exists()){
            try {
                if(getFile().createNewFile()){
                    MelonEco.getInstance().getLogger().log(Level.INFO, "Data file created.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.configuration = new YamlConfiguration();
        try {
            configuration.load(getFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void loadCurrencies() {
        ConfigurationSection section = getConfig().getConfigurationSection("currencies");
        if (section != null){
            Set<String> currencies = section.getKeys(false);
            for(String uuid : currencies){
                String path = "currencies." + uuid;
                String single = getConfig().getString(path + ".singular");
                String plural = getConfig().getString(path + ".plural");
                Currency currency = new Currency(UUID.fromString(uuid), single, plural);
                currency.setColor(ChatColor.valueOf(getConfig().getString(path + ".color").toUpperCase()));
                currency.setDecimalSupported(getConfig().getBoolean(path + ".decimalsupported"));
                currency.setDefaultBalance(getConfig().getDouble(path + ".defaultbalance"));
                currency.setDefaultCurrency(getConfig().getBoolean(path + ".defaultcurrency"));
                currency.setPayable(getConfig().getBoolean(path + ".payable"));
                currency.setSymbol(getConfig().getString(path + ".symbol"));
                AccountManager.getCurrencies().add(currency);
            }
        }
    }

    @Override
    public void saveCurrency(Currency currency) {
        String path = "currencies." + currency.getUuid().toString();
        getConfig().set(path + ".singular", currency.getSingular());
        getConfig().set(path + ".plural", currency.getPlural());
        getConfig().set(path + ".defaultbalance", currency.getDefaultBalance());
        getConfig().set(path + ".symbol", currency.getSymbol());
        getConfig().set(path + ".decimalsupported", currency.isDecimalSupported());
        getConfig().set(path + ".defaultcurrency", currency.isDefaultCurrency());
        getConfig().set(path + ".payable", currency.isPayable());
        getConfig().set(path + ".color", currency.getColor().name());
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCurrency(Currency currency) {
        String path = "currencies." + currency.getUuid().toString();
        getConfig().set(path, null);
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Double> getTopList(Currency currency, int offset, int amount) {
        return null;
    }

    private void loadBalances(Account account){
        String path = "accounts." + account.getUuid().toString();

        ConfigurationSection bsection = getConfig().getConfigurationSection(path + ".balances");
        if (bsection != null){
            Set<String> balances = bsection.getKeys(false);
            if (balances != null && !balances.isEmpty()){
                for(String currency : balances){
                    String path2 = path + ".balances." + currency;
                    double balance = getConfig().getDouble(path2);
                    Currency c = AccountManager.getCurrency(UUID.fromString(currency));
                    if (c != null){
                        account.setBalance(c, balance);
                    }
                }
            }
        }
    }

    // Slower, scans data file for matching name
    @Override
    public Account loadAccount(String name) {

        ConfigurationSection section = getConfig().getConfigurationSection("accounts");
        if (section != null){
            Set<String> accounts = section.getKeys(false);
            if (accounts != null && !accounts.isEmpty()){
                for(String uuid : accounts){
                    String path = "accounts." + uuid;
                    String nick = getConfig().getString(path + ".nickname");
                    if (nick != null && nick.equalsIgnoreCase(name)){

                        Account account = new Account(UUID.fromString(uuid), nick);
                        account.setCanReceiveCurrency(getConfig().getBoolean(path + ".payable"));
                        loadBalances(account);
                        return account;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Account loadAccount(UUID uuid) {

        String path = "accounts." + uuid.toString();
        String nick = getConfig().getString(path + ".nickname");

        if (nick != null){
            Account account = new Account(uuid, nick);
            account.setCanReceiveCurrency(getConfig().getBoolean(path + ".payable"));
            loadBalances(account);
            return account;
        }

        return null;
    }

    @Override
    public void saveAccount(Account account) {
        String path = "accounts." + account.getUuid().toString();
        getConfig().set(path + ".nickname", account.getNickname());
        getConfig().set(path + ".uuid", account.getUuid().toString());
        for(Currency currency : account.getBalances().keySet()){
            double balance = account.getBalance(currency);
            getConfig().set(path + ".balances." + currency.getUuid().toString(), balance);
        }
        getConfig().set(path + ".payable", account.isCanReceiveCurrency());
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteAccount(Account account) {
        String path = "accounts." + account.getUuid().toString();
        getConfig().set(path, null);
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return configuration;
    }

    public File getFile() {
        return file;
    }
}
