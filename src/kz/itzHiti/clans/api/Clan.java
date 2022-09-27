package kz.itzHiti.clans.api;

import org.bukkit.*;
import java.sql.*;
import kz.itzHiti.clans.*;
import java.util.*;

public class Clan
{
    private String clanName;
    private String clanOwner;
    private String welcome;
    private String data;
    private String clanTag;
    private ArrayList<Member> members;
    private ArrayList<String> moders;
    private int size;
    private int bank;
    private int level;
    private int exp;
    private int mexp;
    private Location loc;
    private boolean homestatus;

    public Clan(final String clanName, final String clanOwner, final String welcome, final String data, final String clanTag, final ArrayList<Member> members, final ArrayList<String> moders, final int bank, final Location loc, final int level, final int exp, final int mexp, final boolean homestatus) {
        this.clanName = clanName;
        this.clanOwner = clanOwner;
        this.welcome = welcome;
        this.data = data;
        this.clanTag = clanTag;
        this.members = members;
        this.moders = moders;
        this.bank = bank;
        this.loc = loc;
        this.level = level;
        this.exp = exp;
        this.mexp = mexp;
        this.homestatus = homestatus;
    }

    public Clan(final String clanName, final int size, final String clanOwner, final String data, final String clanTag) {
        this.clanName = clanName;
        this.size = size;
        this.clanOwner = clanOwner;
        this.data = data;
        this.clanTag = clanTag;
    }

    public Clan(final String clanName, final String clanOwner, final String data, final int bank) {
        this.clanName = clanName;
        this.bank = bank;
        this.clanOwner = clanOwner;
        this.data = data;
    }

    public int getBank() {
        return this.bank;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEXP() {
        return this.exp;
    }

    public int getMaxEXP() {
        return this.mexp;
    }

    public boolean getHomeStatus(){
        return this.homestatus;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setEXP(final int exp) {
        this.exp = exp;
    }

    public void setMaxEXP(final int maxEXP) {
        this.mexp = maxEXP;
    }

    public int getSize() {
        return this.size;
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getClanName() {
        return this.clanName;
    }

    public String getWelcome() {
        return this.welcome;
    }

    public String getClanOwner() {
        return this.clanOwner;
    }

    public ArrayList<Member> getMembers() {
        return this.members;
    }

    public ArrayList<String> getModers() {
        return this.moders;
    }

    public String getDataCreated() {
        return this.data;
    }

    public String getClanTag() {
        return this.clanTag;
    }

    public void update() {
        final ArrayList<String> ms = new ArrayList<String>();
        for (final Member member : this.members) {
            ms.add(member.getName().toLowerCase());
        }
        if (Manager.isMySQLEnabled()) {
            try {
                if (!Manager.mysql.getConnection().createStatement().executeQuery("SELECT * FROM `AcidClans` WHERE `clanname`='" + this.getClanName() + "'").next()) {
                    Manager.mysql.execute("INSERT INTO `AcidClans` (`clanname`, `owner`, `welcome`, `data`, `clantag`, `members`, `moders`, `bank`, `home`, `level`, `exp`, `mexp`) VALUES ('" + this.clanName + "','" + this.clanOwner + "','" + this.welcome + "','" + this.data + "','" + ms + "','" + this.moders + "','" + this.bank + "','" + LocationUtil.fromLocation(this.loc) + "','" + this.level + "','" + this.exp + "','" + this.mexp + "')");
                }
                else {
                    Manager.mysql.execute("UPDATE `AcidClans` SET `owner`='" + this.clanOwner + "', `welcome`='" + this.welcome + "', `data`='" + this.data + "', `clantag`='" + this.clanTag + "', `members`='" + ms + "', `moders`='" + this.moders + "', `bank`='" + this.bank + "', `home`='" + LocationUtil.fromLocation(this.loc) + "', `level`='" + this.level + "', `exp`='" + this.exp + "', `mexp`='" + this.mexp + "' WHERE `clanname`='" + this.clanName + "'");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            final String s = this.clanName.replace("В§", "&");
            Manager.data.set("clans." + s + ".owner", (Object)this.clanOwner);
            Manager.data.set("clans." + s + ".members", (Object)ms);
            Manager.data.set("clans." + s + ".moders", (Object)this.moders);
            Manager.data.set("clans." + s + ".welcome", (Object)this.welcome);
            Manager.data.set("clans." + s + ".bank", (Object)this.bank);
            Manager.data.set("clans." + s + ".level", (Object)this.level);
            Manager.data.set("clans." + s + ".exp", (Object)this.exp);
            Manager.data.set("clans." + s + ".mexp", (Object)this.mexp);
            Manager.data.set("clans." + s + ".data", (Object)this.data);
            Manager.data.set("clans." + s + ".clantag", (Object)this.clanTag);
            Manager.data.set("clans." + s + ".homestatus", (Object)this.homestatus);
            bC.save(Manager.data, "data.yml");
            if (this.loc != null) {
                Manager.data.set("clans." + s + ".home.x", (Object)this.loc.getX());
                Manager.data.set("clans." + s + ".home.y", (Object)this.loc.getY());
                Manager.data.set("clans." + s + ".home.z", (Object)this.loc.getZ());
                Manager.data.set("clans." + s + ".home.world", (Object)this.loc.getWorld().getName());
                Manager.data.set("clans." + s + ".home.yaw", (Object)this.loc.getYaw());
                Manager.data.set("clans." + s + ".home.pitch", (Object)this.loc.getPitch());
                bC.save(Manager.data, "data.yml");
            }
        }
    }
}
