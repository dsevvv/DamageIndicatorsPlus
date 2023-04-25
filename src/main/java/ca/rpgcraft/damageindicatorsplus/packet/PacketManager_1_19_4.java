package ca.rpgcraft.damageindicatorsplus.packet;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.util.DamageEventType;
import ca.rpgcraft.damageindicatorsplus.util.HologramUtil;
import jline.internal.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PacketManager_1_19_4 {

    static DamageIndicatorsPlus plugin = DamageIndicatorsPlus.getInstance();

    public int spawnHealIndicator(EntityRegainHealthEvent e){
        Player player = (Player) e.getEntity();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        Level level = serverPlayer.level;
        ServerPlayerConnection connection = serverPlayer.connection;
        ArmorStand armorStand = prepareHealHologram(e, player, level);

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(armorStand);
        ClientboundSetEntityDataPacket packet2 = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());

        connection.send(packet);
        connection.send(packet2);

        return armorStand.getId();
    }

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

        Random rand = new Random();

        int randX = rand.nextInt(2 ) == 0 ? rand.nextInt(offXInt)+10 : -rand.nextInt(offXInt)+10;
        int randZ = rand.nextInt(2 ) == 0 ? rand.nextInt(offZInt)+10 : -rand.nextInt(offZInt)+10;

        double x = (double) randX / 100;
        double z = (double) randZ / 100;

        ArmorStand armorStand = new ArmorStand(level, player.getLocation().getX() + x,player.getLocation().getY() + player.getHeight() + offY,player.getLocation().getZ() + z);

        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(Component.literal(HologramUtil.healHologramName(e.getAmount(), player)));
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

    public int spawnDamageIndicator(EntityDamageEvent e, Entity victim, @Nullable Entity damager, DamageEventType eventType){
        if(damager == null && !(victim instanceof Player)) return -1; // If damager is null, and victim is not a player, ignore the event and return -1

        ArmorStand armorStand;

        switch (eventType) {
            case PLAYER_DAMAGE_PLAYER -> {
                Player pVictim = (Player) victim;
                Player pDamager = (Player) damager;
                CraftPlayer craftVictim = (CraftPlayer) pVictim;
                CraftPlayer craftDamager = (CraftPlayer) pDamager;
                ServerPlayer serverVictim = craftVictim.getHandle();
                ServerPlayer serverDamager = craftDamager.getHandle();
                Level level = serverVictim.level;
                ServerPlayerConnection connVictim = serverVictim.connection;
                ServerPlayerConnection connDamager = serverDamager.connection;
                armorStand = prepareDamageHologram(e, pVictim, pDamager, level);
                ClientboundAddEntityPacket packetAdd = new ClientboundAddEntityPacket(armorStand);
                ClientboundSetEntityDataPacket packetData = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());
                connVictim.send(packetAdd);
                connVictim.send(packetData);
                connDamager.send(packetAdd);
                connDamager.send(packetData);
                return armorStand.getId();
            }
            case PLAYER_DAMAGE_ENTITY -> {
                Player pDamager2 = (Player) damager;
                CraftPlayer craftDamager2 = (CraftPlayer) pDamager2;
                ServerPlayer serverDamager2 = craftDamager2.getHandle();
                Level level2 = serverDamager2.level;
                ServerPlayerConnection connDamager2 = serverDamager2.connection;
                armorStand = prepareDamageHologram(e, victim, pDamager2, level2);
                ClientboundAddEntityPacket packetAdd2 = new ClientboundAddEntityPacket(armorStand);
                ClientboundSetEntityDataPacket packetData2 = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());
                connDamager2.send(packetAdd2);
                connDamager2.send(packetData2);
                return armorStand.getId();
            }
            case ENTITY_DAMAGE_PLAYER -> {
                Player pVictim2 = (Player) victim;
                CraftPlayer craftVictim2 = (CraftPlayer) pVictim2;
                ServerPlayer serverVictim2 = craftVictim2.getHandle();
                Level level3 = serverVictim2.level;
                ServerPlayerConnection connVictim2 = serverVictim2.connection;
                armorStand = prepareDamageHologram(e, pVictim2, damager, level3);
                ClientboundAddEntityPacket packetAdd3 = new ClientboundAddEntityPacket(armorStand);
                ClientboundSetEntityDataPacket packetData3 = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());
                connVictim2.send(packetAdd3);
                connVictim2.send(packetData3);
                return armorStand.getId();
            }
            case PLAYER_DAMAGE -> {
                Player pVictim3 = (Player) victim;
                CraftPlayer craftVictim3 = (CraftPlayer) pVictim3;
                ServerPlayer serverVictim3 = craftVictim3.getHandle();
                Level level4 = serverVictim3.level;
                ServerPlayerConnection connVictim3 = serverVictim3.connection;
                armorStand = prepareDamageHologram(e, pVictim3, null, level4);
                ClientboundAddEntityPacket packetAdd4 = new ClientboundAddEntityPacket(armorStand);
                ClientboundSetEntityDataPacket packetData4 = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().packDirty());
                connVictim3.send(packetAdd4);
                connVictim3.send(packetData4);
                return armorStand.getId();
            }
            default -> {
                return -1;
            }
        }
    }

    static ArmorStand prepareDamageHologram(EntityDamageEvent e, Entity victim, @Nullable Entity damager, Level level){
        ArmorStand armorStand = new ArmorStand(level,
                e.getEntity().getLocation().getX(),
                e.getEntity().getLocation().getY() + e.getEntity().getHeight() + 0.1,
                e.getEntity().getLocation().getZ());

        armorStand.setCustomNameVisible(true);
        if(damager instanceof Player)
            armorStand.setCustomName(Component.literal(HologramUtil.damageHologramName(e.getFinalDamage(), e, (Player) damager)));
        else
            armorStand.setCustomName(Component.literal(HologramUtil.damageHologramName(e.getFinalDamage(), e, null)));

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
            HologramUtil.spawnDamageParticle(victim);

        return armorStand;
    }

    public void removeHologram(int entityId, Player player, int delaySecs){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        ServerPlayerConnection connection = serverPlayer.connection;
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entityId);

        new BukkitRunnable() {
            @Override
            public void run() {
                connection.send(packet);
            }
        }.runTaskLater(plugin, 20L * delaySecs);
    }
}
