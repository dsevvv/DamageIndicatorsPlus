package ca.rpgcraft.damageindicatorsplus.tasks;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class GenerateVectorTask {

    private Random rand;
    private final DamageIndicatorsPlus plugin;

    public GenerateVectorTask(DamageIndicatorsPlus plugin) {
        this.plugin = plugin;
        rand = new Random();
    }

    public Vector generateVector(){
        double horiRange = plugin.getConfig().contains("damage-indicator.velocity.horizontal") ?
                plugin.getConfig().getDouble("damage-indicator.velocity.horizontal") : .25;
        double vertRange = plugin.getConfig().contains("damage-indicator.velocity.vertical") ?
                plugin.getConfig().getDouble("damage-indicator.velocity.vertical") : .15;

        double x = rand.nextInt(2) == 0 ? rand.nextDouble(horiRange) : -rand.nextDouble(horiRange);
        double y = rand.nextDouble(vertRange)+.1;
        double z = rand.nextInt(2) == 0 ? rand.nextDouble(horiRange) : -rand.nextDouble(horiRange);

        return new Vector(x, y, z);
    }
}
