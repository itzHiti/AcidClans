package kz.itzHiti.clans.listeners;

import org.bukkit.event.player.*;
import kz.itzHiti.clans.*;
import kz.itzHiti.clans.api.*;
import org.bukkit.event.*;
import com.nametagedit.plugin.api.events.*;
import com.nametagedit.plugin.api.*;

public class WelcomeJoin implements Listener
{
    @EventHandler
    public void motd(final PlayerJoinEvent e) {
        final Clan c = ClanManager.getClanfromPlayer(e.getPlayer().getName().toLowerCase());
        if (c == null) {
            return;
        }
        if (c.getWelcome().isEmpty()) {
            return;
        }
        e.getPlayer().sendMessage(Manager.getInstance().getMSG("WELCOME_CLAN_JOIN").replace("{clanname}", c.getClanName().replace("&", "§")).replace("{welcome}", c.getWelcome().replace("&", "§")));
    }

    @EventHandler
    public void onNametagEvent(final NametagEvent event) {
        if (event.getChangeType() == NametagEvent.ChangeType.SUFFIX) {
            final Clan clan = ClanManager.getClanfromPlayer(event.getPlayer().toLowerCase());
            new NametagAPI(Manager.in.getHandler(), Manager.in.getManager()).setSuffix(event.getPlayer(), (clan != null) ? (" " + clan.getClanName()) : " ");
        }
    }
}
