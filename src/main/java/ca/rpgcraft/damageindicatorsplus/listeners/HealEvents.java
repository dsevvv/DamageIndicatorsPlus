package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.tasks.VectorGenerator;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealEvents implements Listener {

    private final DamageIndicatorsPlus plugin;
    private final VectorGenerator vectorGenerator;
    private final HologramManager hologramManager;

    public HealEvents(DamageIndicatorsPlus plugin,
                      VectorGenerator vectorGenerator,
                      HologramManager hologramManager){
        this.plugin = plugin;
        this.vectorGenerator = vectorGenerator;
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onPlayerHealEvent(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!plugin.isDIFlag(e.getEntity())) return;
        }

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, vectorGenerator, e, hologramManager);
        createHologramTask.startHealHologramRunnable();
    }
}
