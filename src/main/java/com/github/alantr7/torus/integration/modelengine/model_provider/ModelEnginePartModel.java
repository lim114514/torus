package com.github.alantr7.torus.integration.modelengine.model_provider;

import com.github.alantr7.torus.model.PartModel;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModelEnginePartModel extends PartModel {

    public final ModeledEntity entity;

    public final ActiveModel activeModel;

    public ModelEnginePartModel(ModelEnginePartModelTemplate template, ModeledEntity entity, ActiveModel activeModel) {
        super(template);
        this.entity = entity;
        this.activeModel = activeModel;
    }

    @Override
    public void setLocation(@NotNull Location location) {
        Entity entity = (Entity) this.entity.getBase().getOriginal();
        entity.teleport(location);
    }

    @Override
    public void setRotation(float horizontal, float vertical) {
        Entity entity = (Entity) this.entity.getBase().getOriginal();
        entity.setRotation(horizontal, vertical);
    }

    @Override
    public void remove() {
        entity.destroy();
        ((ArmorStand) entity.getBase().getOriginal()).remove();
    }

}
