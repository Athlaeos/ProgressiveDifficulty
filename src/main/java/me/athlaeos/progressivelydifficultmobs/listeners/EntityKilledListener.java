package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.perks.Perk;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.states.KarmaGainReason;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EntityKilledListener implements Listener {

    private final NamespacedKey monsterKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
    private final NamespacedKey monsterCursedKey = new NamespacedKey(Main.getInstance(), "pdm-curse");

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (!(e.getEntity() instanceof Player)){
            CooldownManager.getInstance().removeEntity(e.getEntity());
        } else {
            PlayerPerksManager manager = PlayerPerksManager.getInstance();
            for (Perk perk : manager.sortPerksByPriority(manager.getPlayersTotalPerks((Player) e.getEntity()))){
                perk.execute(e);
            }
        }

        CompatibleEntityManager categoryManager = CompatibleEntityManager.getInstance();
        LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
        PluginConfigurationManager configManager = PluginConfigurationManager.getInstance();
        PlayerKarmaManager karmaManager = PlayerKarmaManager.getInstance();
        LootTableManager lootTableManager = LootTableManager.getInstance();
        if (e.getEntity().getKiller() != null){
            Player killer = e.getEntity().getKiller();
            LivingEntity mob = e.getEntity();

            if (mob instanceof Raider){
                if (((Raider) mob).isPatrolLeader()){
                    Utils.applyBadOmen(killer);
                }
            }

            if (categoryManager.getHostileMobIcons().containsKey(mob.getType()) || categoryManager.getPassiveMobIcons().containsKey(mob.getType())){
                PersistentDataContainer container = mob.getPersistentDataContainer();
                String mobId;
                if(container.has(monsterKey, PersistentDataType.STRING)) {
                    mobId = container.get(monsterKey, PersistentDataType.STRING);

                    LeveledMonster monster = monsterManager.getMonster(mobId);
                    if (monster != null){
                        if (!monster.isDropsDefaultLootTable()){
                            e.getDrops().clear();
                        }
                        List<ItemStack> extraLoot = lootTableManager.getRandomDrops(killer.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS), monster.getLootTables());

                        if (mob instanceof Slime){
                            e.setDroppedExp((int) Math.floor(monster.getExpDropped() / (16/Math.pow(((Slime) mob).getSize(), 2))));
                            karmaManager.addKarma(killer, monster.getKarmaInfluence() / (16/Math.pow(((Slime) mob).getSize(), 2)),
                                    KarmaGainReason.ENTITY_KILL,
                                    true, false);
                            if (((Slime) mob).getSize() == 1){
                                e.getDrops().addAll(extraLoot);
                            }
                        } else {
                            e.setDroppedExp(monster.getExpDropped());
                            karmaManager.addKarma(killer, monster.getKarmaInfluence(),
                                    KarmaGainReason.ENTITY_KILL,
                                    true, false);
                            e.getDrops().addAll(extraLoot);
                        }
                    }
                } else {
                    if (categoryManager.getHostileMobIcons().containsKey(mob.getType())){
                        karmaManager.addKarma(killer, configManager.getDefaultKarmaOnHostileKilled(),
                                KarmaGainReason.ENTITY_KILL,
                                true, false);
                    }
                    else if (categoryManager.getPassiveMobIcons().containsKey(mob.getType())){
                        karmaManager.addKarma(killer, configManager.getDefaultKarmaOnPassiveKilled(),
                                KarmaGainReason.ENTITY_KILL,
                                true, false);
                    }
                }
            }

            if (mob.getPersistentDataContainer().has(monsterCursedKey, PersistentDataType.STRING)){
                CursedMonsterManager.getInstance().removeCursedEntity(mob);
                if (PluginConfigurationManager.getInstance().isCursedDropPrevention()){
                    if (CooldownManager.getInstance().getCooldown(killer, "enable_curse_drops_duration") == 0){
                        e.getDrops().clear();
                    }
                } else if (PluginConfigurationManager.getInstance().isCurseDropDoubling()){
                    e.getDrops().addAll(e.getDrops());
                }
                e.setDroppedExp((int) Math.floor(e.getDroppedExp() * configManager.getCurseEXPMultiplier()));
            }
        }

        if (e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if (karmaManager.getKarma(p.getUniqueId()) < 0){
                if (configManager.isEvilDeathKarmaPenaltyFractional()){
                    double amount = -((configManager.getEvilDeathKarmaPenalty()/100) * karmaManager.getKarma(p.getUniqueId()));
                    karmaManager.addKarma(p, amount, KarmaGainReason.PLAYER_DEATH, configManager.isEvilDeathKarmaPenaltyMitigated(), false);
                } else {
                    karmaManager.addKarma(p, configManager.getEvilDeathKarmaPenalty(), KarmaGainReason.PLAYER_DEATH, configManager.isEvilDeathKarmaPenaltyMitigated(), false);
                }
            } else if (karmaManager.getKarma(p.getUniqueId()) > 0){
                if (configManager.isGoodDeathKarmaPenaltyFractional()){
                    double amount = (configManager.getGoodDeathKarmaPenalty()/100) * karmaManager.getKarma(p.getUniqueId());
                    karmaManager.addKarma(p, amount, KarmaGainReason.PLAYER_DEATH, configManager.isGoodDeathKarmaPenaltyMitigated(), false);
                } else {
                    karmaManager.addKarma(p, configManager.getGoodDeathKarmaPenalty(), KarmaGainReason.PLAYER_DEATH, configManager.isGoodDeathKarmaPenaltyMitigated(), false);
                }
            }
        }
    }
}
