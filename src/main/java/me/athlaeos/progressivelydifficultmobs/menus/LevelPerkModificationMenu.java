package me.athlaeos.progressivelydifficultmobs.menus;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerPerksManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.pojo.Menu;
import me.athlaeos.progressivelydifficultmobs.pojo.PlayerMenuUtility;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LevelPerkModificationMenu extends Menu {
    private final PluginConfigurationManager config = PluginConfigurationManager.getInstance();

    private final List<Integer> mainLevelItemOrder = Arrays.asList(0, 1, 2, 3, 4, 9, 10, 11, 12, 13, 22, 31, 32, 33, 34, 35, 40, 41, 42 ,43 ,44);
    private final List<Integer> subLevelItemOrder = Arrays.asList(12, 13, 14, 20, 21, 23, 24, 30, 31, 32);
    private int currentLevelPage = 0;
    private int minLevelPages;
    private int maxLevelPages;
    private View currentView = View.LEVELS;
    private int currentPage = 0;
    private int chosenLevel = 0;
    private final ItemStack addPerkButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE, Utils.chat("&a&lAdd new perk"), null);
    private final ItemStack previousPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lPrevious page"), null);
    private final ItemStack nextPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lNext page"), null);
    private final ItemStack backToMenuButton = Utils.createItemStack(Material.BOOK, Utils.chat("&7&lBack to perk level menu"), null);
    private int pagesLength;
    private final NamespacedKey perkIDKey = new NamespacedKey(Main.getInstance(), "pdm_perk-id");

    public LevelPerkModificationMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        minLevelPages = ((int) -Math.ceil(-config.getMinKarmaLevel()/10D)) + 1;
        maxLevelPages = ((int) Math.ceil(config.getMaxKarmaLevel()/10D)) - 1;
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7&lLevel Perks");
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack clickedButton = e.getCurrentItem();
        if (currentView == View.LEVELS){
            if (clickedButton.getItemMeta().hasDisplayName()){
                if (clickedButton.getItemMeta().getDisplayName().contains(Utils.chat("&f&lLevel "))){
                    try {
                        chosenLevel = Integer.parseInt(clickedButton.getItemMeta().getDisplayName().replace(Utils.chat("&f&lLevel "), ""));
                        currentView = View.PERKS_CHOSEN;
                    } catch (IllegalArgumentException ignored){
                    }
                }
            }
            if (e.getCurrentItem().equals(nextPageButton)){
                if (currentLevelPage + 1 > maxLevelPages){
                    return;
                }
                currentLevelPage++;
            }
            if (e.getCurrentItem().equals(previousPageButton)){
                if (currentLevelPage - 1 < minLevelPages){
                    return;
                }
                currentLevelPage--;
            }
            setMenuItems();
        } else if (currentView == View.PERKS_AVAILABLE){
            if (clickedButton.getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Perk]"))){
                    ItemMeta iconMeta = clickedButton.getItemMeta();
                    if (iconMeta.getPersistentDataContainer().has(perkIDKey, PersistentDataType.INTEGER)){
                        int perkID = iconMeta.getPersistentDataContainer().get(perkIDKey, PersistentDataType.INTEGER);
                        PlayerPerksManager.getInstance().addLevelPerk(chosenLevel, perkID);
                    }
                }
            }
            if (clickedButton.equals(previousPageButton)) {
                if ((currentPage - 1) < 0) {
                    return;
                }
                currentPage--;
            } else if (clickedButton.equals(nextPageButton)){
                if ((currentPage + 1) >= pagesLength){
                    return;
                }
                currentPage++;
            }
        } else if (currentView == View.PERKS_CHOSEN){
            if (clickedButton.equals(addPerkButton)){
                currentView = View.PERKS_AVAILABLE;
            } else if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Activated Perk]"))){
                    ItemMeta iconMeta = clickedButton.getItemMeta();
                    if (iconMeta.getPersistentDataContainer().has(perkIDKey, PersistentDataType.INTEGER)){
                        int perkID = iconMeta.getPersistentDataContainer().get(perkIDKey, PersistentDataType.INTEGER);
                        PlayerPerksManager.getInstance().removeLevelPerk(chosenLevel, perkID);
                    }
                }
            }
        }
        if (clickedButton.equals(backToMenuButton)){
            if (currentView == View.PERKS_AVAILABLE){
                currentView = View.PERKS_CHOSEN;
            } else if (currentView == View.PERKS_CHOSEN){
                currentView = View.LEVELS;
            }
        }
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        this.inventory.clear();
        if (currentView == View.LEVELS){
            setLevelsView();
        } else if (currentView == View.PERKS_CHOSEN){
            setPerksChosenView();
        } else if (currentView == View.PERKS_AVAILABLE){
            setPerksAvailableView();
        }
    }

    private void setLevelsView(){
        if (currentLevelPage == 0){
            for (int i = -10; i < 11; i++){
                Material m = Material.WHITE_CONCRETE;
                if (i > -11) m = Material.RED_CONCRETE;
                if (i > -6) m = Material.ORANGE_CONCRETE;
                if (i == 0) m = Material.WHITE_CONCRETE;
                if (i > 0) m = Material.LIME_CONCRETE;
                if (i > 5) m = Material.GREEN_CONCRETE;
                List<String> iconLore = new ArrayList<>();

                List<Perk> perks = PlayerPerksManager.getInstance().getLevelsPerks(i);
                if (perks.size() != 0){
                    for (Perk p : perks){
                        iconLore.add(Utils.chat(String.format("&7%s", p.getDisplayName())));
                    }
                } else {
                    iconLore.add(Utils.chat("&7No perks assigned to level yet"));
                }
                ItemStack icon = Utils.createItemStack(m, Utils.chat("&f&lLevel " + i), iconLore);
                inventory.setItem(mainLevelItemOrder.get(i + 10), icon);
            }
        } else {
            boolean isNegative = (currentLevelPage < 0);
            int rangeMinimum = (10 * ((isNegative) ? -currentLevelPage : currentLevelPage)) + 1;
            int index = 0;
            Material m = (currentLevelPage < 0) ? Material.ORANGE_CONCRETE : Material.LIME_CONCRETE;
            for (int i = rangeMinimum; i < rangeMinimum + 10; i++) {
                if (isNegative){
                    if (config.getMinKarmaLevel() > -i) {
                        index++;
                        continue;
                    }
                } else {
                    if (config.getMaxKarmaLevel() < i) {
                        index++;
                        continue;
                    }
                }
                if (index == 5) m = (currentLevelPage < 0) ? Material.RED_CONCRETE : Material.GREEN_CONCRETE;

                List<String> iconLore = new ArrayList<>();

                List<Perk> perks = PlayerPerksManager.getInstance().getLevelsPerks((isNegative) ? -i : i);
                if (perks.size() != 0){
                    for (Perk p : perks){
                        iconLore.add(Utils.chat(String.format("&7%s", p.getDisplayName())));
                    }
                } else {
                    iconLore.add(Utils.chat("&7No perks assigned to level yet"));
                }
                ItemStack icon = Utils.createItemStack(m, Utils.chat("&f&lLevel " + ((isNegative) ? -i : i)), iconLore);
                inventory.setItem(subLevelItemOrder.get(index), icon);
                index++;
            }
        }
        inventory.setItem(18, previousPageButton);
        inventory.setItem(26, nextPageButton);
    }

    private void setPerksChosenView(){
        PlayerPerksManager perkManager = PlayerPerksManager.getInstance();
        for (Perk perk : perkManager.getLevelsPerks(chosenLevel)){
            List<String> perkIconLore = new ArrayList<>(Arrays.asList(
                    Utils.chat("&7&l[Activated Perk]"),
                    Utils.chat("&8&m                                          ")));
            perkIconLore.addAll(perk.getDescription());
            Material iconMaterial = perk.getIcon();
            ItemStack icon = Utils.createItemStack(iconMaterial, Utils.chat("&f&l" + perk.getDisplayName()), perkIconLore);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.getPersistentDataContainer().set(perkIDKey, PersistentDataType.INTEGER, perk.getId());
            icon.setItemMeta(iconMeta);
            icon.setAmount(1);
            inventory.addItem(icon);
        }

        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 36){
            inventory.addItem(addPerkButton);
        }

        inventory.setItem(40, backToMenuButton);
    }

    private void setPerksAvailableView(){
        List<ItemStack> availablePerks = new ArrayList<>();
        PlayerPerksManager perkManager = PlayerPerksManager.getInstance();

        Map<String, Perk> sortedPerks = new TreeMap<>();
        for (Perk p : perkManager.getAllPerks().values()){
            sortedPerks.put(ChatColor.stripColor(p.getDisplayName()), p);
        }

        for (String perkName : sortedPerks.keySet()){
            Perk perk = sortedPerks.get(perkName);
            if (!perkManager.getLevelsPerks(chosenLevel).contains(perk)){
                List<String> perkIconLore = new ArrayList<>(Arrays.asList(
                        Utils.chat("&7&l[Perk]"),
                        Utils.chat("&8&m                                          ")));
                perkIconLore.addAll(perk.getDescription());
                ItemStack icon = Utils.createItemStack(perk.getIcon(), Utils.chat("&f&l" + perk.getDisplayName()), perkIconLore);
                ItemMeta iconMeta = icon.getItemMeta();
                iconMeta.getPersistentDataContainer().set(perkIDKey, PersistentDataType.INTEGER, perk.getId());
                icon.setItemMeta(iconMeta);
                icon.setAmount(1);
                availablePerks.add(icon);
            }
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(36, availablePerks);
        pagesLength = pages.size();

        if (pagesLength != 0){
            if (currentPage >= pagesLength) currentPage = 0;
            for (ItemStack l : pages.get(currentPage)){
                inventory.addItem(l);
            }
        }

        inventory.setItem(36, previousPageButton);
        inventory.setItem(40, backToMenuButton);
        inventory.setItem(44, nextPageButton);
    }

    private enum View{
        LEVELS,
        PERKS_AVAILABLE,
        PERKS_CHOSEN
    }
}
