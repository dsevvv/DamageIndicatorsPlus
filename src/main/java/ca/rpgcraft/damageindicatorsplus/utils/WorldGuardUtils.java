package ca.rpgcraft.damageindicatorsplus.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class WorldGuardUtils {

    public RegionAssociable createRegionAssociable(Object cause) {
        if (cause instanceof Player) {
            return WorldGuardPlugin.inst().wrapPlayer((Player) cause);
        } else if (cause instanceof Entity entity) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(BukkitAdapter.adapt(entity.getWorld()));
            org.bukkit.Location loc = entity.getLocation(); // getOrigin() can be used on Paper if present
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(loc), config.useMaxPriorityAssociation);
        } else if (cause instanceof Block block) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(BukkitAdapter.adapt(block.getWorld()));
            org.bukkit.Location loc = block.getLocation();
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(loc), config.useMaxPriorityAssociation);
        } else {
            return Associables.constant(Association.NON_MEMBER);
        }
    }

    public boolean isDIFlag(Entity entity){
        RegionAssociable regionAssociable = this.createRegionAssociable(entity);
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(entity.getWorld().getName());
        Location location = new Location(world, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        ApplicableRegionSet set = regions.getApplicableRegions(location.toVector().toBlockPoint());
        Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get("damage-indicators");

        if(set.queryValue(regionAssociable, flag).toString().equalsIgnoreCase("ALLOW")){
            return true;
        }else{
            return false;
        }
    }
}
