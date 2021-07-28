package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.abilities.*;
import me.athlaeos.progressivelydifficultmobs.pojo.Ability;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.ChatColor;

import java.util.*;

public class AbilityManager {

    private static AbilityManager manager = null;
    private Map<String, Ability> instantAbilities = new HashMap<>(); // abilities executed when monster spawns
    private Map<String, Ability> onPlayerDamagedAbilities = new HashMap<>(); // abilities executed when player is hit by mob
    private Map<String, Ability> runningAbilities = new HashMap<>(); // abilities executed when monster spawns, involving runnables
    private Map<String, Ability> onMobDamagedByPlayerAbilities = new HashMap<>(); // abilities executed when player hits mob
    private Map<String, Ability> onTargetEntityAbilities = new HashMap<>(); // abilities executed when monster targets player
    private Map<String, Ability> onMobDamagedAbilities = new HashMap<>(); // abilities executed when monster is damaged
    private Map<String, Ability> onMobClickedAbilities = new HashMap<>(); // abilities executed when monster is clicked

    private Map<String, Ability> allAbilities = new TreeMap<>(); // a collection of all abilities

    public AbilityManager(){
        getAbilities();
        if (PluginConfigurationManager.getInstance().useAnimationRunnables()){
            runningAbilities.put("aaa_pdm_damage_aura", new DamageAuraAbility());
            runningAbilities.put("aaj_pdm_leap", new LungeTowardsPlayerAbility());
        }

        onTargetEntityAbilities.put("on_target_peaceful_300+_karma", new PeacefulAt300KarmaAbility());
        onTargetEntityAbilities.put("on_target_peaceful_600+_karma", new PeacefulAt600KarmaAbility());
        onTargetEntityAbilities.put("on_target_peaceful_900+_karma", new PeacefulAt900KarmaAbility());
        onTargetEntityAbilities.put("on_target_peaceful_-300-_karma", new PeacefulAt300BadKarmaAbility());
        onTargetEntityAbilities.put("on_target_peaceful_-600-_karma", new PeacefulAt600BadKarmaAbility());
        onTargetEntityAbilities.put("on_target_peaceful_-900-_karma", new PeacefulAt900BadKarmaAbility());
        onTargetEntityAbilities.put("on_target_peaceful_villages", new PeacefulToVillagersAbility());
        onTargetEntityAbilities.put("on_target_peaceful_unconditional", new PeacefulUnconditionalAbility());

        onPlayerDamagedAbilities.put("on_damage_big_knockup", new KnockupPlayerAbility());
        onPlayerDamagedAbilities.put("on_damage_aaa_+10%_damage", new DamageBonus10Ability());
        onPlayerDamagedAbilities.put("on_damage_aab_+25%_damage", new DamageBonus25Ability());
        onPlayerDamagedAbilities.put("on_damage_aac_+50%_damage", new DamageBonus50Ability());
        onPlayerDamagedAbilities.put("on_damage_aad_+75%_damage", new DamageBonus75Ability());
        onPlayerDamagedAbilities.put("on_damage_aaf_+100%_damage", new DamageBonus100Ability());
        onPlayerDamagedAbilities.put("on_damage_aag_+200%_damage", new DamageBonus200Ability());
        onPlayerDamagedAbilities.put("on_damage_aah_+400%_damage", new DamageBonus400Ability());
        onPlayerDamagedAbilities.put("on_damage_aai_instant_death", new InstantDeathAbility());
        onPlayerDamagedAbilities.put("on_damage_aaj_-10%_damage", new DamageNerf10Ability());
        onPlayerDamagedAbilities.put("on_damage_aak_-25%_damage", new DamageNerf25Ability());
        onPlayerDamagedAbilities.put("on_damage_aal_-50%_damage", new DamageNerf50Ability());
        onPlayerDamagedAbilities.put("on_damage_aam_-75%_damage", new DamageNerf75Ability());
        onPlayerDamagedAbilities.put("on_damage_aan_-100%_damage", new DamageNerf100Ability());
        onPlayerDamagedAbilities.put("on_damage_aao_fire_aspect_1", new FireAspectIAbility());
        onPlayerDamagedAbilities.put("on_damage_aap_fire_aspect_2", new FireAspectIIAbility());
        onPlayerDamagedAbilities.put("on_damage_aaq_fire_aspect_3", new FireAspectIIIAbility());
        onPlayerDamagedAbilities.put("on_damage_aar_fire_aspect_4", new FireAspectIVAbility());
        onPlayerDamagedAbilities.put("on_damage_aas_fire_aspect_ultimate", new FireAspectAccumulatingAbility());

        instantAbilities.put("instant_charged_creeper", new ChargedCreeperAbility());
        instantAbilities.put("instant_raid_captain", new RaidCaptainAbility());
        instantAbilities.put("instant_turn_baby", new TurnToBabyAbility());
        instantAbilities.put("instant_prevent_spawn", new PreventSpawnAbility());

        onMobDamagedByPlayerAbilities.put("on_damaged_by_player_throwback", new PullWhenDamagedAbility());
        onMobDamagedByPlayerAbilities.put("on_damaged_by_player_knockback", new KnockbackWhenDamagedAbility());

        onMobDamagedAbilities.put("damage_any_projectile_immunity", new ProjectileImmunityAbility());
        onMobDamagedAbilities.put("damage_any_wither_immunity", new WitherImmunityAbility());
        onMobDamagedAbilities.put("damage_any_cramming_immunity", new CrammingImmunityAbility());
        onMobDamagedAbilities.put("damage_any_invincibility", new AllDamageImmunityAbility());
        onMobDamagedAbilities.put("damage_any_explosion_immunity", new ExplosionImmunityAbility());
        onMobDamagedAbilities.put("damage_any_magic_immunity", new MagicImmunityAbility());
        onMobDamagedAbilities.put("damage_any_poison_immunity", new PoisonImmunityAbility());
        onMobDamagedAbilities.put("damage_any_melee_immunity", new MeleeImmunityAbility());
        onMobDamagedAbilities.put("damage_any_falling_immunity", new FallDamageImmunityAbility());

        onMobClickedAbilities.put("on_click_give_offhand_300+_karma", new GiveOffhandAt300KarmaAbility());
        onMobClickedAbilities.put("on_click_give_offhand_600+_karma", new GiveOffhandAt600KarmaAbility());
        onMobClickedAbilities.put("on_click_give_offhand_900+_karma", new GiveOffhandAt900KarmaAbility());
        onMobClickedAbilities.put("on_click_give_offhand_-300-_karma", new GiveOffhandAt300BadKarmaAbility());
        onMobClickedAbilities.put("on_click_give_offhand_-600-_karma", new GiveOffhandAt600BadKarmaAbility());
        onMobClickedAbilities.put("on_click_give_offhand_-900-_karma", new GiveOffhandAt900BadKarmaAbility());
        onMobClickedAbilities.put("on_click_give_offhand_unconditional", new GiveOffhandUnconditionallyAbility());
    }

    /**
     * Registers a running ability
     * Typically reserved for abilities that involve BukkitRunnables
     * These abilities trigger when the monster spawns, and since they use BukkitRunnables they should
     * be used sparingly.
     *
     * For the execute method, player is null
     * @param ability
     */
    public void registerRunningAbility(String name, Ability ability){
        runningAbilities.put(name, ability);
    }

    /**
     * Registers an ability to trigger on an EntityTargetLivingEntityEvent
     *
     * For the execute method, player is null
     * @param name
     * @param ability
     */
    public void registerOnTargetEntityAbility(String name, Ability ability){
        onTargetEntityAbilities.put(name, ability);
    }

    /**
     * Registers an ability that triggers on an EntityDamageByEntityEvent, but only
     * when the monster with this ability damages a player.
     * @param name
     * @param ability
     */
    public void registerOnPlayerDamagedAbility(String name, Ability ability){
        onPlayerDamagedAbilities.put(name, ability);
    }

    /**
     * Registers an ability that triggers on an EntitySpawnEvent
     *
     * For the execute method, player is null
     * @param name
     * @param ability
     */
    public void registerInstantAbility(String name, Ability ability){
        instantAbilities.put(name, ability);
    }

    /**
     * Registers an ability that triggers on an EntityDamageByEntityEvent, but only
     * when the monster with this ability is damaged by a player.
     * @param name
     * @param ability
     */
    public void registerOnMobDamagedByPlayer(String name, Ability ability){
        onMobDamagedByPlayerAbilities.put(name, ability);
    }

    /**
     * Registers an ability that triggers on an EntityDamageEvent
     *
     * For the execute method, player is null
     * @param name
     * @param ability
     */
    public void registerOnMobDamagedAbility(String name, Ability ability){
        onMobDamagedAbilities.put(name, ability);
    }

    /**
     * Registers an ability that triggers on a PlayerInteractEntityEvent, when the player
     * clicks on an entity with an empty hand.
     * @param name
     * @param ability
     */
    public void registerOnMobClickedAbility(String name, Ability ability){
        onMobClickedAbilities.put(name, ability);
    }

    public Map<String, Ability> getAbilities() {
        allAbilities.putAll(instantAbilities);
        allAbilities.putAll(onPlayerDamagedAbilities);
        allAbilities.putAll(runningAbilities);
        allAbilities.putAll(onMobDamagedByPlayerAbilities);
        allAbilities.putAll(onTargetEntityAbilities);
        allAbilities.putAll(onMobDamagedAbilities);
        allAbilities.putAll(onMobClickedAbilities);

        return allAbilities;
    }

    public Map<String, Ability> getInstantAbilities() {
        return instantAbilities;
    }

    public Map<String, Ability> getOnPlayerDamagedAbilities() {
        return onPlayerDamagedAbilities;
    }

    public Map<String, Ability> getOnMobDamagedByPlayerAbilities() {
        return onMobDamagedByPlayerAbilities;
    }

    public Map<String, Ability> getRunningAbilities() {
        return runningAbilities;
    }

    public Map<String, Ability> getOnTargetEntityAbilities() {
        return onTargetEntityAbilities;
    }

    public Map<String, Ability> getOnMobDamagedAbilities() {
        return onMobDamagedAbilities;
    }

    public Map<String, Ability> getOnMobClickedAbilities() {
        return onMobClickedAbilities;
    }

    public Ability getAbility(String id){
        return allAbilities.get(id);
    }

    /**
     * Pretty much exclusively used in the menus. Should not be used elsewhere unless you know what you're doing.
     * @param name
     * @return
     */
    public String getAbilityKeyByName(String name){
        for (String key : allAbilities.keySet()){
            String unstrippedName = allAbilities.get(key).getName();
            String strippedName = ChatColor.stripColor(Utils.chat(unstrippedName));
            if (strippedName.equals(name)){
                return key;
            }
        }
        return null;
    }

    public static AbilityManager getInstance(){
        if (manager == null){
            manager = new AbilityManager();
        }
        return manager;
    }

    public void reload(){
        instantAbilities = new HashMap<>();
        onPlayerDamagedAbilities = new HashMap<>();
        runningAbilities = new HashMap<>();
        onMobDamagedByPlayerAbilities = new HashMap<>();
        onTargetEntityAbilities = new HashMap<>();
        onMobDamagedAbilities = new HashMap<>();
        onMobClickedAbilities = new HashMap<>();
        manager = null;
    }
}
