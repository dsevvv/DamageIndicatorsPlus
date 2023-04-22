package ca.rpgcraft.damageindicatorsplus.entity.player.listener;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.hooks.WorldGuardBridge;
import ca.rpgcraft.damageindicatorsplus.entity.hologram.task.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.util.DamageEventChecks;
import ca.rpgcraft.damageindicatorsplus.entity.hologram.HologramManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerOnPlayerDamage implements Listener {

    private final DamageIndicatorsPlus plugin;
    private final HologramManager hologramManager;

    public PlayerOnPlayerDamage(DamageIndicatorsPlus plugin,
                        HologramManager hologramManager){
        this.plugin = plugin;
        this.hologramManager = hologramManager;

    }

    @EventHandler
    void onPlayerDamageByPlayerEvent(EntityDamageByEntityEvent e) {
        if(!DamageEventChecks.isPlayerOnPlayerEvent(e.getEntity(), e.getDamager())) return;
        if(DamageEventChecks.isIgnored(e)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!allowHologramSpawn(e.getEntity())) return;
        }

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, e, (Player) e.getDamager(), hologramManager);
        createHologramTask.run();
    }

    private boolean allowHologramSpawn(Entity entity){
        if(!DamageIndicatorsPlus.getInstance().isWorldGuard())
            return true;

        return new WorldGuardBridge().isDIFlag(entity);
    }
}
