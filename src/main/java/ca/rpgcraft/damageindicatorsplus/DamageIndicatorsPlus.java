package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.command.Commands;
import ca.rpgcraft.damageindicatorsplus.listeners.*;
import ca.rpgcraft.damageindicatorsplus.tasks.VectorGenerator;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;

import ca.rpgcraft.damageindicatorsplus.utils.PlayerDataManager;
import ca.rpgcraft.damageindicatorsplus.utils.VectorRingBuffer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DamageIndicatorsPlus extends JavaPlugin {

    private final PlayerDataManager playerDataManager = new PlayerDataManager(this);
    private final HologramManager hologramManager = new HologramManager();
    private final VectorGenerator vectorGenerator = new VectorGenerator(this);
    private VectorRingBuffer ringBuffer;

    private boolean isPaper;
    private boolean isProtocolLib;
    private boolean isWorldGuard;
    private boolean isDIFlags;

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
                isProtocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
                isPaper = true;
                if(isProtocolLib){
                    new ProtocolLibHandler(this, vectorGenerator, hologramManager);
                }
            }catch (Exception ignored){
                getLogger().info("Paper API not found.");
                isProtocolLib = false;
                isPaper = false;
            }
        }

        logger.info("Checking for WorldGuard and DIFlags.");
        isWorldGuard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if(isWorldGuard){
            getLogger().info("WorldGuard found.");
        }
        isDIFlags = Bukkit.getPluginManager().isPluginEnabled("DIWGFlags");
        if(isDIFlags){
            getLogger().info("DI-WGFlags found.");
        }

        logger.log(Level.INFO, "Initializing vector generation...");
        VectorGenerator vectorGenerator = new VectorGenerator(this);
        ringBuffer = new VectorRingBuffer(50, vectorGenerator);

        logger.info("Registering listeners.");
        registerListeners();
        registerBStats();

        logger.info("Registering commands.");
        Commands commands = new Commands(this);
        getCommand("di").setExecutor(commands);
        getCommand("di").setTabCompleter(commands);

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
            Bukkit.getPluginManager().registerEvents(new HealEvents(this, vectorGenerator, hologramManager), this);
        }else{
            logger.info("Heal Indicator Disabled.");
        }
    }

    private void enableDamageIndicators(){
        //checking if player indicators are enabled
        if(getConfig().getBoolean("damage-indicator.players")){
            Bukkit.getPluginManager().registerEvents(new PlayerDamage(this, vectorGenerator, hologramManager), this);
            Bukkit.getPluginManager().registerEvents(new EntityOnPlayerDamage(this, vectorGenerator, hologramManager), this);
            Bukkit.getPluginManager().registerEvents(new PlayerOnPlayerDamage(this, vectorGenerator, hologramManager), this);
        }
        //checking if mob indicators are enabled
        if(getConfig().getBoolean("damage-indicator.mobs")){
            Bukkit.getPluginManager().registerEvents(new EntityOnEntityDamage(this, vectorGenerator, hologramManager), this);
            Bukkit.getPluginManager().registerEvents(new PlayerOnEntityDamage(this, vectorGenerator, hologramManager), this);
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

    public boolean isWorldGuard() {
        return isWorldGuard;
    }

    public boolean isDIFlags() {
        return isDIFlags;
    }

    public boolean isDIFlag(Entity entity){
        RegionAssociable regionAssociable = createRegionAssociable(entity);
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(entity.getWorld().getName());
        Location location = new Location(world, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        ApplicableRegionSet set = regions.getApplicableRegions(location.toVector().toBlockPoint());
        Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get("damage-indicators");

        if(set.queryValue(regionAssociable, flag).toString().equalsIgnoreCase("ALLOW")){
            return true;
        }else{
            return false;
        }
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public VectorRingBuffer getRingBuffer() {
        return ringBuffer;
    }

    private RegionAssociable createRegionAssociable(Object cause) {
        if (cause instanceof Player) {
            return WorldGuardPlugin.inst().wrapPlayer((Player) cause);
        } else if (cause instanceof Entity entity) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(BukkitAdapter.adapt(entity.getWorld()));
            org.bukkit.Location loc = entity.getLocation(); // getOrigin() can be used on Paper if present
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(loc), config.useMaxPriorityAssociation);
        } else if (cause instanceof Block block) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(BukkitAdapter.adapt(block.getWorld()));
            org.bukkit.Location loc = block.getLocation();
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(loc), config.useMaxPriorityAssociation);
        } else {
            return Associables.constant(Association.NON_MEMBER);
        }
    }
}
