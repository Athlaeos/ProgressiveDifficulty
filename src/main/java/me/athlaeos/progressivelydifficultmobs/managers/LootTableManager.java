package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.pojo.Drop;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.pojo.LootTable;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LootTableManager {

    private static LootTableManager manager = null;
    private List<LootTable> allLootTables = new ArrayList<>();
    private boolean enabled = false;

    public LootTableManager(){

    }

    public static LootTableManager getInstance(){
        if (manager == null){
            manager = new LootTableManager();
        }
        return manager;
    }

    /*

     */
    public int getNextAvailableLootTable(){
        int nextAvailableID = 0;
        for (LootTable table : allLootTables){
            try {
                int thisTableID = Integer.parseInt(table.getName().replace("loot_table_", ""));
                if (thisTableID >= nextAvailableID){
                    nextAvailableID = thisTableID + 1;
                }
            } catch (IllegalArgumentException ignored){
            }
        }
        return nextAvailableID;
    }

    /**
     * @param name
     * @return the LootTable with the given integer ID
     */
    public LootTable getLootTable(String name){
        for (LootTable t : allLootTables){
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    /**
     * @param name
     * @return the LootTable with the given name, or null if it doesn't exist
     */
    public LootTable getLootTableByName(String name){
        for (LootTable t : allLootTables){
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    /**
     * @return a list of LootTables that have been registered
     */
    public List<LootTable> getAllLootTables() {
        return allLootTables;
    }

    /**
     * Registers a LootTable to be used with the plugin
     * @param table
     */
    public void registerLootTable(LootTable table){
        if (!allLootTables.contains(table)){
            allLootTables.add(table);
        }
    }

    /**
     * Deletes the given LootTable
     * @param table
     */
    public void deleteLootTable(LootTable table){
        for (LeveledMonster m : LeveledMonsterManager.getInstance().getAllMonsters()){
            if (m.getLootTables().contains(table.getName())) {
                m.removeLootTable(table.getName());
            }
        }
        allLootTables.removeIf(l -> l.getName().equals(table.getName()));
    }

    /**
     * Picks items randomly from the given LootTables and returns them.
     * @param lootingLevel
     * @param availableLootTables
     * @return a list of ItemStacks
     */
    public List<ItemStack> getRandomDrops(int lootingLevel, List<String> availableLootTables){
        if (enabled){
            List<ItemStack> droppedLoot = new ArrayList<>();
            for (String i : availableLootTables){
                LootTable t = getLootTable(i);
                if (t == null){
                    continue;
                }
                for (Drop d : t.getDrops()){
                    int dropChance = (int) ((d.getDropChance() + (d.getDropChanceLootingBonus() * lootingLevel)) * 10);
                    if (dropChance > 1000) dropChance = 1000;
                    if (Utils.getRandom().nextInt(1000) + 1 < dropChance){
                        int minDropped = d.getMinAmountDrop() + (lootingLevel * d.getMinAmountDropLootingBonus());
                        int maxDropped = d.getMaxAmountDrop() + (lootingLevel * d.getMaxAmountDropLootingBonus());
                        int amountDropped = Utils.getRandom().nextInt(maxDropped - minDropped + 1) + minDropped;
                        ItemStack droppedItem = new ItemStack(d.getItem());
                        droppedItem.setAmount(amountDropped);
                        droppedLoot.add(droppedItem);
                    }
                }
            }
            return droppedLoot;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @return true if LootTables have finished loading in, false if it's not ready yet.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not LootTables have finished loading in yet
     * This method should not be touched
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
