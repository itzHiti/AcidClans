package kz.itzHiti.clans.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.*;
import kz.itzHiti.clans.*;
import kz.itzHiti.clans.api.*;
import net.md_5.bungee.api.*;
import org.bukkit.*;
import java.util.*;

public class ClanHome implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String l, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Manager.getInstance().getMSG("CLANHOME_USAGE"));
            return false;
        }
        if (!Manager.getInstance().getConfig().getBoolean("SETTINGS.SERVERS_WITH_ALLOW_A_SETHOME_ENABLE")) {
            sender.sendMessage(Manager.getInstance().getMSG("NOT_SETHOME_ALLOW_SERVER"));
            return true;
        }
        for (final Clan c : ClanManager.getClans().values()) {
            if (ChatColor.stripColor(args[0]).toLowerCase().equalsIgnoreCase(ChatColor.stripColor(c.getClanName()).toLowerCase())) {
                if (c.getLocation() == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLANHOME_NULL"));
                    return false;
                }
                if (c.getHomeStatus() == false) {
                    sender.sendMessage(Manager.getInstance().getMSG("TELEPORT_CLAN_HOME_PRIVATE"));
                    return false;
                }
                Bukkit.getPlayerExact(sender.getName()).teleport(c.getLocation());
                sender.sendMessage(Manager.getInstance().getMSG("TELEPORT_CLANHOME"));
                return false;
            }
        }
        sender.sendMessage(Manager.getInstance().getMSG("CLAN_NULL"));
        return false;
    }
}
