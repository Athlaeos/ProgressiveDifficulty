package me.athlaeos.progressivelydifficultmobs.pojo;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {
    private Player owner;
    private int pageNumber = 0;
    private LootTable lootTable = null;
    private Drop currentDrop = null;
    private LeveledMonster currentMonster = null;
    private EntityType selectedMonster = null;
    private int selectedLevel = 0;
    private boolean selectedGlobalSpawns = false;
    private Menu previousMenu = null;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public void setPreviousMenu(Menu previousMenu) {
        this.previousMenu = previousMenu;
    }

    public Menu getPreviousMenu() {
        return previousMenu;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean hasSelectedGlobalSpawns() {
        return selectedGlobalSpawns;
    }

    public void setSelectedGlobalSpawns(boolean selectedGlobalSpawns) {
        this.selectedGlobalSpawns = selectedGlobalSpawns;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void incrementPageNumber(){
        pageNumber++;
    }

    public void decrementPageNumber(){
        pageNumber--;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Drop getCurrentDrop() {
        return currentDrop;
    }

    public void setCurrentDrop(Drop currentDrop) {
        this.currentDrop = currentDrop;
    }

    public LeveledMonster getCurrentMonster() {
        return currentMonster;
    }

    public void setCurrentMonster(LeveledMonster currentMonster) {
        this.currentMonster = currentMonster;
    }

    public EntityType getSelectedMonster() {
        return selectedMonster;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public void setSelectedMonster(EntityType selectedMonster) {
        this.selectedMonster = selectedMonster;
    }
}
