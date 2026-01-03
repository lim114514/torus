package com.github.alantr7.torus.integration.modelengine.model_provider;

import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.model.animation.Animation;
import com.github.alantr7.torus.model.animation.AnimationProvider;
import com.github.alantr7.torus.world.Direction;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.joml.Vector3f;

import java.util.Map;

public class ModelEnginePartModelTemplate extends PartModelTemplate {

    public final String modelId;

    public ModelEnginePartModelTemplate(String name, Vector3f offset, String modelId, Map<String, AnimationProvider<PartModel, Animation>> animations) {
        super(name, offset, animations);
        this.modelId = modelId;
    }

    @Override
    public PartModel build(Location location, Direction direction) {
        ModelBlueprint blueprint = ModelEngineAPI.getAPI().getModelRegistry().get(modelId);
        if (blueprint == null)
            return null;

        Vector3f offset = new Vector3f(this.offset.x, this.offset.y, this.offset.z);
        MathUtils.applyRotation(offset, direction.rotH);

        Location baseEntityLocation = location.clone().add(offset.x, offset.y, offset.z).add(.5, 0, .5);
        baseEntityLocation.setYaw(direction.getOpposite().rotH);
        baseEntityLocation.setPitch(direction.rotV);

        ArmorStand stand = location.getWorld().spawn(baseEntityLocation, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.addScoreboardTag("torus_entity");
        stand.setMarker(true);
        stand.setInvulnerable(true);
        stand.setPersistent(false);
        stand.setAI(false);

        ModeledEntity modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(stand);
        ActiveModel model = ModelEngineAPI.createActiveModel(blueprint);
        modeledEntity.addModel(model, true);

        return new ModelEnginePartModel(modeledEntity, model);
    }

    @Override
    public PartModel recycle(PartModel model, Location location, float rotH, float rotV) {
        return null;
    }

}
