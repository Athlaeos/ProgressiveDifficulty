package me.athlaeos.progressivelydifficultmobs.listeners;

import me.athlaeos.progressivelydifficultmobs.main.Main;
import me.athlaeos.progressivelydifficultmobs.managers.*;
import me.athlaeos.progressivelydifficultmobs.pojo.LeveledMonster;
import me.athlaeos.progressivelydifficultmobs.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntitySpawnListener implements Listener {

	private double spawnChance = Main.getInstance().getConfig().getDouble("chance_custom_spawn");
	private final NamespacedKey monsterIdKey = new NamespacedKey(Main.getInstance(), "pdm-monsterID");
	private final NamespacedKey monsterSpawnCustom = new NamespacedKey(Main.getInstance(), "pdm-monster_spawn_custom");
	private final NamespacedKey monsterSpawnCursed = new NamespacedKey(Main.getInstance(), "pdm-monster_spawn_cursed");

	private final List<SpawnReason> viableSpawnReasons = new ArrayList<>(Arrays.asList(
			SpawnReason.DEFAULT,
			SpawnReason.NATURAL,
			SpawnReason.SPAWNER_EGG,
			SpawnReason.VILLAGE_INVASION,
			SpawnReason.VILLAGE_DEFENSE,
			SpawnReason.SPAWNER,
			SpawnReason.SILVERFISH_BLOCK,
			SpawnReason.RAID,
			SpawnReason.PATROL,
			SpawnReason.NETHER_PORTAL,
			SpawnReason.LIGHTNING,
			SpawnReason.ENDER_PEARL,
			SpawnReason.BUILD_WITHER,
			SpawnReason.BUILD_IRONGOLEM,
			SpawnReason.BUILD_SNOWMAN
	));

	public EntitySpawnListener() {}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onMobSpawn(CreatureSpawnEvent e) {
		PlayerKarmaManager karmaManager = PlayerKarmaManager.getInstance();
		LeveledMonsterManager monsterManager = LeveledMonsterManager.getInstance();
		if (!e.isCancelled()){
			LivingEntity mob = e.getEntity();

			if (e.getSpawnReason() == SpawnReason.NATURAL){
				if (!(e.getEntity() instanceof Boss)){
					for (Player p : Utils.getPlayersInArea(mob.getLocation(), 128)){
						if (!CooldownManager.getInstance().cooldownLowerThanZero(p, "local_peaceful_mode_duration")){
							e.getEntity().remove();
							return;
						}
					}
				}
			}

			if (WorldguardManager.getWorldguardManager().isLocationInRegionWithFlag(e.getEntity().getLocation(), "pdm-custom-monster-deny")){
				return;
			}

			new BukkitRunnable(){
				@Override
				public void run() {
					LeveledMonster chosenMonster = null;
					if (!mob.getPersistentDataContainer().has(monsterIdKey, PersistentDataType.STRING)){
						if (!mob.hasMetadata("pdm-cancel_monster_custom")){
							boolean isBaby = false;
							if (viableSpawnReasons.contains(e.getSpawnReason())) {
								int levelToSpawn = karmaManager.getKarmaLevel(karmaManager.getAverageKarmaSurroundingPlayers(mob.getLocation()));
								if (mob instanceof Zombie){
									if (((Zombie) mob).isBaby()){
										isBaby = true;
										((Zombie) mob).setBaby(false); //this is done because for some reason babies dont get customized
									}
								}
								List<LeveledMonster> availableMonsters = monsterManager.getSpawnableMonsters(levelToSpawn, mob.getType(), mob.getLocation());
								if (availableMonsters.size() > 0){
									if (Utils.getRandom().nextDouble() * 100 < spawnChance){
										chosenMonster = monsterManager.pickRandomMonster(availableMonsters);

										monsterManager.customizeMob(mob, chosenMonster, e.getSpawnReason());
									}
									if (mob instanceof Zombie){
										if (((Zombie) mob).isBaby()){
											isBaby = true;
											((Zombie) mob).setBaby(false);
										}
									}
								}
								if (mob instanceof Zombie){
									((Zombie) mob).setBaby(isBaby);
								}
							}

							if (mob instanceof Raider){
								if (((Raider) mob).isPatrolLeader()){
									ItemStack banner = Utils.createItemStack(Material.WHITE_BANNER, Utils.chat("&6&oOminous Banner"), null);
									BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
									assert bannerMeta != null;
									bannerMeta.addPattern(new Pattern(DyeColor.CYAN, PatternType.RHOMBUS_MIDDLE));
									bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM));
									bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER));
									bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.BORDER));
									bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
									bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.HALF_HORIZONTAL));
									bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE_MIDDLE));
									bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
									banner.setItemMeta(bannerMeta);

									mob.getEquipment().setHelmetDropChance(1.0F);
									mob.getEquipment().setHelmet(banner);
								}
							}
						}
					}

					if (!mob.hasMetadata("pdm-cancel_monster_cursed")) {
						if (!PluginConfigurationManager.getInstance().isVanillaCursed()) {
							if (!mob.getPersistentDataContainer().has(monsterIdKey, PersistentDataType.STRING)) {
								return;
							}
						}
						int maxCurse = PlayerCurseManager.getInstance().getMaxCurseSurroundingPlayers(mob.getLocation());
						if (maxCurse == 0) return;
						double spawnChance = PlayerCurseManager.getInstance().getMonsterSpawnCursedChance(maxCurse);
						if (spawnChance > 1000) spawnChance = 1000D;
						if ((Utils.getRandom().nextInt(1000) + 1) <= (spawnChance)) {
							CursedMonsterManager.getInstance().curseMonster(mob, chosenMonster);
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 1);
		}
	}
}
