package me.athlaeos.progressivelydifficultmobs.managers;

import me.athlaeos.progressivelydifficultmobs.pojo.Container;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.*;

public class BiomeCategoryManager {

    private static BiomeCategoryManager manager = null;
    private Map<String, Container<List<Biome>, Material>> allBiomes = new TreeMap<>();

    public BiomeCategoryManager(){
        registerBiome("Badlands", Arrays.asList(
                "BADLANDS",
                "BADLANDS_PLATEAU",
                "MODIFIED_BADLANDS_PLATEAU",
                "ERODED_BADLANDS",
                "MODIFIED_WOODED_BADLANDS_PLATEAU",
                "WOODED_BADLANDS_PLATEAU"
        ), "RED_SAND");
        registerBiome("Bamboo Forests", Arrays.asList(
                "BAMBOO_JUNGLE",
                "BAMBOO_JUNGLE_HILLS"
        ), "BAMBOO");
        registerBiome("Basalt Deltas", Arrays.asList(
                "BASALT_DELTAS"
        ), "BASALT");
        registerBiome("Beaches", Arrays.asList(
                "BEACH"
        ), "TURTLE_EGG");
        registerBiome("Birch Forests", Arrays.asList(
                "BIRCH_FOREST",
                "BIRCH_FOREST_HILLS",
                "TALL_BIRCH_FOREST",
                "TALL_BIRCH_HILLS"
        ), "BIRCH_LOG");
        registerBiome("Cold Oceans", Arrays.asList(
                "COLD_OCEAN",
                "DEEP_COLD_OCEAN",
                "DEEP_FROZEN_OCEAN",
                "FROZEN_OCEAN"
        ), "BLUE_ICE");
        registerBiome("Crimson Forests", Arrays.asList(
                "CRIMSON_FOREST"
        ), "CRIMSON_FUNGUS");
        registerBiome("Dark Oak Forests", Arrays.asList(
                "DARK_FOREST",
                "DARK_FOREST_HILLS"
        ), "DARK_OAK_LOG");
        registerBiome("Warm Oceans", Arrays.asList(
                "DEEP_LUKEWARM_OCEAN",
                "DEEP_WARM_OCEAN",
                "LUKEWARM_OCEAN",
                "WARM_OCEAN"
        ), "TROPICAL_FISH_BUCKET");
        registerBiome("Deserts", Arrays.asList(
                "DESERT",
                "DESERT_HILLS",
                "DESERT_LAKES"
        ), "SAND");
        registerBiome("The End", Arrays.asList(
                "END_BARRENS",
                "END_HIGHLANDS",
                "END_MIDLANDS",
                "THE_END",
                "SMALL_END_ISLANDS"
        ), "END_STONE");
        registerBiome("Flower Forests", Arrays.asList(
                "FLOWER_FOREST"
        ), "POPPY");
        registerBiome("Forests", Arrays.asList(
                "FOREST",
                "WOODED_HILLS"
        ), "OAK_LOG");
        registerBiome("Giant Spruce Forests", Arrays.asList(
                "GIANT_SPRUCE_TAIGA",
                "GIANT_SPRUCE_TAIGA_HILLS",
                "GIANT_TREE_TAIGA",
                "GIANT_TREE_TAIGA_HILLS"
        ), "PODZOL");
        registerBiome("Mountains", Arrays.asList(
                "GRAVELLY_MOUNTAINS",
                "MODIFIED_GRAVELLY_MOUNTAINS",
                "MOUNTAIN_EDGE",
                "MOUNTAINS",
                "SNOWY_MOUNTAINS",
                "TAIGA_MOUNTAINS",
                "WOODED_MOUNTAINS",
                "SNOWY_TAIGA_MOUNTAINS",
                "STONE_SHORE"
        ), "STONE");
        registerBiome("Ice Spikes", Arrays.asList(
                "ICE_SPIKES"
        ), "PACKED_ICE");
        registerBiome("Jungles", Arrays.asList(
                "JUNGLE",
                "JUNGLE_EDGE",
                "JUNGLE_HILLS",
                "MODIFIED_JUNGLE",
                "MODIFIED_JUNGLE_EDGE"
        ), "JUNGLE_LOG");
        registerBiome("Mushroom Islands", Arrays.asList(
                "MUSHROOM_FIELD_SHORE",
                "MUSHROOM_FIELDS"
        ), "RED_MUSHROOM");
        registerBiome("Nether Wastes", Arrays.asList(
                "NETHER_WASTES"
        ), "NETHERRACK");
        registerBiome("Plains", Arrays.asList(
                "PLAINS",
                "SUNFLOWER_PLAINS"
        ), "GRASS_BLOCK");
        registerBiome("Savannas", Arrays.asList(
                "SAVANNA",
                "SHATTERED_SAVANNA",
                "SHATTERED_SAVANNA_PLATEAU"
        ), "ACACIA_LOG");
        registerBiome("Snowy Taigas/Tundras", Arrays.asList(
                "SNOWY_BEACH",
                "SNOWY_TAIGA",
                "SNOWY_TAIGA_HILLS",
                "SNOWY_TUNDRA"
        ), "SNOW_BLOCK");
        registerBiome("Soul Sand Valleys", Arrays.asList(
                "SOUL_SAND_VALLEY"
        ), "SOUL_SAND");
        registerBiome("Swamps", Arrays.asList(
                "SWAMP",
                "SWAMP_HILLS"
        ), "LILY_PAD");
        registerBiome("Taigas", Arrays.asList(
                "TAIGA",
                "TAIGA_HILLS"
        ), "SPRUCE_LOG");
        registerBiome("Warped Forests", Arrays.asList(
                "WARPED_FOREST"
        ), "WARPED_FUNGUS");
    }

    public static BiomeCategoryManager getInstance(){
        if (manager == null){
            manager = new BiomeCategoryManager();
        }
        return manager;
    }

    public Map<String, Container<List<Biome>, Material>> getAllBiomes() {
        return allBiomes;
    }

    /**
     * Registers an amount of biomes into a category under name to be used in the biome filter.
     *
     * Example:
     * <code>
     * registerBiome("Taigas", Arrays.asList(
     *     "TAIGA",
     *     "TAIGA_HILLS"
     * ), "SPRUCE_LOG");
     * </code>
     *
     * If a biome does not exist in this version of minecraft, it will be skipped.
     * @param category
     * @param stringBiomes
     * @param iconMaterial
     */
    public void registerBiome(String category, List<String> stringBiomes, String iconMaterial){
        List<Biome> biomes = new ArrayList<>();
        for (String stringBiome : stringBiomes) {
            Biome b;
            try {
                b = Biome.valueOf(stringBiome);
            } catch (IllegalArgumentException ignored) {
                System.out.println("Could not register " + stringBiome);
                continue;
            }
            biomes.add(b);
        }
        Material material;
        try {
            material = Material.valueOf(iconMaterial);
        } catch (IllegalArgumentException ignored) {
            material = Material.valueOf("GRASS_BLOCK");
        }
        if (biomes.size() > 0){
            allBiomes.put(category, new Container<>(biomes, material));
        }
    }
}
