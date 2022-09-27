package kz.itzHiti.clans.listeners;

import org.bukkit.event.player.*;
import kz.itzHiti.clans.*;
import net.md_5.bungee.api.*;
import kz.itzHiti.clans.api.*;
import org.bukkit.event.*;

public class ChatEnter implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        final Clan clan = ClanManager.getClanfromPlayer(e.getPlayer().getName().toLowerCase());
        if (e.getFormat().contains("!clan_tag_chat!")) {
            if (clan == null) {
                e.setFormat(e.getFormat().replace("!clan_tag_chat!", ""));
            }
            else {
                final String clanname = Manager.config.getString("SETTINGS.CLAN_TAG_CHAT").replace("{clanname}", clan.getClanName());
                e.setFormat(e.getFormat().replace("!clan_tag_chat!", ChatColor.translateAlternateColorCodes('&', clanname)));
            }
        }
    }
}
