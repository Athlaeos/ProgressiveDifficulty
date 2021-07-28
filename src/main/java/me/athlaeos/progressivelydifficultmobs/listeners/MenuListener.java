package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.pojo.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        if (e.getCurrentItem() == null){
            return;
        }

        if (e.getView().getTopInventory().getHolder() instanceof Menu && e.getView().getBottomInventory() instanceof PlayerInventory){
            e.setCancelled(true);

            Menu m = (Menu) e.getView().getTopInventory().getHolder();

            m.handleMenu(e);
        }
    }
}
