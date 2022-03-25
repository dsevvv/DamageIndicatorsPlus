package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.tasks.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public record HealEvents(DamageIndicatorsPlus plugin,
                         GenerateVectorTask generateVectorTask,
                         HologramManager hologramManager) implements Listener {

    @EventHandler
    public void onPlayerHealEvent(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        CreateHologramTask createHologramTask = new CreateHologramTask(plugin, generateVectorTask, e, hologramManager);
        createHologramTask.startHealHologramRunnable();
    }
}
