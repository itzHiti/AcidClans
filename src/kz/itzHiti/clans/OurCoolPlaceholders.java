package kz.itzHiti.clans;

import me.clip.placeholderapi.expansion.*;
import org.bukkit.entity.*;
import net.md_5.bungee.api.*;
import kz.itzHiti.clans.api.*;

public class OurCoolPlaceholders extends PlaceholderExpansion
{
    public String getIdentifier() {
        return "clan";
    }

    public String getPlugin() {
        return null;
    }

    public String getAuthor() {
        return "itzHiti";
    }

    public String getVersion() {
        return "v1.3";
    }

    public String onPlaceholderRequest(final Player p, final String identifier) { //Запрашиваем наш плейсхолдер
        if (p == null) {
            return "";
        }
        final Clan clan = ClanManager.getClanfromPlayer(p.getName().toLowerCase());
        if (clan == null) {
            return "";
        }
        final String clanname = ChatColor.translateAlternateColorCodes('&', Manager.config.getString("SETTINGS.CLAN_TAG").replace("{clanname}", clan.getClanTag()));
        final String clanname_tab = ChatColor.translateAlternateColorCodes('&', Manager.config.getString("SETTINGS.CLAN_TAG_TAB").replace("{clanname}", clan.getClanTag()));
        if (identifier.equals("tag_tab")) { //Проверка если это клан-тег в табе
            return clanname_tab;
        }
        if (identifier.equals("tag")) { //Проверка если это клан тег над головой
            return clanname;
        }
        return null;
    }
}