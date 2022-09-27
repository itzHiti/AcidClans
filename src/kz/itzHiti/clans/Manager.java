/*
AcidClans v1.3
Plugin created for private use and only for server AcidMine (https://vk.com/acidmine_fun)
Author's VK: https://vk.com/hitioff
If you want to crack or decompile this plugin you are bad person.
2021
 */

package kz.itzHiti.clans;

import kz.itzHiti.clans.api.Member;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import net.milkbowl.vault.economy.*;
import net.milkbowl.vault.permission.*;
import com.nametagedit.plugin.*;
import kz.itzHiti.clans.mysql.*;
import org.bukkit.command.*;
import kz.itzHiti.clans.commands.*;
import org.bukkit.event.*;
import kz.itzHiti.clans.listeners.*;
import org.bukkit.plugin.*;
import net.md_5.bungee.api.*;
import org.bukkit.*;
import java.sql.*;
import kz.itzHiti.clans.api.*;
import com.gmail.filoghost.holographicdisplays.api.*;
import org.bukkit.entity.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class Manager extends JavaPlugin
{
    public static FileConfiguration data;
    private static Manager inst;
    public static FileConfiguration config;
    static Economy econ;
    public static Permission perm;
    public static NametagEdit in;
    public static MySQL mysql;

    public static boolean isMySQLEnabled() {
        return getInstance().getConfig().getBoolean("MySQL.Enable");
    }

    public void onEnable() {
        String encryption_key = this.getConfig().getString("Activation.Key");
        (Manager.inst = this).saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage("[" + getInstance().getName() + "] Loading database..");
        Manager.data = bC.get("data.yml");
        Manager.config = bC.get("config.yml");
        if (isMySQLEnabled()) {
            Manager.mysql = new MySQL(Manager.config.getString("MySQL.Host"), Manager.config.getString("MySQL.UserName"), Manager.config.getInt("MySQL.Port"), Manager.config.getString("MySQL.Password"), Manager.config.getString("MySQL.DataBase"));
        }
        Bukkit.getConsoleSender().sendMessage("[" + getInstance().getName() + "] Database loaded!");
        Bukkit.getConsoleSender().sendMessage("[" + getInstance().getName() + "] Type database: " + (isMySQLEnabled() ? "MySQL" : "YAML FILE"));
        this.loadClans();
        Manager.perm = hookPermissions();
        this.getCommand("clanchat").setExecutor((CommandExecutor)new ClanChat());
        this.getCommand("ch").setExecutor((CommandExecutor)new ClanHome());
        this.getCommand("clan").setExecutor((CommandExecutor)new ClanCommands());
        Bukkit.getPluginManager().registerEvents((Listener)new WelcomeJoin(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ChatEnter(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ClansGUI(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new EXPManager(this), (Plugin)this);
        this.sh4();
        this.sh();
        this.sh2();
        final NametagEdit nte = Manager.in = (NametagEdit)Bukkit.getPluginManager().getPlugin("NametagEdit");
        this.setupEconomy();
        new OurCoolPlaceholders().register();
        getLogger().info("Searching for encryption key...");
        if (!encryption_key.equals("RNIagxbCtaCSDJczNkj4KjbXOcI7mxeD")) {
            getLogger().warning("Encryption key was not found. Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        else {
            getLogger().info("Encryption key has been found!");
        }
    }

    public void onDisable() {
        for (final Clan clan : ClanManager.getClans().values()) {
            clan.update();
        }
        if (isMySQLEnabled()) {
            Manager.mysql.disconnect();
        }
    }

    private static Permission hookPermissions() {
        Permission permission = null;
        final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>)Bukkit.getServicesManager().getRegistration((Class)Permission.class);
        if (permissionProvider != null) {
            permission = (Permission)permissionProvider.getProvider();
        }
        if (permission != null && permission.getName().equals("SuperPerms")) {
            permission = null;
        }
        return permission;
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        Manager.econ = (Economy)rsp.getProvider();
        return Manager.econ != null;
    }

    public Economy getEconomy() {
        return Manager.econ;
    }

    public String getMSG(final String path) {
        return ChatColor.translateAlternateColorCodes('&', Manager.config.getString("MESSAGES." + path).replace("{new}", "\n"));
    }

    public static Manager getInstance() {
        return Manager.inst;
    }

    public void loadClans() {
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            try {
                if (isMySQLEnabled()) {
                    final ResultSet result = Manager.mysql.getConnection().createStatement().executeQuery("SELECT * FROM `AcidClans`");
                    while (result.next()) {
                        ArrayList<String> lm = ListUtil.toList(result.getString("moders"));
                        ArrayList<Member> mb = new ArrayList<Member>();
                        final Iterator<String> iterator = ListUtil.toList(result.getString("members")).iterator();
                        while (iterator.hasNext()) {
                           String f = iterator.next();
                            mb.add(new Member(f));
                        }
                        Location loc = LocationUtil.toLocation(result.getString("home"));
                        ClanManager.getClans().put(result.getString("clanname"), new Clan(result.getString("clanname"), result.getString("owner"), result.getString("welcome"), result.getString("data"), (result.getString("clantag")), mb, lm, result.getInt("bank"), loc, result.getInt("level"), result.getInt("exp"), result.getInt("mexp"), result.getBoolean("homestatus")));
                    }
                }
                else {
                    final Iterator<String> iterator2 = Manager.data.getConfigurationSection("clans").getKeys(false).iterator();
                    while (iterator2.hasNext()) {
                        String s = iterator2.next();
                        String ct = Manager.data.getString("clans." + s + ".clantag");
                        String owner = Manager.data.getString("clans." + s + ".owner");
                        ArrayList lm2 = (ArrayList)Manager.data.getStringList("clans." + s + ".members");
                        ArrayList<Member> mb2 = new ArrayList<Member>();
                        final Iterator<String> iterator3 = lm2.iterator();
                        while (iterator3.hasNext()) {
                            String f2 = iterator3.next();
                            mb2.add(new Member(f2));
                        }
                        int bank = Manager.data.getInt("clans." + s + ".bank");
                        int level = Manager.data.getInt("clans." + s + ".level");
                        int exp = Manager.data.getInt("clans." + s + ".exp");
                        int mexp = Manager.data.getInt("clans." + s + ".mexp");
                        boolean homestatus = Manager.data.getBoolean("clans." + s + ".homestatus");
                        ArrayList ls = (ArrayList)Manager.data.getStringList("clans." + s + ".moders");
                        String welcome = Manager.data.getString("clans." + s + ".welcome");
                        String data = Manager.data.getString("clans." + s + ".data");
                        if (Manager.data.getString("clans." + s + ".home.world") != null) {
                            String world = Manager.data.getString("clans." + s + ".home.world");
                            double x = Manager.data.getDouble("clans." + s + ".home.x");
                            double y = Manager.data.getDouble("clans." + s + ".home.y");
                            double z = Manager.data.getDouble("clans." + s + ".home.z");
                            float float1 = Manager.data.getInt("clans." + s + ".home.yaw");
                            float float2 = Manager.data.getInt("clans." + s + ".home.pitch");
                            Location loc2 = new Location(Bukkit.getWorld(world), x, y, z, float2, float1);
                            ClanManager.getClans().put(s.replace("&", "§"), new Clan(s.replace("&", "§"), owner, welcome, data, ct.replace("&", "§"), mb2, ls, bank, loc2, level, exp, mexp, homestatus));
                        }
                        else {
                            ClanManager.getClans().put(s.replace("&", "§"), new Clan(s.replace("&", "§"), owner, welcome, data, ct.replace("&", "§"), mb2, ls, bank, null, level, exp, mexp, homestatus));
                        }
                    }
                }
            }
            catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }, 50L);
    }

    public void sh4() {
        Bukkit.getServer().getScheduler().runTaskLater((Plugin)this, () -> {
            final Iterator<String> iterator = Manager.data.getConfigurationSection("holograms").getKeys(false).iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                double x = Manager.data.getDouble("holograms." + name + ".location.x");
                double y = Manager.data.getDouble("holograms." + name + ".location.y");
                double z = Manager.data.getDouble("holograms." + name + ".location.z");
                final Location location = new Location(Bukkit.getWorld(Manager.data.getString("holograms." + name + ".location.world")), x, y, z);
                Location loc = location;
                Hologram holo = HologramsAPI.createHologram((Plugin)getInstance(), loc);
                System.out.println("" + loc.getX() + " " + loc.getWorld() + " " + loc.getY() + " " + loc.getZ());
                if (Manager.data.getString("holograms." + name + ".type").contains("MEMBERS")) {
                    ArrayList<Clan> list = new ArrayList<Clan>();
                    final Iterator<Clan> iterator2 = ClanManager.getClans().values().iterator();
                    while (iterator2.hasNext()) {
                        Clan c = iterator2.next();
                        list.add(new Clan(c.getClanName(), c.getMembers().size(), c.getClanOwner(), c.getDataCreated(), c.getClanTag()));
                    }
                    list.sort((o1, o2) -> {
                        if (o1.getSize() > o2.getSize()) {
                            return -1;
                        }
                        else if (o1.getSize() < o2.getSize()) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    });
                    holo.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.TITLE")));
                    holo.insertTextLine(1, "");
                    int x2 = 2;
                    int i = 0;
                    Hologram hologram = HologramsAPI.createHologram((Plugin)getInstance(), loc);
                    Hologram hologram2 = HologramsAPI.createHologram((Plugin)getInstance(), loc);
                    while (i < 10) {
                        if (i < list.size()) {
                            Clan data = (Clan)list.get(i);
                            final int n = x2++;
                            hologram.insertTextLine(n, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE").replace("{number}", ++i + "").replace("{clanname}", data.getClanName()).replace("{admin}", data.getClanOwner()).replace("{members}", data.getSize() + "").replace("{data}", data.getDataCreated())));
                        }
                        else {
                            final int n2 = x2++;
                            hologram2.insertTextLine(n2, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE_NULL").replace("{number}", ++i + "")));
                        }
                    }
                    holo.insertTextLine(12, "");
                    holo.insertTextLine(13, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE_ADDON")));
                    HolographicDisplayTOP.getBase().put(name.toLowerCase(), holo);
                }
                else if (Manager.data.getString("holograms." + name + ".type").contains("BANK")) {
                    ArrayList<Clan> list2 = new ArrayList<Clan>();
                    final Iterator<Clan> iterator3 = ClanManager.getClans().values().iterator();
                    while (iterator3.hasNext()) {
                        Clan c2 = iterator3.next();
                        list2.add(new Clan(c2.getClanName(), c2.getClanOwner(), c2.getDataCreated(), c2.getBank()));
                    }
                    list2.sort((o1, o2) -> {
                        if (o1.getBank() > o2.getBank()) {
                            return -1;
                        }
                        else if (o1.getBank() < o2.getBank()) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    });
                    holo.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.TITLE")));
                    holo.insertTextLine(1, "");
                    int x3 = 2;
                    int j = 0;
                    Hologram hologram3 = HologramsAPI.createHologram((Plugin)getInstance(), loc);
                    Hologram hologram4 = HologramsAPI.createHologram((Plugin)getInstance(), loc);
                    while (j < 10) {
                        if (j < list2.size()) {
                            Clan data2 = (Clan)list2.get(j);
                            int n3 = x3++;
                            hologram3.insertTextLine(n3, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE").replace("{number}", ++j + "").replace("{clanname}", data2.getClanName()).replace("{admin}", data2.getClanOwner()).replace("{bank}", data2.getBank() + "").replace("{data}", data2.getDataCreated())));
                        }
                        else {
                            int n4 = x3++;
                            hologram4.insertTextLine(n4, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE_NULL").replace("{number}", ++j + "")));
                        }
                    }
                    holo.insertTextLine(12, "");
                    holo.insertTextLine(13, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE_ADDON")));
                    HolographicDisplayTOP.getBase().put(name.toLowerCase(), holo);
                }
                else {
                    continue;
                }
            }
        }, 50L);
    }

    public void sh() {
        Bukkit.getServer().getScheduler().runTaskTimer((Plugin)this, () -> {
            final Iterator<String> iterator = Manager.data.getConfigurationSection("holograms").getKeys(false).iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                HolographicDisplayTOP.updateHolograms(s);
            }
        }, 0L, Manager.config.getInt("SETTINGS.HOLO_TOP_UPDATE") * 20L * 60L);
    }

    public boolean checkString(final String allow, final String check) {
        for (final char c : check.toLowerCase().toCharArray()) {
            if (!allow.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    public int getIntColors(final String key) {
        return key.split("&").length - 1;
    }

    public void sh2() {
        Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            final ArrayList<Request> toDelete = new ArrayList<Request>();
            final Iterator<Request> iterator = Request.requests.iterator();
            while (iterator.hasNext()) {
                Request r = iterator.next();
                if (System.currentTimeMillis() - r.getTime() >= 15000L) {
                    toDelete.add(r);
                }
            }
            final Iterator<Request> iterator2 = toDelete.iterator();
            while (iterator2.hasNext()) {
                Request r2 = iterator2.next();
                Player player1 = Bukkit.getPlayer(r2.getPlayer1());
                if (player1 != null) {
                    player1.sendMessage(getInstance().getMSG("REQUEST_DENY"));
                }
                Player player2 = Bukkit.getPlayer(r2.getPlayer2());
                if (player2 != null) {
                    player2.sendMessage(getInstance().getMSG("REQUEST_DENY_YOU"));
                }
                r2.remove();
            }
        }, 0L, 20L);
    }
}
