package kz.itzHiti.clans.api;

import kz.itzHiti.clans.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClansGUI implements Listener {
    @EventHandler
    private void inventoryClick(InventoryClickEvent e)
    {

        Player p = (Player) e.getWhoClicked();

        if (e.getInventory().getTitle().equalsIgnoreCase("§c§lКлан"))
        {
            e.setCancelled(true);
            if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            //Это будет делать каждый раз, когда игрок нажимает на любую часть графического интерфейса, отличную от предложенного элемента, он будет возвращаться, так что ошибок в консоли не будет

        }}}
