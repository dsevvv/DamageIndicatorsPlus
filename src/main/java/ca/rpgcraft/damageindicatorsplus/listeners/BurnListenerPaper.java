package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public record BurnListenerPaper(DamageIndicatorsPlus plugin) implements Listener {

    @EventHandler
    public void onHologramMove(EntityMoveEvent e) {
        if (!(e.getEntity() instanceof ArmorStand hologram)) return;

        if (!hologram.getPersistentDataContainer()
                .has(new NamespacedKey(plugin, "hologram"), PersistentDataType.STRING)) return;

        e.getEntity().setFireTicks(0);
    }
}
