package ca.rpgcraft.damageindicatorsplus.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderBridge {

    public String parse(Player player, String placeholder) {

        return PlaceholderAPI.setPlaceholders(player, placeholder);
    }
}
