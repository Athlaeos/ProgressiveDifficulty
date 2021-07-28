package me.athlaeos.progressivelydifficultmobs.menus;

import me.athlaeos.progressivelydifficultmobs.managers.LootTableManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.pojo.Drop;
import me.athlaeos.progressivelydifficultmobs.pojo.Menu;
import me.athlaeos.progressivelydifficultmobs.pojo.PlayerMenuUtility;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class EditLootTableMenu extends Menu {

    private final ItemStack createNewDropButton;
    private final ItemStack deleteLootTableButton;
    private final ItemStack backToMenuButton;

    private boolean areYouSure = false;

    private HashMap<Integer, Drop> drops;

    public EditLootTableMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        createNewDropButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE, Utils.chat("&a&lAdd new drop"), null);
        deleteLootTableButton = Utils.createItemStack(Material.RED_STAINED_GLASS_PANE, Utils.chat("&c&lDelete LootTable"), Arrays.asList(Utils.chat("&cWarning, this cannot be undone")));
        backToMenuButton = Utils.createItemStack(Material.BOOK, Utils.chat("&7Back to menu"), null);
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7&lEdit LootTable");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem().equals(deleteLootTableButton)){
            if (areYouSure){
                LootTablePersister.deleteLootTable(playerMenuUtility.getLootTable());
                LootTableManager.getInstance().deleteLootTable(playerMenuUtility.getLootTable());
                e.getWhoClicked().sendMessage(Utils.chat("&aLootTable removed"));
                new LootTableOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked())).open();
            } else {
                e.getWhoClicked().sendMessage(Utils.chat("&cAre you sure you want to remove this LootTable?"));
                e.getWhoClicked().sendMessage(Utils.chat("&cThis action cannot be undone,"));
                e.getWhoClicked().sendMessage(Utils.chat("&cpress again for confirmation."));
                areYouSure = true;
            }
        } else if (e.getCurrentItem().equals(createNewDropButton)){
            new DropModificationMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked()), false).open();
        } else if (e.getCurrentItem().getItemMeta().hasLore()){
            if (e.getCurrentItem().getItemMeta().getLore().contains(Utils.chat("&8&l-----DROP INFO-----"))){
                Drop clickedDrop = drops.get(e.getSlot());
                playerMenuUtility.setCurrentDrop(clickedDrop);
                new DropModificationMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked()), true).open();
            }
        } else if (e.getCurrentItem().equals(backToMenuButton)){
            new LootTableOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked())).open();
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        drops = new HashMap<>();
        for (Drop d : playerMenuUtility.getLootTable().getDrops()){
            ItemStack displayItem = new ItemStack(d.getItem());
            drops.put(playerMenuUtility.getLootTable().getDrops().indexOf(d), d);

            Utils.setLoreLines(displayItem, Arrays.asList(
                    Utils.chat("&8&l-----DROP INFO-----"),
                    Utils.chat(String.format("&7Drop chance: %s%% (+ %s%%/L)", d.getDropChance(), d.getDropChanceLootingBonus())),
                    Utils.chat(String.format("&7Drop amount: %s-%s (+ %s-%s/L)", d.getMinAmountDrop(), d.getMaxAmountDrop(), d.getMinAmountDropLootingBonus(), d.getMaxAmountDropLootingBonus()))));

            displayItem.setAmount(1);
            inventory.addItem(displayItem);
        }
        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 45){
            inventory.addItem(createNewDropButton);
        }

        ItemStack info = Utils.createItemStack(Material.WRITABLE_BOOK, Utils.chat("&aCurrently editing: " + playerMenuUtility.getLootTable().getName()), null);

        ItemStack paneItem = Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, "", null);

        inventory.setItem(46, paneItem);
        inventory.setItem(47, paneItem);
        inventory.setItem(48, paneItem);
        inventory.setItem(50, paneItem);
        inventory.setItem(51, paneItem);
        inventory.setItem(52, paneItem);

        inventory.setItem(45, deleteLootTableButton);
        inventory.setItem(49, info);
        inventory.setItem(53, backToMenuButton);
    }
}
