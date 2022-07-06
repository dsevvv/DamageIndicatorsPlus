package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.hooks.WorldGuardBridge;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealEvents implements Listener {

    private final DamageIndicatorsPlus plugin;
    private final HologramManager hologramManager;

    public HealEvents(DamageIndicatorsPlus plugin,
                      HologramManager hologramManager){
        this.plugin = plugin;
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onPlayerHealEvent(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!allowHologramSpawn(e.getEntity())) return;
        }

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, e, hologramManager);
        createHologramTask.startHealHologramRunnable();
    }

    private boolean allowHologramSpawn(Entity entity){
        if(!DamageIndicatorsPlus.getInstance().isWorldGuard())
            return true;

        return new WorldGuardBridge().isDIFlag(entity);
    }
}
