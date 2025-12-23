package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.api.resource.ResourceLocation;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Structure {

    public final TorusAddon addon;

    public final String namespacedId;

    public final String id;

    public String name;

    public boolean isEnabled;

    public int numericId = -1;

    public String configResource;

    public YamlConfiguration config;

    protected final Class<? extends StructureInstance> instanceClass;

    protected byte[] bounds = { 0, 0, 0 };

    @Getter
    protected byte[] size;

    protected byte[] offset = { 0, 0, 0 };

    public boolean isHeavy = true;

    public boolean isInteractable = false;

    public boolean isOmnidirectional = false;

    public Set<String> portableData = new HashSet<>();

    public float[] hologramOffset = {0f, 0f, 0f};

    public float[] hologramTranslation = {1.4f, 0.8f, 0f};

    @Getter @Setter
    private ModelTemplate model;

    public ResourceLocation modelLocation;

    public Structure(TorusAddon addon, String id, String name, Class<? extends StructureInstance> instanceClass) {
        this.addon = addon;
        this.id = id;
        this.namespacedId = addon.id + ":" + id;
        this.name = name;
        this.instanceClass = instanceClass;
        this.configResource = "configs/" + addon.id + "/structures/" + id + ".config.yml";

        ByteArrayBuilder builder = new ByteArrayBuilder();
        createBounds(builder);

        bounds = builder.build();
        if (bounds.length == 0) {
            bounds = new byte[] { 0, 0, 0 };
        } else if (bounds.length % 3 != 0) {
            throw new RuntimeException("Invalid structure bounds!");
        }

        byte[] min = { 127, 127, 127 };
        byte[] max = { -128, -128, -128 };

        for (int i = 0; i < bounds.length; i+=3) {
            min[0] = (byte) Math.min(bounds[i], min[0]);
            min[1] = (byte) Math.min(bounds[i+1], min[1]);
            min[2] = (byte) Math.min(bounds[i+2], min[2]);

            max[0] = (byte) Math.max(bounds[i], max[0]);
            max[1] = (byte) Math.max(bounds[i+1], max[1]);
            max[2] = (byte) Math.max(bounds[i+2], max[2]);
        }

        size = new byte[] { (byte) (max[0] - min[0] + 1), (byte) (max[1] - min[1] + 1), (byte) (max[2] - min[2] + 1) };
    }

    protected void createBounds(ByteArrayBuilder builder) {
    }

    public boolean isPlaceableAt(BlockLocation location, Direction direction) {
        byte[] offset = calculateOffset(direction.getOpposite());
        byte[] bounds = MathUtils.rotateVectors(this.bounds, direction);

        location = location.getRelative(offset[0], offset[1], offset[2]);
        for (int i = 0; i < bounds.length; i += 3) {
            if (location.getRelative(bounds[i], bounds[i + 1], bounds[i + 2]).getBlock().getType().isSolid()) {
                return false;
            }
        }
        return true;
    }

    public StructureInstance place(BlockLocation location, Direction direction) {
        byte[] offset = calculateOffset(direction.getOpposite());
        location = location.getRelative(offset[0], offset[1], offset[2]);
        StructureInstance instance = instantiate(location, direction);
        place(instance);

        location.world.placeStructure(instance);
        return instance;
    }

    public static void place(StructureInstance instance) {
        try {
            instance.setup();
            if (instance instanceof Inspectable inspectable) {
                instance.inspectableData = inspectable.setupInspectableData();
            }
        } catch (Exception exc) {
            instance.isCorrupted = true;
            instance.inspectableData = new InspectableData((byte) 0);
            exc.printStackTrace();
        }

        setupModel(instance);
        setupInspectionTooltip(instance);
    }

    private static void setupModel(StructureInstance instance) {
        try {
            ModelTemplate modelTemplate = instance.structure.getModel();
            if (modelTemplate != null) {
                instance.model = modelTemplate.toModel(instance.location, instance.direction);
            }
            instance.handleModelInit();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private static void setupInspectionTooltip(StructureInstance instance) {
        if (!instance.isCorrupted && !(instance instanceof Inspectable)) {
            return;
        }
        if (instance.inspectableData.inspectableBlocks.isEmpty()) {
            byte[] bounds = instance.getBounds();
            for (int i = 0; i < bounds.length; i += 3) {
                instance.inspectableData.inspectableBlocks.add(instance.location.getRelative(bounds[i], bounds[i + 1], bounds[i + 2]));
            }
        }
        instance.spawnInspectionTooltip();
        if (instance.isCorrupted) {
            instance.inspectionHologram.setText(ChatColor.RED + "Corrupted Structure\n" + StructureInstance.COLOR_PROPERTY + "Try to place it again");
        }
    }

    private byte[] calculateOffset(Direction direction) {
        if (offset == null)
            return new byte[] { 0, 0, 0 };

        return MathUtils.rotateVectors(this.offset, direction);
    }

    @Override
    public String toString() {
        return namespacedId;
    }

    protected abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction);

    protected void loadConfig() {
        // General Settings
        name = config.getString("general_settings.display_name", this.name);
        isEnabled = config.getBoolean("general_settings.enabled", true);
        isHeavy = config.getBoolean("general_settings.heavy", this.isHeavy);

        List<Byte> placementOffset = config.getByteList("general_settings.placement_offset");
        if (placementOffset.size() == 3) {
            for (int i = 0; i < 3; i++) {
                this.offset[i] = placementOffset.get(i);
            }
        }

        List<String> portableData = config.getStringList("general_settings.portable_data");
        if (!portableData.isEmpty()) {
            this.portableData = new HashSet<>(portableData);
        }

        // Info Hologram Settings
        List<Byte> hologramOffset = config.getByteList("info_hologram.offset");
        if (hologramOffset.size() == 3) {
            for (int i = 0; i < 3; i++) {
                this.hologramOffset[i] = hologramOffset.get(i);
            }
        }

        List<Float> hologramTranslation = config.getFloatList("info_hologram.translation");
        if (hologramTranslation.size() == 3) {
            for (int i = 0; i < 3; i++) {
                this.hologramTranslation[i] = hologramTranslation.get(i);
            }
        }
    }

}
