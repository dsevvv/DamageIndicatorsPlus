package ca.rpgcraft.damageindicatorsplus.data;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class PlayerDataManager {

    private static final DamageIndicatorsPlus plugin = DamageIndicatorsPlus.getInstance();
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public PlayerDataManager() {
        saveDefaultPlayerConfig();
    }

    public void reloadPlayerConfig(){
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "players.yml");

        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("players.yml");
        if(defaultStream != null){
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getPlayerDataConfig(){
        if(dataConfig == null)
            reloadPlayerConfig();

        return dataConfig;
    }

    public void savePlayerDataConfig(){
        plugin.getLogger().info("Saving data...");
        if (dataConfig == null || configFile == null)
            return;

        try {
            this.getPlayerDataConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    public void saveDefaultPlayerConfig(){
        if(configFile == null)
            configFile = new File(plugin.getDataFolder(), "players.yml");

        if(!configFile.exists()){
            plugin.saveResource("players.yml", false);
        }
    }
}
