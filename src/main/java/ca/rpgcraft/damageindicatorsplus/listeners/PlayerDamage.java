package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.DamageHologramUtils;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {
    private final DamageIndicatorsPlus plugin;
    private final GenerateVectorTask generateVectorTask;
    private final HologramManager hologramManager;

    public PlayerDamage(DamageIndicatorsPlus plugin,
                        GenerateVectorTask generateVectorTask,
                        HologramManager hologramManager){
        this.plugin = plugin;
        this.generateVectorTask = generateVectorTask;
        this.hologramManager = hologramManager;

    }

    @EventHandler
    void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                || e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) return;

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        if(!DamageHologramUtils.isPlayerEvent(e.getEntity())) return;
        if(DamageHologramUtils.isIgnored(e)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, hologramManager);
        createHologramTask.run();
    }
}
