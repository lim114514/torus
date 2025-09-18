package com.github.alantr7.torus.structure.display;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

import java.util.List;

public class Model {

    public final List<ItemDisplay> entities;

    public Model(List<ItemDisplay> entities) {
        this.entities = entities;
    }

    public void remove() {
        entities.forEach(Entity::remove);
    }

}