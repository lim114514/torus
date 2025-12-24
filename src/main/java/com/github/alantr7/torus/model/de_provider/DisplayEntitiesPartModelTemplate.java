package com.github.alantr7.torus.model.de_provider;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.*;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class DisplayEntitiesPartModelTemplate extends PartModelTemplate {

    public final List<PartModelElementDisplayRenderer> parts = new ArrayList<>();

    public DisplayEntitiesPartModelTemplate(String name) {
        this(name, new Vector3f(.5f, 0, .5f));
    }

    public DisplayEntitiesPartModelTemplate(String name, Vector3f offset) {
        super(name, offset);
    }

    public void add(PartModelElementDisplayRenderer part) {
        parts.add(part);
    }

    @Override
    public PartModel build(Location location, Direction direction) {
        location = location.clone().add(offset.x, offset.y, offset.z);

        List<Display> entities = new ArrayList<>();
        ItemDisplay parent = location.getWorld().spawn(location, ItemDisplay.class);

        for (PartModelElementDisplayRenderer part : parts) {
            Vector3f rotatedOffset = new Vector3f(part.offset[0], part.offset[1], part.offset[2]);

            Display entity = location.getWorld().spawn(location, part.entityType);
            transformEntity(entity, rotatedOffset, part, direction.rotH, direction.rotV);

            parent.addPassenger(entity);
            entities.add(entity);
        }

        return new DisplayEntitiesPartModel(this, parent, entities.stream().map(EntityReference::new).toList());
    }

    @Override
    public PartModel recycle(PartModel model0, Location location, float rotH, float rotV) {
        DisplayEntitiesPartModel model = (DisplayEntitiesPartModel) model0;
        List<Display> entities = new ArrayList<>();
        List<ItemDisplay> itemDisplays = new ArrayList<>();
        List<BlockDisplay> blockDisplays = new ArrayList<>();

        for (EntityReference ref : model.entityReferences) {
            Display entity = ref.getEntity();
            if (entity instanceof ItemDisplay itemDisplay) {
                itemDisplays.add(itemDisplay);
            }
            else if (entity instanceof BlockDisplay blockDisplay) {
                blockDisplays.add(blockDisplay);
            }
        }

        byte entityIdx = 0;
        byte blockDisplayIdx = 0;
        byte itemDisplayIdx = 0;
        for (; entityIdx < parts.size(); entityIdx++) {
            PartModelElementDisplayRenderer part = parts.get(entityIdx);
            Vector3f rotatedOffset = new Vector3f(part.offset[0], part.offset[1], part.offset[2]);

            Display entity;
            if (part.entityType == ItemDisplay.class) {
                if (itemDisplayIdx < itemDisplays.size()) {
                    entity = itemDisplays.get(itemDisplayIdx++);
                    entity.teleport(location);
                } else {
                    entity = location.getWorld().spawn(location, part.entityType);
                }
            }
            else {
                if (blockDisplayIdx < blockDisplays.size()) {
                    entity = blockDisplays.get(blockDisplayIdx++);
                    entity.teleport(location);
                } else {
                    entity = location.getWorld().spawn(location, part.entityType);
                }
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

        return new DisplayEntitiesPartModel(this, model.parent, entities.stream().map(EntityReference::new).toList());
    }

    private void transformEntity(Display entity, Vector3f translation, PartModelElementDisplayRenderer part, float rotH, float rotV) {
        entity.setPersistent(false);
        if (part.entityType == ItemDisplay.class) {
            ((ItemDisplay) entity).setItemStack(((PartModelElementItemDisplayRenderer) part).itemStack.clone());
        } else if (part.entityType == BlockDisplay.class) {
            ((BlockDisplay) entity).setBlock(((PartModelElementBlockDisplayRenderer) part).blockData.clone());
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
