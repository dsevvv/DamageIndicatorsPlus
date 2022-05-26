package ca.rpgcraft.damageindicatorsplus.tasks;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.text.DecimalFormat;
import java.util.Random;

public class CreateHologramTask extends BukkitRunnable {

    private final DamageIndicatorsPlus plugin;
    private EntityDamageEvent entityDamageEvent;
    private EntityRegainHealthEvent entityHealEvent;
    private final VectorGenerator vectorGenerator;
    private final HologramManager hologramManager;
    private final Random rand = new Random();
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    private Player playerDamager = null;

    public CreateHologramTask(DamageIndicatorsPlus plugin, VectorGenerator vectorGenerator, EntityDamageEvent e, HologramManager hologramManager) {
        this.plugin = plugin;
        this.entityDamageEvent = e;
        this.vectorGenerator = vectorGenerator;
        this.hologramManager = hologramManager;
    }

    public CreateHologramTask(DamageIndicatorsPlus plugin, VectorGenerator vectorGenerator, EntityDamageEvent e, Player playerDamager, HologramManager hologramManager) {
        this.plugin = plugin;
        this.entityDamageEvent = e;
        this.playerDamager = playerDamager;
        this.vectorGenerator = vectorGenerator;
        this.hologramManager = hologramManager;
    }

    public CreateHologramTask(DamageIndicatorsPlus plugin, VectorGenerator vectorGenerator, EntityRegainHealthEvent e, HologramManager hologramManager) {
        this.plugin = plugin;
        this.entityHealEvent = e;
        this.vectorGenerator = vectorGenerator;
        this.hologramManager = hologramManager;
    }

    /**
     * Damage Hologram
     */
    @Override
    public void run() {
        int lifespanSecs = plugin.getConfig().contains("damage-indicator.lifespan") ? plugin.getConfig().getInt("damage-indicator.lifespan") : 1;
        double dmgFinal = entityDamageEvent.getFinalDamage();
        Entity victim = entityDamageEvent.getEntity();

        ArmorStand hologram;

        if(plugin.isPaper()){
            hologram = hologramManager.addHologram((ArmorStand) victim.getWorld().spawnEntity(victim.getLocation(), EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM));
        }else{
            hologram = hologramManager.addHologram((ArmorStand) victim.getWorld().spawnEntity(victim.getLocation(), EntityType.ARMOR_STAND));
        }

        prepareHologram(hologram);
        hologram.setCustomName(hologramName(dmgFinal));

        boolean isParticle = !plugin.getConfig().contains("damage-indicator.particle.enabled") || plugin.getConfig().getBoolean("damage-indicator.particle.enabled");

        if(isParticle)
            spawnParticle(victim);

        CleanupHologramTask cleanupTask = new CleanupHologramTask(hologram, hologramManager);
        cleanupTask.runTaskLater(plugin, 20L * lifespanSecs);
    }

    /**
     * Heal Hologram
     */
    public void startHealHologramRunnable(){
        new BukkitRunnable() {
            @Override
            public void run() {
                int lifespanSecs = plugin.getConfig().contains("heal-indicator.lifespan") ? plugin.getConfig().getInt("heal-indicator.lifespan") : 1;
                double healFinal = entityHealEvent.getAmount();
                Entity target = entityHealEvent.getEntity();
                double offX = plugin.getConfig().contains("heal-indicator.particle.offset.x") ?
                        plugin.getConfig().getDouble("heal-indicator.particle.offset.x") : .5;
                double offY = plugin.getConfig().contains("heal-indicator.particle.offset.y") ?
                        plugin.getConfig().getDouble("heal-indicator.particle.offset.y") : .5;
                double offZ = plugin.getConfig().contains("heal-indicator.particle.offset.z") ?
                        plugin.getConfig().getDouble("heal-indicator.particle.offset.z") : .5;

                double offXUp = offX * 100;
                double offYUp = offY * 100;
                double offZUp = offZ * 100;

                int offXInt = (int) offXUp;
                int offYInt = (int) offYUp;
                int offZInt = (int) offZUp;

                int randX = rand.nextInt(2 ) == 0 ? rand.nextInt(offXInt)+10 : -rand.nextInt(offXInt)+10;
                int randZ = rand.nextInt(2 ) == 0 ? rand.nextInt(offZInt)+10 : -rand.nextInt(offZInt)+10;
                int randY = rand.nextInt(offYInt)+10;

                double x = (double) randX / 100;
                double y = (double) randY / 100;
                double z = (double) randZ / 100;

                ArmorStand hologram;

                if(plugin.isPaper()){
                    hologram = hologramManager.addHologram((ArmorStand) target.getWorld().spawnEntity(target.getLocation().add(x, y, z), EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM));
                }else{
                    hologram = hologramManager.addHologram((ArmorStand) target.getWorld().spawnEntity(target.getLocation(), EntityType.ARMOR_STAND));
                }

                prepareHologram(hologram);
                hologram.setGravity(false);
                hologram.setCustomName(healHologramName(healFinal));

                boolean isParticle = !plugin.getConfig().contains("heal-indicator.particle.enabled") || plugin.getConfig().getBoolean("heal-indicator.particle.enabled");

                if(isParticle)
                    hologram.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, hologram.getLocation().add(0, 1.25, 0), 3, .25, .1, .25);

                CleanupHologramTask cleanupTask = new CleanupHologramTask(hologram, hologramManager);
                cleanupTask.runTaskLater(plugin, 20L * lifespanSecs);
            }
        }.run();
    }

    private String hologramName(double dmgFinal) {
        String customName = plugin.getConfig().getString("damage-indicator.indicator-message") != null ?
                ChatColor.translateAlternateColorCodes('&', new StringBuilder(plugin.getConfig().getString("damage-indicator.indicator-message")).append(decimalFormat.format(dmgFinal)).toString()) : ChatColor.translateAlternateColorCodes('&', new StringBuilder("&c-").append(decimalFormat.format(dmgFinal)).toString());

        //checking if a critical hit
        if(playerDamager != null) {
            if(isCritical(playerDamager)){
                String criticalName = plugin.getConfig().getString("damage-indicator.critical-message") != null ?
                        ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("damage-indicator.critical-message")) : ChatColor.translateAlternateColorCodes('&',"&4&l&oCrit!");

                customName = customName + " " + criticalName;
            }
        }

        return customName;
    }

    private String healHologramName(double healFinal) {
        return plugin.getConfig().getString("heal-indicator.indicator-message") != null ?
                ChatColor.translateAlternateColorCodes('&', new StringBuilder(plugin.getConfig().getString("heal-indicator.indicator-message")).append(decimalFormat.format(healFinal)).toString()) : ChatColor.translateAlternateColorCodes('&',  new StringBuilder("&a+").append(decimalFormat.format(healFinal)).toString());
    }

    private void prepareHologram(ArmorStand hologram){
        if(!plugin.isProtocolLib()){
            hologram.setVisible(false);
        }
        hologram.setVelocity(plugin.getRingBuffer().getNext());
        hologram.getPersistentDataContainer().set(new NamespacedKey(plugin, "hologram"), PersistentDataType.STRING, "true");
        hologram.setBasePlate(false);
        hologram.setCollidable(false);
        hologram.setArms(false);
        hologram.setSmall(true);
        hologram.setSilent(true);
        hologram.setCanPickupItems(false);
        hologram.setGliding(true);
        hologram.setLeftLegPose(EulerAngle.ZERO.add(180, 0, 0));
        hologram.setRightLegPose(EulerAngle.ZERO.add(180, 0, 0));
        hologram.setInvulnerable(true);
        hologram.setCustomNameVisible(true);
    }

    private void spawnParticle(Entity victim){
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

    private boolean isCritical(Player playerDamager){
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
