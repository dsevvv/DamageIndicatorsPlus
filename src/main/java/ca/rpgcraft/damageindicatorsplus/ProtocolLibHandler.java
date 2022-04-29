package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.tasks.GenerateVectorTask;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ProtocolLibHandler {

    private final DamageIndicatorsPlus plugin;
    private final GenerateVectorTask generateVectorTask;

    public ProtocolLibHandler(DamageIndicatorsPlus plugin, GenerateVectorTask generateVectorTask){
        this.plugin = plugin;
        this.generateVectorTask = generateVectorTask;
        initProtocolLib();
    }

    public void initProtocolLib(){
        plugin.getLogger().info("ProtocolLib found.");
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player p = event.getPlayer();

                int entityID = packet.getIntegers().read(0);
                if(entityID < 0){
                    return;
                }
                Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(p.getWorld(), entityID);

                if(!(entity instanceof ArmorStand)) return;
                ArmorStand hologram = (ArmorStand) entity;
                if(!(hologram.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))) return;

                hologram.setVisible(false);
                hologram.setVelocity(generateVectorTask.generateVector());
            }
        });
    }
}
