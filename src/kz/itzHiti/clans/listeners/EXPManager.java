package kz.itzHiti.clans.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import kz.itzHiti.clans.*;
import kz.itzHiti.clans.api.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class EXPManager implements Listener {

    private Manager plugin;

    public EXPManager(Manager main) {
        this.plugin = main;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        final Player player = e.getEntity().getKiller();
        if (player != null) {
            final Clan clan = ClanManager.getClanfromPlayer(player.getName());
            if (clan != null && !(e.getEntity() instanceof Player)) {
                final int random = 1 + (int) (Math.random() * 5.0);
                final String clanName = ChatColor.translateAlternateColorCodes('&', clan.getClanName());
                int clanExp = Manager.data.getInt("clans." + clanName + ".exp") + random;
                clanExp += (int) (Math.random() * 5.0);
                final FileConfiguration data = Manager.data;
                data.set("clans." + clanName + ".exp", (Object) clanExp);
                int levl = data.getInt("clans." + clanName + ".level");
                int mexp = data.getInt("clans." + clanName + ".mexp");
                if (clanExp >= data.getInt("clans." + clanName + ".mexp")) {
                    data.set("clans." + clanName + ".mexp", (Object) (data.getInt("clans." + clanName + ".mexp") + 500));
                    data.set("clans." + clanName + ".level", (Object) (data.getInt("clans." + clanName + ".level") + 1));
                    data.set("clans." + clanName + ".exp", (Object) 0);
                    player.sendMessage(Manager.getInstance().getMSG("CLAN_LEVEL_UP").replace("{level}", new StringBuilder().append(data.getInt("clans." + clanName + ".level")).toString()));
                    clanExp = 0;
                    ++levl;
                    mexp += 500;
                } else {
                    data.set("clans." + clanName + ".exp", (Object) clanExp);
                }
                clan.setEXP(clanExp);
                clan.setLevel(levl);
                clan.setMaxEXP(mexp);
                clan.update();
                bC.save(data, "data.yml");
            }

        }
    }
}