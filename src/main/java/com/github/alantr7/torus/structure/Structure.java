package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.TorusAPI;
import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.api.resource.Resource;
import com.github.alantr7.torus.api.resource.ResourceLocation;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.ModelType;
import com.github.alantr7.torus.model.controller.ModelCase;
import com.github.alantr7.torus.model.controller.ModelController;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.world.Pitch;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Structure(TorusAddon addon, String id, String name, Class<? extends StructureInstance> instanceClass) {
        this.addon = addon;
        this.id = id;
        this.namespacedId = addon.id + ":" + id;
        this.name = name;
        this.instanceClass = instanceClass;
        this.configResource = "configs/" + addon.id + "/structures/" + id + ".yml";

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

    private static final Pattern STATE_CONFIG_PATTERN = Pattern.compile("[a-z]+=[a-zA-Z0-9]+");
    private static final Map<StateType<?>, Function<String, Object>> stateParsers = Map.of(
      StateType.BOOLEAN,    Boolean::parseBoolean,
      StateType.INT,        Integer::parseInt
    );

    @SuppressWarnings({"unchecked", "deprecation"})
    protected void loadModelController(ConfigurationSection modelControllerSection) {
        if (modelControllerSection == null)
            return;

        String rawModelType = modelControllerSection.getString("type");
        ConfigurationSection casesSection = modelControllerSection.getConfigurationSection("cases");

        if (rawModelType == null || casesSection == null)
            return;

        ModelType modelType;
        if (rawModelType.equalsIgnoreCase("SINGLEPART")) {
            modelType = ModelType.SINGLEPART;
        } else if (rawModelType.equalsIgnoreCase("MULTIPART")) {
            modelType = ModelType.MULTIPART;
        } else return;

        LinkedList<ModelCase> cases = new LinkedList<>();

        for (String rawStateSet : casesSection.getKeys(false)) {
            ConfigurationSection caseSection = casesSection.getConfigurationSection(rawStateSet);
            String rawModel = caseSection.getString("model");
            String animations = caseSection.getString("animations");

            Map<State<Object>, Object> states = new HashMap<>();

            // Load state conditions
            if (rawStateSet.startsWith("state[")) {
                int stateStartPos = rawStateSet.indexOf('[');
                String rawStates = rawStateSet.substring(stateStartPos + 1, rawStateSet.length() - 1);

                Matcher stateMatcher = STATE_CONFIG_PATTERN.matcher(rawStates);
                while (stateMatcher.find()) {
                    String[] stateKeyValuePair = rawStates.substring(stateMatcher.start(), stateMatcher.end()).split("=");

                    String key = stateKeyValuePair[0];
                    String rawValue = stateKeyValuePair[1];

                    State<?> state = allowedStates.get(key);
                    if (state == null) {
                        TorusLogger.error(Category.MODELS, "Unrecognized state: " + key);
                        continue;
                    }

                    Function<String, Object> parser = stateParsers.get(state.type);
                    if (parser == null) {
                        TorusLogger.error(Category.MODELS, "There is no parser for state type: " + state.type);
                        continue;
                    }

                    Object value;
                    try {
                        value = parser.apply(rawValue);
                    } catch (Exception e) {
                        TorusLogger.error(Category.MODELS, "Error during parsing: '" + rawValue + "' is not a valid " + state.type);
                        continue;
                    }

                    states.put((State<Object>) state, value);
                }
            }

            if (!caseSection.isSet("model")) {
                TorusLogger.error(Category.MODELS, "Model path is not set.");
                continue;
            }

            Map<String, String> variables = new HashMap<>();
            if (caseSection.isConfigurationSection("variables")) {
                ConfigurationSection variablesSection = caseSection.getConfigurationSection("variables");
                for (String varName : variablesSection.getKeys(false)) {
                    variables.put(varName, variablesSection.getString(varName));
                }
            }


            ModelTemplate template = TorusPlugin.getInstance().getModelManager().getCached(rawModel);
            if (template == null || !variables.isEmpty()) {
                ResourceLocation modelLocation = new ResourceLocation(
                  addon.externalContainer, "models/" + rawModel + ".model",
                  addon.classpathContainer, "configs/torus/models/" + rawModel + ".model"
                );

                Resource modelResource = modelLocation.getResource();
                if (modelResource == null) {
                    TorusLogger.error(Category.MODELS, "Model does not exist at set path.");
                    continue;
                }

                template = TorusAPI.getModelLoader().load(modelResource, variables);
                if (template == null) {
                    TorusLogger.error(Category.MODELS, "Could not load model template.");
                    continue;
                }

                if (variables.isEmpty()) {
                    TorusPlugin.getInstance().getModelManager().cache(rawModel, template);
                }
            }

            if (rawStateSet.equals("fallback")) {
                cases.addLast(new ModelCase(states, template, animations));
            } else {
                cases.add(Math.max(0, cases.size() - 1), new ModelCase(states, template, animations));
            }
        }

        setModelController(new ModelController(modelType, cases));
    }

}
