package com.github.alantr7.torus.model.de_provider;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.joml.Vector3f;

public final class PartModelElementBlockDisplayRenderer extends PartModelElementDisplayRenderer {

    public final BlockData blockData;

    public PartModelElementBlockDisplayRenderer(Material material, Vector3f offset, Vector3f scale, float rotH, float rotV) {
        super(BlockDisplay.class, offset, scale, rotH, rotV);
        this.blockData = material.createBlockData();
    }

    public PartModelElementBlockDisplayRenderer(BlockData blockData, float... data) {
        super(BlockDisplay.class, data);
        this.blockData = blockData;
    }

}