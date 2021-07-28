package me.athlaeos.progressivelydifficultmobs.menus;

import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.pojo.Drop;
import me.athlaeos.progressivelydifficultmobs.pojo.Menu;
import me.athlaeos.progressivelydifficultmobs.pojo.PlayerMenuUtility;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class DropModificationMenu extends Menu {

    private final NumberFormat df = NumberFormat.getInstance(Locale.getDefault());
    private double dropChance = 50;
    private double dropChanceLootingBonus = 10;
    private int minDrops = 1;
    private int maxDrops = 3;
    private int minDropsLootingBonus = 0;
    private int maxDropsLootingBonus = 1;

    private ItemStack playerSelectedItem = null;
    private final boolean editExitingDrop;
    private boolean areYouSure = false;

    private ItemStack dropChanceButton;
    private ItemStack dropChanceLootingButton;
    private ItemStack dropsButton;
    private ItemStack dropsLootingButton;

    private final ItemStack confirmNewDropButton = Utils.createItemStack(Material.CRAFTING_TABLE, Utils.chat("&a&lCreate drop for &f" + playerMenuUtility.getLootTable().getName()), null);
    private final ItemStack dropItemPlaceholder = Utils.createItemStack(Material.NETHER_STAR, Utils.chat("&a&lClick an item in your inventory to select item"), null);

    private final ItemStack deleteDropButton = Utils.createItemStack(Material.RED_STAINED_GLASS_PANE, Utils.chat("&cDelete drop"), null);

    public DropModificationMenu(PlayerMenuUtility playerMenuUtility, boolean editExitingDrop) {
        super(playerMenuUtility);
        this.editExitingDrop = editExitingDrop;

        if (editExitingDrop) {
            playerSelectedItem = playerMenuUtility.getCurrentDrop().getItem();
            dropChance = playerMenuUtility.getCurrentDrop().getDropChance();
            dropChanceLootingBonus = playerMenuUtility.getCurrentDrop().getDropChanceLootingBonus();
            minDrops = playerMenuUtility.getCurrentDrop().getMinAmountDrop();
            maxDrops = playerMenuUtility.getCurrentDrop().getMaxAmountDrop();
            minDropsLootingBonus = playerMenuUtility.getCurrentDrop().getMinAmountDropLootingBonus();
            maxDropsLootingBonus = playerMenuUtility.getCurrentDrop().getMaxAmountDropLootingBonus();
        }
        updateValueButtons();


//        Utils.changeDisplayName(confirmnewdropbutton, "&a&lCreate drop for " + playerMenuUtility.getCurrentLootTable().getName());
    }

    @Override
    public String getMenuName() {
        if (playerMenuUtility.getCurrentDrop() == null){
            return Utils.chat("&7&lCreate new drop");
        } else {
            return Utils.chat("&7&lEdit drop in &6" + playerMenuUtility.getLootTable().getName());
        }
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem().equals(confirmNewDropButton)) {
            if (inventory.getItem(10).getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&a&lClick an item in your inventory to select item"))) {
                e.getWhoClicked().sendMessage(Utils.chat("&cPlease select the item to drop from your inventory first"));
                return;
            }
            if (editExitingDrop){
                playerMenuUtility.getLootTable().updateDrop(playerMenuUtility.getCurrentDrop(), inventory.getItem(10), dropChance, dropChanceLootingBonus, minDrops, maxDrops, minDropsLootingBonus, maxDropsLootingBonus);
                e.getWhoClicked().sendMessage(Utils.chat("&aDrop updated successfully!"));
            } else {
                playerMenuUtility.getLootTable().addDrop(new Drop(inventory.getItem(10), dropChance, dropChanceLootingBonus, minDrops, minDropsLootingBonus, maxDrops, maxDropsLootingBonus));
                e.getWhoClicked().sendMessage(Utils.chat("&aDrop created successfully!"));
            }
            LootTablePersister.saveLootTables();
            new EditLootTableMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked())).open();
            return;
        } else if (e.getCurrentItem().equals(deleteDropButton)){
            if (areYouSure){
                playerMenuUtility.getLootTable().removeDrop(playerMenuUtility.getCurrentDrop());
                e.getWhoClicked().sendMessage(Utils.chat("&cDrop removed"));
                new EditLootTableMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked())).open();
                return;
            } else {
                e.getWhoClicked().sendMessage(Utils.chat("&cAre you sure you want to remove this Drop?"));
                e.getWhoClicked().sendMessage(Utils.chat("&cThis action cannot be undone,"));
                e.getWhoClicked().sendMessage(Utils.chat("&cpress again for confirmation."));
                areYouSure = true;
                return;
            }
        }

        if (e.getClickedInventory().getHolder() instanceof Player) {
            inventory.setItem(10, new ItemStack(e.getCurrentItem()));
            inventory.getItem(10).setAmount(1);
            playerSelectedItem = inventory.getItem(10);
            setMenuItems();
            return;
        }

        if (e.getCurrentItem().equals(dropChanceButton)){
            if (e.getClick() == ClickType.LEFT) {
                dropChance = alterValueDropChance(0.1, dropChance);
            } else if (e.getClick() == ClickType.RIGHT){
                dropChance = alterValueDropChance(-0.1, dropChance);
            } else if (e.getClick() == ClickType.SHIFT_LEFT){
                dropChance = alterValueDropChance(2.5, dropChance);
            } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                dropChance = alterValueDropChance(-2.5, dropChance);
            }
        }

        if (e.getCurrentItem().equals(dropChanceLootingButton)){
            if (e.getClick() == ClickType.LEFT) {
                dropChanceLootingBonus = alterValueDropChance(0.1, dropChanceLootingBonus);
            } else if (e.getClick() == ClickType.RIGHT){
                dropChanceLootingBonus = alterValueDropChance(-0.1, dropChanceLootingBonus);
            } else if (e.getClick() == ClickType.SHIFT_LEFT){
                dropChanceLootingBonus = alterValueDropChance(2.5, dropChanceLootingBonus);
            } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                dropChanceLootingBonus = alterValueDropChance(-2.5, dropChanceLootingBonus);
            }
        }

        if (e.getCurrentItem().equals(dropsButton)){
            if (e.getClick() == ClickType.LEFT) {
                changeMinDrops(1);
            } else if (e.getClick() == ClickType.RIGHT){
                changeMinDrops(-1);
            } else if (e.getClick() == ClickType.SHIFT_LEFT){
                changeMaxDrops(1);
            } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                changeMaxDrops(-1);
            }
        }

        if (e.getCurrentItem().equals(dropsLootingButton)){
            if (e.getClick() == ClickType.LEFT) {
                changeMinLootingDrops(1);
            } else if (e.getClick() == ClickType.RIGHT){
                changeMinLootingDrops(-1);
            } else if (e.getClick() == ClickType.SHIFT_LEFT){
                changeMaxLootingDrops(1);
            } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                changeMaxLootingDrops(-1);
            }
        }
        updateValueButtons();
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        if (playerSelectedItem == null){
            dropItemPlaceholder.setAmount(1);
            inventory.setItem(10, dropItemPlaceholder);
        } else {
            playerSelectedItem.setAmount(1);
            inventory.setItem(10, playerSelectedItem);
        }

        inventory.setItem(12, dropChanceButton);
        inventory.setItem(13, dropChanceLootingButton);
        inventory.setItem(15, dropsButton);
        inventory.setItem(16, dropsLootingButton);

        inventory.setItem(26, confirmNewDropButton);
        if (editExitingDrop){
            inventory.setItem(18, deleteDropButton);
        }
    }

    private void updateValueButtons(){
        dropChanceButton = Utils.createItemStack(Material.IRON_SWORD, Utils.chat("&e&lDrop chance : &f" + dropChance + "%"),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 0.1% and"),
                        Utils.chat("&6right click to decrease by 0.1%"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&62.5% instead."),
                        Utils.chat("&7The base chance for a killed mob"),
                        Utils.chat("&7to successfully drop this item"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If set to &e100%&7, the mob will"),
                        Utils.chat("&7always attempt to drop this item."),
                        Utils.chat("&7Note: The mob may not drop anything"),
                        Utils.chat("&7if the minimum amount of items to"),
                        Utils.chat("&7drop is set to 0.")));
        dropChanceLootingButton = Utils.createItemStack(Material.GOLDEN_SWORD, Utils.chat("&e&lDrop chance looting bonus : &f+" + dropChanceLootingBonus + "%&e/level"),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 0.1% and"),
                        Utils.chat("&6right click to decrease by 0.1%"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&62.5% instead."),
                        Utils.chat("&7The drop chance is increased"),
                        Utils.chat("&7by the specified amount per"),
                        Utils.chat("&7each level of &elooting&7 on the"),
                        Utils.chat("&7killer's weapon."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If the drop chance is 50%,"),
                        Utils.chat("&7and the &ebonus looting chance"),
                        Utils.chat("&7is &e10%&7, the drop chance will"),
                        Utils.chat("&7be 80% with &eLooting III&7."),
                        Utils.chat("&7(50% + &e10%&7 * 3 = &e80%&7)")));
        dropsButton = Utils.createItemStack(Material.PAPER, Utils.chat("&e&lDrop range : &f" + minDrops + "&e-&f" + maxDrops),
                Arrays.asList(
                        Utils.chat("&6Left click to increase min value by 1"),
                        Utils.chat("&6and right click to decrease by 1."),
                        Utils.chat("&6Hold shift to alter max value instead."),
                        Utils.chat("&7The range of amounts of items that"),
                        Utils.chat("&7is dropped if drop chance is"),
                        Utils.chat("&7successful. "),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If &eminimum&7 is set to &e1&7, the &bmaximum"),
                        Utils.chat("&7set to &b3&7, and with a drop chance"),
                        Utils.chat("&7of 50%, the mob has a 50% chance to"),
                        Utils.chat("&7drop &e1&7 to &b3&7 items on death."),
                        Utils.chat("&7Note: every amount within the range"),
                        Utils.chat("&7has the same chance to apply."),
                        Utils.chat("&7With a range of &e1&7-&b3&7, the mob has a"),
                        Utils.chat("&733.3% chance to drop 1 item, 33.3% chance"),
                        Utils.chat("&7to drop 2, 33.3% to drop 3, etc.")));
        dropsLootingButton = Utils.createItemStack(Material.PAPER, Utils.chat("&e&lDrop range looting bonus : &f+" + minDropsLootingBonus + "&e-&f" + maxDropsLootingBonus + "&e/level"),
                Arrays.asList(
                        Utils.chat("&6Left click to increase min value by 1"),
                        Utils.chat("&6and right click to decrease by 1."),
                        Utils.chat("&6Hold shift to edit max value instead."),
                        Utils.chat("&7The minimum amount of items dropped"),
                        Utils.chat("&7within the range boosted per level"),
                        Utils.chat("&7of looting."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If looting bonus is set to &e1&7-&b2,"),
                        Utils.chat("&7and the drop range set to 1-3"),
                        Utils.chat("&7the mob can drop &e4-&b9 items on death"),
                        Utils.chat("&7with Looting III."),
                        Utils.chat("&71-3 + &e(1 * 3)&7-&b(2 * 3) &7= &e3&7-&b6"),
                        Utils.chat("&71-3 + &e3&7-&b6 &7= &e4&7-&b9")));
    }

    private double alterValueDropChance(double value, double currentValue){
        double returnValue;
        if (value < 0) {
            if (currentValue == 0.0) {
                returnValue = 100.0;
            } else if ((currentValue + value) < 0) {
                returnValue = 0.0;
            } else {
                returnValue = currentValue + value;
            }
        } else {
            if (currentValue == 100.0) {
                returnValue = 0.0;
            } else if ((currentValue + value) > 100) {
                returnValue = 100.0;
            } else {
                returnValue = currentValue + value;
            }
        }
        returnValue = Double.parseDouble(df.format(returnValue).replace(",", "."));
        return returnValue;
    }

    private void changeMinDrops(int value) {
        if (value < 0) {
            if (minDrops - 1 < 0) {
                minDrops = 0;
            } else {
                minDrops--;
            }
        } else {
            if (minDrops + 1 > maxDrops) maxDrops++;
            minDrops++;
        }
    }

    private void changeMaxDrops(int value) {
        if (value < 0) {
            if (maxDrops - 1 < 0) {
                maxDrops = 0;
                return;
            }
            if (maxDrops - 1 < minDrops) {
                minDrops--;
            }
            maxDrops--;
        } else {
            maxDrops++;
        }
    }

    private void changeMinLootingDrops(int value) {
        if (value < 0) {
            if (minDropsLootingBonus - 1 < 0) {
                minDropsLootingBonus = 0;
            } else {
                minDropsLootingBonus--;
            }
        } else {
            if (minDropsLootingBonus + 1 > maxDropsLootingBonus) maxDropsLootingBonus++;
            minDropsLootingBonus++;
        }
    }

    private void changeMaxLootingDrops(int value) {
        if (value < 0) {
            if (maxDropsLootingBonus - 1 < 0) {
                maxDropsLootingBonus = 0;
                return;
            }
            if (maxDropsLootingBonus - 1 < minDropsLootingBonus) {
                minDropsLootingBonus--;
            }
            maxDropsLootingBonus--;
        } else {
            maxDropsLootingBonus++;
        }
    }
}
