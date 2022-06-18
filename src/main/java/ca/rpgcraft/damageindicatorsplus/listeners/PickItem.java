package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;

public class PickItem implements Listener {

    @EventHandler
    public void onPlayerPickHologram(InventoryEvent e){
        DamageIndicatorsPlus.getPlugin(DamageIndicatorsPlus.class).getLogger().severe(e.getEventName());
    }
}
