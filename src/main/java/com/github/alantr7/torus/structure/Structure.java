package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.api.resource.ResourceLocation;
import com.github.alantr7.torus.structure.config.StandardConfigGenerator;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyLoader;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.ModelType;
import com.github.alantr7.torus.model.controller.ModelCase;
import com.github.alantr7.torus.model.controller.ModelController;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.world.Pitch;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Structure {

    public final TorusAddon addon;

    public final String namespacedId;

    public final String id;

    public boolean isEnabled;

    public int numericId = -1;

    protected final Class<? extends StructureInstance> instanceClass;

    public ResourceLocation configResource;

    public StandardConfigGenerator configGenerator;

    private final Map<String, Property<?>> properties = new HashMap<>();

    @Getter
    protected final Map<String, State<?>> allowedStates = new HashMap<>();

    @Getter
    protected byte[] collisionVectors = { 0, 0, 0 };

    @Getter
    protected byte[] size;

    @Getter
    protected byte[] offset = { 0, 0, 0 };

    public boolean isHeavy = true;

    public boolean isInteractable = false;

    public boolean isOmnidirectional = false;

    public boolean isTickable = true;

    public boolean isVirtualizable = false;

    public boolean hasCollision = true;

    public Set<String> portableData = new HashSet<>();

    public float[] hologramOffset = {0f, 0f, 0f};

    public float[] hologramTranslation = {1.4f, 0.8f, 0f};

    @Getter @Setter
    private ModelController modelController = new ModelController(ModelType.SINGLEPART, Collections.singleton(new ModelCase(
      Collections.emptyMap(), ModelTemplate.EMPTY, null
    )));

    public Structure(TorusAddon addon, String id, Class<? extends StructureInstance> instanceClass) {
        this(addon, id, "Untitled Structure", instanceClass);
    }

    public Structure(TorusAddon addon, String id, String name, Class<? extends StructureInstance> instanceClass) {
        this.addon = addon;
        this.id = id;
        this.namespacedId = addon.id + ":" + id;
        this.properties.put("general_settings.name", new Property<>("general_settings.name", PropertyType.STRING, name));
        this.instanceClass = instanceClass;
        this.configResource = new ResourceLocation(addon.classpathContainer, "structures/" + id + ".yml");
        this.configGenerator = StandardConfigGenerator.INSTANCE;

        ByteArrayBuilder builder = new ByteArrayBuilder();
        createBounds(builder);

        collisionVectors = builder.build();
        if (collisionVectors.length == 0) {
            collisionVectors = new byte[] { 0, 0, 0 };
        } else if (collisionVectors.length % 3 != 0) {
            throw new RuntimeException("Invalid structure bounds!");
        }

        byte[] min = { 127, 127, 127 };
        byte[] max = { -128, -128, -128 };

        for (int i = 0; i < collisionVectors.length; i+=3) {
            min[0] = (byte) Math.min(collisionVectors[i], min[0]);
            min[1] = (byte) Math.min(collisionVectors[i+1], min[1]);
            min[2] = (byte) Math.min(collisionVectors[i+2], min[2]);

            max[0] = (byte) Math.max(collisionVectors[i], max[0]);
            max[1] = (byte) Math.max(collisionVectors[i+1], max[1]);
            max[2] = (byte) Math.max(collisionVectors[i+2], max[2]);
        }

        size = new byte[] { (byte) (max[0] - min[0] + 1), (byte) (max[1] - min[1] + 1), (byte) (max[2] - min[2] + 1) };
    }

    public String getName() {
        return getProperty("general_settings.name", PropertyType.STRING);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name, PropertyType<T> type) {
        Property<Object> value = (Property<Object>) properties.get(name);
        return value != null && value.type == type ? (T) value.value : null;
    }

    public Collection<Property<?>> getProperties() {
        return properties.values();
    }

    public void registerProperty(Property<?> property) {
        properties.put(property.name, property);
    }

    public void loadPropertyValues(PropertyLoader loader) {
        for (Property<?> prop : properties.values()) {
            loader.load(prop);
        }
    }

    protected void registerState(State<?> state) {
        this.allowedStates.put(state.key, state);
    }

    protected void createBounds(ByteArrayBuilder builder) {
    }

    public boolean isPlaceableAt(BlockLocation location, Direction direction) {
        byte[] offset = calculateOffset(direction.getOpposite());
        byte[] bounds = MathUtils.rotateVectors(this.collisionVectors, direction);

        location = location.getRelative(offset[0], offset[1], offset[2]);
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = location.getRelative(bounds[i], bounds[i + 1], bounds[i + 2]);
            if (relative.getBlock().getType().isSolid() || relative.getStructure() != null) {
                return false;
            }
        }
        return true;
    }

    public StructureInstance place(BlockLocation location, Direction direction, Pitch pitch) {
        byte[] offset = calculateOffset(direction.getOpposite());
        location = location.getRelative(offset[0], offset[1], offset[2]);
        StructureInstance instance = instantiate(location, direction, pitch);
        place(instance);

        location.world.placeStructure(instance);
        return instance;
    }

    public static void place(StructureInstance instance) {
        try {
            instance.setup();
            if (instance instanceof Inspectable inspectable) {
                instance.inspectableDataContainer = inspectable.setupInspectableData();
            }
        } catch (Exception exc) {
            instance.isCorrupted = true;
            instance.inspectableDataContainer = new InspectableDataContainer((byte) 0);
            exc.printStackTrace();
        }

        if (instance.location.getChunk().status == Status.PHYSICAL) {
            instance.makePhysical();
        }
        else if (instance.location.getChunk().status == Status.VIRTUAL) {
            instance.makeVirtual();
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

    protected abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch);

//    protected void loadConfig() {
//        // General Settings
//        name = config.getString("general_settings.display_name", this.name);
//        isEnabled = config.getBoolean("general_settings.enabled", true);
//        isHeavy = config.getBoolean("general_settings.heavy", this.isHeavy);
//
//        List<Byte> placementOffset = config.getByteList("general_settings.placement_offset");
//        if (placementOffset.size() == 3) {
//            for (int i = 0; i < 3; i++) {
//                this.offset[i] = placementOffset.get(i);
//            }
//        }
//
//        List<String> portableData = config.getStringList("general_settings.portable_data");
//        if (!portableData.isEmpty()) {
//            this.portableData = new HashSet<>(portableData);
//        }
//
//        // Info Hologram Settings
//        List<Byte> hologramOffset = config.getByteList("info_hologram.offset");
//        if (hologramOffset.size() == 3) {
//            for (int i = 0; i < 3; i++) {
//                this.hologramOffset[i] = hologramOffset.get(i);
//            }
//        }
//
//        List<Float> hologramTranslation = config.getFloatList("info_hologram.translation");
//        if (hologramTranslation.size() == 3) {
//            for (int i = 0; i < 3; i++) {
//                this.hologramTranslation[i] = hologramTranslation.get(i);
//            }
//        }
//    }
}
