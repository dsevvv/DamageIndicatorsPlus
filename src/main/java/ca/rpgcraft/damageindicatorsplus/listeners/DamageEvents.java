package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.DamageHologramUtils;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public record DamageEvents(DamageIndicatorsPlus plugin,
                           GenerateVectorTask generateVectorTask,
                           HologramManager hologramManager) implements Listener {

    @EventHandler
    void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
                || e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) return;

        if(!DamageHologramUtils.isPlayerEvent(e.getEntity())) return;
        if(DamageHologramUtils.isIgnored(e)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, hologramManager);
        createHologramTask.run();
    }

    @EventHandler
    void onEntityDamageByEntityEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if(!DamageHologramUtils.isEntityOnEntityEvent(e.getEntity(), e.getDamager())) return;
        if(DamageHologramUtils.isIgnored(e)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, hologramManager);
        createHologramTask.run();
    }

    @EventHandler
    void onEntityDamageByPlayerEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if(!DamageHologramUtils.isPlayerOnEntityEvent(e.getEntity(), e.getDamager())) return;
        if (DamageHologramUtils.isIgnored(e)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, (Player) e.getDamager(), hologramManager);
        createHologramTask.run();
    }

    @EventHandler
    void onPlayerDamageByEntityEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if(!DamageHologramUtils.isEntityOnPlayerEvent(e.getEntity(), e.getDamager())) return;
        if(DamageHologramUtils.isIgnored(e)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, hologramManager);
        createHologramTask.run();
    }

    @EventHandler
    void onPlayerDamageByPlayerEvent(EntityDamageByEntityEvent e) {
        if(!DamageHologramUtils.isPlayerOnPlayerEvent(e.getEntity(), e.getDamager())) return;
        if(DamageHologramUtils.isIgnored(e)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, (Player) e.getDamager(), hologramManager);
        createHologramTask.run();
    }
}
