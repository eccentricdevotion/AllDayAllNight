package me.eccentric_nz.plugins.alldayallnight;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AllDayAllNight extends JavaPlugin {

    protected static AllDayAllNight plugin;
    public List<World> adanWorlds;
    public String MY_PLUGIN_NAME;
    PluginManager pm = Bukkit.getServer().getPluginManager();
    private AllDayAllNightCommands commando;
    long repeat;

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        MY_PLUGIN_NAME = "[AllDayAllNight] ";
        this.saveDefaultConfig();
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                doWorldConfig();
                adanWorlds = getWorlds();
                commando = new AllDayAllNightCommands(plugin);
                getCommand("adan").setExecutor(commando);
                repeat = getConfig().getLong("check_every");
            }
        }, 10L);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                timechk();
            }
        }, 60L, repeat);
    }

    private void timechk() {
        for (World w : adanWorlds) {
            Long now = w.getTime();
            Long dawn = getConfig().getLong("worlds." + w.getName() + ".dawn");
            Long dusk = getConfig().getLong("worlds." + w.getName() + ".dusk");
            if (now < dawn || now > dusk) {
                // set the time to dawn
                w.setTime(dawn);
            }
        }
    }

    private void doWorldConfig() {
        // add worlds
        List<World> worlds = Bukkit.getServer().getWorlds();
        for (World w : worlds) {
            String worldname = "worlds." + w.getName();
            if (!getConfig().contains(worldname)) {
                getConfig().set(worldname + ".enabled", false);
                getConfig().set(worldname + ".dawn", 0);
                getConfig().set(worldname + ".dusk", 12000);
                System.out.println(MY_PLUGIN_NAME + " Added '" + w.getName() + "' to config.");
            }
        }
        // now remove worlds that may have been deleted
        Set<String> cWorlds = getConfig().getConfigurationSection("worlds").getKeys(false);
        for (String cw : cWorlds) {
            if (getServer().getWorld(cw) == null) {
                getConfig().set("worlds." + cw, null);
                System.out.println(MY_PLUGIN_NAME + " Removed '" + cw + " from config");
            }
        }
        saveConfig();
    }

    private List<World> getWorlds() {
        List<World> list = new ArrayList<World>();
        Set<String> worlds = getConfig().getConfigurationSection("worlds").getKeys(false);
        for (String w : worlds) {
            if (Bukkit.getServer().getWorld(w) != null && getConfig().getBoolean("worlds." + w + ".enabled")) {
                System.out.println(MY_PLUGIN_NAME + "Enabling " + w + " for switching");
                list.add(getServer().getWorld(w));
            }
        }
        return list;
    }
}
