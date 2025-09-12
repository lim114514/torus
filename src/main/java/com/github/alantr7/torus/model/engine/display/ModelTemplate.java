package com.github.alantr7.torus.model.engine.display;

import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.MathUtils;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class ModelTemplate {

    private final List<ItemDisplayModelTemplate> parts = new ArrayList<>();

    public void add(ItemDisplayModelTemplate part) {
        parts.add(part);
    }

    public Model build(Location location, Direction direction) {
        List<ItemDisplay> entities = new ArrayList<>();
        for (ItemDisplayModelTemplate part : parts) {
            Vector3f rotatedOffset = new Vector3f(part.offset().x, part.offset().y, part.offset().z);
//            MathUtils.applyRotation(rotatedOffset, part.rotH() + direction.rotH);

            Location partLocation = new Location(
              location.getWorld(),
              location.getX() + rotatedOffset.x,
              location.getY() + rotatedOffset.y,
              location.getZ() + rotatedOffset.z
            );

            ItemDisplay entity = location.getWorld().spawn(partLocation, ItemDisplay.class);
            entity.setItemStack(new ItemStack(part.material()));
//            entity.setItemDisplayTransform(part.transform());

            Transformation transformation = entity.getTransformation();
            transformation.getScale().set(part.scale());
            entity.setTransformation(transformation);
            entity.setRotation(direction.rotH + part.rotH(), direction.rotV + part.rotV());

            entities.add(entity);
        }

        return new Model(entities);
    }

}
