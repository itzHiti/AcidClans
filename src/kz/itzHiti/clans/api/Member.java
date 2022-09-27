package kz.itzHiti.clans.api;

import kz.itzHiti.clans.*;
import java.util.*;

public class Member
{
    private String playerName;

    public Member(final String playerName) {
        this.playerName = playerName;
    }

    public boolean isClan() {
        for (final String s : Manager.data.getConfigurationSection("clans").getKeys(false)) {
            if (Manager.data.getStringList("clans." + s + ".members.").contains(this.playerName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwner() {
        for (final Clan c : ClanManager.getClans().values()) {
            if (this.playerName.toLowerCase().equalsIgnoreCase(c.getClanOwner().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isModer() {
        for (final Clan c : ClanManager.getClans().values()) {
            for (final String s : c.getModers()) {
                if (s.toLowerCase().equalsIgnoreCase(this.playerName.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getName() {
        return this.playerName;
    }
}

