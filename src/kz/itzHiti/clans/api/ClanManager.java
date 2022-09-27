package kz.itzHiti.clans.api;

import kz.itzHiti.clans.*;
import java.util.*;
import org.bukkit.*;

public class ClanManager
{
    private static HashMap<String, Clan> clans;

    public static Clan createClan(final String clanName, final String clanOwner, final String welcome, final String data, final String clanTag, final ArrayList<Member> members, final ArrayList<String> moders, final int bank, final Location loc, final int level, final int exp, final int mexp, final boolean homestatus) {
        ClanManager.clans.put(clanName, new Clan(clanName, clanOwner, welcome, data, clanTag, members, moders, bank, loc, level, exp, mexp, homestatus));
        return new Clan(clanName, clanOwner, welcome, data, clanTag, members, moders, bank, loc, level, exp, mexp, homestatus);
    }

    public static Clan getClan(final String a) {
        return ClanManager.clans.get(a.toLowerCase());
    }

    public static void deleteClan(final Clan clan) {
        if (Manager.isMySQLEnabled()) {
            Manager.mysql.execute("DELETE FROM `AcidClans` WHERE `clanname`='" + clan.getClanName().replace("§", "&") + "'");
        }
        else {
            Manager.data.set("clans." + clan.getClanName().replace("§", "&"), (Object)null);
            bC.save(Manager.data, "data.yml");
        }
        ClanManager.clans.remove(clan.getClanName());
        for (final Member m : clan.getMembers()) {
            final OfflinePlayer p = Bukkit.getOfflinePlayer(m.getName());
            p.isOnline();
        }
    }

    public static Clan getClanfromPlayer(final String player) {
        for (final Clan clan : ClanManager.clans.values()) {
            for (final Member member : clan.getMembers()) {
                if (member.getName().equalsIgnoreCase(player.toLowerCase())) {
                    return clan;
                }
            }
        }
        return null;
    }

    public static void setMotd(final Clan c, final String text) {
        createClan(c.getClanName(), c.getClanOwner(), text, c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void leave(final String playerName, final Clan c) {
        final ArrayList<Member> lm = c.getMembers();
        Member m = null;
        for (final Member member : lm) {
            if (member.getName().toLowerCase().equalsIgnoreCase(playerName)) {
                m = member;
            }
        }
        lm.remove(m);
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), lm, c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void revModer(final String playerName, final Clan c) {
        final ArrayList<String> lm = c.getModers();
        lm.remove(playerName.toLowerCase());
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), lm, c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void despositBank(final int bank, final Clan c) {
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank() + bank, c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void withdrawBank(final int bank, final Clan c) {
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank() - bank, c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void addModer(final String playerName, final Clan c) {
        final ArrayList<String> lm = c.getModers();
        lm.add(playerName.toLowerCase());
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), lm, c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void invite(final String playerName, final Clan c) {
        final ArrayList<Member> list = c.getMembers();
        list.add(new Member(playerName));
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), list, c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void editName(final String name, final Clan c) {
        if (Manager.isMySQLEnabled()) {
            Manager.mysql.execute("UPDATE `AcidClans` SET `clanname`='" + name + "' WHERE `clanname`='" + c.getClanName() + "'");
        }
        else {
            Manager.data.set("clans." + c.getClanName().replace("§", "&"), (Object)null);
            bC.save(Manager.data, "data.yml");
        }
        ClanManager.clans.remove(c.getClanName());
        createClan(name, c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void editTag(final String tag, final Clan c) {
        if (Manager.isMySQLEnabled()) {
            Manager.mysql.execute("UPDATE `AcidClans` SET `clantag`='" + tag + "' WHERE `clanname`='" + c.getClanName() + "'");
        }
        else {
            Manager.data.set("clans." + c.getClanName().replace("§", "&"), (Object)null);
            bC.save(Manager.data, "data.yml");
        }
        ClanManager.clans.remove(c.getClanTag());
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), tag, c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void setHome(final Location loc, final Clan c) {
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), loc, c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void delHome(final Clan c) {
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), null, c.getLevel(), c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void getLevel(final int level, final Clan c) {
        if (Manager.isMySQLEnabled()) {
            Manager.mysql.execute("UPDATE `AcidClans` SET `level`='" + level + "' WHERE `clanname`='" + c.getClanName() + "'");
        }
        else {
            Manager.data.set("clans." + c.getClanName().replace("§", "&"), (Object)null);
            bC.save(Manager.data, "data.yml");
        }
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel() + level, c.getEXP(), c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void getEXP(final int exp, final int xp, final Clan c) {
        if (Manager.isMySQLEnabled()) {
            Manager.mysql.execute("UPDATE `AcidClans` SET `exp`='" + exp + "' WHERE `clanname`='" + c.getClanName() + "'");
        }
        else {
            Manager.data.set("clans." + c.getClanName().replace("§", "&"), (Object)null);
            bC.save(Manager.data, "data.yml");
        }
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), (c.getEXP() + exp) * xp, c.getMaxEXP(), c.getHomeStatus()).update();
    }

    public static void getMaxEXP(final int mexp, final Clan c) {
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP() + mexp, c.getHomeStatus()).update();
    }

    public static void getHomeStatus(final boolean homestatus, final Clan c){
        createClan(c.getClanName(), c.getClanOwner(), c.getWelcome(), c.getDataCreated(), c.getClanTag(), c.getMembers(), c.getModers(), c.getBank(), c.getLocation(), c.getLevel(), c.getEXP(), c.getMaxEXP(), homestatus).update();
    }

    public static HashMap<String, Clan> getClans() {
        return ClanManager.clans;
    }

    static {
        ClanManager.clans = new HashMap<String, Clan>();
    }
}

