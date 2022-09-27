package kz.itzHiti.clans.api;

import com.gmail.filoghost.holographicdisplays.api.*;
import kz.itzHiti.clans.*;
import net.md_5.bungee.api.*;
import java.util.*;

public class HolographicDisplayTOP
{
    private static HashMap<String, Hologram> base;

    public static void updateHolograms(final String name) {
        try {
            final Hologram holo = HolographicDisplayTOP.base.get(name);
            holo.clearLines();
            if (Manager.data.getString("holograms." + name + ".type").contains("MEMBERS")) {
                final List<Clan> list = new ArrayList<Clan>();
                for (final Clan c : ClanManager.getClans().values()) {
                    list.add(new Clan(c.getClanName(), c.getMembers().size(), c.getClanOwner(), c.getDataCreated(), c.getClanTag()));
                }
                list.sort(new Comparator<Clan>() {
                    @Override
                    public int compare(final Clan o1, final Clan o2) {
                        if (o1.getSize() > o2.getSize()) {
                            return -1;
                        }
                        if (o1.getSize() < o2.getSize()) {
                            return 1;
                        }
                        return 0;
                    }
                });
                holo.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.TITLE")));
                holo.insertTextLine(1, "");
                int x2 = 2;
                int i = 0;
                while (i < 11) {
                    if (i < list.size()) {
                        final Clan data = list.get(i);
                        holo.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE").replace("{number}", ++i + "").replace("{clanname}", data.getClanName()).replace("{admin}", data.getClanOwner()).replace("{members}", data.getSize() + "").replace("{data}", data.getDataCreated())));
                    }
                    else {
                        holo.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE_NULL").replace("{number}", ++i + "")));
                    }
                }
                holo.insertTextLine(13, "");
                holo.insertTextLine(14, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE_ADDON")));
            }
            else if (Manager.data.getString("holograms." + name + ".type").contains("BANK")) {
                final List<Clan> list = new ArrayList<Clan>();
                for (final Clan c : ClanManager.getClans().values()) {
                    list.add(new Clan(c.getClanName(), c.getClanOwner(), c.getDataCreated(), c.getBank()));
                }
                list.sort(new Comparator<Clan>() {
                    @Override
                    public int compare(final Clan o1, final Clan o2) {
                        if (o1.getBank() > o2.getBank()) {
                            return -1;
                        }
                        if (o1.getBank() < o2.getBank()) {
                            return 1;
                        }
                        return 0;
                    }
                });
                holo.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.TITLE")));
                holo.insertTextLine(1, "");
                int x2 = 2;
                int i = 0;
                while (i < 11) {
                    if (i < list.size()) {
                        final Clan data = list.get(i);
                        holo.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE").replace("{number}", ++i + "").replace("{clanname}", data.getClanName()).replace("{admin}", data.getClanOwner()).replace("{bank}", data.getBank() + "").replace("{data}", data.getDataCreated())));
                    }
                    else {
                        holo.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE_NULL").replace("{number}", ++i + "")));
                    }
                }
                holo.insertTextLine(13, "");
                holo.insertTextLine(14, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE_ADDON")));
            }
        }
        catch (Exception ex) {}
    }

    public static HashMap<String, Hologram> getBase() {
        return HolographicDisplayTOP.base;
    }

    static {
        HolographicDisplayTOP.base = new HashMap<String, Hologram>();
    }
}

