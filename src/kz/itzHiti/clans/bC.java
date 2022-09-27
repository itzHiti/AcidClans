package kz.itzHiti.clans;

import org.bukkit.configuration.file.*;
import java.io.*;

public class bC
{
    public static FileConfiguration get(final String name) {
        final File f = new File(Manager.getInstance().getDataFolder(), name);
        if (Manager.getInstance().getResource(name) == null) {
            return save((FileConfiguration)YamlConfiguration.loadConfiguration(f), name);
        }
        if (!f.exists()) {
            Manager.getInstance().saveResource(name, false);
        }
        return (FileConfiguration)YamlConfiguration.loadConfiguration(f);
    }

    public static FileConfiguration save(final FileConfiguration config, final String name) {
        try {
            config.save(new File(Manager.getInstance().getDataFolder(), name));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}
