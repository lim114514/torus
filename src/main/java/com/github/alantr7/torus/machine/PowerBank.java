package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Pitch;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PowerBank extends Structure {

    public static int ENERGY_CAPACITY = 20_000;

    public static int ENERGY_MAXIMUM_INPUT = 500;

    public static int ENERGY_MAXIMUM_OUTPUT = 500;

    static final ModelTemplate MODEL_CHARGE_INDICATOR = new ModelTemplate(1);
    static {
        DisplayEntitiesPartModelTemplate charge = new DisplayEntitiesPartModelTemplate("charge");
        charge.parts.add(new PartModelElementItemDisplayRenderer(
          Material.CYAN_CONCRETE,
          new Vector3f(0, 0.1675f, -0.38475f),
          new Vector3f(0.12f, 0.0011f, 0.0625f),
          0f, 0f)
        );

        MODEL_CHARGE_INDICATOR.add(charge);
    }

    public PowerBank() {
        super(TorusPlugin.DEFAULT_ADDON, "power_bank", "Power Bank", PowerBankInstance.class);
        portableData.add("energy");
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new PowerBankInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef(
            "power_connector", new Vector3f(0, 0, 0), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.ALL, direction.getOpposite().mask()
          )),
          new StructureComponentDef("charge", new Vector3f())
        }), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_MAXIMUM_INPUT = config.getInt("energy_settings.maximum_input", ENERGY_MAXIMUM_INPUT);
        ENERGY_MAXIMUM_OUTPUT = config.getInt("energy_settings.maximum_output", ENERGY_MAXIMUM_OUTPUT);
    }

}
