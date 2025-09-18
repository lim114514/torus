package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.structure.display.Model;
import org.joml.Vector3f;

public record StructureComponentDef(String name, Vector3f offset, Model model) {
}
