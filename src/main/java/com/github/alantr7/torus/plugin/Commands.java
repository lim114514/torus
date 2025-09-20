package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.commands.annotations.CommandHandler;
import com.github.alantr7.bukkitplugin.commands.factory.CommandBuilder;
import com.github.alantr7.bukkitplugin.commands.registry.Command;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.structure.StructureInstance;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Singleton
public class Commands {

    @com.github.alantr7.bukkitplugin.annotations.generative.Command(name = "torus", description = "")
    public static final String TORUS = "torus";

    @CommandHandler Command give = CommandBuilder.using("torus")
      .parameter("give")
      .parameter("{item}", p -> p.tabComplete((args) -> {
          return TorusPlugin.getInstance().getItemManager().getItemIds();
      }))
      .executes(ctx -> {
          TorusItem item = TorusPlugin.getInstance().getItemManager().getItemById((String) ctx.getArgument("item"));
          if (item == null) {
              ctx.respond("Unknown item.");
              return;
          }

          ((Player) ctx.getExecutor()).getInventory().addItem(item.toItemStack());
          ctx.respond("You received 1 x " + item.namespacedId);
      });

    @CommandHandler Command remove = CommandBuilder.using("torus")
      .parameter("remove")
      .executes(ctx -> {
          TorusPlugin.getInstance().getWorldManager().getWorlds().forEach(world -> {
              new ArrayList<>(world.getLoaded().values()).forEach(StructureInstance::remove);
          });
          ctx.respond("Removed all structures.");
      });

}
