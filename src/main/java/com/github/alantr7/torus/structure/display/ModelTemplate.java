package com.github.alantr7.torus.structure.display;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.MathUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelTemplate {

    private final List<ItemDisplayModelTemplate> parts = new ArrayList<>();

    public static final ModelTemplate EMPTY = new ModelTemplate();

    public void add(ItemDisplayModelTemplate part) {
        parts.add(part);
    }

    public Model build(Location location, Direction direction) {
        List<ItemDisplay> entities = new ArrayList<>();
        for (ItemDisplayModelTemplate part : parts) {
            Vector3f rotatedOffset = new Vector3f(part.offset().x, part.offset().y, part.offset().z);
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

        return new Model(entities.stream().map(EntityReference::new).toList());
    }

    public Model recycle(Model model, Location location, Direction direction) {
        List<ItemDisplay> entities = new ArrayList<>();

        int entityIdx = 0;
        for (; entityIdx < parts.size(); entityIdx++) {
            ItemDisplayModelTemplate part = parts.get(entityIdx);
            Vector3f rotatedOffset = new Vector3f(part.offset().x, part.offset().y, part.offset().z);
            MathUtils.applyRotation(rotatedOffset, direction.rotH);

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

            transformEntity(entity, part, direction);
            entities.add(entity);
        }

        for (; entityIdx < model.entityReferences.size(); entityIdx++) {
            model.entityReferences.get(entityIdx).getEntity().remove();
        }

        return new Model(entities.stream().map(EntityReference::new).toList());
    }

    private void transformEntity(ItemDisplay entity, ItemDisplayModelTemplate part, Direction direction) {
        entity.setItemStack(new ItemStack(part.material()));
        entity.getPersistentDataContainer().set(new NamespacedKey(TorusPlugin.getInstance(), "purpose"), PersistentDataType.STRING, "structure_model");

        Transformation transformation = entity.getTransformation();
        transformation.getScale().set(part.scale());
        entity.setTransformation(transformation);
        entity.setRotation(direction.rotH + part.rotH(), direction.rotV + part.rotV());
    }

}
