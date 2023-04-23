package ca.rpgcraft.damageindicatorsplus.listener;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.packet.PacketManager_1_19_4;
import ca.rpgcraft.damageindicatorsplus.util.DamageEventChecks;
import ca.rpgcraft.damageindicatorsplus.util.DamageEventType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    private final DamageIndicatorsPlus plugin;

    public DamageListener(DamageIndicatorsPlus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    void onEntityDamageByPlayerEvent(EntityDamageByEntityEvent e) {
        if(!DamageEventChecks.isPlayerOnEntityEvent(e.getEntity(), e.getDamager())) return;
        if (DamageEventChecks.isIgnored(e)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge"))
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;


        if(plugin.isDIFlags() && plugin.isWorldGuard())
            if(!DamageEventChecks.allowHologramSpawn(e.getEntity())) return;

        int lifespanSecs = plugin.getConfig().contains("damage-indicator.lifespan") ? plugin.getConfig().getInt("damage-indicator.lifespan") : 1;

        if(plugin.is1_19_4){
            PacketManager_1_19_4 packetManager = new PacketManager_1_19_4();
            int id = packetManager.spawnDamageIndicator(e, e.getEntity(), e.getDamager(), DamageEventType.PLAYER_DAMAGE_ENTITY);

            if(id != -1)
                packetManager.removeHologram(id, (Player) e.getDamager(), lifespanSecs);
        }
    }

    @EventHandler
    void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                || e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        if(!DamageEventChecks.isPlayerEvent(e.getEntity())) return;
        if(DamageEventChecks.isIgnored(e)) return;

        if(plugin.isDIFlags() && plugin.isWorldGuard())
            if(!DamageEventChecks.allowHologramSpawn(e.getEntity())) return;


        int lifespanSecs = plugin.getConfig().contains("damage-indicator.lifespan") ? plugin.getConfig().getInt("damage-indicator.lifespan") : 1;

        if(plugin.is1_19_4){
            PacketManager_1_19_4 packetManager = new PacketManager_1_19_4();
            int id = packetManager.spawnDamageIndicator(e, e.getEntity(), null, DamageEventType.PLAYER_DAMAGE);

            if(id != -1)
                packetManager.removeHologram(id, (Player) e.getEntity(), lifespanSecs);
        }
    }

    @EventHandler
    void onPlayerDamageByEntityEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if(!DamageEventChecks.isEntityOnPlayerEvent(e.getEntity(), e.getDamager())) return;
        if(DamageEventChecks.isIgnored(e)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!DamageEventChecks.allowHologramSpawn(e.getEntity())) return;
        }

        int lifespanSecs = plugin.getConfig().contains("damage-indicator.lifespan") ? plugin.getConfig().getInt("damage-indicator.lifespan") : 1;

        if(plugin.is1_19_4){
            PacketManager_1_19_4 packetManager = new PacketManager_1_19_4();
            int id = packetManager.spawnDamageIndicator(e, e.getEntity(), e.getDamager(), DamageEventType.ENTITY_DAMAGE_PLAYER);

            if(id != -1)
                packetManager.removeHologram(id, (Player) e.getEntity(), lifespanSecs);
        }
    }

    @EventHandler
    void onPlayerDamageByPlayerEvent(EntityDamageByEntityEvent e) {
        if(!DamageEventChecks.isPlayerOnPlayerEvent(e.getEntity(), e.getDamager())) return;
        if(DamageEventChecks.isIgnored(e)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!DamageEventChecks.allowHologramSpawn(e.getEntity())) return;
        }

        int lifespanSecs = plugin.getConfig().contains("damage-indicator.lifespan") ? plugin.getConfig().getInt("damage-indicator.lifespan") : 1;

        if(plugin.is1_19_4){
            PacketManager_1_19_4 packetManager = new PacketManager_1_19_4();
            int id = packetManager.spawnDamageIndicator(e, e.getEntity(), e.getDamager(), DamageEventType.PLAYER_DAMAGE_PLAYER);

            if(id != -1){
                packetManager.removeHologram(id, (Player) e.getEntity(), lifespanSecs);
                packetManager.removeHologram(id, (Player) e.getDamager(), lifespanSecs);
            }
        }
    }

    //UNUSED FOR NOW -- ENTITY ON ENTITY EVENT
//    @EventHandler
//    void onEntityDamageByEntityEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
//        if(!DamageEventChecks.isEntityOnEntityEvent(e.getEntity(), e.getDamager())) return;
//        if(DamageEventChecks.isIgnored(e)) return;
//
//        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge"))
//            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
//
//
//        if(plugin.isDIFlags() && plugin.isWorldGuard())
//            if(!DamageEventChecks.allowHologramSpawn(e.getEntity())) return;
//
//
//        int lifespanSecs = plugin.getConfig().contains("damage-indicator.lifespan") ? plugin.getConfig().getInt("damage-indicator.lifespan") : 1;
//
//        if(plugin.is1_19_4){
//        }
//    }
}
