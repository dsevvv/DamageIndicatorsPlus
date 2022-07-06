package ca.rpgcraft.damageindicatorsplus.hooks;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.tasks.VectorGenerator;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import ca.rpgcraft.damageindicatorsplus.utils.PlayerDataManager;
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

public class ProtocolLibBridge {

    private final DamageIndicatorsPlus plugin;
    private final VectorGenerator vectorGenerator;
    private final HologramManager hologramManager;

    public ProtocolLibBridge(DamageIndicatorsPlus plugin, VectorGenerator vectorGenerator, HologramManager hologramManager){
        this.plugin = plugin;
        this.vectorGenerator = vectorGenerator;
        this.hologramManager = hologramManager;
        initProtocolLib();
    }

    public void initProtocolLib(){
        plugin.getLogger().info("ProtocolLib found.");
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SPAWN_ENTITY) {
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