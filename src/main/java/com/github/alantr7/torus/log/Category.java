package com.github.alantr7.torus.log;

public enum Category {

    GENERAL(""),
    STRUCTURES("[StructureManager] "),
    MODELS("[ModelManager] "),
    ITEMS("[ItemManager] "),
    RECIPES("[RecipeManager] "),
    WORLD("[WorldManager] "),
    INTEGRATIONS("[Integrations] ");

    public final String prefix;

    Category(String prefix) {
        this.prefix = prefix;
    }

}
