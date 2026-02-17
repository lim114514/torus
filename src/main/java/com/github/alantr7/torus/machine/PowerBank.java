package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Pitch;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class PowerBank extends Structure {

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
        super(TorusPlugin.DEFAULT_ADDON, "power_bank", translatable("structure.power_bank.name"), PowerBankInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.HEAVY | StructureFlag.TICKABLE);
        portableData.add("energy");
        hologramOffset = new float[] { 0, 1f, 0 };
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 20_000));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 500));
        registerProperty(new Property<>("energy_settings.maximum_output", PropertyType.INT, 500));
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new PowerBankInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f()),
          new StructurePartDef(
            "power_connector", new Vector3f(0, 0, 0), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.ALL, direction.getOpposite().mask()
          )),
          new StructurePartDef("charge", new Vector3f())
        }), direction);
    }

}
