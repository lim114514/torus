package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;

import java.util.HashMap;
import java.util.Map;

public class Lifecycle {

    private final Map<String, LifecycleAdapter> adapters = new HashMap<>();

    public void subscribe(TorusAddon addon, LifecycleAdapter listener) {
        adapters.put(addon.id, listener);
    }

    public void start() {
        run(LifecycleAction.LOAD_STRUCTURES);
        run(LifecycleAction.LOAD_ITEMS);
        run(LifecycleAction.LOAD_RECIPES);
    }

    public void run(LifecycleAction action) {
        switch (action) {
            case LOAD_STRUCTURES:
                for (LifecycleAdapter adapter : adapters.values())
                    adapter.registerStructures(TorusPlugin.getInstance().getStructureRegistry());
                TorusLogger.info(Category.GENERAL, "Structure registration lifecycle completed.");
                break;

            case LOAD_ITEMS:
                for (LifecycleAdapter adapter : adapters.values())
                    adapter.registerItems(TorusPlugin.getInstance().getItemRegistry());
                TorusLogger.info(Category.GENERAL, "Item registration lifecycle completed.");
                break;

            case LOAD_RECIPES:
                for (LifecycleAdapter adapter : adapters.values())
                    adapter.registerRecipes(TorusPlugin.getInstance().getRecipeRegistry());
                TorusLogger.info(Category.GENERAL, "Recipe registration lifecycle completed.");
                break;
        }
    }

}
