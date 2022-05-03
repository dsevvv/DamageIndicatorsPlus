package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.listeners.*;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class DamageIndicatorsPlus extends JavaPlugin {

    private final HologramManager hologramManager = new HologramManager();
    private final GenerateVectorTask generateVectorTask = new GenerateVectorTask(this);

    private boolean isPaper;
    private boolean isProtocolLib;

    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        long startTimeMili = System.currentTimeMillis();

        try {
            logger.info("Author: " + new URL("https://www.spigotmc.org/members/dsevvv.1425637/"));
        } catch (MalformedURLException ignored) {
        }

        saveDefaultConfig();
        fillMissingConfigBlocks();

        logger.info("Checking for Paper and ProtocolLib.");

        //checking for a paper server, if a paper server is detected, will check for ProtocolLib
        try {
            Class.forName("io.papermc.paper.event.player.PlayerArmSwingEvent");
            logger.info("Paper API found.");
            isProtocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
            isPaper = true;
            if(isProtocolLib){
                new ProtocolLibHandler(this, generateVectorTask, hologramManager);
            }
        }catch (Exception ignored){
            getLogger().info("Paper API not found.");
            isProtocolLib = false;
            isPaper = false;
        }

        logger.info("Registering listeners.");

        registerListeners();
        registerBStats();

        logger.info("Time elapsed: " + (System.currentTimeMillis() - startTimeMili) + "ms");
    }

    @Override
    public void onDisable() {
        logger.info("Removing all holograms...");
        for(ArmorStand hologram : hologramManager.getHologramList().values()) {
            hologram.remove();
        }
    }

    private void registerListeners(){
        //deciding which burn listener to register, depends on if paper is running or not
        if(isPaper){
            Bukkit.getPluginManager().registerEvents(new BurnListenerPaper(this), this);
        }else {
            Bukkit.getPluginManager().registerEvents(new BurnListenerSpigot(this), this);
        }

        //checking config if damage indicators should be enabled
        if(getConfig().getBoolean("damage-indicator.enabled")){
            logger.info("Damage Indicator Enabled.");
            enableDamageIndicators();
        }else{
            logger.info("Damage Indicator Disabled.");
        }

        //checking config if heal indicators should be enabled
        if(getConfig().getBoolean("heal-indicator.enabled")){
            logger.info("Heal Indicator Enabled.");
            Bukkit.getPluginManager().registerEvents(new HealEvents(this, generateVectorTask, hologramManager), this);
        }else{
            logger.info("Heal Indicator Disabled.");
        }
    }

    private void enableDamageIndicators(){
        //checking if player indicators are enabled
        if(getConfig().getBoolean("damage-indicator.players")){
            Bukkit.getPluginManager().registerEvents(new PlayerDamage(this, generateVectorTask, hologramManager), this);
            Bukkit.getPluginManager().registerEvents(new EntityOnPlayerDamage(this, generateVectorTask, hologramManager), this);
            Bukkit.getPluginManager().registerEvents(new PlayerOnPlayerDamage(this, generateVectorTask, hologramManager), this);
        }
        //checking if mob indicators are enabled
        if(getConfig().getBoolean("damage-indicator.mobs")){
            Bukkit.getPluginManager().registerEvents(new EntityOnEntityDamage(this, generateVectorTask, hologramManager), this);
            Bukkit.getPluginManager().registerEvents(new PlayerOnEntityDamage(this, generateVectorTask, hologramManager), this);
        }
    }

    private void fillMissingConfigBlocks(){
        //sweeping edge block
        if(!getConfig().contains("damage-indicator.sweeping-edge", true)){
            getLogger().info("Generating sweeping edge option in config.");
            ConfigurationSection diSection = getConfig().getConfigurationSection("damage-indicator");
            ConfigurationSection se = diSection.createSection("sweeping-edge");
            diSection.set("sweeping-edge", false);
            saveConfig();
        }
        //players toggle block
        if(!getConfig().contains("damage-indicator.players", true)){
            getLogger().info("Generating player toggle option in config.");
            ConfigurationSection diSection = getConfig().getConfigurationSection("damage-indicator");
            ConfigurationSection se = diSection.createSection("players");
            diSection.set("players", true);
            saveConfig();
        }
        //mobs toggle block
        if(!getConfig().contains("damage-indicator.mobs", true)){
            getLogger().info("Generating mobs toggle option in config.");
            ConfigurationSection diSection = getConfig().getConfigurationSection("damage-indicator");
            ConfigurationSection se = diSection.createSection("mobs");
            diSection.set("mobs", true);
            saveConfig();
        }
    }

    private void registerBStats(){
        logger.info("Registering bstats.");
        Metrics metrics = new Metrics(this, 14743);
        metrics.addCustomChart(new MultiLineChart("players_and_servers", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() {
                Map<String, Integer> valueMap = new HashMap<>();
                valueMap.put("servers", 1);
                valueMap.put("players", Bukkit.getOnlinePlayers().size());
                return valueMap;
            }
        }));
    }

    public boolean isPaper() {
        return isPaper;
    }

    public boolean isProtocolLib() {
        return isProtocolLib;
    }
}
