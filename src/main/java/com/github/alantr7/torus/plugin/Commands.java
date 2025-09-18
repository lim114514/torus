package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.commands.annotations.CommandHandler;
import com.github.alantr7.bukkitplugin.commands.factory.CommandBuilder;
import com.github.alantr7.bukkitplugin.commands.registry.Command;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureInstance;

import java.util.ArrayList;

@Singleton
public class Commands {

    @com.github.alantr7.bukkitplugin.annotations.generative.Command(name = "torus", description = "")
    public static final String TORUS = "torus";

    @CommandHandler Command remove = CommandBuilder.using("torus")
      .parameter("remove")
      .executes(ctx -> {
          TorusPlugin.getInstance().getWorldManager().getWorlds().forEach(world -> {
              new ArrayList<>(world.getLoaded().values()).forEach(StructureInstance::remove);
          });
          ctx.respond("Removed all structures.");
      });

}
