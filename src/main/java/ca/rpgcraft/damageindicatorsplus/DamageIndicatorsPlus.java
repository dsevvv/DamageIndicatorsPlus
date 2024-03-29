package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.command.Commands;
import ca.rpgcraft.damageindicatorsplus.listener.DamageListener;
import ca.rpgcraft.damageindicatorsplus.listener.HealListener;
import ca.rpgcraft.damageindicatorsplus.data.PlayerDataManager;
import ca.rpgcraft.damageindicatorsplus.listener.JoinListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class DamageIndicatorsPlus extends JavaPlugin {

    private PlayerDataManager playerDataManager;
//    private VectorRingBuffer ringBuffer;

    private boolean isPaper;
    private boolean isWorldGuard = false;
    private boolean isDIFlags = false;
    private boolean isPAPI = false;
    public boolean is1_19_4 = false;

    private final Logger logger = getLogger();

    private final NamespacedKey key = new NamespacedKey(this, "hologram");

    @Override
    public void onEnable() {
        long startTimeMili = System.currentTimeMillis();

        playerDataManager = new PlayerDataManager();

        try {
            logger.info("Author: " + new URL("https://www.spigotmc.org/members/dsevvv.1425637/"));
        } catch (MalformedURLException ignored) {}


        saveDefaultConfig();
        fillMissingConfigBlocks();
        playerDataManager.saveDefaultPlayerConfig();

        logger.info("Checking for Paper and ProtocolLib.");

        //checking for a paper server, if a paper server is detected, will check for ProtocolLib
        boolean tryPaper = false;
        try{
            Class.forName("com.mohistmc.MohistMC");
            logger.info("Mohist found.");
        }catch (ClassNotFoundException e){
            tryPaper = true;
            logger.info("Mohist not found.");
        }
        if(tryPaper){
            try {
                Class.forName("io.papermc.paper.event.player.PlayerArmSwingEvent");
                logger.info("Paper API found.");
                isPaper = true;

            }catch (Exception ignored){
                getLogger().info("Paper API not found.");
                isPaper = false;
            }
        }

        logger.info("Checking for WorldGuard and DIFlags.");
        isWorldGuard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if(isWorldGuard){
            getLogger().info("WorldGuard found.");
        }
        isDIFlags = Bukkit.getPluginManager().isPluginEnabled("DIWGFlags") && isWorldGuard;
        if(isDIFlags){
            getLogger().info("DI-WGFlags found.");
        }
        isPAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if(isPAPI){
            getLogger().info("PlaceholderAPI found.");
        }

//        logger.log(Level.INFO, "Initializing vector generation...");
//        VectorGenerator vectorGenerator = new VectorGenerator();
//        ringBuffer = new VectorRingBuffer(50, vectorGenerator);

        logger.info("Registering listeners.");
        registerListeners();
        registerBStats();

        logger.info("Registering commands.");
        Commands commands = new Commands(this);
        getCommand("di").setExecutor(commands);
        getCommand("di").setTabCompleter(commands);

        //check if server is 1.19.4
        if(Bukkit.getServer().getVersion().contains("1.19.4")){
            is1_19_4 = true;
            logger.info("Server is running 1.19.4.");
        }

        logger.info("Time elapsed: " + (System.currentTimeMillis() - startTimeMili) + "ms");
    }

    @Override
    public void onDisable() {
        logger.info("Goodbye!");
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        //checking config if damage indicators should be enabled
        if(getConfig().getBoolean("damage-indicator.enabled")){
            logger.info("Damage Indicator Enabled.");
            Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        }else{
            logger.info("Damage Indicator Disabled.");
        }

        //checking config if heal indicators should be enabled
        if(getConfig().getBoolean("heal-indicator.enabled")){
            logger.info("Heal Indicator Enabled.");
            Bukkit.getPluginManager().registerEvents(new HealListener(this), this);
        }else{
            logger.info("Heal Indicator Disabled.");
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
        //default-toggle
        if(!getConfig().contains("damage-indicator.default-toggle", true)){
            getLogger().info("Generating default-toggle option in config.");
            ConfigurationSection diSection = getConfig().getConfigurationSection("damage-indicator");
            ConfigurationSection se = diSection.createSection("default-toggle");
            diSection.set("default-toggle", true);
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

    public boolean isWorldGuard() {
        return isWorldGuard;
    }

    public boolean isDIFlags() {
        return isDIFlags;
    }

    public boolean isPAPI() {
        return isPAPI;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

//    public VectorRingBuffer getRingBuffer() {
//        return ringBuffer;
//    }

    public static DamageIndicatorsPlus getInstance(){
        return DamageIndicatorsPlus.getPlugin(DamageIndicatorsPlus.class);
    }

    public NamespacedKey getHologramKey() {
        return key;
    }
}
