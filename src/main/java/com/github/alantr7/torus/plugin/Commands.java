package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.commands.annotations.CommandHandler;
import com.github.alantr7.bukkitplugin.commands.factory.CommandBuilder;
import com.github.alantr7.bukkitplugin.commands.registry.Command;
import com.github.alantr7.torus.machine.BlockBreaker;
import com.github.alantr7.torus.machine.EnergyCable;
import com.github.alantr7.torus.machine.SolarGenerator;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Singleton
public class Commands {

    @com.github.alantr7.bukkitplugin.annotations.generative.Command(name = "torus", description = "")
    public static final String TORUS = "torus";

    @CommandHandler Command place = CommandBuilder.using("torus")
        .parameter("place")
        .parameter("{machine}", p -> p.tabComplete("generator:solar", "machine:block_breaker", "utils:cable"))
        .executes(ctx -> {
            if (ctx.getArgument("machine").equals("machine:block_breaker")) {
                BlockBreaker breaker = new BlockBreaker();
                BlockLocation location = new BlockLocation(((Player) ctx.getExecutor()).getLocation());
                Direction direction = Direction.NORTH;

                StructureInstance instance = breaker.instantiate(location, direction);
                instance.create();

                TorusWorld.placeStructure(instance);
                ctx.respond("Placed block breaker at your location: " + location);
            }
            else if (ctx.getArgument("machine").equals("generator:solar")) {
                SolarGenerator generator = new SolarGenerator();
                BlockLocation location = new BlockLocation(((Player) ctx.getExecutor()).getLocation());
                Direction direction = Direction.NORTH;

                StructureInstance instance = generator.instantiate(location, direction);
                instance.create();

                TorusWorld.placeStructure(instance);
                ctx.respond("Placed solar generator at your location: " + location);
            }
            else if (ctx.getArgument("machine").equals("utils:cable")) {
                EnergyCable breaker = new EnergyCable();
                BlockLocation location = new BlockLocation(((Player) ctx.getExecutor()).getLocation());
                Direction direction = Direction.NORTH;

                StructureInstance instance = breaker.instantiate(location, direction);
                instance.create();

                TorusWorld.placeStructure(instance);
                ctx.respond("Placed cable breaker at your location: " + location);
            }
        });


    @CommandHandler Command remove = CommandBuilder.using("torus")
      .parameter("remove")
      .executes(ctx -> {
          new ArrayList<>(TorusWorld.loaded.values()).forEach(TorusWorld::removeStructure);
          ctx.respond("Removed.");
      });

}
