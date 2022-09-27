package kz.itzHiti.clans.commands;

import org.bukkit.command.*;
import kz.itzHiti.clans.*;
import org.bukkit.*;
import kz.itzHiti.clans.api.*;
import java.util.*;

public class ClanChat implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String l, final String[] args) {
        final Clan clan = ClanManager.getClanfromPlayer(sender.getName());
        if (clan == null) {
            sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(Manager.getInstance().getMSG("CLANCHAT_USAGE"));
            return false;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            sb.append(args[i]).append(" ");
        }
        for (final Member member : clan.getMembers()) {
            if (Bukkit.getPlayer(member.getName()) != null) {
                if (new Member(sender.getName()).isModer()) {
                    Bukkit.getPlayer(member.getName()).sendMessage(Manager.getInstance().getMSG("FORMAT_CHAT.MODER_CLAN").replace("{player}", sender.getName()).replace("{message}", ChatColor.stripColor(sb.toString())));
                    return false;
                }
                if (new Member(sender.getName()).isOwner()) {
                    Bukkit.getPlayer(member.getName()).sendMessage(Manager.getInstance().getMSG("FORMAT_CHAT.OWNER_CLAN").replace("{player}", sender.getName()).replace("{message}", ChatColor.stripColor(sb.toString())));
                    return false;
                }
                Bukkit.getPlayer(member.getName()).sendMessage(Manager.getInstance().getMSG("FORMAT_CHAT.MEMBER_CLAN").replace("{player}", sender.getName()).replace("{message}", ChatColor.stripColor(sb.toString())));
            }
        }
        return false;
    }
}

