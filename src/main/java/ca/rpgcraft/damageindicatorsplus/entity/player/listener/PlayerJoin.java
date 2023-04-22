package ca.rpgcraft.damageindicatorsplus.entity.player.listener;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        DamageIndicatorsPlus plugin = DamageIndicatorsPlus.getInstance();
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getPlayerDataManager().getPlayerDataConfig();
        List<String> playerList = config.getStringList("players");

        if(!playerList.contains(String.valueOf(player.getUniqueId())) && !plugin.getConfig().getBoolean("damage-indicator.default-toggle")){
            playerList.add(String.valueOf(player.getUniqueId()));
            config.set("players", playerList);
            plugin.getPlayerDataManager().savePlayerDataConfig();
        }
    }
}
