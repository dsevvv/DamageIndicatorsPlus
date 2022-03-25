package ca.rpgcraft.damageindicatorsplus.utils;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageHologramUtils {

    public static boolean isPlayerEvent(Entity victim){
        if (!(victim instanceof Player)) return false;
        return !(victim instanceof ArmorStand);
    }

    public static boolean isPlayerOnPlayerEvent(Entity victim, Entity damager){
        if (!(victim instanceof Player)) return false;
        if (victim instanceof ArmorStand) return false;
        return damager instanceof Player;
    }

    public static boolean isPlayerOnEntityEvent(Entity victim, Entity damager){
        if (victim instanceof Player) return false;
        if (victim instanceof ArmorStand) return false;
        if (!(damager instanceof Player)) return false;

        return true;
    }

    public static boolean isEntityOnEntityEvent(Entity victim, Entity damager){
        if (victim instanceof Player) return false;
        if (victim instanceof ArmorStand) return false;
        return !(damager instanceof Player);
    }

    public static boolean isEntityOnPlayerEvent(Entity victim, Entity damager){
        if (!(victim instanceof Player)) return false;
        if (victim instanceof ArmorStand) return false;
        return !(damager instanceof Player);
    }

    public static boolean isIgnored(EntityDamageEvent e){
        return e.isCancelled() || e.getFinalDamage() == 0;
    }
}
