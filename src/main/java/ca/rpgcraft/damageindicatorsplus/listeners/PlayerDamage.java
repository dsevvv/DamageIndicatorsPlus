package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.tasks.VectorGenerator;
import ca.rpgcraft.damageindicatorsplus.utils.DamageHologramUtils;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import ca.rpgcraft.damageindicatorsplus.utils.WorldGuardUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {
    private final DamageIndicatorsPlus plugin;
    private final VectorGenerator vectorGenerator;
    private final HologramManager hologramManager;

    public PlayerDamage(DamageIndicatorsPlus plugin,
                        VectorGenerator vectorGenerator,
                        HologramManager hologramManager){
        this.plugin = plugin;
        this.vectorGenerator = vectorGenerator;
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

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!new WorldGuardUtils().isDIFlag(e.getEntity())) return;
        }

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, vectorGenerator, e, hologramManager);
        createHologramTask.run();
    }
}
