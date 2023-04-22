package ca.rpgcraft.damageindicatorsplus.command;

import ca.rpgcraft.damageindicatorsplus.DamageIndicatorsPlus;
import ca.rpgcraft.damageindicatorsplus.entity.hologram.task.CleanupHologramTask;
import ca.rpgcraft.damageindicatorsplus.entity.player.data.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private final DamageIndicatorsPlus plugin;

    public Commands(DamageIndicatorsPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase("di")) return false;
        if(args.length == 0){
            sendHelp(sender);
            return false;
        }
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        //clear command
        if(args[0].equalsIgnoreCase("clear")){
            if(!(sender instanceof Player player)){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be a player to run this command!"));
                return false;
            }
            if(!player.hasPermission("di.clear")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to run this command!"));
                return false;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aClearing holograms in world " + player.getWorld().getName()));
            new CleanupHologramTask(null, DamageIndicatorsPlus.getInstance().getHologramManager()).asyncClearWorld(player.getWorld());
        }

        //toggle command
        if(args[0].equalsIgnoreCase("toggle")){
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be a player to run this command!"));
                return false;
            }

            Player player = (Player) sender;
            FileConfiguration fileConfiguration = playerDataManager.getPlayerDataConfig();

            List<String> playerList = fileConfiguration.getStringList("players");

            if(!playerList.contains(String.valueOf(player.getUniqueId()))){
                playerList.add(String.valueOf(player.getUniqueId()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDamage Indicators: &4Disabled"));
            }else{
                playerList.remove(String.valueOf(player.getUniqueId()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDamage Indicators: &2Enabled"));
            }
            fileConfiguration.set("players", playerList);
            playerDataManager.savePlayerDataConfig();
        }

        //reload command
        if(args[0].equalsIgnoreCase("reload")){
            if(!sender.hasPermission("di.reload")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to run this command!"));
                return false;
            }
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDamage Indicators: &2Config reloaded"));
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase("di")) return null;
        if(args.length == 1){
            List<String> completions = new ArrayList<>();

            completions.add("clear");
            completions.add("toggle");
            completions.add("reload");

            List<String> result = new ArrayList<>();

            for(String a : completions){
                if(a.toLowerCase().startsWith(args[0].toLowerCase())){
                    result.add(a);
                }
            }
            return result;

        }
        return null;
    }

    private void sendHelp(CommandSender sender){
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                "&e========== &bDamage Indicators &e=========="));
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                "&e/di clear &7-&a Clears all holograms in the world you are in."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                "&c/di toggle &7- &aToggles visibility of damage indicators."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                "&e/di reload &7- &aReloads the config.yml file."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                "&e========== &bDamage Indicators &e=========="));
    }
}
