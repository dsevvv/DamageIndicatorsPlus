package ca.rpgcraft.damageindicatorsplus.listeners;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class PickItem implements Listener {

    @EventHandler
    public void onPlayerPickHologram(InventoryClickEvent e){
        if(e.getClick().isCreativeAction()){
            e.getWhoClicked().getNearbyEntities(5, 5, 5).forEach(entity -> {
                if(entity instanceof ArmorStand){
                    ArmorStand armorStand = (ArmorStand) entity;
                    if(armorStand.getPersistentDataContainer().has(new NamespacedKey(DamageIndicatorsPlus.getPlugin(DamageIndicatorsPlus.class), "hologram"), PersistentDataType.STRING)){
                        e.setCancelled(true);
                    }
                }
            });
        }
    }
}
