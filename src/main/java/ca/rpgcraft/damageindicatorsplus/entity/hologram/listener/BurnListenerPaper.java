package ca.rpgcraft.damageindicatorsplus.entity.hologram.listener;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class BurnListenerPaper implements Listener {

    private final DamageIndicatorsPlus plugin;

    public BurnListenerPaper(DamageIndicatorsPlus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onHologramMove(EntityMoveEvent e) {
        if (!(e.getEntity() instanceof ArmorStand)) return;

        ArmorStand hologram = (ArmorStand) e.getEntity();

        if (!hologram.getPersistentDataContainer()
                .has(new NamespacedKey(plugin, "hologram"), PersistentDataType.STRING)) return;

        e.getEntity().setFireTicks(0);
    }
}
