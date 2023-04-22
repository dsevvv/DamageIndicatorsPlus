package ca.rpgcraft.damageindicatorsplus.entity.listener;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.entity.hologram.HologramManager;
import ca.rpgcraft.damageindicatorsplus.entity.hologram.task.CreateHologramTask;
import ca.rpgcraft.damageindicatorsplus.hooks.PlaceholderBridge;
import ca.rpgcraft.damageindicatorsplus.hooks.WorldGuardBridge;
import ca.rpgcraft.damageindicatorsplus.util.DamageEventChecks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;

public class PlayerOnEntityDamage implements Listener {

    private final DamageIndicatorsPlus plugin;
    private final HologramManager hologramManager;
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.0");
    private Player playerDamager = null;

    public PlayerOnEntityDamage(DamageIndicatorsPlus plugin,
                        HologramManager hologramManager){
        this.plugin = plugin;
        this.hologramManager = hologramManager;

    }

    @EventHandler
    void onEntityDamageByPlayerEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        if(e.isCancelled()) return;
        if(!DamageEventChecks.isPlayerOnEntityEvent(e.getEntity(), e.getDamager())) return;
        if (DamageEventChecks.isIgnored(e)) return;
        this.playerDamager = (Player) e.getDamager();

        if(!plugin.getConfig().getBoolean("damage-indicator.sweeping-edge")){
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        }

        if(plugin.isDIFlags() && plugin.isWorldGuard()){
            if(!allowHologramSpawn(e.getEntity())) return;
        }

        if(plugin.is1_19_4){
            Player player = (Player) e.getDamager();
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();
            Level level = serverPlayer.level;

            ServerPlayerConnection connection = serverPlayer.connection;
            ArmorStand armorStand = new ArmorStand(level, e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY() + e.getEntity().getHeight() + 0.1, e.getEntity().getLocation().getZ());

            armorStand.setCustomNameVisible(true);
            Component name = Component.literal(hologramName(e.getFinalDamage(), e));
            armorStand.setCustomName(name);

            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setSilent(true);
            armorStand.setInvulnerable(true);
            armorStand.setNoBasePlate(true);
            armorStand.setShowArms(false);
            armorStand.collides = false;

            Vector vec = plugin.getRingBuffer().getNext();
            armorStand.setDeltaMovement(new Vec3(vec.getX(), vec.getY(), vec.getZ()));

            ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(armorStand);
            ClientboundSetEntityDataPacket packet2 = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());

            connection.send(packet);
            connection.send(packet2);

            boolean isParticle = !plugin.getConfig().contains("damage-indicator.particle.enabled") || plugin.getConfig().getBoolean("damage-indicator.particle.enabled");

            if(isParticle)
                spawnParticle(e.getEntity());

            new BukkitRunnable() {
                @Override
                public void run() {
                    ClientboundRemoveEntitiesPacket packet3 = new ClientboundRemoveEntitiesPacket(armorStand.getId());
                    connection.send(packet3);
                }
            }.runTaskLater(plugin, 20);
        }else{
            CreateHologramTask createHologramTask = new CreateHologramTask(plugin, e, (Player) e.getDamager(), hologramManager);
            createHologramTask.run();
        }
    }

    private boolean allowHologramSpawn(Entity entity){
        if(!DamageIndicatorsPlus.getInstance().isWorldGuard())
            return true;

        return new WorldGuardBridge().isDIFlag(entity);
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

    private String hologramName(double dmgFinal, EntityDamageEvent e) {
        //get from config
        String customName = plugin.getConfig().getString("damage-indicator.indicator-message") != null ?
                ChatColor.translateAlternateColorCodes('&', new StringBuilder(plugin.getConfig().getString("damage-indicator.indicator-message")).toString()) : ChatColor.translateAlternateColorCodes('&', new StringBuilder("&c-").append(decimalFormat.format(dmgFinal)).toString());

        //parse {damage} from config into dmgFinal number
        customName = customName.replace("{damage}", decimalFormat.format(dmgFinal));

        //checking if a critical hit
        if(playerDamager != null) {
            if(isCritical(playerDamager)){
                String criticalName = plugin.getConfig().getString("damage-indicator.critical-message") != null ?
                        ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("damage-indicator.critical-message")) : ChatColor.translateAlternateColorCodes('&',"&4&l&oCrit!");

                if(plugin.isPAPI())
                    criticalName = new PlaceholderBridge().parse(playerDamager, criticalName);
                else
                    criticalName = criticalName.replaceAll("%[^%]+%", "");

                customName = customName + " " + criticalName;
                if(plugin.isPAPI())
                    return new PlaceholderBridge().parse(playerDamager, customName);
                else
                    return customName.replaceAll("%[^%]+%", "");
            }
            else {
                if(plugin.isPAPI())
                    return new PlaceholderBridge().parse(playerDamager, customName);
                else
                    return customName.replaceAll("%[^%]+%", "");
            }
        }

        if(plugin.isPAPI() && e.getEntity() instanceof Player)
            return new PlaceholderBridge().parse((Player) e.getEntity(), customName);
        else
            //remove any text between % and the next %
            return customName.replaceAll("%[^%]+%", "");
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
