package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.listeners.*;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.net.URL;
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
            getLogger().info("ProtocolLib found.");
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Player p = event.getPlayer();

                    int entityID = packet.getIntegers().read(0);
                    Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(p.getWorld(), entityID);

                    if(!(entity instanceof ArmorStand hologram)) return;
                    if(!(hologram.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.BEEHIVE))) return;

                    hologram.setVisible(false);
                    hologram.setVelocity(generateVectorTask.generateVector());
                }
            });
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
