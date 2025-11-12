package com.github.alantr7.torus.model;

import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PartModelElementItemDisplayRenderer {

    public final ItemStack itemStack;
    public ItemDisplay.ItemDisplayTransform transform;
    public final float[] offset;
    public final float[] scale;
    public final Quaternionf rotation;

    public PartModelElementItemDisplayRenderer(Material material, Vector3f offset, Vector3f scale, float rotH, float rotV) {
        this(material, offset, scale, new Vector3f(rotV, rotH, 0));
    }

    public PartModelElementItemDisplayRenderer(Material material, Vector3f offset, Vector3f scale, Vector3f rotation) {
        this.itemStack = new ItemStack(material);
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
        this.offset = new float[] { offset.x, offset.y, offset.z };
        this.scale = new float[] { scale.x, scale.y, scale.z };
        this.rotation = new Quaternionf()
          .rotateXYZ((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
    }

    public PartModelElementItemDisplayRenderer(Material material, float... data) {
        this(new ItemStack(material), data);
    }

    public PartModelElementItemDisplayRenderer(ItemStack stack, float... data) {
        this.itemStack = stack;
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
        this.offset = new float[] { data[0], data[1], data[2] };
        this.scale = new float[] { data[3], data[4], data[5] };
        if (data.length < 8) {
            rotation = new Quaternionf();
        } else {
            rotation = new Quaternionf().rotateAxis((float) Math.toRadians(data[6]), 0, 1, 0).rotateAxis((float) Math.toRadians(data[7]), 1, 0, 0);
        }
    }

}