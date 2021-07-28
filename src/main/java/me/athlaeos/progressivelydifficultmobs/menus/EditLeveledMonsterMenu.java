package me.athlaeos.progressivelydifficultmobs.menus;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.persistence.LeveledMonsterPersister;
import me.athlaeos.progressivelydifficultmobs.persistence.LootTablePersister;
import me.athlaeos.progressivelydifficultmobs.pojo.*;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


// WARNING
// This is the longest and ugliest class of the entire plugin at almost 1100 lines of code of inconsistent coding practices
// If you end up working on this, which you shouldn't because you clearly decompiled it without permission, I'm sorry anyway
public class EditLeveledMonsterMenu extends Menu {

    private final LootTableManager lootTableManager = LootTableManager.getInstance();

    private final boolean editExistingMonster;
    private final DecimalFormat df = new DecimalFormat("#.###");

    private View currentView = View.MAIN;

    private int spawnWeight = 10;
    private double health = 20;
    private int expDropped = 0;
    private List<String> lootTables = new ArrayList<>();
    private boolean enabled = false;
    private double karmaInfluence = 0;
    private boolean dropsDefaultLootTable = true;
    private boolean displayCustomName = false;
    private boolean isBoss = false;
    private List<String> biomeFilter = new ArrayList<>();
    private List<String> worldFilter = new ArrayList<>();
    private List<String> regionFilter = new ArrayList<>();
    private List<String> abilityList = new ArrayList<>();
    private int minYRange = 0;
    private int maxYRange = 255;
    private double helmetDropChance = 0D;
    private double chestplateDropChance = 0D;
    private double leggingsDropChance = 0D;
    private double bootsDropChance = 0D;
    private double mainHandDropChance = 0D;
    private double offHandDropChance = 0D;

    private ItemStack helmet = null;
    private ItemStack chestPlate = null;
    private ItemStack leggings = null;
    private ItemStack boots = null;
    private ItemStack mainHand = null;
    private ItemStack offHand = null;
    private ItemStack helmetLabel = null;
    private ItemStack chestPlateLabel = null;
    private ItemStack leggingsLabel = null;
    private ItemStack bootsLabel = null;
    private ItemStack mainHandLabel = null;
    private ItemStack offHandLabel = null;

    private ItemStack spawnWeightButton;
    private ItemStack healthButton;
    private ItemStack expDroppedButton;
    private ItemStack enabledButton;
    private ItemStack karmaInfluenceButton;
    private ItemStack dropsDefaultLootTableButton;
    private ItemStack lootTablesButton;
    private ItemStack displayNameVisibleButton;
    private ItemStack isBossButton;
    private ItemStack minYRangeButton;
    private ItemStack maxYRangeButton;
    private ItemStack biomeFilterButton;
    private ItemStack regionFilterButton;
    private ItemStack worldFilterButton;
    private ItemStack abilitiesButton;

    private final ItemStack resetEquipmentButton = Utils.createItemStack(Material.CHAINMAIL_CHESTPLATE, Utils.chat("&cReset equipment"),
            Arrays.asList(
                    Utils.chat("&7Changes the mob's equipment to"),
                    Utils.chat("&7your currently equipped gear."),
                    Utils.chat("&7Careful, as this can only be"),
                    Utils.chat("&7undone by cancelling and starting over"),
                    Utils.chat("&7before saving.")
                    ));
    private final ItemStack createMobButton = Utils.createItemStack(Material.GREEN_STAINED_GLASS_PANE, Utils.chat("&a&lFinish mob"), null);
    private final ItemStack deleteButton = Utils.createItemStack(Material.RED_STAINED_GLASS_PANE, null, null);
    private final ItemStack equipmentInfoLabel = Utils.createItemStack(Material.PAPER, Utils.chat("&fAbout worn equipment"),
            Arrays.asList(
                    Utils.chat("&7Your currently held armor and"),
                    Utils.chat("&7equipment (including off-hand"),
                    Utils.chat("&7and main-hand) will be copied"),
                    Utils.chat("&7over to a new mob/entity"),
                    Utils.chat("&7and displayed here."),
                    Utils.chat("&8&m                                          "),
                    Utils.chat("&7If you have not yet equipped"),
                    Utils.chat("&7your desired equipment close"),
                    Utils.chat("&7this menu and try again."),
                    Utils.chat("&7You don't need to do this when"),
                    Utils.chat("&7updating an already-existing"),
                    Utils.chat("&7mob.")));
    private final ItemStack createNewLootTableButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE, Utils.chat("&a&lAdd new LootTable"), null);

    private ItemStack mobInfoLabel;

    private boolean areYouSure = false;

    //these variables are exclusively used in the loot table overview submenu
    private final ItemStack addListElementButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE, Utils.chat("&a&lAdd new"), null);
    private final ItemStack backToMenuButton = Utils.createItemStack(Material.BOOK, Utils.chat("&7&lBack"), null);

    //these variables are exclusively used in the loot table selection submenu
    int currentPage = 0;
    private final ItemStack previousPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lPrevious page"), null);
    private final ItemStack nextPageButton = Utils.createItemStack(Material.SPECTRAL_ARROW, Utils.chat("&a&lNext page"), null);
    private int pagesLength;

    public EditLeveledMonsterMenu(PlayerMenuUtility playerMenuUtility, boolean editExistingMonster) {
        super(playerMenuUtility);
        this.editExistingMonster = editExistingMonster;

        if (editExistingMonster){
            LeveledMonster currentMonster = playerMenuUtility.getCurrentMonster();
            spawnWeight = currentMonster.getSpawnWeight();
            health = currentMonster.getBaseHealth();
            expDropped = currentMonster.getExpDropped();
            enabled = currentMonster.isEnabled();
            karmaInfluence = currentMonster.getKarmaInfluence();
            lootTables = new ArrayList<>(currentMonster.getLootTables());
            dropsDefaultLootTable = currentMonster.isDropsDefaultLootTable();
            isBoss = currentMonster.isBoss();
            displayCustomName = currentMonster.isDisplayNameVisible();
            minYRange = currentMonster.getMinYRange();
            maxYRange = currentMonster.getMaxYRange();
            biomeFilter = new ArrayList<>(currentMonster.getBiomeFilter());
            worldFilter = new ArrayList<>(currentMonster.getWorldFilter());
            abilityList = new ArrayList<>(currentMonster.getAbilities());
            regionFilter = new ArrayList<>(currentMonster.getRegionFilter());
            helmetDropChance = currentMonster.getHelmetDropChance();
            chestplateDropChance = currentMonster.getChestplateDropChance();
            leggingsDropChance = currentMonster.getLeggingsDropChance();
            bootsDropChance = currentMonster.getBootsDropChance();
            mainHandDropChance = currentMonster.getMainHandDropChance();
            offHandDropChance = currentMonster.getOffHandDropChance();

            helmet = currentMonster.getHelmet();
            chestPlate = currentMonster.getChestPlate();
            leggings = currentMonster.getLeggings();
            boots = currentMonster.getBoots();
            mainHand = currentMonster.getMainHand();
            offHand = currentMonster.getOffHand();

            Utils.changeDisplayName(deleteButton, Utils.chat("&c&lDelete"));
        } else {
            helmet = playerMenuUtility.getOwner().getInventory().getHelmet();
            chestPlate = playerMenuUtility.getOwner().getInventory().getChestplate();
            leggings = playerMenuUtility.getOwner().getInventory().getLeggings();
            boots = playerMenuUtility.getOwner().getInventory().getBoots();
            mainHand = playerMenuUtility.getOwner().getInventory().getItemInMainHand();
            offHand = playerMenuUtility.getOwner().getInventory().getItemInOffHand();

            Utils.changeDisplayName(deleteButton, Utils.chat("&c&lCancel"));
        }

        updateValueButtons();
    }

    @Override
    public String getMenuName() {
        String name = "&7&lEditing ";
        if (playerMenuUtility.hasSelectedGlobalSpawns()){
            name += "globally spawning ";
        } else {
            name += "lv " + playerMenuUtility.getCurrentMonster().getLevel()+ " ";
        }
        name += playerMenuUtility.getCurrentMonster().getName();
        return Utils.chat(name);
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (currentView == View.MAIN){
            if (e.getCurrentItem().equals(resetEquipmentButton)){
                helmet = playerMenuUtility.getOwner().getInventory().getHelmet();
                chestPlate = playerMenuUtility.getOwner().getInventory().getChestplate();
                leggings = playerMenuUtility.getOwner().getInventory().getLeggings();
                boots = playerMenuUtility.getOwner().getInventory().getBoots();
                mainHand = playerMenuUtility.getOwner().getInventory().getItemInMainHand();
                offHand = playerMenuUtility.getOwner().getInventory().getItemInOffHand();
            }

            if (e.getCurrentItem().equals(helmetLabel)){
                if (e.getClick() == ClickType.LEFT) {
                    helmetDropChance = alterValueDropChance(0.001, helmetDropChance);
                } else if (e.getClick() == ClickType.RIGHT){
                    helmetDropChance = alterValueDropChance(-0.001, helmetDropChance);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    helmetDropChance = alterValueDropChance(0.025, helmetDropChance);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    helmetDropChance = alterValueDropChance(-0.025, helmetDropChance);
                }
            }

            if (e.getCurrentItem().equals(chestPlateLabel)){
                if (e.getClick() == ClickType.LEFT) {
                    chestplateDropChance = alterValueDropChance(0.001, chestplateDropChance);
                } else if (e.getClick() == ClickType.RIGHT){
                    chestplateDropChance = alterValueDropChance(-0.001, chestplateDropChance);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    chestplateDropChance = alterValueDropChance(0.025, chestplateDropChance);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    chestplateDropChance = alterValueDropChance(-0.025, chestplateDropChance);
                }
            }

            if (e.getCurrentItem().equals(leggingsLabel)){
                if (e.getClick() == ClickType.LEFT) {
                    leggingsDropChance = alterValueDropChance(0.001, leggingsDropChance);
                } else if (e.getClick() == ClickType.RIGHT){
                    leggingsDropChance = alterValueDropChance(-0.001, leggingsDropChance);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    leggingsDropChance = alterValueDropChance(0.025, leggingsDropChance);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    leggingsDropChance = alterValueDropChance(-0.025, leggingsDropChance);
                }
            }

            if (e.getCurrentItem().equals(bootsLabel)){
                if (e.getClick() == ClickType.LEFT) {
                    bootsDropChance = alterValueDropChance(0.001, bootsDropChance);
                } else if (e.getClick() == ClickType.RIGHT){
                    bootsDropChance = alterValueDropChance(-0.001, bootsDropChance);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    bootsDropChance = alterValueDropChance(0.025, bootsDropChance);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    bootsDropChance = alterValueDropChance(-0.025, bootsDropChance);
                }
            }

            if (e.getCurrentItem().equals(mainHandLabel)){
                if (e.getClick() == ClickType.LEFT) {
                    mainHandDropChance = alterValueDropChance(0.001, mainHandDropChance);
                } else if (e.getClick() == ClickType.RIGHT){
                    mainHandDropChance = alterValueDropChance(-0.001, mainHandDropChance);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    mainHandDropChance = alterValueDropChance(0.025, mainHandDropChance);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    mainHandDropChance = alterValueDropChance(-0.025, mainHandDropChance);
                }
            }

            if (e.getCurrentItem().equals(offHandLabel)){
                if (e.getClick() == ClickType.LEFT) {
                    offHandDropChance = alterValueDropChance(0.001, offHandDropChance);
                } else if (e.getClick() == ClickType.RIGHT){
                    offHandDropChance = alterValueDropChance(-0.001, offHandDropChance);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    offHandDropChance = alterValueDropChance(0.025, offHandDropChance);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    offHandDropChance = alterValueDropChance(-0.025, offHandDropChance);
                }
            }

            if (e.getCurrentItem().equals(spawnWeightButton)){
                if (e.getClick() == ClickType.LEFT) {
                    alterSpawnWeight(1);
                } else if (e.getClick() == ClickType.RIGHT){
                    alterSpawnWeight(-1);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    alterSpawnWeight(10);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    alterSpawnWeight(-10);
                }
            }

            if (e.getCurrentItem().equals(minYRangeButton)){
                if (e.getClick() == ClickType.LEFT) {
                    alterMinYRange(1);
                } else if (e.getClick() == ClickType.RIGHT){
                    alterMinYRange(-1);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    alterMinYRange(10);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    alterMinYRange(-10);
                }
            }

            if (e.getCurrentItem().equals(maxYRangeButton)){
                if (e.getClick() == ClickType.LEFT) {
                    alterMaxYRange(1);
                } else if (e.getClick() == ClickType.RIGHT){
                    alterMaxYRange(-1);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    alterMaxYRange(10);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    alterMaxYRange(-10);
                }
            }

            if (e.getCurrentItem().equals(healthButton)){
                if (e.getClick() == ClickType.LEFT) {
                    alterHealth(1);
                } else if (e.getClick() == ClickType.RIGHT){
                    alterHealth(-1);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    alterHealth(10);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    alterHealth(-10);
                }
            }

            if (e.getCurrentItem().equals(expDroppedButton)){
                if (e.getClick() == ClickType.LEFT) {
                    alterExpDropped(1);
                } else if (e.getClick() == ClickType.RIGHT){
                    alterExpDropped(-1);
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    alterExpDropped(10);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    alterExpDropped(-10);
                }
            }

            if (e.getCurrentItem().equals(karmaInfluenceButton)){
                if (e.getClick() == ClickType.LEFT) {
                    karmaInfluence += 1;
                } else if (e.getClick() == ClickType.RIGHT){
                    karmaInfluence -= 1;
                } else if (e.getClick() == ClickType.SHIFT_LEFT){
                    karmaInfluence += 10;
                } else if (e.getClick() == ClickType.SHIFT_RIGHT){
                    karmaInfluence -= 10;
                }
            }

            if (e.getCurrentItem().equals(enabledButton)){
                if (enabled){
                    enabledButton.setType(Material.LEVER);
                    enabled = false;
                    Utils.changeDisplayName(enabledButton, Utils.chat("&e&lMonster spawning : &cDisabled"));
                } else {
                    enabledButton.setType(Material.REDSTONE_TORCH);
                    enabled = true;
                    Utils.changeDisplayName(enabledButton, Utils.chat("&e&lMonster spawning : &aEnabled"));
                }
            }

            if (e.getCurrentItem().equals(isBossButton)){
                isBoss = !isBoss;
            }

            if (e.getCurrentItem().equals(displayNameVisibleButton)){
                displayCustomName = !displayCustomName;
            }

            if (e.getCurrentItem().equals(dropsDefaultLootTableButton)){
                if (dropsDefaultLootTable){
                    dropsDefaultLootTableButton.setType(Material.RED_CONCRETE);
                    dropsDefaultLootTable = false;
                    Utils.changeDisplayName(dropsDefaultLootTableButton, Utils.chat("&e&lDrops default loot table : &cDisabled"));
                } else {
                    dropsDefaultLootTableButton.setType(Material.GREEN_CONCRETE);
                    dropsDefaultLootTable = true;
                    Utils.changeDisplayName(dropsDefaultLootTableButton, Utils.chat("&e&lDrops default loot table : &aEnabled"));
                }
            }

            if (e.getCurrentItem().equals(backToMenuButton)){
                new LeveledMonsterOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked()), playerMenuUtility.getSelectedMonster(), playerMenuUtility.getSelectedLevel(), playerMenuUtility.hasSelectedGlobalSpawns()).open();
                return;
            }

            if (e.getCurrentItem().equals(createMobButton)){
                playerMenuUtility.getCurrentMonster().setBaseHealth(health);
                playerMenuUtility.getCurrentMonster().setEquipment(helmet, chestPlate, leggings, boots, mainHand, offHand);
                playerMenuUtility.getCurrentMonster().setDropsDefaultLootTable(dropsDefaultLootTable);
                playerMenuUtility.getCurrentMonster().setEnabled(enabled);
                playerMenuUtility.getCurrentMonster().setExpDropped(expDropped);
                playerMenuUtility.getCurrentMonster().setKarmaInfluence(karmaInfluence);
                playerMenuUtility.getCurrentMonster().setLootTables(lootTables);
                playerMenuUtility.getCurrentMonster().setSpawnWeight(spawnWeight);
                playerMenuUtility.getCurrentMonster().setBoss(isBoss);
                playerMenuUtility.getCurrentMonster().setDisplayNameVisible(displayCustomName);
                playerMenuUtility.getCurrentMonster().setBiomeFilter(biomeFilter);
                playerMenuUtility.getCurrentMonster().setMinYRange(minYRange);
                playerMenuUtility.getCurrentMonster().setMaxYRange(maxYRange);
                playerMenuUtility.getCurrentMonster().setWorldFilter(worldFilter);
                playerMenuUtility.getCurrentMonster().setAbilities(abilityList);
                playerMenuUtility.getCurrentMonster().setHelmetDropChance(helmetDropChance);
                playerMenuUtility.getCurrentMonster().setChestplateDropChance(chestplateDropChance);
                playerMenuUtility.getCurrentMonster().setLeggingsDropChance(leggingsDropChance);
                playerMenuUtility.getCurrentMonster().setBootsDropChance(bootsDropChance);
                playerMenuUtility.getCurrentMonster().setMainHandDropChance(mainHandDropChance);
                playerMenuUtility.getCurrentMonster().setOffHandDropChance(offHandDropChance);
                playerMenuUtility.getCurrentMonster().setRegionFilter(regionFilter);

                e.getWhoClicked().sendMessage(Utils.chat("&aSaved changes!"));
                if (editExistingMonster){
                    LeveledMonsterManager.getInstance().updateMonster(playerMenuUtility.getCurrentMonster());
                    new LeveledMonsterOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked()), playerMenuUtility.getSelectedMonster(), playerMenuUtility.getSelectedLevel(), playerMenuUtility.hasSelectedGlobalSpawns()).open();
                } else {
                    LeveledMonsterManager.getInstance().registerMonster(playerMenuUtility.getCurrentMonster());
                    e.getWhoClicked().closeInventory();
                }
                LeveledMonsterPersister.saveLeveledMonster(playerMenuUtility.getCurrentMonster());
                return;
            } else if (e.getCurrentItem().equals(deleteButton)){
                if (editExistingMonster){
                    if (areYouSure){
                        LeveledMonsterPersister.deleteLeveledMonster(playerMenuUtility.getCurrentMonster());
                        LeveledMonsterManager.getInstance().deleteMonster(playerMenuUtility.getCurrentMonster().getName());
                        e.getWhoClicked().sendMessage(Utils.chat("&aDeleted monster"));
                        new LeveledMonsterOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked()), playerMenuUtility.getCurrentMonster().getEntityType(), playerMenuUtility.getCurrentMonster().getLevel(), playerMenuUtility.hasSelectedGlobalSpawns()).open();
                    } else {
                        e.getWhoClicked().sendMessage(Utils.chat("&aAre you sure you want to delete this monster?"));
                        e.getWhoClicked().sendMessage(Utils.chat("&aThis cannot be undone."));
                        e.getWhoClicked().sendMessage(Utils.chat("&aClick again to confirm"));
                        areYouSure = true;
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.chat("&aCancelled creation"));
                    e.getWhoClicked().closeInventory();
                }
            }

            if (e.getCurrentItem().equals(lootTablesButton)){
                currentView = View.LOOTTABLESSELECTED;
            }

            if (e.getCurrentItem().equals(regionFilterButton)){
                currentView = View.REGIONSSELECTED;
            }

            if (e.getCurrentItem().equals(biomeFilterButton)){
                currentView = View.BIOMESSELECTED;
            }

            if (e.getCurrentItem().equals(worldFilterButton)){
                currentView = View.WORLDSSELECTED;
            }

            if (e.getCurrentItem().equals(abilitiesButton)){
                currentView = View.ABILITIESSELECTED;
            }

            updateValueButtons();
        } else if (currentView == View.LOOTTABLESELECT){
            if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().contains(Utils.chat("&7&l[LootTable]"))){
                    LootTable table = LootTableManager.getInstance().getLootTableByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    if (table == null){
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cLootTable no longer exists!"));
                        setMenuItems();
                        return;
                    }
                    lootTables.add(table.getName());
                    currentPage = 0;
                }
            }
            if (e.getCurrentItem().equals(previousPageButton)) {
                if ((currentPage - 1) < 0) {
                    return;
                }
                currentPage--;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                if ((currentPage + 1) >= pagesLength){
                    return;
                }
                currentPage--;
            } else if (e.getCurrentItem().equals(createNewLootTableButton)){
                LootTable table = new LootTable("loot_table_" + lootTableManager.getNextAvailableLootTable(), new ArrayList<>(), Material.BOOK);
                lootTableManager.registerLootTable(table);
                playerMenuUtility.setLootTable(table);
                playerMenuUtility.setPreviousMenu(this);
                LootTablePersister.saveLootTables();
                new LootTableOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(playerMenuUtility.getOwner())).open();
            }
        } else if (currentView == View.LOOTTABLESSELECTED){
            if (e.getCurrentItem().equals(addListElementButton)){
                currentView = View.LOOTTABLESELECT;
            } else if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().contains(Utils.chat("&7&l[Implemented LootTable]"))){
                    LootTable table = LootTableManager.getInstance().getLootTableByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    if (table == null){
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cLootTable no longer exists!"));
                        setMenuItems();
                    } else {
                        lootTables.remove(table.getName());
                    }
                }
            }
        } else if (currentView == View.BIOMESELECT){
            if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Biome]"))){
                    String stringBiome = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    biomeFilter.add(stringBiome);
                }
            }
            if (e.getCurrentItem().equals(previousPageButton)) {
                if ((currentPage - 1) < 0) {
                    return;
                }
                currentPage--;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                if ((currentPage + 1) >= pagesLength){
                    return;
                }
                currentPage--;
            }
        } else if (currentView == View.BIOMESSELECTED){
            if (e.getCurrentItem().equals(addListElementButton)){
                currentView = View.BIOMESELECT;
            } else if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Whitelisted Biome]"))){
                    String stringBiome = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    biomeFilter.remove(stringBiome);
                }
            }
        } else if (currentView == View.WORLDSELECT){
            if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[World]"))){
                    String world = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    worldFilter.add(world);
                }
            }
            if (e.getCurrentItem().equals(previousPageButton)) {
                if ((currentPage - 1) < 0) {
                    return;
                }
                currentPage--;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                if ((currentPage + 1) >= pagesLength){
                    return;
                }
                currentPage--;
            }
        } else if (currentView == View.WORLDSSELECTED){
            if (e.getCurrentItem().equals(addListElementButton)){
                currentView = View.WORLDSELECT;
            } else if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Whitelisted World]"))){
                    String world = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    worldFilter.remove(world);
                }
            }
        } else if (currentView == View.ABILITYSELECT){
            if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Ability]"))){
                    String ability = AbilityManager.getInstance().getAbilityKeyByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    abilityList.add(ability);
                    if (abilityList.size() >= 45){
                        currentView = View.ABILITIESSELECTED;
                    }
                }
            }
            if (e.getCurrentItem().equals(previousPageButton)) {
                if ((currentPage - 1) < 0) {
                    return;
                }
                currentPage--;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                if ((currentPage + 1) >= pagesLength){
                    return;
                }
                currentPage++;
            }
        } else if (currentView == View.ABILITIESSELECTED){
            if (e.getCurrentItem().equals(addListElementButton)){
                currentView = View.ABILITYSELECT;
            } else if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Activated Ability]"))){
                    String abilityKey = AbilityManager.getInstance().getAbilityKeyByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    abilityList.remove(abilityKey);
                }
            }
        } else if (currentView == View.REGIONSELECT){
            if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Region]"))){
                    String stringRegion = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    regionFilter.add(stringRegion);
                }
            }
            if (e.getCurrentItem().equals(previousPageButton)) {
                if ((currentPage - 1) < 0) {
                    return;
                }
                currentPage--;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                if ((currentPage + 1) >= pagesLength){
                    return;
                }
                currentPage--;
            }
        } else if (currentView == View.REGIONSSELECTED){
            if (e.getCurrentItem().equals(addListElementButton)){
                currentView = View.REGIONSELECT;
            } else if (e.getCurrentItem().getItemMeta().hasLore()){
                if (e.getCurrentItem().getItemMeta().getLore().get(0).equals(Utils.chat("&7&l[Whitelisted Region]"))){
                    String stringRegion = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    regionFilter.remove(stringRegion);
                }
            }
        }

        if (e.getCurrentItem().equals(backToMenuButton)){
            if (currentView == View.LOOTTABLESELECT){
                currentView = View.LOOTTABLESSELECTED;
            } else if (currentView == View.LOOTTABLESSELECTED || currentView == View.BIOMESSELECTED || currentView == View.WORLDSSELECTED || currentView == View.ABILITIESSELECTED || currentView == View.REGIONSSELECTED){
                currentView = View.MAIN;
            } else if (currentView == View.BIOMESELECT){
                currentView = View.BIOMESSELECTED;
            } else if (currentView == View.WORLDSELECT){
                currentView = View.WORLDSSELECTED;
            } else if (currentView == View.ABILITYSELECT){
                currentView = View.ABILITIESSELECTED;
            } else if (currentView == View.REGIONSELECT){
                currentView = View.REGIONSSELECTED;
            }
        }

        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        if (currentView == View.MAIN){
            setMainMenu();
        } else if (currentView == View.LOOTTABLESSELECTED){
            setLootTablesSelectedMenu();
        } else if (currentView == View.LOOTTABLESELECT){
            setLootTableSelectMenu();
        } else if (currentView == View.BIOMESSELECTED) {
            setBiomesSelectedMenu();
        } else if (currentView == View.BIOMESELECT){
            setBiomeSelectMenu();
        } else if (currentView == View.WORLDSSELECTED){
            setWorldsSelectedMenu();
        } else if (currentView == View.WORLDSELECT){
            setWorldSelectMenu();
        } else if (currentView == View.ABILITIESSELECTED){
            setAbilitiesSelectedMenu();
        } else if (currentView == View.ABILITYSELECT){
            setAbilitySelectMenu();
        } else if (currentView == View.REGIONSSELECTED){
            setRegionsSelectedMenu();
        } else if (currentView == View.REGIONSELECT){
            setRegionSelectMenu();
        }
    }

    private void setMainMenu(){
        LeveledMonster currentMonster = playerMenuUtility.getCurrentMonster();
        mobInfoLabel = Utils.createItemStack(
                CompatibleEntityManager.getInstance().getAllMobIcons().get(currentMonster.getEntityType()),
                Utils.chat("&e&lEntity preview"),
                Arrays.asList(
                        Utils.chat(String.format("&7Type: &e%s", currentMonster.getEntityType().toString().toLowerCase())),
                        Utils.chat(String.format("&7Display name: &e%s", (currentMonster.getDisplayName() == null) ? "&7none" : currentMonster.getDisplayName())),
                        Utils.chat(String.format("&7Boss: &e%s", (isBoss) ? "&aYes" : "&cNo")),
                        Utils.chat(String.format("&7Health: &e%s", health)),
                        Utils.chat(String.format("&7Rarity: &e%s", spawnWeight)),
                        Utils.chat(String.format("&7Available LootTables: &e%s", lootTables.size())),
                        Utils.chat(String.format("&7Karma on death: &e%s", karmaInfluence)),
                        Utils.chat(String.format("&7Exp on death: &e%s", expDropped)),
                        Utils.chat(String.format("&7Drops normal drops: &e%s", dropsDefaultLootTable))
                ));

        List<String> lootTablesLore = new ArrayList<>(Arrays.asList(
                Utils.chat("&6Left click to edit."),
                Utils.chat("&8&m                                          "),
                Utils.chat("&7The LootTables the monster can"),
                Utils.chat("&7pick its drops from."),
                Utils.chat("&8&m                                          ")
        ));
        if (lootTables.size() > 0){
            for (String l : lootTables){
                if (lootTableManager.getLootTable(l) != null){
                    lootTablesLore.add(Utils.chat(String.format("&7%s : %s drops", lootTableManager.getLootTable(l).getName(), lootTableManager.getLootTable(l).getDrops().size())));
                }
            }
        } else {
            lootTablesLore.add(Utils.chat("&7No LootTables as of yet"));
        }
        lootTablesButton = Utils.createItemStack(Material.BOOK, Utils.chat("&e&lLootTables"), lootTablesLore);

        List<String> biomeFilterLore = new ArrayList<>(Arrays.asList(
                Utils.chat("&6Left click to edit."),
                Utils.chat("&8&m                                          "),
                Utils.chat("&7The biomes the monster can spawn"),
                Utils.chat("&7in."),
                Utils.chat("&7Leave empty to allow all biomes."),
                Utils.chat("&8&m                                          ")
        ));
        if (biomeFilter.size() > 0){
            for (String biome : biomeFilter){
                biomeFilterLore.add(Utils.chat(String.format("&7%s", biome)));
            }
        } else {
            biomeFilterLore.add(Utils.chat("&7Monster spawns in all biomes."));
        }
        biomeFilterButton = Utils.createItemStack(Material.GRASS_BLOCK, Utils.chat("&e&lBiome Whitelist"), biomeFilterLore);

        List<String> regionFilterLore = new ArrayList<>(Arrays.asList(
                Utils.chat("&6Left click to edit."),
                Utils.chat("&8&m                                          "),
                Utils.chat("&7The regions the monster can spawn"),
                Utils.chat("&7in."),
                Utils.chat("&7Leave empty to allow all regions."),
                Utils.chat("&8&m                                          ")
        ));
        if (regionFilter.size() > 0){
            for (String region : regionFilter){
                regionFilterLore.add(Utils.chat(String.format("&7%s", region)));
            }
        } else {
            regionFilterLore.add(Utils.chat("&7Monster spawns in all regions."));
        }
        regionFilterButton = Utils.createItemStack(Material.WOODEN_AXE, Utils.chat("&e&lRegion Whitelist"), regionFilterLore);

        List<String> worldFilterLore = new ArrayList<>(Arrays.asList(
                Utils.chat("&6Left click to edit."),
                Utils.chat("&8&m                                          "),
                Utils.chat("&7The worlds the monster can spawn"),
                Utils.chat("&7in."),
                Utils.chat("&7Leave empty to allow all worlds."),
                Utils.chat("&8&m                                          ")
        ));
        if (worldFilter.size() > 0){
            for (String world : worldFilter){
                worldFilterLore.add(Utils.chat(String.format("&7%s", world)));
            }
        } else {
            worldFilterLore.add(Utils.chat("&7Monster spawns in all worlds"));
        }
        worldFilterButton = Utils.createItemStack(Material.STONE, Utils.chat("&e&lWorld Whitelist"), worldFilterLore);

        List<String> abilitiesLore = new ArrayList<>(Arrays.asList(
                Utils.chat("&6Left click to edit."),
                Utils.chat("&8&m                                          "),
                Utils.chat("&7The abilities the monster can spawn"),
                Utils.chat("&7with."),
                Utils.chat("&8&m                                          ")
        ));
        if (abilityList.size() > 0){
            List<String> iterableAbilityList = new ArrayList<>(abilityList);
            for (String ability : iterableAbilityList){
                Ability a = AbilityManager.getInstance().getAbilities().get(ability);
                if (a == null){
                    abilityList.remove(ability);
                    continue;
                }
                abilitiesLore.add(Utils.chat(String.format("&7%s", ChatColor.stripColor(a.getName()))));
            }
        }
        if (abilityList.size() == 0){
            abilitiesLore.add(Utils.chat("&7No special abilities on this mob"));
        }
        abilitiesButton = Utils.createItemStack(Material.BLAZE_POWDER, Utils.chat("&e&lAbilities"), abilitiesLore);

        inventory.setItem(1, equipmentInfoLabel);

        inventory.setItem(10, helmetLabel);
        inventory.setItem(18, mainHandLabel);
        inventory.setItem(19, chestPlateLabel);
        inventory.setItem(20, offHandLabel);
        inventory.setItem(28, leggingsLabel);
        inventory.setItem(37, bootsLabel);

        inventory.setItem(13, dropsDefaultLootTableButton);
        inventory.setItem(14, isBossButton);
        inventory.setItem(15, enabledButton);
        inventory.setItem(16, displayNameVisibleButton);
        inventory.setItem(22, karmaInfluenceButton);
        if (playerMenuUtility.getCurrentMonster().getEntityType() != EntityType.ENDER_DRAGON){
            inventory.setItem(23, expDroppedButton);
        } else {
            inventory.setItem(23, Utils.createItemStack(
                    Material.RED_STAINED_GLASS_PANE,
                    Utils.chat("&cCustom exp disabled"),
                    Arrays.asList(
                            Utils.chat("&7Ender Dragons give their"),
                            Utils.chat("&7exp differently than other"),
                            Utils.chat("&7mobs, making it difficult"),
                            Utils.chat("&7to consistently change their"),
                            Utils.chat("&7exp reward."),
                            Utils.chat("&7Their exp reward is the default"),
                            Utils.chat("&7amount.")
                    )));
        }
        inventory.setItem(24, healthButton);
        inventory.setItem(25, spawnWeightButton);
        inventory.setItem(31, biomeFilterButton);
        inventory.setItem(32, lootTablesButton);
        inventory.setItem(33, abilitiesButton);
        inventory.setItem(34, worldFilterButton);
        inventory.setItem(38, resetEquipmentButton);
        inventory.setItem(41, minYRangeButton);
        inventory.setItem(42, maxYRangeButton);
        if (WorldguardManager.getWorldguardManager().useWorldGuard()) inventory.setItem(43, regionFilterButton);

        inventory.setItem(45, deleteButton);
        inventory.setItem(48, mobInfoLabel);
        inventory.setItem(50, backToMenuButton);
        inventory.setItem(53, createMobButton);
    }

    private void setBiomesSelectedMenu(){
        for (String b : biomeFilter){
            List<String> biomeIconLore = new ArrayList<>(Arrays.asList(
                    Utils.chat("&7&l[Whitelisted Biome]"),
                    Utils.chat("&8&m                                          "),
                    Utils.chat("&7If the base type of the monster"),
                    Utils.chat("&7spawns in this biome, the customized"),
                    Utils.chat("&7monster can only appear here."),
                    Utils.chat("&7Biomes that are not here will not"),
                    Utils.chat("&7spawn this monster type."),
                    Utils.chat("&7If this is left empty, mobs"),
                    Utils.chat("&7can spawn everywhere regardless"),
                    Utils.chat("&7of filter.")));
            Material iconMaterial = BiomeCategoryManager.getInstance().getAllBiomes().get(b).getValue();
            ItemStack icon = Utils.createItemStack(iconMaterial, Utils.chat("&f&l" + b), biomeIconLore);
            icon.setAmount(1);
            inventory.addItem(icon);
        }

        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 45){
            inventory.addItem(addListElementButton);
        }

        inventory.setItem(49, backToMenuButton);
    }

    private void setBiomeSelectMenu(){
        List<ItemStack> availableBiomes = new ArrayList<>();

        for (String stringBiome : BiomeCategoryManager.getInstance().getAllBiomes().keySet()){
            if (!biomeFilter.contains(stringBiome)){
                List<String> biomeIconLore = new ArrayList<>(Arrays.asList(
                        Utils.chat("&7&l[Biome]"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If the base type of the monster"),
                        Utils.chat("&7spawns in this biome, the customized"),
                        Utils.chat("&7monster can only appear here.")));
                ItemStack icon = Utils.createItemStack(BiomeCategoryManager.getInstance().getAllBiomes().get(stringBiome).getValue(), Utils.chat("&f&l" + stringBiome), biomeIconLore);
                icon.setAmount(1);
                availableBiomes.add(icon);
            }
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, availableBiomes);
        pagesLength = pages.size();

        if (pagesLength != 0){
            if (currentPage >= pagesLength) currentPage = 0;
            for (ItemStack l : pages.get(currentPage)){
                inventory.addItem(l);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, backToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    private void setWorldsSelectedMenu(){
        for (String world : worldFilter){
            List<String> worldIconLore = new ArrayList<>(Arrays.asList(
                    Utils.chat("&7&l[Whitelisted World]"),
                    Utils.chat("&8&m                                          "),
                    Utils.chat("&7If the base type of the monster"),
                    Utils.chat("&7spawns in the dimension of this world,"),
                    Utils.chat("&7the customized monster can only appear"),
                    Utils.chat("&7here. Worlds that are not here will not"),
                    Utils.chat("&7spawn this monster type."),
                    Utils.chat("&7If this is left empty, mobs"),
                    Utils.chat("&7can spawn in any world regardless"),
                    Utils.chat("&7of filter.")));
            ItemStack icon = Utils.createItemStack(Material.GRASS_BLOCK, Utils.chat("&f&l" + world), worldIconLore);
            icon.setAmount(1);
            inventory.addItem(icon);
        }

        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 45){
            inventory.addItem(addListElementButton);
        }

        inventory.setItem(49, backToMenuButton);
    }

    private void setWorldSelectMenu(){
        List<ItemStack> availableWorlds = new ArrayList<>();

        for (World world : Main.getInstance().getServer().getWorlds()){
            if (!worldFilter.contains(world.getName())){
                List<String> worldIconLore = new ArrayList<>(Arrays.asList(
                        Utils.chat("&7&l[World]"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If the base type of the monster"),
                        Utils.chat("&7spawns in this world or dimension,"),
                        Utils.chat("&7the customized monster can only"),
                        Utils.chat("&7appear here.")));
                ItemStack icon = Utils.createItemStack(Material.GRASS_BLOCK, Utils.chat("&f&l" + world.getName()), worldIconLore);
                icon.setAmount(1);
                availableWorlds.add(icon);
            }
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, availableWorlds);
        pagesLength = pages.size();

        if (pagesLength != 0){
            if (currentPage >= pagesLength) currentPage = 0;
            for (ItemStack l : pages.get(currentPage)){
                inventory.addItem(l);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, backToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    private void setRegionsSelectedMenu(){
        for (String region : regionFilter){
            List<String> regionIconLore = new ArrayList<>(Arrays.asList(
                    Utils.chat("&7&l[Whitelisted Region]"),
                    Utils.chat("&8&m                                          "),
                    Utils.chat("&7If a monster can naturally spawn"),
                    Utils.chat("&7in the area of this region, its"),
                    Utils.chat("&7customized variant can only be"),
                    Utils.chat("&7customized within these whitelisted"),
                    Utils.chat("&7regions."),
                    Utils.chat("&7If this is left empty, mobs"),
                    Utils.chat("&7can spawn in any region regardless"),
                    Utils.chat("&7of filter.")));
            ItemStack icon = Utils.createItemStack(Material.GOLDEN_AXE, Utils.chat("&f&l" + region), regionIconLore);
            icon.setAmount(1);
            inventory.addItem(icon);
        }

        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 45){
            inventory.addItem(addListElementButton);
        }

        inventory.setItem(49, backToMenuButton);
    }

    private void setRegionSelectMenu(){
        List<ItemStack> availableRegions = new ArrayList<>();
        for (String region : WorldguardManager.getWorldguardManager().getAllRegions()){
            if (!regionFilter.contains(region)){
                List<String> regionIconLore = new ArrayList<>(Arrays.asList(
                        Utils.chat("&7&l[Region]"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If the base type of the monster"),
                        Utils.chat("&7can spawn in this region,"),
                        Utils.chat("&7the customized monster can only"),
                        Utils.chat("&7appear here.")));
                ItemStack icon = Utils.createItemStack(Material.WOODEN_AXE, Utils.chat("&f&l" + region), regionIconLore);
                icon.setAmount(1);
                availableRegions.add(icon);
            }
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, availableRegions);
        pagesLength = pages.size();

        if (pagesLength != 0){
            if (currentPage >= pagesLength) currentPage = 0;
            for (ItemStack l : pages.get(currentPage)){
                inventory.addItem(l);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, backToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    private void setAbilitiesSelectedMenu(){
        for (String ability : abilityList){
            Ability a = AbilityManager.getInstance().getAbility(ability);
            if (a != null){
                List<String> abilityIconLore = new ArrayList<>(Arrays.asList(
                        Utils.chat("&7&l[Activated Ability]"),
                        Utils.chat("&8&m                                          ")));
                abilityIconLore.addAll(a.getDescription());
                ItemStack icon = Utils.createItemStack(a.getIcon(), Utils.chat("&f&l" + a.getName()), abilityIconLore);
                icon.setAmount(1);
                inventory.addItem(icon);
            }
        }

        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 45){
            inventory.addItem(addListElementButton);
        }

        inventory.setItem(49, backToMenuButton);
    }

    private void setAbilitySelectMenu(){
        List<ItemStack> availableAbilities = new ArrayList<>();

        for (String abilityId : AbilityManager.getInstance().getAbilities().keySet()){
            Ability a = AbilityManager.getInstance().getAbility(abilityId);
            if (a != null){
                List<EntityType> mobWhiteList = a.getMobWhiteList();
                if (mobWhiteList != null){
                    if (mobWhiteList.size() != 0){
                        if (!mobWhiteList.contains(playerMenuUtility.getCurrentMonster().getEntityType())){
                            continue;
                        }
                    }
                }
                if (!abilityList.contains(abilityId)){
                    List<String> abilityIconLore = new ArrayList<>(Arrays.asList(
                            Utils.chat("&7&l[Ability]"),
                            Utils.chat("&8&m                                          ")));
                    abilityIconLore.addAll(a.getDescription());
                    ItemStack icon = Utils.createItemStack(a.getIcon(), Utils.chat("&f&l" + a.getName()), abilityIconLore);
                    icon.setAmount(1);
                    availableAbilities.add(icon);
                }
            }
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, availableAbilities);
        pagesLength = pages.size();
        if (pagesLength != 0){
            if (currentPage >= pagesLength) {
                currentPage = 0;
            }
            for (ItemStack l : pages.get(currentPage)){
                inventory.addItem(l);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, backToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    private void setLootTablesSelectedMenu(){
        for (String l : lootTables){
            LootTable lt = lootTableManager.getLootTable(l);
            if (lt == null) continue;
            List<String> drops = new ArrayList<>(Arrays.asList(Utils.chat("&7&l[Implemented LootTable]"), Utils.chat("&7&l--Contents--")));
            for (Drop d : lt.getDrops()) {
                drops.add(Utils.chat(String.format("&7&a%s&7-&a%s &7of &a%s &7with &a%s%% &7chance", d.getMinAmountDrop(), d.getMaxAmountDrop(), d.getItem().getType(), d.getDropChance())));
            }
            ItemStack icon = Utils.createItemStack((lt.getIcon() == null) ? Material.BOOK : lt.getIcon(), Utils.chat("&f&l" + lt.getName()), drops);
            icon.setAmount(1);
            inventory.addItem(icon);
        }

        int occupiedSlots = 0;
        for (ItemStack i : inventory.getContents()){
            if (i != null){
                occupiedSlots++;
            }
        }

        if (occupiedSlots < 45){
            inventory.addItem(addListElementButton);
        }

        inventory.setItem(49, backToMenuButton);
    }

    private void setLootTableSelectMenu(){
        List<ItemStack> allLootTables = new ArrayList<>();

        for (LootTable l : lootTableManager.getAllLootTables()){
            if (!lootTables.contains(l.getName())){
                List<String> drops = new ArrayList<>(Arrays.asList(Utils.chat("&7&l[LootTable]"), Utils.chat("&7&l--Contents--")));
                for (Drop d : l.getDrops()) {
                    drops.add(Utils.chat(String.format("&7&a%s&7-&a%s &7of &a%s &7with &a%s%% &7chance", d.getMinAmountDrop(), d.getMaxAmountDrop(), d.getItem().getType(), d.getDropChance())));
                }
                ItemStack icon = Utils.createItemStack((l.getIcon() == null) ? Material.BOOK : l.getIcon(), Utils.chat("&f&l" + l.getName()), drops);
                icon.setAmount(1);
                allLootTables.add(icon);
            }
        }
        allLootTables.add(createNewLootTableButton);

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, allLootTables);
        pagesLength = pages.size();

        if (pagesLength != 0){
            for (ItemStack l : pages.get(currentPage)){
                inventory.addItem(l);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, backToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    private void updateValueButtons(){
        if (helmet != null){
            helmetLabel = helmet.clone();
            List<String> loreLines = new ArrayList<>();
            loreLines.add(Utils.chat(String.format("&6Helmet drop chance: &e&l%.1f%%", (helmetDropChance * 100))));
            loreLines.add(Utils.chat("&8&m                                          "));
            loreLines.addAll(Utils.getLoreLines(helmetLabel));
            Utils.setLoreLines(helmetLabel, loreLines);
        }
        if (chestPlate != null){
            chestPlateLabel = chestPlate.clone();
            List<String> loreLines = new ArrayList<>();
            loreLines.add(Utils.chat(String.format("&6Chestplate drop chance: &e&l%.1f%%", (chestplateDropChance * 100))));
            loreLines.add(Utils.chat("&8&m                                          "));
            loreLines.addAll(Utils.getLoreLines(chestPlateLabel));
            Utils.setLoreLines(chestPlateLabel, loreLines);
        }
        if (leggings != null){
            leggingsLabel = leggings.clone();
            List<String> loreLines = new ArrayList<>();
            loreLines.add(Utils.chat(String.format("&6Leggings drop chance: &e&l%.1f%%", (leggingsDropChance * 100))));
            loreLines.add(Utils.chat("&8&m                                          "));
            loreLines.addAll(Utils.getLoreLines(leggingsLabel));
            Utils.setLoreLines(leggingsLabel, loreLines);
        }
        if (boots != null){
            bootsLabel = boots.clone();
            List<String> loreLines = new ArrayList<>();
            loreLines.add(Utils.chat(String.format("&6Boots drop chance: &e&l%.1f%%", (bootsDropChance * 100))));
            loreLines.add(Utils.chat("&8&m                                          "));
            loreLines.addAll(Utils.getLoreLines(bootsLabel));
            Utils.setLoreLines(bootsLabel, loreLines);
        }
        if (mainHand != null){
            mainHandLabel = mainHand.clone();
            List<String> loreLines = new ArrayList<>();
            loreLines.add(Utils.chat(String.format("&6Main hand drop chance: &e&l%.1f%%", (mainHandDropChance * 100))));
            loreLines.add(Utils.chat("&8&m                                          "));
            loreLines.addAll(Utils.getLoreLines(mainHandLabel));
            Utils.setLoreLines(mainHandLabel, loreLines);
        }
        if (offHand != null){
            offHandLabel = offHand.clone();
            List<String> loreLines = new ArrayList<>();
            loreLines.add(Utils.chat(String.format("&6Off hand drop chance: &e&l%.1f%%", (offHandDropChance * 100))));
            loreLines.add(Utils.chat("&8&m                                          "));
            loreLines.addAll(Utils.getLoreLines(offHandLabel));
            Utils.setLoreLines(offHandLabel, loreLines);
        }

        spawnWeightButton = Utils.createItemStack(Material.PAPER, Utils.chat("&e&lSpawn chance (weight) : &f" + spawnWeight),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 1 and"),
                        Utils.chat("&6right click to decrease by 1"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&610 instead."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7The spawn chance for the mob to spawn"),
                        Utils.chat("&7compared to other spawnable monsters."),
                        Utils.chat("&7If all monsters of the same type and"),
                        Utils.chat("&7level have a combined weight of, for"),
                        Utils.chat("&7example, 200. A monster with a spawn"),
                        Utils.chat("&7weight of 100 will have a 50% chance"),
                        Utils.chat("&7to spawn instead of the other options."),
                        Utils.chat("&7Basically, lower = more rare!")));

        healthButton = Utils.createItemStack(Material.GOLDEN_APPLE, Utils.chat("&e&lBase health : &f" + health),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 1 and"),
                        Utils.chat("&6right click to decrease by 1"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&610 instead."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7The base health of the monster."),
                        Utils.chat("&7Each half heart equals 1 health point,"),
                        Utils.chat("&7so a monster with 20 health will have"),
                        Utils.chat("&710 hearts."),
                        Utils.chat("&7This can be further boosted by armor"),
                        Utils.chat("&7or other equipables' attributes")));

        expDroppedButton = Utils.createItemStack(Material.EXPERIENCE_BOTTLE, Utils.chat("&e&lEXP dropped on death : &f" + expDropped),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 1 and"),
                        Utils.chat("&6right click to decrease by 1"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&610 instead."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7Experience points dropped when the monster"),
                        Utils.chat("&7is killed by a player."),
                        Utils.chat("&7Vanilla monsters typically drop 5"),
                        Utils.chat("&7experience on death, while animals"),
                        Utils.chat("&7usually drop 1-3 (2 on average).")));

        karmaInfluenceButton = Utils.createItemStack(Material.NETHER_STAR, Utils.chat("&e&lKarma influence : &f" + karmaInfluence),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 1 and"),
                        Utils.chat("&6right click to decrease by 1"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&610 instead."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7Karma points granted or taken when killed."),
                        Utils.chat("&7Make negative for &cbad karma."),
                        Utils.chat("&7Make positive for &agood karma.")));

        isBossButton = Utils.createItemStack((isBoss) ? Material.END_CRYSTAL : Material.GLASS_PANE, Utils.chat("&e&lMonster is boss : " + ((isBoss) ? "&aYes" : "&cNo")),
                Arrays.asList(
                        Utils.chat("&6Left click to toggle between"),
                        Utils.chat("&6&ayes &6and &cno"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If enabled, the monster will create"),
                        Utils.chat("&7boss bar visible to surrounding"),
                        Utils.chat("&7players. Otherwise it won't")));

        displayNameVisibleButton = Utils.createItemStack(Material.NAME_TAG, Utils.chat("&e&lDisplayname visible : " + ((displayCustomName) ? "&aYes" : "&cNo")),
                Arrays.asList(
                        Utils.chat("&6Left click to toggle between"),
                        Utils.chat("&6&ayes &6and &cno"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If disabled, the monster's custom"),
                        Utils.chat("&7name will only be visible if you're "),
                        Utils.chat("&7looking directly at them. If enabled,"),
                        Utils.chat("&7the name will be visible through blocks"),
                        Utils.chat("&7and over large distances."),
                        Utils.chat("&7Recommended to enable for rare"),
                        Utils.chat("&7monsters or bosses.")));

        enabledButton = Utils.createItemStack((enabled) ? Material.REDSTONE_TORCH : Material.LEVER, Utils.chat("&e&lMonster spawning : " + ((enabled) ? "&aEnabled" : "&cDisabled")),
                Arrays.asList(
                        Utils.chat("&6Left click to toggle between"),
                        Utils.chat("&6&aenabled &6and &cdisabled"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If disabled, the monster will not"),
                        Utils.chat("&7spawn at all."),
                        Utils.chat("&7If you're working on a monster or"),
                        Utils.chat("&7its loot tables it's best to keep"),
                        Utils.chat("&7it disabled until you're done.")));

        dropsDefaultLootTableButton = Utils.createItemStack((dropsDefaultLootTable) ? Material.GREEN_CONCRETE : Material.RED_CONCRETE, Utils.chat("&e&lDrops default loot table : " + ((dropsDefaultLootTable) ? "&aEnabled" : "&cDisabled")),
                Arrays.asList(
                        Utils.chat("&6Left click to toggle between"),
                        Utils.chat("&6&aenabled &6and &cdisabled"),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7If enabled, the monster's default"),
                        Utils.chat("&7loot table will spawn on top of"),
                        Utils.chat("&7their given loot tables."),
                        Utils.chat("&7If disabled, it will only drop"),
                        Utils.chat("&7items from their custom loot tables")));

        minYRangeButton = Utils.createItemStack(Material.BEDROCK, Utils.chat("&e&lMinimum spawn height : &f" + minYRange),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 1 and"),
                        Utils.chat("&6right click to decrease by 1"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&610 instead."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7The minimum spawn height of this"),
                        Utils.chat("&7monster. If &eminimum&7 is set to"),
                        Utils.chat("&e0 &7and &bmaximum&7 set to &b64"),
                        Utils.chat("&7this monster will only spawn between"),
                        Utils.chat("&7y=&e0 &7and y=&b64&7 (aka underground)."),
                        Utils.chat("&dCurrent range: y=" + minYRange + "-" + maxYRange)));

        maxYRangeButton = Utils.createItemStack(Material.FEATHER, Utils.chat("&e&lMaximum spawn height : &f" + maxYRange),
                Arrays.asList(
                        Utils.chat("&6Left click to increase by 1 and"),
                        Utils.chat("&6right click to decrease by 1"),
                        Utils.chat("&6Shift-click to increase/decrease by"),
                        Utils.chat("&610 instead."),
                        Utils.chat("&8&m                                          "),
                        Utils.chat("&7The maximum spawn height of this"),
                        Utils.chat("&7monster. If &eminimum&7 is set to"),
                        Utils.chat("&e128 &7and &bmaximum&7 set to &b255"),
                        Utils.chat("&7this monster will only spawn between"),
                        Utils.chat("&7y=&e128 &7and y=&b255&7 (aka in the sky"),
                        Utils.chat("&7or above the nether roof)"),
                        Utils.chat("&dCurrent range: y=" + minYRange + "-" + maxYRange)));
    }

    private double alterValueDropChance(double value, double currentValue){
        double returnValue;
        if (value < 0) {
            if (currentValue == 0.0) {
                returnValue = 1.0;
            } else if ((currentValue + value) < 0) {
                returnValue = 0.0;
            } else {
                returnValue = currentValue + value;
            }
        } else {
            if (currentValue == 1.0) {
                returnValue = 0.0;
            } else if ((currentValue + value) > 1) {
                returnValue = 1.0;
            } else {
                returnValue = currentValue + value;
            }
        }
        returnValue = Double.parseDouble(df.format(returnValue));
        return returnValue;
    }

    private void alterSpawnWeight(int amount){
        if (amount < 0){
            if ((spawnWeight + amount) <= 0){
                spawnWeight = 0;
            } else {
                spawnWeight += amount;
            }
        } else {
            spawnWeight += amount;
        }
    }

    private void alterHealth(int amount){
        if (amount < 0){
            if ((health + amount) <= 1){
                health = 1;
            } else {
                health += amount;
            }
        } else {
            health += amount;
        }
    }

    private void alterExpDropped(int amount){
        if (amount < 0){
            if ((expDropped + amount) <= 0){
                expDropped = 0;
            } else {
                expDropped += amount;
            }
        } else {
            expDropped += amount;
        }
    }

    private void alterMinYRange(int amount){
        if (amount < 0){
            if ((minYRange + amount) <= 0){
                minYRange = 0;
            } else {
                minYRange += amount;
            }
        } else {
            if ((minYRange + amount) >= 254){
                minYRange = 254;
            } else {
                minYRange += amount;
            }
            if (minYRange > maxYRange){
                maxYRange = minYRange + 1;
            }
        }
    }

    private void alterMaxYRange(int amount){
        if (amount < 0){
            if ((maxYRange + amount) <= 1){
                maxYRange = 1;
            } else {
                maxYRange += amount;
            }
            if (maxYRange < minYRange){
                minYRange = maxYRange - 1;
            }
        } else {
            if ((maxYRange + amount) >= 255){
                maxYRange = 255;
            } else {
                maxYRange += amount;
            }
        }
    }

    private enum View{
        MAIN,
        LOOTTABLESSELECTED,
        LOOTTABLESELECT,
        ABILITYSELECT,
        ABILITIESSELECTED,
        BIOMESELECT,
        BIOMESSELECTED,
        WORLDSELECT,
        WORLDSSELECTED,
        REGIONSELECT,
        REGIONSSELECTED
    }
}
