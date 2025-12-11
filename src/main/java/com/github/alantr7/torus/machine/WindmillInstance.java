package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import lombok.Getter;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

public class WindmillInstance extends StructureInstance implements EnergyContainer {

    float efficiency;

    @Getter
    public final Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    WindmillInstance(LoadContext context) {
        super(context);
    }

    public WindmillInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.WINDMILL, location, bodyDef, direction);
    }

    float angle;

    @Override
    public void tick() {
        model.getPart("blade_1").entityReferences.forEach(ref -> {
            ItemDisplay entity = ref.getEntity();
            Transformation transform = entity.getTransformation();

            Quaternionf rotation = transform.getLeftRotation();
            rotation.setAngleAxis(angle, 0, 0, 1);

            entity.setTransformation(transform);
            entity.setInterpolationDelay(0);
            entity.setInterpolationDuration(20);
        });
        model.getPart("blade_2").entityReferences.forEach(ref -> {
            ItemDisplay entity = ref.getEntity();
            Transformation transform = entity.getTransformation();

            Quaternionf rotation = transform.getLeftRotation();
            rotation.setAngleAxis(angle, 0, 0, 1);

            entity.setTransformation(transform);
            entity.setInterpolationDelay(0);
            entity.setInterpolationDuration(20);
        });
        angle += Windmill.MAXIMUM_SPEED * efficiency;
        supplyEnergy((int) (efficiency * 75));
    }

    @Override
    protected void setup() throws SetupException {
        getSocket("out_energy").maximumOutput = Windmill.ENERGY_MAXIMUM_OUTPUT;
        efficiency = (float) Math.pow(Math.E, -8f/(location.y / 8f + 8f)) * 1.15505059f;
        angle = (float) (Math.random() * Math.PI / 2f) * efficiency;
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 2)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this))
          .property("Eff", () -> (int) (efficiency * 100) + "%");
    }

    @Override
    public int getEnergyCapacity() {
        return Windmill.ENERGY_CAPACITY;
    }

}
