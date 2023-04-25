package ca.rpgcraft.damageindicatorsplus.vector;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.util.Vector;

import java.util.Random;

public class VectorGenerator {

    private static final DamageIndicatorsPlus plugin = DamageIndicatorsPlus.getInstance();

    public static Vector getVector(){
        Random rand = new Random();

        double horiRange = plugin.getConfig().contains("damage-indicator.velocity.horizontal") ?
                plugin.getConfig().getDouble("damage-indicator.velocity.horizontal") : .25;
        double vertRange = plugin.getConfig().contains("damage-indicator.velocity.vertical") ?
                plugin.getConfig().getDouble("damage-indicator.velocity.vertical") : .15;

        double horiRangeUp = horiRange * 100;
        double vertRangeUp = vertRange * 100;

        int horiRangeInt = (int) horiRangeUp;
        int vertRangeInt = (int) vertRangeUp;

        int xInt = rand.nextInt(2) == 0 ? rand.nextInt(horiRangeInt) : -rand.nextInt(horiRangeInt);
        int yInt = rand.nextInt(vertRangeInt)+1;
        int zInt = rand.nextInt(2) == 0 ? rand.nextInt(horiRangeInt) : -rand.nextInt(horiRangeInt);

        double xDouble = xInt;
        double yDouble = yInt;
        double zDouble = zInt;

        double x = xDouble / 75;
        double y = yDouble / 35;
        double z = zDouble / 75;

        return new Vector(x, y, z);
    }
}
