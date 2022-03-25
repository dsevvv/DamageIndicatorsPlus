package ca.rpgcraft.damageindicatorsplus.tasks;

import ca.rpgcraft.damageindicatorsplus.utils.HologramManager;
import org.bukkit.entity.ArmorStand;
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
}
