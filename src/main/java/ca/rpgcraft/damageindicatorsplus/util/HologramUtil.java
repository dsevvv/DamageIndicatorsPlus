package ca.rpgcraft.damageindicatorsplus.util;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.hooks.PlaceholderBridge;
import jline.internal.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Random;

public interface HologramUtil {

    DamageIndicatorsPlus plugin = DamageIndicatorsPlus.getInstance();
    DecimalFormat decimalFormat = new DecimalFormat("#0.0");
    Random rand = new Random();

    static ArmorStand prepareHealHologram(EntityRegainHealthEvent e, Player player, Level level){
        boolean isParticle = !plugin.getConfig().contains("heal-indicator.particle.enabled") || plugin.getConfig().getBoolean("heal-indicator.particle.enabled");

        double offX = plugin.getConfig().contains("heal-indicator.offset.x") ?
                plugin.getConfig().getDouble("heal-indicator.offset.x") : .5;
        double offY = plugin.getConfig().contains("heal-indicator.offset.y") ?
                plugin.getConfig().getDouble("heal-indicator.offset.y") : .5;
        double offZ = plugin.getConfig().contains("heal-indicator.offset.z") ?
                plugin.getConfig().getDouble("heal-indicator.offset.z") : .5;

        double offXUp = offX * 100;
        double offZUp = offZ * 100;

        int offXInt = (int) offXUp;
        int offZInt = (int) offZUp;

        int randX = rand.nextInt(2 ) == 0 ? rand.nextInt(offXInt)+10 : -rand.nextInt(offXInt)+10;
        int randZ = rand.nextInt(2 ) == 0 ? rand.nextInt(offZInt)+10 : -rand.nextInt(offZInt)+10;

        double x = (double) randX / 100;
        double z = (double) randZ / 100;

        ArmorStand armorStand = new ArmorStand(level, player.getLocation().getX() + x,player.getLocation().getY() + player.getHeight() + offY,player.getLocation().getZ() + z);

        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(HologramUtil.healHologramName(e.getAmount(), player));
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
        armorStand.setNoBasePlate(true);
        armorStand.setShowArms(false);
        armorStand.setNoGravity(true);
        armorStand.collides = false;

        if(isParticle)
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 1.25, 0), 3, .25, .1, .25);

        return armorStand;
    }

    static Component healHologramName(double healFinal, Player player) {
        String customName = plugin.getConfig().getString("heal-indicator.indicator-message") != null ?
                ChatColor.translateAlternateColorCodes(
                        '&', new StringBuilder(
                                plugin.getConfig().getString("heal-indicator.indicator-message")).toString()) :
                ChatColor.translateAlternateColorCodes(
                        '&',  new StringBuilder("&a+").append(decimalFormat.format(healFinal)).toString());

        customName = customName.replace("{heal}", decimalFormat.format(healFinal));

        if(plugin.isPAPI())
            return Component.literal(new PlaceholderBridge().parse(player, customName));
        else
            return Component.literal(customName.replaceAll("%[^%]+%", ""));
    }

    static ArmorStand prepareDamageHologram(EntityDamageEvent e, Entity victim, @Nullable Entity damager, Level level){
        ArmorStand armorStand = new ArmorStand(level,
                e.getEntity().getLocation().getX(),
                e.getEntity().getLocation().getY() + e.getEntity().getHeight() + 0.1,
                e.getEntity().getLocation().getZ());

        armorStand.setCustomNameVisible(true);
        if(damager instanceof Player)
            armorStand.setCustomName(HologramUtil.damageHologramName(e.getFinalDamage(), e, (Player) damager));
        else
            armorStand.setCustomName(HologramUtil.damageHologramName(e.getFinalDamage(), e, null));

        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
        armorStand.setNoBasePlate(true);
        armorStand.setShowArms(false);
        armorStand.collides = false;

        boolean isParticle = !plugin.getConfig().contains("damage-indicator.particle.enabled") || plugin.getConfig().getBoolean("damage-indicator.particle.enabled");

        if(isParticle)
            spawnDamageParticle(victim);

        return armorStand;
    }

    private static void spawnDamageParticle(Entity victim){
        String particleTypeStr = plugin.getConfig().getString("damage-indicator.particle.particle-type") != null ?
                plugin.getConfig().getString("damage-indicator.particle.particle-type") : "REDSTONE";

        int particleCount = plugin.getConfig().contains("damage-indicator.particle.particle-count") ?
                plugin.getConfig().getInt("damage-indicator.particle.particle-count") : 30;

        double locX = plugin.getConfig().contains("damage-indicator.particle.loc-x") ?
                plugin.getConfig().getDouble("damage-indicator.particle.loc-x") : 0;
        double locY = plugin.getConfig().contains("damage-indicator.particle.loc-y") ?
                plugin.getConfig().getDouble("damage-indicator.particle.loc-y") : .5;
        double locZ = plugin.getConfig().contains("damage-indicator.particle.loc-z") ?
                plugin.getConfig().getDouble("damage-indicator.particle.loc-z") : 0;
        double offX = plugin.getConfig().contains("damage-indicator.particle.offset-x") ?
                plugin.getConfig().getDouble("damage-indicator.particle.offset-x") : .5;
        double offY = plugin.getConfig().contains("damage-indicator.particle.offset-y") ?
                plugin.getConfig().getDouble("damage-indicator.particle.offset-y") : .5;
        double offZ = plugin.getConfig().contains("damage-indicator.particle.offset-z") ?
                plugin.getConfig().getDouble("damage-indicator.particle.offset-z") : .5;

        Particle particleType;
        try{
            particleType = Particle.valueOf(particleTypeStr);
        }catch (Exception ignored){
            particleType = Particle.REDSTONE;
        }

        if(particleType.equals(Particle.REDSTONE)){
            victim.getWorld().spawnParticle(particleType, victim.getLocation().add(locX, locY, locZ), particleCount, offX, offY, offZ, new Particle.DustOptions(Color.RED, 1));
        }else{
            victim.getWorld().spawnParticle(particleType, victim.getLocation().add(locX, locY, locZ), particleCount, offX, offY, offZ);
        }
    }

    static Component damageHologramName(double dmgFinal, EntityDamageEvent e,@Nullable Player player) {
        //get from config
        String customName = plugin.getConfig().getString("damage-indicator.indicator-message") != null ?
                ChatColor.translateAlternateColorCodes('&', new StringBuilder(plugin.getConfig().getString("damage-indicator.indicator-message")).toString()) : ChatColor.translateAlternateColorCodes('&', new StringBuilder("&c-").append(decimalFormat.format(dmgFinal)).toString());

        //parse {damage} from config into dmgFinal number
        customName = customName.replace("{damage}", decimalFormat.format(dmgFinal));

        //checking if a critical hit
        if(player != null) {
            if(isCritical(player)){
                String criticalName = plugin.getConfig().getString("damage-indicator.critical-message") != null ?
                        ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("damage-indicator.critical-message")) : ChatColor.translateAlternateColorCodes('&',"&4&l&oCrit!");

                if(plugin.isPAPI())
                    criticalName = new PlaceholderBridge().parse(player, criticalName);
                else
                    criticalName = criticalName.replaceAll("%[^%]+%", "");

                customName = customName + " " + criticalName;
                if(plugin.isPAPI())
                    return Component.literal(new PlaceholderBridge().parse(player, customName));
                else
                    return Component.literal(customName.replaceAll("%[^%]+%", ""));
            }
            else {
                if(plugin.isPAPI())
                    return Component.literal(new PlaceholderBridge().parse(player, customName));
                else
                    return Component.literal(customName.replaceAll("%[^%]+%", ""));
            }
        }

        if(plugin.isPAPI() && e.getEntity() instanceof Player)
            return Component.literal(new PlaceholderBridge().parse((Player) e.getEntity(), customName));
        else
            //remove any text between % and the next %
            return Component.literal(customName.replaceAll("%[^%]+%", ""));
    }

    static boolean isCritical(Player playerDamager){
        return playerDamager.getFallDistance() > 0
                && !(((Entity) playerDamager).isOnGround())
                && !(playerDamager.getLocation().getBlock().getType().equals(Material.VINE))
                && !(playerDamager.getLocation().getBlock().getType().equals(Material.LADDER))
                && !(playerDamager.getLocation().getBlock().getType().equals(Material.WATER))
                && !(playerDamager.getLocation().getBlock().getType().equals(Material.LAVA))
                && !(playerDamager.hasPotionEffect(PotionEffectType.BLINDNESS))
                && playerDamager.getVehicle() == null;
    }
}
