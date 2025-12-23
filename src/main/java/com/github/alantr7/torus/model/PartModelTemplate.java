package com.github.alantr7.torus.model;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PartModelTemplate {

    public final String name;

    public PartModelTemplate(String name) {
        this.name = name;
    }

    public final List<PartModelElementItemDisplayRenderer> parts = new ArrayList<>();

    public void add(PartModelElementItemDisplayRenderer part) {
        parts.add(part);
    }

    public PartModel build(Location location, Direction direction) {
        List<Display> entities = new ArrayList<>();
        ItemDisplay parent = location.getWorld().spawn(location, ItemDisplay.class);

        for (PartModelElementItemDisplayRenderer part : parts) {
            Vector3f rotatedOffset = new Vector3f(part.offset[0], part.offset[1], part.offset[2]);

            Display entity = location.getWorld().spawn(location, ItemDisplay.class);
            transformEntity(entity, rotatedOffset, part, direction.rotH, direction.rotV);

            parent.addPassenger(entity);
            entities.add(entity);
        }

        return new PartModel(this, parent, entities.stream().map(EntityReference::new).toList());
    }

    public PartModel recycle(PartModel model, Location location, float rotH, float rotV) {
        List<Display> entities = new ArrayList<>();
        int entityIdx = 0;
        for (; entityIdx < parts.size(); entityIdx++) {
            PartModelElementItemDisplayRenderer part = parts.get(entityIdx);
            Vector3f rotatedOffset = new Vector3f(part.offset[0], part.offset[1], part.offset[2]);

            Display entity;
            if (entityIdx < model.entityReferences.size()) {
                entity = model.entityReferences.get(entityIdx).getEntity();
                entity.teleport(location);
            } else {
                entity = location.getWorld().spawn(location, ItemDisplay.class);
            }

            transformEntity(entity, rotatedOffset, part, rotH, rotV);
            if (!model.parent.getPassengers().contains(entity)) {
                model.parent.addPassenger(entity);
            }
            entities.add(entity);
        }

        for (; entityIdx < model.entityReferences.size(); entityIdx++) {
            model.entityReferences.get(entityIdx).getEntity().remove();
        }

        return new PartModel(this, model.parent, entities.stream().map(EntityReference::new).toList());
    }

    private void transformEntity(Display entity, Vector3f translation, PartModelElementItemDisplayRenderer part, float rotH, float rotV) {
        entity.setPersistent(false);
        if (entity instanceof BlockDisplay blockDisplay) {
            blockDisplay.setBlock(part.itemStack.clone().getType().createBlockData());
        } else if (entity instanceof ItemDisplay itemDisplay) {
            itemDisplay.setItemStack(part.itemStack.clone());
        }
        entity.getPersistentDataContainer().set(new NamespacedKey(TorusPlugin.getInstance(), "purpose"), PersistentDataType.STRING, "structure_model");

        Transformation transformation = entity.getTransformation();
        transformation.getTranslation().set(rotV == 0 ? translation : new Vector3f(translation).add(0, -0.5f, -0.5f * rotV / Math.abs(rotV)));
        transformation.getScale().set(part.scale);
        transformation.getLeftRotation().set(part.rotation);
        entity.setTransformation(transformation);
        entity.setRotation(rotH, rotV);
    }

}
