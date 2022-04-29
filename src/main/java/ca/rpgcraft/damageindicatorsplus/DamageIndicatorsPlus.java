package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.listeners.*;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

import org.bukkit.event.entity.CreatureSpawnEvent;
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

        try {
            Class.forName("io.papermc.paper.event.player.PlayerArmSwingEvent");
            getLogger().info("Paper API found.");
            isProtocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
            isPaper = true;
        }catch (Exception ignored){
            getLogger().info("Paper API not found.");
            isProtocolLib = false;
            isPaper = false;
        }

        if(isProtocolLib){
            new ProtocolLibHandler(this, generateVectorTask);
        }

        logger.info("Registering listeners.");

        boolean isDamageIndicators = true;
        if(getConfig().contains("damage-indicator.enabled")){
            isDamageIndicators = getConfig().getBoolean("damage-indicator.enabled");
        }
        if(isDamageIndicators){
            logger.info("Damage Indicator Enabled.");
            Bukkit.getPluginManager().registerEvents(new DamageEvents(this, generateVectorTask, hologramManager), this);
        }else{
            logger.info("Damage Indicator Disabled.");
        }

        boolean isHealIndicators = true;
        if(getConfig().contains("heal-indicator.enabled")){
            isHealIndicators = getConfig().getBoolean("heal-indicator.enabled");
        }
        if(isHealIndicators){
            logger.info("Heal Indicator Enabled.");
            Bukkit.getPluginManager().registerEvents(new HealEvents(this, generateVectorTask, hologramManager), this);
        }else{
            logger.info("Heal Indicator Disabled.");
        }

        if(isPaper){
            Bukkit.getPluginManager().registerEvents(new BurnListenerPaper(this), this);
        }else {
            Bukkit.getPluginManager().registerEvents(new BurnListenerSpigot(this), this);
        }

        logger.info("Time elapsed: " + (System.currentTimeMillis() - startTimeMili) + "ms");

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

    @Override
    public void onDisable() {
        logger.info("Removing all holograms...");
        for(ArmorStand hologram : hologramManager.getHologramList().values()) {
            hologram.remove();
        }
    }

    public boolean isPaper() {
        return isPaper;
    }

    public boolean isProtocolLib() {
        return isProtocolLib;
    }
}
