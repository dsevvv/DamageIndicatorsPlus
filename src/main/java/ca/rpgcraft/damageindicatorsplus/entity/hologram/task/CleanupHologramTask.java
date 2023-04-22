package ca.rpgcraft.damageindicatorsplus.entity.hologram.task;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.entity.hologram.HologramManager;

import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class CleanupHologramTask extends BukkitRunnable {

    private final ArmorStand hologram;
    private final HologramManager hologramManager;

    public CleanupHologramTask(ArmorStand hologram, HologramManager hologramManager) {
        this.hologram = hologram;
        this.hologramManager = hologramManager;
    }

    @Override
    public void run() {
        hologramManager.removeHologram(hologram);
        hologram.remove();
    }

    public void asyncClearWorld(World world){
        new BukkitRunnable() {

            @Override
            public void run() {

                for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class)) {

                    PersistentDataContainer container = armorStand.getPersistentDataContainer();

                    if(container.has(DamageIndicatorsPlus.getInstance().getHologramKey(), PersistentDataType.STRING)){
                        hologramManager.removeHologram(armorStand);
                        armorStand.remove();
                    }
                }
            }
        }.runTask(DamageIndicatorsPlus.getInstance());
    }
}
