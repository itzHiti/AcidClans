package kz.itzHiti.clans.commands;

import org.bukkit.command.*;
import java.text.*;
import org.bukkit.*;
import net.md_5.bungee.api.chat.*;
import kz.itzHiti.clans.api.*;
import kz.itzHiti.clans.*;
import com.gmail.filoghost.holographicdisplays.api.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;

public class ClanCommands implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String l, final String[] args) {
        Player pl = (Player)sender;
        final Member member = new Member(sender.getName());
        final Clan clan = ClanManager.getClanfromPlayer(sender.getName().toLowerCase());
        if (args.length == 0) {
            if (member.isOwner()) {
                sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN_OWNER"));
            }
            else if (member.isModer()) {
                sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN_MODER"));
            }
            else if (clan != null) {
                sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN_MEMBER"));
            }
            else {
                sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN"));
            }
            return false;
        }
        final String lowerCase = args[0].toLowerCase();
        switch (lowerCase) {
            case "create": {
                if (clan != null) {
                    sender.sendMessage(Manager.getInstance().getMSG("IN_THE_CLAN"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("CREATE_USAGE"));
                    return false;
                }
                if (Manager.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(sender.getName())) < Manager.config.getInt("SETTINGS.CLAN_CREATE_MONEY")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_MONEY_CREATE").replace("{money}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLAN_CREATE_MONEY")).toString()));
                    return false;
                }
                if (args[1].replace("&", "§").length() < Manager.config.getInt("SETTINGS.CLAN_MIN_LENGTH")) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_MIN_LENGTH").replace("{min-length}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLAN_MIN_LENGTH")).toString()));
                    return false;
                }
                if (args[1].replace("&", "§").length() > Manager.config.getInt("SETTINGS.CLAN_MAX_LENGTH")) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_MAX_LENGTH").replace("{max-length}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLAN_MAX_LENGTH")).toString()));
                    return false;
                }
                if (Manager.config.getBoolean("SETTINGS.ONE_CODE_SYMBOL") && Manager.getInstance().getIntColors(args[1]) > 1) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_ONE_CODE_SYMBOL_CREATE"));
                    return false;
                }
                for (final Clan c : ClanManager.getClans().values()) {
                    if (ChatColor.stripColor(c.getClanName().toLowerCase()).equalsIgnoreCase(ChatColor.stripColor(args[1].toLowerCase()))) {
                        sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_EXIT"));
                        return false;
                    }
                }
                if (ChatColor.stripColor(args[1].replace("&", "§")).isEmpty()) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_NAME"));
                    return false;
                }
                if (Manager.getInstance().checkString("????????????????????????????????abcdefghijklmnopqrstuvwxyz_0123456789", ChatColor.stripColor(args[1].replace("&", "§").toLowerCase()))) {
                    sender.sendMessage(Manager.getInstance().getMSG("BLOCK_SYMBOL"));
                    return false;
                }
                final ArrayList<Member> l2 = new ArrayList<Member>();
                final ArrayList<String> l3 = new ArrayList<String>();
                l2.add(member);
                final Date date = new Date();
                final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                ClanManager.createClan(args[1], sender.getName().toLowerCase(), "", format.format(date), "" , l2, l3, 0, null, 0, 0, 500, true).update();
                Manager.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(sender.getName()), (double)Double.valueOf(Manager.config.getInt("SETTINGS.CLAN_CREATE_MONEY")));
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_CREATED").replace("{clanname}", args[1].replace("&", "§")));
                break;
            }
            case "disband": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_DISBAND"));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("FOR_OWNER_DISBANDED").replace("{clanname}", clan.getClanName().replace("&", "§")));
                for (final Member mem : clan.getMembers()) {
                    final Player p = Bukkit.getPlayer(mem.getName());
                    if (p != null && !sender.getName().toLowerCase().equalsIgnoreCase(mem.getName().toLowerCase())) {
                        p.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_DISBANDED").replace("{owner}", sender.getName()));
                    }
                }
                ClanManager.deleteClan(clan);
                break;
            }
            case "info": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                final StringBuffer bw1 = new StringBuffer();
                String st = "";
                if (!Manager.data.getStringList("clans." + clan.getClanName() + ".moders").isEmpty()) {
                    for (final String s : clan.getModers()) {
                        bw1.append(s + ", ");
                    }
                    st = bw1.substring(0, bw1.length() - 2);
                }
                if (st.isEmpty()) {
                    st = "§cНету";
                }
                String st2 = "";
                final StringBuffer bw2 = new StringBuffer();
                if (!Manager.data.getStringList("clans." + clan.getClanName() + ".members").isEmpty()) {
                    for (final Member s2 : clan.getMembers()) {
                        bw2.append(s2.getName() + ", ");
                    }
                    st2 = bw2.substring(0, bw2.length() - 2);
                }
                String welcome = "";
                if (clan.getWelcome().isEmpty()) {
                    welcome = "§cНету";
                } else {
                    welcome = clan.getWelcome();
                }
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_INFO").replace("{clanname}", clan.getClanName().replace("&", "§")).replace("{owner}", clan.getClanOwner()).replace("{moders}", st).replace("{moders-size}", new StringBuilder().append(clan.getModers().size()).toString()).replace("{members}", st2).replace("{members-size}", new StringBuilder().append(clan.getMembers().size()).toString()).replace("{data}", clan.getDataCreated()).replace("{welcome}", welcome).replace("{max-size-members}", new StringBuilder().append(Manager.config.getInt("SETTINGS.MAX_MEMBERS_IN_THE_CLAN")).toString()));
                break;
            }
            case "menu": {
                {
                    if (clan == null) {
                        sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                        return false;
                    }
                    // Здесь мы создаём наш "инвентарь" по комманде выше (/clan info)
                    Inventory help = Bukkit.getServer().createInventory(pl, 45, "§c§lКлан");

                    //Здесь мы указываем наш предмет
                    ItemStack ref1 = new ItemStack(Material.getMaterial(Manager.config.getString("GUI.material")));
                    ItemStack ref2 = new ItemStack(Material.getMaterial(Manager.config.getString("GUI.material2")));
                    ItemStack ref3 = new ItemStack(Material.getMaterial(Manager.config.getString("GUI.material3")));
                    ItemStack ref4 = new ItemStack(Material.getMaterial(Manager.config.getString("GUI.material4")));
                    ItemStack ref5 = new ItemStack(Material.getMaterial(Manager.config.getString("GUI.material5")));
                    ItemMeta metaref1 = ref1.getItemMeta();
                    ItemMeta metaref2 = ref2.getItemMeta();
                    ItemMeta metaref3 = ref3.getItemMeta();
                    ItemMeta metaref4 = ref4.getItemMeta();
                    ItemMeta metaref5 = ref5.getItemMeta();
                    ArrayList<String> lore = new ArrayList<String>();
                    ArrayList<String> lore2 = new ArrayList<String>();
                    ArrayList<String> lore3 = new ArrayList<String>();
                    ArrayList<String> lore4 = new ArrayList<String>();
                    ArrayList<String> lore5 = new ArrayList<String>();
                    lore.add(" ");
                    lore.add("§7Клан §r" + ClanManager.getClanfromPlayer(sender.getName()).getClanName());

                    lore2.add(" ");
                    lore2.add("§7Глава клана: §f" + ClanManager.getClanfromPlayer(sender.getName( )).getClanOwner());

                    lore3.add(" ");
                    lore3.add("§7Модераторы клана: §f" + ClanManager.getClanfromPlayer(sender.getName()).getModers().toString());

                    lore4.add(" ");
                    lore4.add("§7Баланс клана: §f" + ClanManager.getClanfromPlayer(sender.getName()).getBank() + " §a$");

                    lore5.add(" ");
                    lore5.add("§7Уровень клана: §f" + ClanManager.getClanfromPlayer(sender.getName()).getLevel() + " (§7" + ClanManager.getClanfromPlayer(sender.getName()).getEXP() + " §f/§6§l " + ClanManager.getClanfromPlayer(sender.getName()).getMaxEXP() +" опыта §f)");

                    metaref1.setLore(lore);
                    metaref1.setDisplayName(" ");

                    metaref2.setLore(lore2);
                    metaref2.setDisplayName(" ");

                    metaref3.setLore(lore3);
                    metaref3.setDisplayName(" ");

                    metaref4.setLore(lore4);
                    metaref4.setDisplayName(" ");

                    metaref5.setLore(lore5);
                    metaref5.setDisplayName(" ");

                    ref1.setItemMeta(metaref1);
                    help.setItem(13, ref1);

                    ref2.setItemMeta(metaref2);
                    help.setItem(22, ref2);

                    ref3.setItemMeta(metaref3);
                    help.setItem(21, ref3);

                    ref4.setItemMeta(metaref4);
                    help.setItem(23, ref4);

                    ref5.setItemMeta(metaref5);
                    help.setItem(31, ref5);


                    //Здесь мы открываем наш инвентарь
                    pl.openInventory(help);
                    break;
                }
            }
            case "setmotd": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_SETMOTD"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("SETMOTD_USAGE"));
                    return false;
                }
                final StringBuilder sb = new StringBuilder();
                for (int i = 1; args.length > i; ++i) {
                    sb.append(args[i] + " ");
                }
                ClanManager.setMotd(clan, sb.toString());
                sender.sendMessage(Manager.getInstance().getMSG("SET_MOTD_CLAN").replace("{welcome}", sb.toString()));
                break;
            }
            case "sethome": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_SETHOME"));
                    return false;
                }
                if (!Manager.getInstance().getConfig().getBoolean("SETTINGS.SERVERS_WITH_ALLOW_A_SETHOME_ENABLE")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_SETHOME_ALLOW_SERVER"));
                    return true;
                }
                if (!(Manager.data.getInt("clans." + clan.getClanName() + ".level") >= 5)) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_ENOUGH_LEVEL"));
                    return false;
                }
                ClanManager.setHome(Bukkit.getPlayerExact(sender.getName()).getLocation(), clan);
                sender.sendMessage(Manager.getInstance().getMSG("SET_HOME_CLAN"));
                break;
            }
            case "kick": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner() && !member.isModer()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_AND_MODER_KICK"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("KICK_USAGE"));
                    return false;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_PLAYER_ONLINE").replace("{player}", args[1]));
                    return false;
                }
                final Clan io = ClanManager.getClanfromPlayer(args[1]);
                if (!io.getClanName().equalsIgnoreCase(clan.getClanName())) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_PLAYER_MY_CLAN").replace("{player}", args[1]));
                    return false;
                }
                if (args[1].toLowerCase().equalsIgnoreCase(sender.getName().toLowerCase()) && args[1].toLowerCase().equalsIgnoreCase(sender.getName().toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("KICK_ME"));
                    return false;
                }
                if (new Member(args[1].toLowerCase()).isModer()) {
                    sender.sendMessage(Manager.getInstance().getMSG("ERROR_MEMBER_IS_MODER"));
                    return false;
                }
                if (new Member(args[1].toLowerCase()).isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_KICKED"));
                    return false;
                }
                ClanManager.leave(args[1], clan);
                for (final Member mem2 : clan.getMembers()) {
                    if (mem2.getName().equalsIgnoreCase(args[1].toLowerCase())) {
                        Bukkit.getPlayerExact(args[1]).sendMessage(Manager.getInstance().getMSG("YOU_KICK_CLAN"));
                    }
                    else {
                        final Player p2 = Bukkit.getPlayerExact(mem2.getName());
                        if (p2 == null) {
                            continue;
                        }
                        p2.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_KICK").replace("{player}", args[1]));
                    }
                }
                break;
            }
            case "home": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (clan.getLocation() == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_HOME"));
                    return false;
                }
                if (!Manager.getInstance().getConfig().getBoolean("SETTINGS.SERVERS_WITH_ALLOW_A_SETHOME_ENABLE")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_SETHOME_ALLOW_SERVER"));
                    return true;
                }
                Bukkit.getPlayerExact(sender.getName()).teleport(clan.getLocation());
                sender.sendMessage(Manager.getInstance().getMSG("TELEPORT_CLAN_HOME"));
                break;
            }
            case "setpublichome": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (clan.getLocation() == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_HOME"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_SETHOME"));
                    return false;
                }
                if (!Manager.getInstance().getConfig().getBoolean("SETTINGS.SERVERS_WITH_ALLOW_A_SETHOME_ENABLE")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_SETHOME_ALLOW_SERVER"));
                    return true;
                }
                ClanManager.getHomeStatus(true, clan);
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_HOME_PUBLIC"));
                break;
            }
            case "setprivatehome": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (clan.getLocation() == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_HOME"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_SETHOME"));
                    return false;
                }
                if (!Manager.getInstance().getConfig().getBoolean("SETTINGS.SERVERS_WITH_ALLOW_A_SETHOME_ENABLE")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_SETHOME_ALLOW_SERVER"));
                    return true;
                }
                ClanManager.getHomeStatus(false, clan);
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_HOME_PRIVATE"));
                break;
            }
            case "delhome": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_DELHOME"));
                    return false;
                }
                if (clan.getLocation() == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_HOME"));
                    return false;
                }
                ClanManager.delHome(clan);
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_DELETED_HOME"));
                break;
            }
            case "invite": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isModer() && !member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_AND_MODER_INVITE"));
                    return false;
                }
                if (Request.isFrom(sender.getName()) != null) {
                    sender.sendMessage(Manager.getInstance().getMSG("REQUEST_IS_FROM"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("INVITE_USAGE"));
                    return false;
                }
                final Clan co = ClanManager.getClanfromPlayer(args[1].toLowerCase());
                if (args[1].toLowerCase().equalsIgnoreCase(sender.getName().toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("SENDER_EQUELS_ADDER"));
                    return false;
                }
                if (co != null) {
                    sender.sendMessage(Manager.getInstance().getMSG("IN_THE_CLAN_PLAYER").replace("{player}", args[1]));
                    return false;
                }
                final Player p3 = Bukkit.getPlayerExact(args[1]);
                if (p3 == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_PLAYER_ONLINE").replace("{player}", args[1]));
                    return false;
                }
                if (Request.isTo(args[1]) != null) {
                    sender.sendMessage(Manager.getInstance().getMSG("REQUEST_IS_TO").replace("{player}", args[1]));
                    return false;
                }
                final TextComponent message = new TextComponent(Manager.getInstance().getMSG("CLICK_AND_HOVER_MESSAGE.ACCEPT"));
                final TextComponent message2 = new TextComponent("                ");
                final TextComponent message3 = new TextComponent("  ");
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept"));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Manager.getInstance().getMSG("CLICK_AND_HOVER_MESSAGE.ACCEPT_INFO")).create()));
                final TextComponent message4 = new TextComponent(Manager.getInstance().getMSG("CLICK_AND_HOVER_MESSAGE.DENY"));
                message2.addExtra((BaseComponent)message4);
                message2.addExtra((BaseComponent)message3);
                message2.addExtra((BaseComponent)message);
                message4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan deny"));
                message4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Manager.getInstance().getMSG("CLICK_AND_HOVER_MESSAGE.DENY_INFO")).create()));
                Request.send(sender.getName(), args[1]);
                sender.sendMessage(Manager.getInstance().getMSG("YOU_IVNITED_CLAN").replace("{player}", args[1]));
                p3.sendMessage(Manager.getInstance().getMSG("NOTIFICATION_CLAN_INVITE").replace("{player}", sender.getName()).replace("{clanname}", clan.getClanName().replace("&", "§")));
                p3.spigot().sendMessage((BaseComponent)message2);
                break;
            }
            case "deltop": {
                if (!sender.hasPermission("clans.admin")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NO_PERM"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("DELTOP_USAGE"));
                    return false;
                }
                if (!HolographicDisplayTOP.getBase().containsKey(args[1].toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_DEL_TOP"));
                    return false;
                }
                final Hologram holo = HolographicDisplayTOP.getBase().get(args[1].toLowerCase());
                holo.clearLines();
                Manager.data.set("holograms." + args[1].toLowerCase(), (Object)null);
                bC.save(Manager.data, "data.yml");
                HolographicDisplayTOP.getBase().remove(args[1].toLowerCase());
                sender.sendMessage(Manager.getInstance().getMSG("DELETED_TOP_CLAN"));
                break;
            }
            case "leave": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_LEAVE_IS_OWNER"));
                    return false;
                }
                if (member.isModer()) {
                    for (final Member mem : clan.getMembers()) {
                        final Player p = Bukkit.getPlayerExact(mem.getName());
                        if (sender.getName().toLowerCase().equalsIgnoreCase(mem.getName())) {
                            p.sendMessage(Manager.getInstance().getMSG("LEAVE_MODER"));
                        }
                        else {
                            if (p == null) {
                                continue;
                            }
                            p.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_LEAVE_MODER").replace("{player}", sender.getName()));
                        }
                    }
                    ClanManager.revModer(sender.getName(), clan);
                    break;
                }
                for (final Member mem : clan.getMembers()) {
                    final Player p = Bukkit.getPlayerExact(mem.getName());
                    if (sender.getName().toLowerCase().equalsIgnoreCase(mem.getName())) {
                        p.sendMessage(Manager.getInstance().getMSG("LEAVE_CLAN"));
                    }
                    else {
                        if (p == null) {
                            continue;
                        }
                        p.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_LEAVE").replace("{player}", sender.getName()));
                    }
                }
                ClanManager.leave(sender.getName(), clan);
                break;
            }
            case "tag": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_TAG"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("TAG_USAGE"));
                    return false;
                }
                if (Manager.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(sender.getName())) < Manager.config.getInt("MONEY_TAG_COMMAND")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_MONEY_TAG").replace("{price-change}", new StringBuilder().append(Manager.config.getInt("MONEY_TAG_COMMAND")).toString()));
                    return false;
                }
                if (args[1].replace("&", "§").toLowerCase().equalsIgnoreCase(clan.getClanName().replace("&", "§").toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("OLD_TAG_CHANGE"));
                    return false;
                }
                if (ChatColor.stripColor(args[1].replace("&", "§")).isEmpty()) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_TAG_IS_EMPTY"));
                    return false;
                }
                if (Manager.getInstance().checkString("????????????????????????????????abcdefghijklmnopqrstuvwxyz_0123456789", ChatColor.stripColor(args[1].replace("&", "§").toLowerCase()))) {
                    sender.sendMessage(Manager.getInstance().getMSG("BLOCK_SYMBOL_CLAN_TAG"));
                    return false;
                }
                if (Manager.config.getBoolean("SETTINGS.ONE_CODE_SYMBOL") && Manager.getInstance().getIntColors(args[1]) > 1) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_ONE_CODE_SYMBOL_TAG"));
                    return false;
                }
                if (args[1].replace("&", "§").length() < Manager.config.getInt("SETTINGS.CLANTAG_MIN_LENGTH")) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLANTAG_MIN_LENGTH").replace("{ct-min-length}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLANTAG_MIN_LENGTH")).toString()));
                    return false;
                }
                if (args[1].replace("&", "§").length() > Manager.config.getInt("SETTINGS.CLANTAG_MAX_LENGTH")) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLANTAG_MAX_LENGTH").replace("{ct-max-length}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLANTAG_MAX_LENGTH")).toString()));
                    return false;
                }
                if (!(Manager.data.getInt("clans." + clan.getClanName() + ".level") >= 10)) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_ENOUGH_LEVEL"));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_TAG_CHANGE").replace("{old-tag}", clan.getClanName().replace("&", "§")).replace("{tag}", args[1].replace("&", "§")));
                ClanManager.editTag(args[1], clan);
                break;
            }
            case "editname": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_NAME"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("NAME_USAGE"));
                    return false;
                }
                if (Manager.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(sender.getName())) < Manager.config.getInt("MONEY_NAME_COMMAND")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_MONEY_NAME").replace("{price-change}", new StringBuilder().append(Manager.config.getInt("MONEY_NAME_COMMAND")).toString()));
                    return false;
                }
                if (args[1].replace("&", "§").toLowerCase().equalsIgnoreCase(clan.getClanName().replace("&", "§").toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("OLD_NAME_CHANGE"));
                    return false;
                }
                if (ChatColor.stripColor(args[1].replace("&", "§")).isEmpty()) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NAME_IS_EMPTY"));
                    return false;
                }
                if (Manager.getInstance().checkString("????????????????????????????????abcdefghijklmnopqrstuvwxyz_0123456789", ChatColor.stripColor(args[1].replace("&", "§").toLowerCase()))) {
                    sender.sendMessage(Manager.getInstance().getMSG("BLOCK_SYMBOL_CLAN_TAG"));
                    return false;
                }
                if (Manager.config.getBoolean("SETTINGS.ONE_CODE_SYMBOL") && Manager.getInstance().getIntColors(args[1]) > 1) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_NOT_ONE_CODE_SYMBOL_TAG"));
                    return false;
                }
                if (args[1].replace("&", "§").length() < Manager.config.getInt("SETTINGS.CLAN_MIN_LENGTH")) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_MIN_LENGTH").replace("{min-length}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLAN_MIN_LENGTH")).toString()));
                    return false;
                }
                if (args[1].replace("&", "§").length() > Manager.config.getInt("SETTINGS.CLAN_MAX_LENGTH")) {
                    sender.sendMessage(Manager.getInstance().getMSG("CLAN_MAX_LENGTH").replace("{max-length}", new StringBuilder().append(Manager.config.getInt("SETTINGS.CLAN_MAX_LENGTH")).toString()));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_NAME_CHANGE").replace("{old-name}", clan.getClanName().replace("&", "§")).replace("{name}", args[1].replace("&", "§")));
                ClanManager.editName(args[1], clan);
                break;
            }
            case "msg": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isModer() && !member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_AND_MODER_MSG"));
                    return false;
                }
                final StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; ++i) {
                    sb.append(args[i] + " ");
                }
                for (final Member mem2 : clan.getMembers()) {
                    if (Bukkit.getPlayerExact(mem2.getName()) != null) {
                        sender.sendMessage(Manager.getInstance().getMSG("BROADCAST_CLAN").replace("{message}", sb.toString()));
                    }
                }
                break;
            }
            case "promote": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_PROMOTE"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("PROMOTE_USAGE"));
                    return false;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_PLAYER_ONLINE").replace("{player}", args[1]));
                    return false;
                }
                final Clan io = ClanManager.getClanfromPlayer(args[1]);
                if (!io.getClanName().equalsIgnoreCase(clan.getClanName())) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_MY_CLAN").replace("{player}", args[1]));
                    return false;
                }
                if (new Member(args[1].toLowerCase()).isModer()) {
                    sender.sendMessage(Manager.getInstance().getMSG("IS_MODER_CLAN").replace("{player}", args[1]));
                    return false;
                }
                if (sender.getName().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("SENDER_EQUELS_PROMOTE"));
                    return false;
                }
                ClanManager.addModer(args[1], clan);
                for (final Member mem2 : clan.getMembers()) {
                    if (mem2.getName().equalsIgnoreCase(args[1].toLowerCase())) {
                        Bukkit.getPlayerExact(args[1]).sendMessage(Manager.getInstance().getMSG("MEMBER_INFO_SET_MODER"));
                    }
                    else {
                        final Player p2 = Bukkit.getPlayerExact(mem2.getName());
                        if (p2 == null) {
                            continue;
                        }
                        p2.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_INFO_SET_MODER").replace("{player}", args[1]));
                    }
                }
                break;
            }
            case "demote": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_DEMOTE"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("DEMOTE_USAGE"));
                    return false;
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_PLAYER_ONLINE").replace("{player}", args[1]));
                    return false;
                }
                final Clan io = ClanManager.getClanfromPlayer(args[1]);
                if (!io.getClanName().equalsIgnoreCase(clan.getClanName())) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_MY_CLAN").replace("{player}", args[1]));
                    return false;
                }
                if (!new Member(args[1].toLowerCase()).isModer()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IS_MODER_CLAN").replace("{player}", args[1]));
                    return false;
                }
                ClanManager.revModer(args[1], clan);
                for (final Member mem2 : clan.getMembers()) {
                    if (mem2.getName().equalsIgnoreCase(args[1].toLowerCase())) {
                        Bukkit.getPlayerExact(args[1]).sendMessage(Manager.getInstance().getMSG("MEMBER_INFO_DEL_MODER"));
                    }
                    else {
                        final Player p2 = Bukkit.getPlayerExact(mem2.getName());
                        if (p2 == null) {
                            continue;
                        }
                        p2.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_INFO_DEL_MODER").replace("{player}", args[1]));
                    }
                }
                break;
            }
            case "desposit": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("DESPOSIT_USAGE"));
                    return false;
                }
                int money = 0;
                try {
                    money = Math.abs(Integer.parseInt(args[1]));
                }
                catch (Exception e) {
                    sender.sendMessage(Manager.getInstance().getMSG("ERROR_PRICE"));
                    return false;
                }
                if (money == 0) {
                    sender.sendMessage(Manager.getInstance().getMSG("ERROR_PRICE"));
                    return false;
                }
                if (money > Manager.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(sender.getName()))) {
                    sender.sendMessage(Manager.getInstance().getMSG("BALANCE_NOT_PLAYER"));
                    return false;
                }
                ClanManager.despositBank(money, clan);
                Manager.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(sender.getName()), (double)money);
                for (final Member mem2 : clan.getMembers()) {
                    if (mem2.getName().equalsIgnoreCase(sender.getName().toLowerCase())) {
                        sender.sendMessage(Manager.getInstance().getMSG("BALANCE_CLAN_DESPOSIT").replace("{money}", new StringBuilder().append(money).toString()).replace("{new-balance}", new StringBuilder().append(clan.getBank() + money).toString()));
                    }
                    else {
                        final Player p2 = Bukkit.getPlayerExact(mem2.getName());
                        if (p2 == null) {
                            continue;
                        }
                        p2.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_DESPOSIT").replace("{money}", new StringBuilder().append(money).toString()).replace("{player}", sender.getName()));
                    }
                }
                break;
            }
            case "balance": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("BALANCE_INFO").replace("{money}", new StringBuilder().append(clan.getBank()).toString()));
                break;
            }
            case "take": {
                if (clan == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_IN_THE_CLAN"));
                    return false;
                }
                if (!member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_OWNER_TAKE"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(Manager.getInstance().getMSG("TAKE_USAGE"));
                    return false;
                }
                int money = 0;
                try {
                    money = Math.abs(Integer.parseInt(args[1]));
                }
                catch (Exception e) {
                    sender.sendMessage(Manager.getInstance().getMSG("ERROR_PRICE"));
                    return false;
                }
                if (money == 0) {
                    sender.sendMessage(Manager.getInstance().getMSG("ERROR_PRICE"));
                    return false;
                }
                if (clan.getBank() == 0) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_FOUND_BALANCE"));
                    return false;
                }
                if (clan.getBank() < money) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_FOUND_BALANCE"));
                    return false;
                }
                ClanManager.withdrawBank(money, clan);
                Manager.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(sender.getName()), (double)money);
                for (final Member mem2 : clan.getMembers()) {
                    if (sender.getName().toLowerCase().equalsIgnoreCase(mem2.getName())) {
                        sender.sendMessage(Manager.getInstance().getMSG("BALANCE_CLAN_TAKE").replace("{money}", new StringBuilder().append(money).toString()).replace("{new-balance}", new StringBuilder().append(clan.getBank() - money).toString()));
                    }
                    else {
                        final Player p2 = Bukkit.getPlayerExact(mem2.getName());
                        if (p2 == null) {
                            continue;
                        }
                        p2.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_TAKE").replace("{money}", new StringBuilder().append(money).toString()).replace("{player}", sender.getName()));
                    }
                }
                break;
            }
            case "deny": {
                final Request r = Request.isTo(sender.getName());
                if (r == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_REQUESTS"));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("DENY_REQUEST"));
                r.remove();
                break;
            }
            case "accept": {
                final Request r = Request.isTo(sender.getName());
                if (r == null) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_REQUESTS"));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("ACCEPT_REQUEST"));
                final Clan cd = ClanManager.getClanfromPlayer(r.getPlayer1());
                ClanManager.invite(sender.getName().toLowerCase(), cd);
                r.remove();
                for (final Member me : cd.getMembers()) {
                    final Player p4 = Bukkit.getPlayerExact(me.getName());
                    if (p4 != null) {
                        p4.sendMessage(Manager.getInstance().getMSG("FOR_MEMBERS_ACCEPT").replace("{player}", sender.getName()).replace("{clanname}", cd.getClanName().replace("&", "§")));
                    }
                }
                break;
            }
            case "listtop": {
                if (!sender.hasPermission("clans.admin")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NO_PERM"));
                    return false;
                }
                final StringBuffer sb2 = new StringBuffer();
                for (final String s3 : Manager.data.getConfigurationSection("holograms").getKeys(false)) {
                    sb2.append(Manager.getInstance().getMSG("LIST_FORMAT_TOP").replace("{top-name}", s3));
                }
                if (sb2.toString().isEmpty()) {
                    sender.sendMessage(Manager.getInstance().getMSG("LIST_NULL"));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("LIST_TOPNAME").replace("{list-top}", sb2.toString()));
                break;
            }
            case "settop": {
                if (!sender.hasPermission("clans.admin")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NO_PERM"));
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage(Manager.getInstance().getMSG("SETTOP_USAGE"));
                    return false;
                }
                if (HolographicDisplayTOP.getBase().containsKey(args[1].toLowerCase())) {
                    sender.sendMessage(Manager.getInstance().getMSG("NOT_TOP_IS_EMPTY"));
                    return false;
                }
                final Location l4 = Bukkit.getPlayer(sender.getName()).getLocation();
                final Hologram holo2 = HologramsAPI.createHologram((Plugin)Manager.getInstance(), l4);
                if (args[2].toUpperCase().equalsIgnoreCase("MEMBERS")) {
                    final List<Clan> list = new ArrayList<Clan>();
                    for (final Clan c2 : ClanManager.getClans().values()) {
                        list.add(new Clan(c2.getClanName(), c2.getMembers().size(), c2.getClanOwner(), c2.getDataCreated(), c2.getClanTag()));
                    }
                    list.sort((o1, o2) -> {
                        if (o1.getSize() > o2.getSize()) {
                            return -1;
                        }
                        else if (o1.getSize() < o2.getSize()) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    });
                    holo2.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.TITLE")));
                    holo2.insertTextLine(1, "");
                    int x2 = 2;
                    int j = 0;
                    while (j < 10) {
                        if (j < list.size()) {
                            final Clan data = list.get(j);
                            holo2.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE").replace("{number}", ++j + "").replace("{clanname}", data.getClanName()).replace("{admin}", data.getClanOwner()).replace("{members}", data.getSize() + "").replace("{data}", data.getDataCreated()).replace("{clantag}", data.getClanName() + "")));
                        }
                        else {
                            holo2.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE_NULL").replace("{number}", ++j + "")));
                        }
                    }
                    holo2.insertTextLine(12, "");
                    holo2.insertTextLine(13, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.MEMBERS.LINE_ADDON")));
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".type", (Object)args[2].toUpperCase());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.x", (Object)l4.getX());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.y", (Object)l4.getY());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.z", (Object)l4.getZ());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.world", (Object)l4.getWorld().getName());
                    bC.save(Manager.data, "data.yml");
                    HolographicDisplayTOP.getBase().put(args[1].toLowerCase(), holo2);
                    return false;
                }
                if (args[2].toUpperCase().equalsIgnoreCase("BANK")) {
                    final List<Clan> list = new ArrayList<Clan>();
                    for (final Clan c2 : ClanManager.getClans().values()) {
                        list.add(new Clan(c2.getClanName(), c2.getClanOwner(), c2.getDataCreated(), c2.getBank()));
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
                    holo2.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.TITLE")));
                    holo2.insertTextLine(1, "");
                    int x2 = 2;
                    int j = 0;
                    while (j < 10) {
                        if (j < list.size()) {
                            final Clan data = list.get(j);
                            holo2.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE").replace("{number}", ++j + "").replace("{clanname}", data.getClanName()).replace("{admin}", data.getClanOwner()).replace("{bank}", data.getBank() + "").replace("{data}", data.getDataCreated()).replace("{clantag}", data.getClanName() + "")));
                        }
                        else {
                            holo2.insertTextLine(x2++, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE_NULL").replace("{number}", ++j + "")));
                        }
                    }
                    holo2.insertTextLine(12, "");
                    holo2.insertTextLine(13, ChatColor.translateAlternateColorCodes('&', Manager.config.getString("HOLOGRAPHICDISPLAY-TOP.BANK.LINE_ADDON")));
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".type", (Object)args[2].toUpperCase());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.x", (Object)l4.getX());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.y", (Object)l4.getY());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.z", (Object)l4.getZ());
                    Manager.data.set("holograms." + args[1].toLowerCase() + ".location.world", (Object)l4.getWorld().getName());
                    bC.save(Manager.data, "data.yml");
                    HolographicDisplayTOP.getBase().put(args[1].toLowerCase(), holo2);
                    sender.sendMessage(Manager.getInstance().getMSG("CREATE_TOP_HOLO").replace("{topname}", args[1]));
                    return false;
                }
                sender.sendMessage(Manager.getInstance().getMSG("ERROR_TYPE"));
                break;
            }
            case "top-reload": {
                if (!sender.hasPermission("clans.admin")) {
                    sender.sendMessage(Manager.getInstance().getMSG("NO_PERM"));
                    return false;
                }
                for (final String s4 : Manager.data.getConfigurationSection("holograms").getKeys(false)) {
                    HolographicDisplayTOP.updateHolograms(s4);
                }
                sender.sendMessage(Manager.getInstance().getMSG("CLAN_TOP_RELOAD"));
                break;
            }
            default: {
                if (member.isOwner()) {
                    sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN_OWNER"));
                    break;
                }
                if (member.isModer()) {
                    sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN_MODER"));
                    break;
                }
                if (clan != null) {
                    sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN_MEMBER"));
                    break;
                }
                sender.sendMessage(Manager.getInstance().getMSG("HELP_CLAN"));
                break;
            }
        }
        return false;
    }
}

