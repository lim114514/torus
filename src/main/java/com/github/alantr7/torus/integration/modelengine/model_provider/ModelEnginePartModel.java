package com.github.alantr7.torus.integration.modelengine.model_provider;

import com.github.alantr7.torus.model.PartModel;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class ModelEnginePartModel extends PartModel {

    public final ModeledEntity entity;

    public final ActiveModel activeModel;

    public ModelEnginePartModel(ModeledEntity entity, ActiveModel activeModel) {
        this.entity = entity;
        this.activeModel = activeModel;
    }

    @Override
    public void teleport(Location location) {
        Entity entity = (Entity) this.entity.getBase().getOriginal();
        entity.teleport(location);
    }

    @Override
    public void remove() {
        entity.destroy();
        ((ArmorStand) entity.getBase().getOriginal()).remove();
    }

}
