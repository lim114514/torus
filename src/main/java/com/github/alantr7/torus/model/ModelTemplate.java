package com.github.alantr7.torus.model;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.MathUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelTemplate {

    public final String name;

    public ModelTemplate(String name) {
        this.name = name;
    }

    public ModelTemplate() {
        this(null);
    }

    private final List<ModelPartItemDisplayRenderer> parts = new ArrayList<>();

    public static final ModelTemplate EMPTY = new ModelTemplate();

    public void add(ModelPartItemDisplayRenderer part) {
        parts.add(part);
    }

    public Model build(Location location, Direction direction) {
        List<ItemDisplay> entities = new ArrayList<>();
        for (ModelPartItemDisplayRenderer part : parts) {
            Vector3f rotatedOffset = new Vector3f(part.offset[0], part.offset[1], part.offset[2]);
            MathUtils.applyRotation(rotatedOffset, direction.rotH);

            Location partLocation = new Location(
              location.getWorld(),
              location.getX() + rotatedOffset.x,
              location.getY() + rotatedOffset.y,
              location.getZ() + rotatedOffset.z
            );

            ItemDisplay entity = location.getWorld().spawn(partLocation, ItemDisplay.class);
            transformEntity(entity, part, direction);
            entities.add(entity);
        }

        return new Model(this, entities.stream().map(EntityReference::new).toList());
    }

    public Model recycle(Model model, Location location, float rotH, float rotV) {
        List<ItemDisplay> entities = new ArrayList<>();

        int entityIdx = 0;
        for (; entityIdx < parts.size(); entityIdx++) {
            ModelPartItemDisplayRenderer part = parts.get(entityIdx);
            Vector3f rotatedOffset = new Vector3f(part.offset[0], part.offset[1], part.offset[2]);
            MathUtils.applyRotation(rotatedOffset, rotH);

            Location partLocation = new Location(
              location.getWorld(), location.getX() + rotatedOffset.x, location.getY() + rotatedOffset.y, location.getZ() + rotatedOffset.z
            );

            ItemDisplay entity;
            if (entityIdx < model.entityReferences.size()) {
                entity = model.entityReferences.get(entityIdx).getEntity();
                entity.teleport(partLocation);
            } else {
                entity = location.getWorld().spawn(partLocation, ItemDisplay.class);
            }

            transformEntity(entity, part, rotH, rotV);
            entities.add(entity);
        }

        for (; entityIdx < model.entityReferences.size(); entityIdx++) {
            model.entityReferences.get(entityIdx).getEntity().remove();
        }

        return new Model(this, entities.stream().map(EntityReference::new).toList());
    }

    private void transformEntity(ItemDisplay entity, ModelPartItemDisplayRenderer part, Direction direction) {
        transformEntity(entity, part, direction.rotH, direction.rotV);
    }

    private void transformEntity(ItemDisplay entity, ModelPartItemDisplayRenderer part, float rotH, float rotV) {
        entity.setPersistent(false);
        entity.setItemStack(part.itemStack.clone());
        entity.getPersistentDataContainer().set(new NamespacedKey(TorusPlugin.getInstance(), "purpose"), PersistentDataType.STRING, "structure_model");

        Transformation transformation = entity.getTransformation();
        transformation.getScale().set(part.scale);
        entity.setTransformation(transformation);
        entity.setRotation(rotH + part.rotH, rotV + part.rotV);
    }

}
