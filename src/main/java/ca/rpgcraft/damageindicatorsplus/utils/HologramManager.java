package ca.rpgcraft.damageindicatorsplus.utils;

import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.UUID;

public class HologramManager {

    private final HashMap<UUID, ArmorStand> hologramList;

    public HologramManager()
    {
        this.hologramList = new HashMap<>();
    }

    public HashMap<UUID, ArmorStand> getHologramList()
    {
        return hologramList;
    }

    public ArmorStand addHologram(ArmorStand hologram)
    {
        hologramList.put(hologram.getUniqueId(), hologram);
        return hologram;
    }

    public void removeHologram(ArmorStand hologram) {
        if(!hologramList.containsKey(hologram.getUniqueId())) return;

        hologramList.remove(hologram.getUniqueId());
    }
}
