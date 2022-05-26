package ca.rpgcraft.damageindicatorsplus;

import ca.rpgcraft.damageindicatorsplus.tasks.VectorGenerator;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import ca.rpgcraft.damageindicatorsplus.utils.PlayerDataManager;
import ca.rpgcraft.damageindicatorsplus.utils.VectorRingBuffer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class ProtocolLibHandler {

    private final DamageIndicatorsPlus plugin;
    private final VectorGenerator vectorGenerator;
    private final HologramManager hologramManager;

    public ProtocolLibHandler(DamageIndicatorsPlus plugin, VectorGenerator vectorGenerator, HologramManager hologramManager){
        this.plugin = plugin;
        this.vectorGenerator = vectorGenerator;
        this.hologramManager = hologramManager;
        initProtocolLib();
    }

    public void initProtocolLib(){
        VectorRingBuffer vectorRingBuffer = plugin.getRingBuffer();
        plugin.getLogger().info("ProtocolLib found.");
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
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

                FileConfiguration fileConfiguration = playerDataManager.getPlayerDataConfig();

                List<String> playerList = fileConfiguration.getStringList("players");

                if(playerList.contains(String.valueOf(p.getUniqueId()))){
                    event.setCancelled(true);

                }

                hologram.setVisible(false);
            }
        });
    }
}
