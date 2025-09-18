package com.github.alantr7.torus.structure.display;

import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.joml.Vector3f;
import org.joml.Vector3i;

public record ItemDisplayModelTemplate(Material material, ItemDisplay.ItemDisplayTransform transform, int cmd, Vector3f offset, Vector3f scale, float rotH, float rotV) {
}
