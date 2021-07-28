package me.athlaeos.progressivelydifficultmobs.menus;

import me.athlaeos.progressivelydifficultmobs.managers.LootTableManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.pojo.Drop;
import me.athlaeos.progressivelydifficultmobs.pojo.LootTable;
import me.athlaeos.progressivelydifficultmobs.pojo.Menu;
import me.athlaeos.progressivelydifficultmobs.pojo.PlayerMenuUtility;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LootTableOverviewMenu extends Menu {

    private final ItemStack previousPageButton;
    private final ItemStack nextPageButton;
    private final ItemStack currentPageInfoLabel;
    private final ItemStack backToMenuButton;
    private int pagesLength = 0;
    private Menu returnToMenu = null;

    private final LootTableManager lootTableManager = LootTableManager.getInstance();

    public LootTableOverviewMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        previousPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lPrevious page"), null);
        nextPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lNext page"), null);
        currentPageInfoLabel = Utils.createItemStack(Material.ENDER_EYE, Utils.chat("&a&lCurrent page: "), null);
        backToMenuButton = Utils.createItemStack(Material.ENCHANTED_BOOK, Utils.chat("&7&lBack to monster menu"), null);

        if (playerMenuUtility.getPreviousMenu() != null){
            returnToMenu = playerMenuUtility.getPreviousMenu();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7&lLootTable Overview");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().equals(previousPageButton)) {
            if ((playerMenuUtility.getPageNumber() - 1) < 0) {
                return;
            }
            playerMenuUtility.decrementPageNumber();
            setMenuItems();
        } else if (e.getCurrentItem().equals(nextPageButton)){
            if ((playerMenuUtility.getPageNumber() + 1) >= pagesLength){
                return;
            }
            playerMenuUtility.incrementPageNumber();
            setMenuItems();
        } else if (e.getCurrentItem().equals(backToMenuButton)){
            playerMenuUtility.getPreviousMenu().open();
        } else if (e.getCurrentItem().getItemMeta().hasLore()){
            if (e.getCurrentItem().getItemMeta().getLore().contains(Utils.chat("&7&l[LootTable]"))){
                LootTable table = LootTableManager.getInstance().getLootTableByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                if (table == null){
                    playerMenuUtility.getOwner().sendMessage(Utils.chat("&cLootTable no longer exists!"));
                    setMenuItems();
                    return;
                }
                playerMenuUtility.setLootTable(table);
                new EditLootTableMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked())).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        List<ItemStack> lootTables = new ArrayList<>();
        for (LootTable l : lootTableManager.getAllLootTables()){
            ItemStack tableEntry = new ItemStack((l.getIcon() == null) ? Material.BOOK : l.getIcon());
            Utils.changeDisplayName(tableEntry, Utils.chat("&r&f&l" + l.getName()));
            List<String> entryLore = new ArrayList<>(Arrays.asList(Utils.chat("&7&l[LootTable]"), Utils.chat("&7&l--Contents--")));
            if (l.getDrops().size() == 0) {
                entryLore.add(Utils.chat("&7N/A"));
            } else {
                for (Drop d : l.getDrops()) {
                    entryLore.add(Utils.chat(String.format("&7&a%s&7-&a%s &7of &a%s &7with &a%s%% &7chance",
                            d.getMinAmountDrop(),
                            d.getMaxAmountDrop(),
                            d.getItem().getType(),
                            d.getDropChance())));
                }
            }
            Utils.setLoreLines(tableEntry, entryLore);
            lootTables.add(tableEntry);
        }
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, lootTables);
        pagesLength = pages.size();


        if (pagesLength > 0){
            if (playerMenuUtility.getPageNumber() >= pagesLength) playerMenuUtility.setPageNumber(pagesLength - 1);
            for (ItemStack l : pages.get(playerMenuUtility.getPageNumber())){
                inventory.addItem(l);
            }
        }
        Utils.changeDisplayName(currentPageInfoLabel, Utils.chat("&d&lCurrent page: " + (playerMenuUtility.getPageNumber() + 1) + "&7&l/&d&l" + pagesLength));

        inventory.setItem(45, previousPageButton);
        inventory.setItem((returnToMenu == null) ? 49 : 48, currentPageInfoLabel);
        if (returnToMenu != null) inventory.setItem(50, backToMenuButton);
        inventory.setItem(53, nextPageButton);
    }
}
