package me.athlaeos.progressivelydifficultmobs.perks;

import me.athlaeos.progressivelydifficultmobs.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;

import java.util.List;

public abstract class Perk {
    protected Material icon = null;
    protected String displayName;
    protected String name;
    protected List<String> description;
    protected int id;
    protected PerkTriggerPriority perkPriority = PerkTriggerPriority.NEUTRAL;
    protected final YamlConfiguration config = ConfigManager.getInstance().getConfig("perks.yml").get();

    public abstract void execute(Event e);

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PerkTriggerPriority getPerkPriority() {
        return perkPriority;
    }
}
