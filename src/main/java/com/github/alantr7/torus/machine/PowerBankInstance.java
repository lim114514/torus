package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModel;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.structure.socket.EnergySocket;
import com.github.alantr7.torus.structure.socket.TransferPreferences;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

import java.util.Set;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class PowerBankInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected EnergySocket socket;

    protected Display chargeIndicatorDisplay;

    private static final TransferPreferences inputPreferences = new TransferPreferences(
      Set.of("torus:power_bank"), TransferPreferences.MODE_BLACKLIST
    );

    public PowerBankInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.POWER_BANK, location, bodyDef, direction);
    }

    PowerBankInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() throws SetupException {
        socket = requireSocket("power_connector", EnergySocket.class);
        socket.maximumInput = structure.getProperty("energy_settings.maximum_input", PropertyType.INT);
        socket.maximumOutput = structure.getProperty("energy_settings.maximum_output", PropertyType.INT);

        chargeIndicatorDisplay = ((DisplayEntitiesPartModel) PowerBank.MODEL_CHARGE_INDICATOR.toModel(location, direction, pitch)
          .parts.get("charge")).entityReferences.getFirst().getEntity();
    }

    @Override
    public InspectableDataContainer setupInspectableData() {
        return new InspectableDataContainer((byte) 1)
          .property(translatable("inspection.energy_unit"), InspectableDataContainer.TEMPLATE_RF.apply(this));
    }

    int energyAtLastTick;
    @Override
    public void tick(boolean isVirtual) {
        socket.maintainEnergy(this, inputPreferences);
        if (energyAtLastTick != storedEnergy.get()) {
            updateChargeIndicator();
        }

        energyAtLastTick = storedEnergy.get();
    }

    @Override
    public void onModelDestroy() {
        chargeIndicatorDisplay.remove();
    }

    @Override
    public int getEnergyCapacity() {
        return structure.getProperty("energy_settings.capacity", PropertyType.INT);
    }

    private void updateChargeIndicator() {
        float ratio = (float) storedEnergy.get() / getEnergyCapacity();
        float height = 1.125f * ratio;
        Transformation transformation = chargeIndicatorDisplay.getTransformation();
        transformation.getScale().y = height;
        transformation.getTranslation().y = 0.1675f + height / 2f;
        chargeIndicatorDisplay.setTransformation(transformation);
    }

}
