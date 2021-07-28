package me.athlaeos.progressivelydifficultmobs.menus;

import me.athlaeos.progressivelydifficultmobs.managers.CompatibleEntityManager;
import me.athlaeos.progressivelydifficultmobs.managers.LeveledMonsterManager;
import me.athlaeos.progressivelydifficultmobs.managers.PlayerMenuUtilManager;
import me.athlaeos.progressivelydifficultmobs.managers.PluginConfigurationManager;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.pojo.Menu;
import me.athlaeos.progressivelydifficultmobs.pojo.PlayerMenuUtility;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeveledMonsterOverviewMenu extends Menu {

    private final LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
    private final CompatibleEntityManager categoryManager = CompatibleEntityManager.getInstance();
    private final PluginConfigurationManager config = PluginConfigurationManager.getInstance();

    private int levelSelected;
    private boolean selectedGlobal;
    private int currentPage = 0;
    private int pagesLength;
    private int currentLevelPage = 0;
    private int minLevelPages;
    private int maxLevelPages;
    private EntityType currentEntity = null;
    private View currentView = View.CATEGORIES;

    //purely used in the level select menu
    private final List<Integer> mainLevelItemOrder = Arrays.asList(0, 1, 2, 3, 4, 9, 10, 11, 12, 13, 22, 31, 32, 33, 34, 35, 40, 41, 42 ,43 ,44);
    private final List<Integer> positiveLevelItemOrder = Arrays.asList(12, 13, 14, 20, 21, 23, 24, 30, 31, 32);

    private final ItemStack previousPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lPrevious page"), null);
    private final ItemStack nextPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lNext page"), null);
    private final ItemStack currentPageInfoLabel = Utils.createItemStack(Material.ENDER_EYE, Utils.chat("&a&lCurrent page: "), null);
    private final ItemStack backToMenuButton = Utils.createItemStack(Material.BOOK, Utils.chat("&7&lBack to monster selection menu"), null);

    public LeveledMonsterOverviewMenu(PlayerMenuUtility playerMenuUtility, EntityType currentEntity, int levelSelected, boolean selectedGlobal) {
        super(playerMenuUtility);
        if (currentEntity != null){
            this.currentEntity = currentEntity;
            this.levelSelected = levelSelected;
            this.selectedGlobal = selectedGlobal;
            currentView = View.LEVEL;
        }
        minLevelPages = ((int) -Math.ceil(-config.getMinKarmaLevel()/10D)) + 1;
        maxLevelPages = ((int) Math.ceil(config.getMaxKarmaLevel()/10D)) - 1;
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7&lAll monsters");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem().getItemMeta().hasLore()){
            if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Monster Type]"))){
                currentEntity = EntityType.valueOf(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                playerMenuUtility.setSelectedMonster(currentEntity);
                currentView = View.LEVELS;
                setMenuItems();
                return;
            }
        }

        if (currentView == View.LEVEL){
            if (!e.getCurrentItem().equals(backToMenuButton)) {
                LeveledMonster selectedMonster = monsterManager.getMonster(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                playerMenuUtility.setCurrentMonster(selectedMonster);
                new EditLeveledMonsterMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked()), true).open();
                return;
            }
        }

        if (currentView == View.LEVELS){
            if (!Arrays.asList(nextPageButton, previousPageButton, backToMenuButton).contains(e.getCurrentItem())) {
                try{
                    levelSelected = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
                    playerMenuUtility.setSelectedLevel(levelSelected);
                    if (monsterManager.getMonsterByTypeAndLevel(currentEntity, levelSelected).size() > 0){
                        currentView = View.LEVEL;
                        setMenuItems();
                        return;
                    }
                } catch (Exception ignored){
                    if (e.getCurrentItem().getItemMeta().getDisplayName().split(" ")[0].contains("Global")){
                        playerMenuUtility.setSelectedGlobalSpawns(true);
                        if (monsterManager.getMonsterByTypeAndIfGlobal(currentEntity).size() > 0){
                            currentView = View.LEVEL;
                            setMenuItems();
                            return;
                        }
                    } else {
                        return;
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
        }

        if (e.getCurrentItem().equals(backToMenuButton)){
            if (currentView == View.LEVELS){
                currentView = View.CATEGORIES;
            } else if (currentView == View.LEVEL){
                currentView = View.LEVELS;
                setMenuItems();
                return;
            }
        }

        if (e.getCurrentItem().equals(nextPageButton)){
            if (currentPage + 1 >= pagesLength){
                return;
            }
            currentPage++;
        }
        if (e.getCurrentItem().equals(previousPageButton)){
            if (currentPage - 1 < 0){
                return;
            }
            currentPage--;
        }
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        if (currentView == View.CATEGORIES){
            setCategoriesView();
        } else if (currentView == View.LEVELS){
            setLevelsView();
        } else if (currentView == View.LEVEL){
            setLevelView();
        }
    }

    private enum View {
        CATEGORIES,
        LEVELS,
        LEVEL
    }

    private void setLevelView(){
        List<LeveledMonster> spawnableMonsters;
        if (selectedGlobal){
            spawnableMonsters = monsterManager.getMonsterByTypeAndIfGlobal(currentEntity).stream().limit(45).collect(Collectors.toList());
        } else {
            spawnableMonsters = monsterManager.getMonsterByTypeAndLevel(currentEntity, levelSelected).stream().limit(45).collect(Collectors.toList());
        }
        for (LeveledMonster l : spawnableMonsters){
            ItemStack monsterIcon = Utils.createItemStack(
                    categoryManager.getAllMobIcons().get(currentEntity),
                    Utils.chat("&7&l" + l.getName()),
                    Arrays.asList(
                            Utils.chat(String.format("&7Display name: &e%s", l.getDisplayName())),
                            Utils.chat(String.format("&7Health: &e%s", l.getBaseHealth())),
                            Utils.chat(String.format("&7Rarity: &e%s", l.getSpawnWeight())),
                            Utils.chat(String.format("&7Available LootTables: &e%s", l.getLootTables().size())),
                            Utils.chat(String.format("&7Karma on death: &e%s", l.getKarmaInfluence())),
                            Utils.chat(String.format("&7Exp on death: &e%s", l.getExpDropped())),
                            Utils.chat(String.format("&7Drops normal drops: &e%s", l.isDropsDefaultLootTable()))
                    ));
            inventory.addItem(monsterIcon);
        }
        inventory.setItem(49, backToMenuButton);
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

                if (monsterManager.getMonsterByTypeAndLevel(currentEntity, i).size() != 0){
                    for (LeveledMonster monster : monsterManager.getMonsterByTypeAndLevel(currentEntity, i)){
                        iconLore.add(Utils.chat(String.format(((monster.isEnabled()) ? "&aE " : "&cD ") + ((monster.isBoss()) ? "&cBoss " : "") + "&e%s&7HP | &e%s&7 Karma | &e%s Spawn weight", monster.getBaseHealth(), monster.getKarmaInfluence(),
                                monster.getSpawnWeight())));
                    }
                } else {
                    iconLore.add(Utils.chat("&7No monsters registered for this type"));
                }
                ItemStack icon = Utils.createItemStack(m, Utils.chat("&f&lLevel " + i + " " + currentEntity), iconLore);
                inventory.setItem(mainLevelItemOrder.get(i + 10), icon);
            }
            //global spawns button
            List<String> iconLore = new ArrayList<>();

            if (monsterManager.getMonsterByTypeAndIfGlobal(currentEntity).size() != 0){
                for (LeveledMonster monster : monsterManager.getMonsterByTypeAndIfGlobal(currentEntity)){
                    iconLore.add(Utils.chat(String.format(((monster.isEnabled()) ? "&aE " : "&cD ") + ((monster.isBoss()) ? "&cBoss " : "") + "&e%s&7HP | &e%s&7 Karma | &e%s Spawn weight", monster.getBaseHealth(), monster.getKarmaInfluence(),
                            monster.getSpawnWeight())));
                }
            } else {
                iconLore.add(Utils.chat("&7No global monsters registered for this type"));
            }
            ItemStack icon = Utils.createItemStack(Material.GRAY_CONCRETE, Utils.chat("&f&lGlobal " + currentEntity), iconLore);
            inventory.setItem(37, icon);
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

                List<LeveledMonster> monsters = monsterManager.getMonsterByTypeAndLevel(currentEntity, (isNegative) ? -i : i);
                if (monsters.size() != 0) {
                    for (LeveledMonster monster : monsters) {
                        iconLore.add(Utils.chat(String.format(((monster.isEnabled()) ? "&aE " : "&cD ") + ((monster.isBoss()) ? "&cBoss " : "") + "&e%s&7HP | &e%s&7 Karma | &e%s Spawn weight", monster.getBaseHealth(), monster.getKarmaInfluence(),
                                monster.getSpawnWeight())));
                    }
                } else {
                    iconLore.add(Utils.chat("&7No monsters registered for this type"));
                }
                ItemStack icon = Utils.createItemStack(m, Utils.chat("&f&lLevel " + ((isNegative) ? -i : i) + " " + currentEntity), iconLore);
                inventory.setItem(positiveLevelItemOrder.get(index), icon);
                index++;
            }
        }
        inventory.setItem(18, previousPageButton);
        inventory.setItem(26, nextPageButton);
        inventory.setItem(49, backToMenuButton);
    }

    private void setCategoriesView(){
        Utils.changeDisplayName(currentPageInfoLabel, Utils.chat("&a&lCurrent page: " + currentPage + "/" + pagesLength));

        List<ItemStack> allIcons = new ArrayList<>();
        for (EntityType e : categoryManager.getHostileMobIcons().keySet()){
            List<String> iconLore = new ArrayList<>();
            iconLore.add(Utils.chat("&7&l[Monster Type]"));
            if (monsterManager.getMonstersByType(e).size() != 0){
                iconLore.add(Utils.chat("&7" + monsterManager.getMonstersByType(e).size() + " registered for type " + e.toString()));
            } else {
                iconLore.add(Utils.chat("&7No monsters registered for this type"));
            }

            ItemStack icon = Utils.createItemStack(
                    categoryManager.getAllMobIcons().get(e),
                    Utils.chat("&f&l" + e.toString()),
                    iconLore);

            allIcons.add(icon);
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, allIcons);
        pagesLength = pages.size();

        for (ItemStack i : pages.get(currentPage)){
            inventory.addItem(i);
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, currentPageInfoLabel);
        inventory.setItem(53, nextPageButton);
    }
}
