package com.github.alantr7.torus.model;

import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public final class ModelPartItemDisplayRenderer {

    public final ItemStack itemStack;
    public ItemDisplay.ItemDisplayTransform transform;
    public final float[] offset;
    public final float[] scale;
    public final float rotH;
    public final float rotV;

    public ModelPartItemDisplayRenderer(Material material, Vector3f offset, Vector3f scale, float rotH, float rotV) {
        this.itemStack = new ItemStack(material);
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
        this.offset = new float[] { offset.x, offset.y, offset.z };
        this.scale = new float[] { scale.x, scale.y, scale.z };
        this.rotH = rotH;
        this.rotV = rotV;
    }

    public ModelPartItemDisplayRenderer(Material material, float... data) {
        this(new ItemStack(material), data);
    }

    public ModelPartItemDisplayRenderer(ItemStack stack, float... data) {
        this.itemStack = stack;
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
        this.offset = new float[] { data[0], data[1], data[2] };
        this.scale = new float[] { data[3], data[4], data[5] };
        if (data.length < 8) {
            rotH = rotV = 0;
        } else {
            rotH = data[6];
            rotV = data[7];
        }
    }

}