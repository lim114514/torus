package com.github.alantr7.torus.model.de_provider;

import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public final class PartModelElementItemDisplayRenderer extends PartModelElementDisplayRenderer {

    public final ItemStack itemStack;
    public ItemDisplay.ItemDisplayTransform transform;

    public PartModelElementItemDisplayRenderer(Material material, Vector3f offset, Vector3f scale, float rotH, float rotV) {
        super(ItemDisplay.class, offset, scale, rotH, rotV);
        this.itemStack = new ItemStack(material);
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
    }

    public PartModelElementItemDisplayRenderer(ItemStack stack, float... data) {
        super(ItemDisplay.class, data);
        this.itemStack = stack;
        this.transform = ItemDisplay.ItemDisplayTransform.NONE;
    }

}