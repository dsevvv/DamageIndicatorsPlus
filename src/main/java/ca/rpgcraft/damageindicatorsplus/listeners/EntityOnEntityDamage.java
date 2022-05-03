package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.DamageHologramUtils;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityOnEntityDamage implements Listener {

    private final DamageIndicatorsPlus plugin;
    private final GenerateVectorTask generateVectorTask;
    private final HologramManager hologramManager;

    public EntityOnEntityDamage(DamageIndicatorsPlus plugin,
                        GenerateVectorTask generateVectorTask,
                        HologramManager hologramManager){
        this.plugin = plugin;
        this.generateVectorTask = generateVectorTask;
        this.hologramManager = hologramManager;

    }

    @EventHandler
    void onEntityDamageByEntityEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if(!DamageHologramUtils.isEntityOnEntityEvent(e.getEntity(), e.getDamager())) return;
        if(DamageHologramUtils.isIgnored(e)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, hologramManager);
        createHologramTask.run();
    }
}
