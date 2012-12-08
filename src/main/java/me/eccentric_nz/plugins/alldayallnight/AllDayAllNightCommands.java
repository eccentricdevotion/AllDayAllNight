package me.eccentric_nz.plugins.alldayallnight;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AllDayAllNightCommands implements CommandExecutor {

    private AllDayAllNight plugin;

    public AllDayAllNightCommands(AllDayAllNight plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("adan")) {
            if (!sender.hasPermission("alldayallnight.admin")) {
                sender.sendMessage(plugin.MY_PLUGIN_NAME + "You do not have permission to use this command!");
                return true;
            }
            if (args[0].equalsIgnoreCase("check_every")) {
                Long time;
                try {
                    time = Long.parseLong(args[1]);
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(plugin.MY_PLUGIN_NAME + ChatColor.RED + "The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("check_every", time);
                plugin.repeat = time;
            }
            if (args.length < 3) {
                sender.sendMessage(plugin.MY_PLUGIN_NAME + "Not enough command arguments!");
                return false;
            }
            Set<String> worlds = plugin.getConfig().getConfigurationSection("worlds").getKeys(false);
            if (!worlds.contains(args[1])) {
                sender.sendMessage(plugin.MY_PLUGIN_NAME + "Could not find world in the config!");
                return true;
            }
            if (args[0].equalsIgnoreCase("toggle")) {
                String tf = args[2].toLowerCase();
                if (!tf.equals("true") && !tf.equals("false")) {
                    sender.sendMessage(plugin.MY_PLUGIN_NAME + ChatColor.RED + "The last argument must be true or false!");
                    return false;
                }
                plugin.getConfig().set("worlds." + args[1] + ".enabled", Boolean.valueOf(tf));
                if (tf.equals("true")) {
                    plugin.adanWorlds.add(Bukkit.getServer().getWorld(args[1]));
                } else {
                    plugin.adanWorlds.remove(Bukkit.getServer().getWorld(args[1]));
                }
            }
            if (args[0].equalsIgnoreCase("dawn")) {
                Long time;
                try {
                    time = Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(plugin.MY_PLUGIN_NAME + ChatColor.RED + "The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("worlds." + args[1] + ".dawn", time);
            }
            if (args[0].equalsIgnoreCase("dusk")) {
                Long time;
                try {
                    time = Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    sender.sendMessage(plugin.MY_PLUGIN_NAME + ChatColor.RED + "The last argument must be a number!");
                    return false;
                }
                plugin.getConfig().set("worlds." + args[1] + ".dusk", time);
            }
            plugin.saveConfig();
            sender.sendMessage(plugin.MY_PLUGIN_NAME + "Config updated successfully");
            return true;
        }
        return false;
    }
}