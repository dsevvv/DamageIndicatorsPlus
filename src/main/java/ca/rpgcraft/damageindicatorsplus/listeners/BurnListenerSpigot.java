package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public record BurnListenerSpigot(DamageIndicatorsPlus plugin) implements Listener {

    @EventHandler
    void onHologramBurn(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof ArmorStand hologram)) return;

        if (!hologram.getPersistentDataContainer()
                .has(new NamespacedKey(plugin, "hologram"), PersistentDataType.STRING)) return;

        e.getEntity().setFireTicks(0);
    }
}

