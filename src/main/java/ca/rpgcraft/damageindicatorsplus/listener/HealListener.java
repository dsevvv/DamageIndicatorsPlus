package ca.rpgcraft.damageindicatorsplus.listener;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.packet.PacketManager_1_19_4;
import ca.rpgcraft.damageindicatorsplus.util.DamageEventChecks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealListener implements Listener {

    private final DamageIndicatorsPlus plugin;

    public HealListener(DamageIndicatorsPlus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHealEvent(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        if(plugin.isDIFlags() && plugin.isWorldGuard())
            if(!DamageEventChecks.allowHologramSpawn(e.getEntity())) return;

        int lifespanSecs = plugin.getConfig().contains("heal-indicator.lifespan") ? plugin.getConfig().getInt("heal-indicator.lifespan") : 1;

        if(plugin.is1_19_4){
            PacketManager_1_19_4 packetManager = new PacketManager_1_19_4();
            int id = packetManager.spawnHealIndicator(e);

            if(id != -1)
                packetManager.removeHologram(id, (Player) e.getEntity(), lifespanSecs);
        }
    }
}
